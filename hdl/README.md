# RAM Tester Peripheral

This is the peripheral that's flashed to the PL by the FSBL. It's little more
than a state machine that reads sequentially from an AXI port. It can be set to
fetch a certain number of blocks from a user-specified memory buffer. The buffer
can have a power-of-two length, and accesses wrap around once they go off the
end. Once all the blocks have been fetched, one can read out the maximum length
of a single transfer and how many cycles the whole process took, both truncated
to 32 bits.

## Building

This project uses [SpinalHDL][1], a Scala EDSL for hardware description, to
generate the final Verilog code. As such, just `sbt run`. The output will be in
the `build/` directory.

## Usage

### Ports

As specified in `Gen.scala`, the clock is triggered on the rising edge, and the
reset is active-low.

Other than that, the peripheral has only two AXI interfaces. The `axi_ps` AXI4
Lite Slave interface is meant to interface with the Zynq PS, hooked up to a
general-purpose master. The `axi_ram` AXI4 Master interface is supposed to be
connected to a high-performance slave on the PS.

Since the Verilog is auto-generated, the names of the signals are not
consistent. Thankfully, Vivado supports attributes to specify which interface a
signal belongs to and what function it serves. If you aren't using Vivado,
manually read the `X_INTERFACE_INFO` attributes. The second parameter is the
interface name, and the last is the function it serves (e.g. `ARREADY`).

### Registers

The peripheral's base address is currently hard-coded to be `0x4000_0000`. All
the memory-mapped registers are 32-bit and are located at the following
addresses:

| Offset |      Register      | Initial Value |  R/W  |
| :----: | :----------------- | ------------: | :---: |
| `0x00` | `cur_addr`         | `0x1000_0000` |   W   |
| `0x04` | `blocks_rem`       |           `0` |  RW   |
| `0x08` | `addr_update_mask` | `0x000f_ffff` |   W   |
| `0x10` | `cycles_taken`     |       -       |   R   |
| `0x14` | `max_latency`      |       -       |   R   |

#### `cur_addr`

Internally used to keep track of the address the peripheral is currently reading
from. Since it's write-only it functions to program the starting address of the
memory buffer to test with. Must be reprogrammed after every test.

#### `blocks_rem`

Internally used to keep track of how many blocks need to be fetched before
stopping. If it's zero, the peripheral is idle. When zero, can be written to
tell the peripheral how many blocks to fetch. Writing a non-zero value with set
the test in motion.

#### `addr_update_mask`

```
addr <= (addr & ~addr_update_mask) ^ ((addr + increment) & addr_update_mask)
```

When moving to the next block, the address is calculated as shown above. For
power-of-two address buffer sizes, this register should be set to one less than
the size of the buffer. This way, accesses "wrap around" when the end of the
buffer is reached.

#### `cycles_taken`

How many clock cycles the previous test took. This field only has meaning after
a test is complete.

#### `max_latency`

The maximum number of clock cycles a single transfer took, from issue to
recieving the last beat. This field only has meaning after a test is complete.

[1]: https://github.com/SpinalHDL/SpinalHDL "GitHub: SpinalHDL/SpinalHDL"
