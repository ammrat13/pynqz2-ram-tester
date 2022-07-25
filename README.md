# Pynq Z2 RAM Throughput and Latency Tester

This repository consists of a peripheral and the code to drive it toward the
goal of measuring the throughput and latency of DRAM with respect to the
Programmable Logic. Once set up, the peripheral sequentially accesses data in
DRAM, meanwhile the processor hammers it with random memory requests. The
peripheral has performance counters which can be read at the end of the test.

```
Peripheral xferd: 0x2faf0800 bytes
Host xferd:       0x05c93e70 bytes
Time taken:       0x750adc46 cycles
Max latency:      0x00000055 cycles
```

The results I obtained are shown above. The PS, PL, and DRAM clock speeds were
`650MHz`, `100MHz`, and `525MHz` respectively. The PL took `0.393 us/xfer` on
average, for a throughput of `155.4 MiB/s`. However, in the worst case the PL
takes over twice as long as it does on average to transfer a block,
potentially cutting the throughput down to `71.8 MiB/s`.

In my testing, it seems like the PS's transfer rate is half that of the PL.
However, other `ldr` and `str` instructions could mess with this metric. The
caches were disabled, though, so that's not a confounding factor.

# Usage

See either `hdl/` or `src/` for instructions on building and using the
peripheral or the code respectively.
