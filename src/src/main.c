#include "main.h"

MMIO_WO32(CUR_ADDR,         0x40000000)
MMIO_RW32(BLOCKS_REM,       0x40000004)
MMIO_WO32(ADDR_UPDATE_MASK, 0x40000008)
MMIO_RO32(CYCLES_TAKEN, 0x40000010)
MMIO_RO32(MAX_LATENCY,  0x40000014)


// The memory test buffer is where we will issue dummy reads and writes
// Where does it start and how long is it?
const uint32_t CUR_ADDR_INIT         = 0x10000000;
const uint32_t ADDR_UPDATE_MASK_INIT = 0x000fffff;

// How long the peripheral (and thus, we) should be taking memory for
const uint32_t BLOCKS_REM_INIT = 50000000;

// How many passes of the main loop we should do before polling the peripheral
const uint32_t POLL_INTERVAL = 100;
// How often to write instead of reading
const uint32_t WRITE_INTERVAL = 5;

void main(void) {

    // Printout on start to make sure we got here
    uart_put_string("\r\nPynq Z2 Ram Tester:\r\n");

    // Populate the registers, starting the test
    uart_put_string("Starting test...\r\n");
    *CUR_ADDR         = CUR_ADDR_INIT;
    *ADDR_UPDATE_MASK = ADDR_UPDATE_MASK_INIT;
    *BLOCKS_REM = BLOCKS_REM_INIT;

    // Performance counter for how many times we poll
    uint32_t poll_perfcount = 0;

    // Establish a pointer to the memory test buffer
    volatile uint32_t *const buf = (volatile uint32_t *) CUR_ADDR_INIT;

    // Main loop
    while(*BLOCKS_REM != 0) {
        poll_perfcount++;

        for(size_t i = 0; i < POLL_INTERVAL; i++) {
            // Get the index we're updating
            size_t rw_index = (rand() & ADDR_UPDATE_MASK_INIT) >> 2;
            // Read or write
            if(i % WRITE_INTERVAL != 0)
                buf[rw_index];
            else
                buf[rw_index] = rw_index;
        }
    }


    uart_put_string("Finished test!\r\n");

    uart_put_string("Peripheral xferd: ");
    uart_put_hex_32(BLOCKS_REM_INIT * 16);
    uart_put_string(" bytes\r\n");

    uart_put_string("Host xferd:       ");
    uart_put_hex_32(poll_perfcount * POLL_INTERVAL * sizeof(uint32_t));
    uart_put_string(" bytes\r\n");

    uart_put_string("Time taken:       ");
    uart_put_hex_32(*CYCLES_TAKEN);
    uart_put_string(" cycles\r\n");

    uart_put_string("Max latency:      ");
    uart_put_hex_32(*MAX_LATENCY);
    uart_put_string(" cycles\r\n");
}
