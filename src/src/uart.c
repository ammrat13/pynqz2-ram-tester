#include "uart.h"

// MMIO registers we use
static volatile uint32_t *const UART0_SR   = (volatile uint32_t *) 0xe000002c;
static volatile uint32_t *const UART0_FIFO = (volatile uint32_t *) 0xe0000030;
// Bits in those registers
static const uint32_t UART_SR_TXEMPTY = (1 << 3);

void uart_put_char(char c) {
    // Wait for the TX FIFO to be empty, ...
    while(!(*UART0_SR & UART_SR_TXEMPTY));
    // ... then write
    *UART0_FIFO = c;
}

void uart_put_string(const char *s) {
    // Iterate over all the characters, writing each of them
    // Stop on null terminator
    while(*s != 0) {
        uart_put_char(*s);
        s++;
    }
}

void uart_put_hex_32(uint32_t n) {
    // Create a buffer for the result
    // 32-bits means 8 nibbles
    // Start with "0x" and end with a null terminator
    char ret[11];
    ret[0]  = '0';
    ret[1]  = 'x';
    ret[10] = 0;
    // Populate the buffer
    for(size_t i = 9; i >= 2; i--, n >>= 4) {
        uint8_t v = n & 0xf;
        ret[i] = (v >= 10) ? (v - 10) + 'a' : v + '0';
    }
    // Print
    uart_put_string(ret);
}
