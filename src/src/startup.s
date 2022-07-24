// Set up the registers, then call main. Usually required to be at the start of
// RAM, but the bootloader can handle ELFs so that isn't required.

.section ".text._start"
.arm

// ENTRYPOINT
.global _start
_start:

    // Zero out all the registers
    // Skip SP because we'll set it later
    // Skip PC for obvious reasons
    mov r0,  #0
    mov r1,  #0
    mov r2,  #0
    mov r3,  #0
    mov r4,  #0
    mov r5,  #0
    mov r6,  #0
    mov r7,  #0
    mov r8,  #0
    mov r9,  #0
    mov r10, #0
    mov r11, #0
    mov r12, #0
    mov lr,  #0

    // Set the stack pointer where the linker script tells us to
    ldr sp, =_stack_bottom

    // Call main
    bl main

    // Go into an infinite loop when done
_end:
    wfi
    b _end
