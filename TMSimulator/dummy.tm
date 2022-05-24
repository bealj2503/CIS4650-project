* Standard prelude:
  0:     LD  6,0(0) 	load gp with maxaddress
  1:    LDA  5,0(6) 	copy to gp to fp
  2:     ST  0,0(0) 	clear location 0
* Jump around i/o routines here
* code for input routine
  4:     ST  0,-1(5) 	store return
  5:     IN  0,0,0 	input
  6:     LD  7,-1(5) 	return to caller
* code for output routine
  7:     ST  0,-1(5) 	store return
  8:     LD  0,-2(5) 	load output value
  9:    OUT  0,0,0 	output
 10:     LD  7,-1(5) 	return to caller
  3:    LDA  7,7(7) 	jump around i/o code

  12:   ST  0, -1(5)
* imagine x is at -2(5)
  13:     ST  5,-3(5) 	push ofp
  14:    LDA  5,-3(5) 	push frame
  15:    LDA  0,1(7) 	load ac with ret ptr
  16:    LDA  7,-13(7) 	jump to fun loc
  17:     LD  5,0(5) 	pop frame
  18:    ST  0, -2(5)
  19:   LD 1, -2(5)
  20:   LD 7, -1(5)
  11: LDA 7, 9(7)
  21: ST 5,-1(5)
  22: LDA 5, -1(5)
  23: LDA 0,1(7)
  24: LDA 7,-13(7)
  25: LD 5,0(5)
  26: HALT 0,0,0
