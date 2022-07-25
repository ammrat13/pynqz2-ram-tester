/*
    Basic functions to print to UART 0. They are not efficient, using polling
    and only transmitting when the FIFO is empty.
*/

#pragma once

// Standard integer types are good to have
#include <stddef.h>
#include <stdbool.h>
#include <stdint.h>

// Print a single character
void uart_put_char(char c);
// Print a null-terminated string
void uart_put_string(const char *s);

// Print a 32-bit number in hex
void uart_put_hex_32(uint32_t n);
