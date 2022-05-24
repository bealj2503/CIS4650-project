4650 Project
=============
Group members: Jacob Beal, Lorent Aliu

Description:
This is a compiler made for the C minus test language for CIS 4650 project. 

How to run:
Ensure Java, JCUP and JFLEX GNUMake are installed correctly on your machine.

Change the CLASSPATH variable in the makefile to 
-cp location of cup jar file:. aswell as del to rm -f on linux
or -cp location of cup jar file;. on windows

Run the make command to generate all necessary files.

If on windows:
java -cp (location of JCUP jar);. Main (cm file)

If on linux: 
java -cp (location of JCUP jar):. Main (cm file)

Certain flags produce different results,

-a will generate an abstract syntax tree that will be printed to stdout

-s will generate a symbol table that will be printed to stdout

-g will generate assembly code file which will produce a .tm file that can be simulated using the TM simulator found in the TMsimulator folder.

To make a new build for the TM Simulator, type "make clean" and "make". 

To run a compiled program such as "sort.tm", type "./tm sort.tm".  Once in the simulator, type "h" for 
the list of available commands.  In particular, type "g" to run the program, and the output should be 
displayed on the screen. To run the program again, type "c" to clear up the environment and type "g" to run the program.

To quit the simulator, simply type "q" to exit.

Example cm files test.cm and test1.cm are located in the examples directory.