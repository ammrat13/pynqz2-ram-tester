package com.github.ammrat13.pynqz2ramtester

import spinal.core._

import spinal.lib.master
import spinal.lib.slave

import spinal.lib.bus.amba4.axi.Axi4ReadOnly
import spinal.lib.bus.amba4.axi.Axi4Config

import spinal.lib.bus.amba4.axilite.AxiLite4
import spinal.lib.bus.amba4.axilite.AxiLite4Config
import spinal.lib.bus.amba4.axilite.AxiLite4SlaveFactory

import spinal.lib.fsm.EntryPoint
import spinal.lib.fsm.State
import spinal.lib.fsm.StateMachine

object PynqZ2RamTester {

  /** Configuration for RAM AXI interface */
  val RAM_CONFIG: Axi4Config = Axi4Config(
    dataWidth = 32,
    addressWidth = 32,
    useLen = true,
    useLast = true,
    useId = false,
    useRegion = false,
    useBurst = false,
    useLock = false,
    useCache = false,
    useSize = false,
    useQos = false,
    useResp = false,
    useStrb = false
  )

  /** Configuration for PS interaction */
  val PS_CONFIG: AxiLite4Config =
    AxiLite4Config(dataWidth = 32, addressWidth = 32)
}

class PynqZ2RamTester extends Component {

  /** How we interact with RAM
    * @see
    *   [[PynqZ2RamTester.RAM_CONFIG]]
    */
  val ram = master(Axi4ReadOnly(PynqZ2RamTester.RAM_CONFIG))
  // Some values for the RAM are hard-coded
  // * Accesses are privileged and secure
  // * Burst length is always 16
  ram.ar.payload.prot := B"001"
  ram.ar.payload.len := U"x0f"

  /** Registers we expose to the PS
    * @see
    *   [[PynqZ2RamTester.PS_CONFIG]]
    */
  val ps = slave(AxiLite4(PynqZ2RamTester.PS_CONFIG))
  // Easy way to add registers to ps_interface
  private val ps_factory = AxiLite4SlaveFactory(ps)

  /** What address we read from
    *
    * Mapped write-only to PS address `0x4000_0000`. Hooked directly to AR on
    * [[this.ram]]. Will be updated during testing, so don't change it while the
    * test is running.
    */
  val cur_addr = {
    // Initialize with a sane initial address - the start of DRAM
    val ret = RegInit(U"x0010_0000")
    // Updated on write to address 0x4000_0000, and hooked to RAM address
    ps_factory.write(ret, BigInt("40000000", 16))
    ram.ar.payload.addr := ret
    // Done with initialization
    ret
  }

  /** How many blocks remain to read
    *
    * Mapped write-only to the PS address `0x4000_0004`. Serves as a counter for
    * the internal FSM, so don't change it while the test is running.
    */
  val blocks_rem = {
    val ret = RegInit(U(0, 32 bits))
    this.ps_factory.write(ret, BigInt("40000004", 16))
    ret
  }

  /** Mask for address incrementing
    *
    * Mapped write-only to the PS address `0x4000_0008`. This mask signifies
    * what bits of the address to update when moving to the next block. It's
    * controlling what power of two to modulo by. Affects updating blocks, so
    * don't change it while the test is running.
    */
  val addr_update_mask = {
    val ret = RegInit(U"x000f_ffff") // 1MiB
    ps_factory.write(ret, BigInt("40000008", 16))
    ret
  }

  /** Number of cycles taken
    *
    * Mapped read-only to the PS address `0x4000_0010`.
    */
  val cycles_taken = {
    val ret = RegInit(U(0, 32 bits))
    ps_factory.read(ret, BigInt("40000010", 16))
    ret
  }

  /** Maximum number of cycles between initiating the transaction and finishing
    *
    * Mapped read-only to the PS address `0x4000_0014`.
    */
  val max_latency = {
    val ret = RegInit(U(0, 32 bits))
    ps_factory.read(ret, BigInt("40000014", 16))
    ret
  }

  /** The main state machine
    *
    * Starts in an idle state. When the number of blocks remaining is set to a
    * nonzero value, reset all the registers, then go to the main loop. In the
    * loop, read a burst of 64 bytes from the current address, time how long it
    * is until the last block arrives, then increment.
    */
  val fsm = new StateMachine {

    // Default values for signals
    ram.ar.valid := False
    ram.r.ready := False
    cycles_taken := cycles_taken + 1

    // Counter for latency
    val latency = RegInit(U(0, 32 bits))

    // Idle state
    // When we have blocks to process, go to another state
    val idle: State = new State with EntryPoint {
      // Don't increment cycles taken
      // Wait for blocks_rem to be set to something nonzero
      whenIsActive {
        cycles_taken := cycles_taken
        when(blocks_rem =/= U(0))(goto(send))
      }
      // Reset on switch
      onExit {
        cycles_taken := U(0)
        latency := U(0)
      }
    }

    // Send state
    // Sends the transaction to RAM
    val send: State = new State {
      // Send transaction to RAM
      // Remember to increment latency
      whenIsActive {
        ram.ar.valid := True
        ram.ar.payload.addr := cur_addr
        latency := latency + 1
        when(ram.ar.ready)(goto(recv))
      }
      // Update address modulo the mask
      onExit {
        cur_addr :=
          ((cur_addr) & (~addr_update_mask)) ^ ((cur_addr + 64) & (addr_update_mask))
      }
    }

    // Recieve state
    // Wait for a completed transaction from RAM
    val recv: State = new State {
      // Wait for the last beat of the transfer
      whenIsActive {
        ram.r.ready := True
        latency := latency + 1
        when(ram.r.valid && ram.r.payload.last) {
          when(blocks_rem - 1 === U(0)) {
            exit()
          } otherwise {
            goto(send)
          }
        }
      }
      // Decrement blocks remaining
      // Update max latency
      onExit {
        blocks_rem := blocks_rem - 1
        latency := U(0)
        when(latency > max_latency) {
          max_latency := latency
        }
      }
    }
  }
}
