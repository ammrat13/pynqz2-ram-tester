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

  /** Attributes to add to the RAM AXI interface */
  val RAM_ATTRIBS: Seq[(Axi4ReadOnly => Data, Attribute)] = Seq(
    (
      _.ar.ready,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram ARREADY"
      )
    ),
    (
      _.ar.valid,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram ARVALID"
      )
    ),
    (
      _.ar.payload.addr,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram ARADDR"
      )
    ),
    (
      _.ar.payload.len,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram ARLEN"
      )
    ),
    (
      _.ar.payload.prot,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram ARPROT"
      )
    ),
    (
      _.r.ready,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram RREADY"
      )
    ),
    (
      _.r.valid,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram RVALID"
      )
    ),
    (
      _.r.payload.data,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram RDATA"
      )
    ),
    (
      _.r.payload.last,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ram RLAST"
      )
    )
  )

  /** Attributes to add to the PS interface */
  val PS_ATTRIBS: Seq[(AxiLite4 => Data, Attribute)] = Seq(
    (
      _.ar.ready,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps ARREADY"
      )
    ),
    (
      _.ar.valid,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps ARVALID"
      )
    ),
    (
      _.ar.payload.addr,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps ARADDR"
      )
    ),
    (
      _.ar.payload.prot,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps ARPROT"
      )
    ),
    (
      _.r.ready,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps RREADY"
      )
    ),
    (
      _.r.valid,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps RVALID"
      )
    ),
    (
      _.r.payload.data,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps RDATA"
      )
    ),
    (
      _.r.payload.resp,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps RRESP"
      )
    ),
    (
      _.aw.valid,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps AWVALID"
      )
    ),
    (
      _.aw.ready,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps AWREADY"
      )
    ),
    (
      _.aw.payload.addr,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps AWADDR"
      )
    ),
    (
      _.aw.payload.prot,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps AWPROT"
      )
    ),
    (
      _.w.ready,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps WREADY"
      )
    ),
    (
      _.w.valid,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps WVALID"
      )
    ),
    (
      _.w.payload.data,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps WDATA"
      )
    ),
    (
      _.w.payload.strb,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps WSTRB"
      )
    ),
    (
      _.b.ready,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps BREADY"
      )
    ),
    (
      _.b.valid,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps BVALID"
      )
    ),
    (
      _.b.payload.resp,
      new AttributeString(
        "X_INTERFACE_INFO",
        "xilinx.com:interface:aximm:1.0 axi_ps BRESP"
      )
    )
  )
}

class PynqZ2RamTester extends Component {

  /** How we interact with RAM
    * @see
    *   [[PynqZ2RamTester.RAM_CONFIG]]
    * @see
    *   [[PynqZ2RamTester.RAM_ATTRIBS]]
    */
  val ram = {
    val ret = master(Axi4ReadOnly(PynqZ2RamTester.RAM_CONFIG));
    PynqZ2RamTester.RAM_ATTRIBS.map { case (p, a) => p(ret).addAttribute(a) }
    ret
  }
  // Some values for the RAM are hard-coded
  // * Accesses are privileged and secure
  // * Burst length is always 16
  ram.ar.payload.prot := B"001"
  ram.ar.payload.len := U"x0f"

  /** Registers we expose to the PS
    * @see
    *   [[PynqZ2RamTester.PS_CONFIG]]
    * @see
    *   [[PynqZ2RamTester.PS_ATTRIBS]]
    */
  val ps = {
    val ret = slave(AxiLite4(PynqZ2RamTester.PS_CONFIG));
    PynqZ2RamTester.PS_ATTRIBS.map { case (p, a) => p(ret).addAttribute(a) }
    AxiLite4SlaveFactory(ret)
  }

  /** What address we read from
    *
    * Mapped write-only to PS address `0x4000_0000`. Hooked directly to AR on
    * [[PynqZ2RamTester.ram]]. Will be updated during testing, so don't change
    * it while the test is running.
    */
  val cur_addr = {
    // Initialize with a sane initial address - somewhere in DRAM
    val ret = RegInit(U"x1000_0000")
    // Updated on write to address 0x4000_0000, and hooked to RAM address
    ps.write(ret, BigInt("40000000", 16))
    ram.ar.payload.addr := ret
    // Done with initialization
    ret
  }

  /** How many blocks remain to read
    *
    * Mapped read-and-write to the PS address `0x4000_0004`. Serves as a counter
    * for the internal FSM, so don't change it while the test is running.
    */
  val blocks_rem = {
    val ret = RegInit(U(0, 32 bits))
    this.ps.readAndWrite(ret, BigInt("40000004", 16))
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
    ps.write(ret, BigInt("40000008", 16))
    ret
  }

  /** Number of cycles taken
    *
    * Mapped read-only to the PS address `0x4000_0010`.
    */
  val cycles_taken = {
    val ret = RegInit(U(0, 32 bits))
    ps.read(ret, BigInt("40000010", 16))
    ret
  }

  /** Maximum number of cycles between initiating the transaction and finishing
    *
    * Mapped read-only to the PS address `0x4000_0014`.
    */
  val max_latency = {
    val ret = RegInit(U(0, 32 bits))
    ps.read(ret, BigInt("40000014", 16))
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
    // Increment by default
    val latency = RegInit(U(0, 32 bits))
    latency := latency + 1

    // Idle state
    // When we have blocks to process, go to another state
    val idle: State = new State with EntryPoint {
      // Don't increment cycles taken or latency
      // Wait for blocks_rem to be set to something nonzero
      whenIsActive {
        cycles_taken := cycles_taken
        latency := latency
        when(blocks_rem =/= U(0)) { goto(send) }
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
      whenIsActive {
        ram.ar.valid := True
        ram.ar.payload.addr := cur_addr
        when(ram.ar.ready) { goto(recv) }
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
        when(ram.r.valid && ram.r.payload.last) {
          when(blocks_rem - 1 === U(0)) {
            goto(idle)
          } otherwise {
            goto(send)
          }
        }
      }
      // Decrement blocks remaining
      // Reset latency for the next block, and update the max latency
      onExit {
        blocks_rem := blocks_rem - 1
        latency := U(0)
        when(latency > max_latency) { max_latency := latency }
      }
    }
  }
}
