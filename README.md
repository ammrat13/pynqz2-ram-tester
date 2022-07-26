# Pynq Z2 RAM Throughput and Latency Tester

This repository consists of a peripheral and the code to drive it toward the
goal of measuring the throughput and latency of DRAM with respect to the
Programmable Logic. Once set up, the peripheral sequentially accesses data in
DRAM, meanwhile the processor hammers it with random memory requests. The
peripheral has performance counters which can be read at the end of the test.

Note that the peripheral accesses DRAM sequentially instead of randomly. This
was representative of most of my workloads, but it may not be for yours.

```
Peripheral xferd: 0x02faf080 128-byte blocks
Host xferd:       0x013e1cb8 4-byte blocks
Time taken:       0x6f6525dc 100MHz cycles
Max latency:      0x00000055 100MHz cycles
```

The results I obtained are shown above. The PS, PL, and DRAM clock speeds were
`650MHz`, `100MHz`, and `525MHz` respectively. The PL took `0.374 us/xfer` on
average, for a throughput of `326.6 MiB/s`. However, in the worst case the PL
takes over twice as long as it does on average to transfer a block, potentially
cutting the throughput down to `143.6 MiB/s`.

In my testing, it seems like the PS's transfer rate is less than half that of
the PL. However, other `ldr` and `str` instructions could mess with this metric.
The caches were disabled for this test.

## Usage

This code was built and tested with the [Pynq Z2][1] development board. In
theory, it should work with any Zynq 7000 system with UART0 connected. If it has
less than 512 MiB of RAM, `CUR_ADDR_INIT` in `src/src/main.c` may be too high.
Set it to a point low enough that you don't go off the end of DRAM but high
enough that you don't overwrite the existing code and data.

See either `hdl/` or `src/` for instructions on building and using the
peripheral or the code respectively.

[1]: https://www.tulembedded.com/FPGA/ProductsPYNQ-Z2.html "TUL: Pynq Z2"
