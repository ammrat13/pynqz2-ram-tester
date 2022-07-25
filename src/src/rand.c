#include "rand.h"

// XKCD 221
static uint32_t seed = 0x45534342;

// Borland C/C++ LCG
// https://en.wikipedia.org/wiki/Linear_congruential_generator
static const uint32_t A = 22695477;
static const uint32_t C = 1;

uint32_t rand(void) {
    // Increment
    seed *= A;
    seed += C;
    // Return
    // Reverse the bits first since we want more entropy in the lower bits
    return __builtin_bitreverse32(seed);
}
