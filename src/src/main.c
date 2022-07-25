#include "main.h"

MMIO_WO32(CUR_ADDR,         0x40000000)
MMIO_RW32(BLOCKS_REM,       0x40000004)
MMIO_WO32(ADDR_UPDATE_MASK, 0x40000008)
MMIO_RO32(CYCLES_TAKEN, 0x40000010)
MMIO_RO32(MAX_LATENCY,  0x40000014)

void main(void) {

    // Printout on start to make sure we got here
    uart_put_string("\r\nPynq Z2 Ram Tester:\r\n");

    // Mark all registers as unused for now
    ((void) CUR_ADDR);
    ((void) BLOCKS_REM);
    ((void) ADDR_UPDATE_MASK);
    ((void) CYCLES_TAKEN);
    ((void) MAX_LATENCY);
}
