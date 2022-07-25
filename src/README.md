# RAM Tester Code

This is the code that runs on the PS after the board has been initialized by the
FSBL. It sets up the peripheral's registers, then hammers RAM with requests at
the same time as the peripheral tries to access it. At the end, it prints out
some statistics.

## Building

I built with `clang` and the `Makefile` is set up as such, though there's no
obvious reason `gcc` shouldn't work.

The default target is to build a `BOOT.BIN` file for SD card booting. This
either requires `bootgen` to be in your path or for the `BOOTGEN` variable to be
set appropriately. The BIF file looks in the `xilinx/` directory at the root of
this repository for files. Specifically, it looks for `xilinx/fsbl.elf` as the
first-stage bootloader, and for `xilinx/bitstream.bit` for the PL's bitstream.
Alternatively, `make elf` can be used to build an ELF file.

## Running

At the code's entrypoint, the board is expected to be completely initialized.
Obviously, DRAM has to work, as does UART0 since that's where the printouts go.
Ensure that these peripherals are enabled when configuring the PS. I
additionally enabled the SD card so I could boot from it, then I used the
provided first-stage bootloader to initialize the board.

The test takes a while to run. For me it took about 30 seconds. If you feel it's
taking too long, try reducing `BLOCKS_REM_INIT` in `src/main.c`.
