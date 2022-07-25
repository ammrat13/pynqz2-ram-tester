/*
    Utility definitions for MMIO registers.
*/

#pragma once

// MMIO register declarations
// Have variations depending on the read/writeability of the hardware register,
// even though these generate the same code. These also generate `static` at the
// start, so they can only be used in one file, which is fine for our purposes.
// Also, don't put a semicolon after these.
#define MMIO_RW32(name, addr) \
    static volatile uint32_t *const name = (volatile uint32_t *) addr;
#define MMIO_RO32(name, addr) \
    static volatile uint32_t *const name = (volatile uint32_t *) addr;
#define MMIO_WO32(name, addr) \
    static volatile uint32_t *const name = (volatile uint32_t *) addr;

// MMIO register bitfield
#define MMIO_BITFIELD(name, bit) \
    static const uint32_t name = (1 << bit);

// Whether a bit is set or clear
#define MMIO_BIT_SET(reg, bit) ((*(reg) & (bit)) != 0)
#define MMIO_BIT_CLR(reg, bit) ((*(reg) & (bit)) == 0)
