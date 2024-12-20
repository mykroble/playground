.global _start
.align 2
.section .data
prompt: .asciz "Enter a number: "  
buffer: .space 10                  

_start:
    
    mov x0, 1                      
    ldr x1, =prompt                
    mov x2, #14                    
    mov x8, #0x200004              
    svc #0                         

    mov x0, 0                     
    ldr x1, =buffer               
    mov x2, #10                   
    mov x8, #0x200003             
    svc #0                        

    mov x0, #0                    
    mov x8, #0x200000            
    svc #0                        

