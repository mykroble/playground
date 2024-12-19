.global _main
.align 2
_main:
    // Let's assume we set X0 to either 1 for "Good Morning" or 0 for "Good Night"
    mov X0, #1             // Set the register X0 to 1 (Good Morning)
    
    // Check if X0 == 1 (Good Morning)
    cmp X0, #1             // Compare X0 with 1
    bne _good_night        // If not equal (bne), branch to _good_night

_good_morning:
    // Print "Good Morning"
    mov X0, #1             // File descriptor for stdout
    adr X1, good_morning   // Load address of "Good Morning" string
    mov X2, #13            // Length of "Good Morning"
    mov X16, #4            // System call number for write (4)
    svc 0                  // Make system call (write)

    b _terminate           // Jump to terminate

_good_night:
    // Print "Good Night"
    mov X0, #1             // File descriptor for stdout
    adr X1, good_night     // Load address of "Good Night" string
    mov X2, #12            // Length of "Good Night"
    mov X16, #4            // System call number for write (4)
    svc 0                  // Make system call (write)

_terminate:
    mov X0, #0             // Exit status
    mov X16, #1            // System call number for exit (1)
    svc 0                  // Make system call (exit)

good_morning: .ascii "Good Morning\n"
good_night:   .ascii "Good Night\n"