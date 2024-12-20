.global _main
.align 2

// Main entry point
_main:
    // Prompt and read first operand
    adr X1, operand1_msg       // Address of "Enter first operand" message
    bl _print_string           // Print message
    bl _read_integer           // Read integer input into X0
    mov X2, X0                 // Store first operand in X2

    // Prompt and read second operand
    adr X1, operand2_msg       // Address of "Enter second operand" message
    bl _print_string           // Print message
    bl _read_integer           // Read integer input into X1

    // Perform addition
    add X3, X2, X0             // X3 = X2 + X0

    // Print result
    adr X1, result_msg         // Address of "Result: " message
    bl _print_string           // Print message
    mov X0, X3                 // Move result to X0 for printing
    bl _print_integer          // Print the result

    // Terminate program
    b _terminate

// Function: Print string
// Input: X1 = address of string
_print_string:
    // Find the length of the string
    mov X2, #0                 // Clear length counter
_find_length:
    ldrb W3, [X1, X2]          // Load byte at (X1 + X2)
    cbz W3, _length_found       // If null terminator found, exit loop
    add X2, X2, #1             // Increment length counter
    b _find_length

_length_found:
    mov X0, #1                 // File descriptor: stdout
    mov X16, #4                // Syscall number: write
    svc 0                      // Perform syscall
    ret


// Function: Read integer from stdin
// Output: X0 = integer value
_read_integer:
    mov X0, #0                 // File descriptor: stdin
    adr X1, input_buffer       // Address of input buffer
    mov X2, #12                // Max input length
    mov X16, #3                // Syscall number: read
    svc 0

    // Check if any input was actually read
    cbz X0, _invalid_input     // If no bytes were read, it's invalid

    // Null-terminate the buffer to safely process input
    adr X1, input_buffer       // Reload buffer address
    add X1, X1, X0             // Move pointer to the end of the input
    mov W3, #0                 // Null terminator
    strb W3, [X1]              // Write null terminator at the end

    // Convert ASCII to integer
    adr X1, input_buffer       // Reload buffer address
    mov X0, #0                 // Clear X0 for result
    mov X4, #10                // Base 10 for conversion

_read_loop:
    ldrb W3, [X1], #1          // Load byte and increment pointer
    cmp W3, #0                 // Check for null terminator
    beq _read_done             // Exit on null terminator
    cmp W3, #0x30              // Check if character >= '0'
    blt _invalid_input         // If less, it's invalid
    cmp W3, #0x39              // Check if character <= '9'
    bgt _invalid_input         // If greater, it's invalid
    sub W3, W3, #0x30          // Convert ASCII '0'-'9' to integer
    uxtw X3, W3                // Zero-extend W3 to X3 (64-bit)
    mul X0, X0, X4             // Multiply result by 10
    add X0, X0, X3             // Add current digit to result
    b _read_loop

_invalid_input:
    adr X1, error_msg          // Address of error message
    bl _print_string           // Print error message
    b _terminate               // Exit program

_read_done:
    ret


// Function: Print integer
// Input: X0 = integer value
_print_integer:
    stp X29, X30, [SP, #-16]!  // Save frame pointer and return address
    mov X29, SP                // Update frame pointer
    sub SP, SP, #16            // Allocate space on stack

    mov X1, SP                 // Address of buffer for string
    add X1, X1, #15            // Point to the end of the buffer
    mov W2, #0x30              // ASCII '0'
    mov W3, #10                // Base 10

_print_loop_int:
    udiv X4, X0, X3            // Divide by 10, quotient in X4
    msub X5, X4, X3, X0        // Compute remainder
    add W5, W5, W2             // Convert to ASCII
    strb W5, [X1], #-1         // Store character and decrement pointer
    mov X0, X4                 // Update quotient
    cbnz X0, _print_loop_int   // Repeat until quotient is 0

    mov W5, #0
    strb W5, [X1]              // Null-terminate the string

    // Print the string
    mov X0, #1                 // File descriptor: stdout
    mov X2, SP                 // Address of string
    add X2, X1, #15            // Calculate length
    mov X16, #4                // Syscall number: write
    svc 0

    add SP, SP, #16            // Restore stack
    ldp X29, X30, [SP], #16    // Restore frame pointer and return address
    ret

// Terminate the program
_terminate:
    mov X0, #0                 // Exit status
    mov X16, #1                // Syscall number: exit
    svc 0

// Data section
operand1_msg: .ascii "Enter first operand: \n\0"
operand2_msg: .ascii "Enter second operand: \n\0"
result_msg:   .ascii "Result: "
error_msg:    .ascii "Invalid input. Please enter a valid number.\n\0"
input_buffer: .space 12        // Buffer for input
.balign 4 