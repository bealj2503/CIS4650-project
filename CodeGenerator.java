
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import absyn.*;


public class CodeGenerator implements AbsynVisitor {
  public ArrayList<HashMap<String, NodeType>> table; 
  public ArrayList<HashMap<String, VarType>> varLocation;
  public int curr;
  public String currType;
  public ArrayList<String> errorList;
  public boolean hasRet;
  public String fileName;
  //Checkpoint 3 vars
  public int assemblyLine;
  public int numLocalVars;
  public int numGlobalVars;
  public int mainAccessLine;
  public String outString;
  public int currentOffset;
  public int fp;
  public boolean fpHasSet;
  public ArrayList<HashMap<String, Integer>> funcLocation;
  public ArrayList<HashMap<String, Integer>> varArrSize;
  // prof vars
  public int highEmitLoc;
  public int emitLoc;
  public int newStack;
  public int previousOffset;
  public CodeGenerator(String file){
    this.table = new ArrayList<HashMap<String,NodeType>>();
    this.table.add(new HashMap<String,NodeType>());
    this.curr = 0;
    this.currType = "";
    this.errorList = new ArrayList<String>();
    this.hasRet = false;
    //checkpoint 3
    this.fileName = file.split(".cm")[0]+".tm";
    this.outString = "* Standard prelude:\n0:     LD  6,0(0) 	load gp with maxaddress\n1:    LDA  5,0(6) 	copy to gp to fp\n2:     ST  0,0(0) 	clear location 0\n* Jump around i/o routines here\n3:    LDA  7,7(7) 	jump around i/o code\n* code for input routine\n4:     ST  0,-1(5) 	store return\n5:     IN  0,0,0 	input\n6:     LD  7,-1(5) 	return to caller\n* code for output routine\n7:     ST  0,-1(5) 	store return\n8:     LD  0,-2(5) 	load output value\n9:    OUT  0,0,0 	output\n10:     LD  7,-1(5) 	return to caller\n* End of standard prelude.\n";
    this.assemblyLine = 11;
    this.mainAccessLine = -1;
    this.numLocalVars = 0;
    this.numGlobalVars = 0;
    this.currentOffset = 2;
    this.fp = 1023;
    this.varLocation = new ArrayList<HashMap<String, VarType>>();
    this.varLocation.add(new HashMap<String,VarType>());
    this.funcLocation = new ArrayList<HashMap<String,Integer>>();
    this.funcLocation.add(new HashMap<String,Integer>());
    this.fpHasSet = false;
    this.newStack = 0;
    this.previousOffset=0;
    this.varArrSize = new ArrayList<HashMap<String,Integer>>();
    this.varArrSize.add(new HashMap<String,Integer>());
    // prof vars
    this.emitLoc = 0;
    this.highEmitLoc = 0;
  }
  final static int SPACES = 1;
  
  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( "|   " );
  }

  public void visit( ExpList expList, int level, boolean isAddr ) {
    
    while( expList != null && expList.head != null ) {
      
      if((expList.head instanceof ReturnExp) && this.hasRet == false){
        this.hasRet = true;
      }
      expList.head.accept( this, level, isAddr);
      expList = expList.tail;
    } 
  }

  public void visit( AssignExp exp, int level, boolean isAddr ) {
      int offset = -1;
      int levelVar = -1;
      VarArrExp lhs = (VarArrExp)exp.lhs;
      int recallOffset = 0;

      for(int i = 0;i<=this.curr;i++){
        if(this.varLocation.get(i).get(lhs.name.name) != null){
          offset= this.varLocation.get(i).get(lhs.name.name).location;
          levelVar = i;
          break;

        }
        
      }

      if(lhs.num != null){
        if(lhs.num instanceof VarArrExp){
          VarArrExp indexExp = (VarArrExp) lhs.num;
          int indexOffset = -1;
          for(int i = 0; i< this.curr;i++){
            if(this.varLocation.get(i).get(indexExp.name.name)!= null){
              indexOffset = this.varLocation.get(i).get(indexExp.name.name).location;
            }
          }
          
          bracketInstruction("LDA", 0, -offset, 5);
          bracketInstruction("LD", 1, -indexOffset, 5);
          expInstruction("SUB", 0, 0, 1);
          bracketInstruction("ST", 0, -currentOffset, 5);
          recallOffset = currentOffset;
          currentOffset++;
          
        }
        else if(lhs.num instanceof IntExp){
          IntExp indexExp = (IntExp) lhs.num;
          int index = Integer.parseInt(indexExp.value);
          bracketInstruction("LDA", 0, -offset-index, 5);
          bracketInstruction("ST", 0, -currentOffset, 5);
          recallOffset = currentOffset;
          currentOffset++;
        }
      }
      // exp.lhs.accept( this, level );
      if(exp.rhs instanceof IntExp){
        IntExp integerEx = (IntExp)exp.rhs;
        int val = Integer.parseInt(integerEx.value);
        
        if(lhs.num != null){
          bracketInstruction("LDC", 0, val, 0);
          bracketInstruction("LD", 1, -recallOffset, 5);
          bracketInstruction("ST", 0, 0,1);
          

        }
        else{
          if(levelVar > 0){
            bracketInstruction("LDC", 0, val, 0);
            bracketInstruction("ST", 0, -offset, 5);
          }
          else{
            bracketInstruction("LDC", 0, val, 0);
            bracketInstruction("ST", 0, -offset, 6);
          }
          
        }
        
        
        
      }
      else if(exp.rhs instanceof OpExp){
        if(lhs.num != null){
          exp.rhs.accept( this, level,isAddr );
          bracketInstruction("LD", 1, -recallOffset, 5);
          bracketInstruction("ST", 0, 0, 1);
        }
        else{
          exp.rhs.accept( this, level,isAddr );
          bracketInstruction("ST", 0, -offset, 5);  
        }
        
        
      }
      else if(exp.rhs instanceof CallExp){
        if(lhs.num != null){
          exp.rhs.accept(this,level,isAddr);
          bracketInstruction("LD", 1, -recallOffset, 5);
          bracketInstruction("ST", 0, 0, 1);
        }
        else{
          exp.rhs.accept(this,level,isAddr);
          bracketInstruction("ST", 0, -offset, 5); 
        }
        
      }
      else if(exp.rhs instanceof VarArrExp){
        VarArrExp rhs = (VarArrExp) exp.rhs;
        int rightOffset = -1;
        for(int i = 0;i<=this.curr;i++){
          if(this.varLocation.get(i).get(rhs.name.name) != null){
            rightOffset= this.varLocation.get(i).get(rhs.name.name).location;
            
            break;
      
          }     
        }
        if(lhs.num != null){
          bracketInstruction("LD", 0, -rightOffset, 5); 
          bracketInstruction("LD", 1, -recallOffset, 5);
          bracketInstruction("ST", 0, 0, 1);
        }
        else{
          bracketInstruction("LD", 0, -rightOffset, 5); 
          bracketInstruction("ST", 0, -offset, 5); 
        }
        
      }
      

    
    
  }

  public void visit( IfExp exp, int level, boolean isAddr ) {
    if(exp.test instanceof OpExp){
      OpExp test = (OpExp) exp.test;
      exp.test.accept(this, level, isAddr);
      //bracketInstruction("ST", 0, -currentOffset, 5);
      //currentOffset++;
      int jumpLine = this.assemblyLine;
      assemblyLine++;
      if(exp.elsepart != null)
        exp.elsepart.accept(this,level,isAddr);
      int unconditionalJump = this.assemblyLine;
      assemblyLine++;
      int trueLine = this.assemblyLine;
      exp.thenpart.accept(this, level,isAddr);
      int trueEnd = this.assemblyLine-1;
      int uncondOff = (trueEnd-unconditionalJump);
      this.outString+="\n"+unconditionalJump+":    LDA  7,"+uncondOff+"(7)";
      if(test.op == 5){
        this.outString+= "\n"+jumpLine+":    JLT  0,"+((trueLine-jumpLine)-1)+"(7)";
      }
      else if(test.op == 6){
        this.outString+= "\n"+jumpLine+":    JGT  0,"+((trueLine-jumpLine)-1)+"(7)";
      }
      else if(test.op == 7){
        this.outString+= "\n"+jumpLine+":    JGE  0,"+((trueLine-jumpLine)-1)+"(7)";
      }
      else if(test.op == 8){
        this.outString+= "\n"+jumpLine+":    JLE  0,"+((trueLine-jumpLine)-1)+"(7)";
      }
      else if(test.op == 9){
        this.outString+= "\n"+jumpLine+":    JNE  0,"+((trueLine-jumpLine)-1)+"(7)";
      }
      else if(test.op == 10){
        this.outString+= "\n"+jumpLine+":    JEQ  0,"+((trueLine-jumpLine)-1)+"(7)";
      }
    }
  }

  public void visit( IntExp exp, int level, boolean isAddr ) {
    int val = Integer.parseInt(exp.value);
    bracketInstruction("LDC", 0, val, 0);
 
    bracketInstruction("ST", 0, -this.currentOffset, 5);
    this.currentOffset++;
     
  }

  public void visit( OpExp exp, int level, boolean isAddr ) {
      if(exp.left instanceof IntExp){
        IntExp lhs = (IntExp) exp.left;
        String className = exp.right.getClass().getSimpleName();
        if(className.equals("IntExp")){
          IntExp rhs = (IntExp) exp.right;
          bracketInstruction("LDC", 0, Integer.parseInt(lhs.value), 0);
          bracketInstruction("LDC", 1, Integer.parseInt(rhs.value), 0);
        }
      }
      else if(exp.left instanceof VarArrExp){
        VarArrExp lhs = (VarArrExp) exp.left;
        int offset = -1;
        for(int i = 0;i<=this.curr;i++){
          if(this.varLocation.get(i).get(lhs.name.name) != null){
            offset= this.varLocation.get(i).get(lhs.name.name).location;

        
          }
          
        }
        if((exp.right instanceof IntExp) && offset != -1){
          bracketInstruction("LD", 0, -offset, 5);
          bracketInstruction("LDC", 1, Integer.parseInt(((IntExp)exp.right).value), 0);
        }
        if(exp.right instanceof VarArrExp  && offset != -1){
          VarArrExp rhs = (VarArrExp) exp.right;
          bracketInstruction("LD", 0, -offset, 5);
          int rhsOffset = -1;
          for(int i = 0;i<=this.curr;i++){
            if(this.varLocation.get(i).get(rhs.name.name) != null){
              rhsOffset= this.varLocation.get(i).get(rhs.name.name).location;
            }
          }
          if(rhsOffset!= -1){
            bracketInstruction("LD", 1, -rhsOffset, 5);
          }
        }
        if(exp.right instanceof CallExp && offset != -1){
          exp.right.accept(this, level, isAddr);
          bracketInstruction("LDA", 1, 0, 0);
          bracketInstruction("LD", 0, -offset, 5);
        }
        if(exp.right instanceof OpExp && offset != -1){
          exp.right.accept(this, level, isAddr);
          bracketInstruction("LDA", 1, 0, 0);
          bracketInstruction("LD", 0, -offset, 5);
        }
      }
      else if(exp.left instanceof CallExp){
        exp.left.accept(this, level, isAddr);
        bracketInstruction("ST", 0, -this.currentOffset, 5, " call exp");
        this.currentOffset++;
        if(exp.right instanceof CallExp){
          exp.right.accept(this, level, isAddr);
        }
        else if(exp.right instanceof VarArrExp){
          VarArrExp rhs = (VarArrExp) exp.right;
          int rhsOffset = -1;
          for(int i = 0;i<=this.curr;i++){
            if(this.varLocation.get(i).get(rhs.name.name) != null){
              rhsOffset= this.varLocation.get(i).get(rhs.name.name).location;
            }
          }
          if(rhsOffset!= -1){
            bracketInstruction("LD", 0, -rhsOffset, 5);
          }
        }
        bracketInstruction("LDA", 1, 0, 0);
        bracketInstruction("LD", 0, -this.currentOffset+1, 5, " call exp");
      }
      if(exp.op == 0){
        expInstruction("ADD", 0, 0, 1);
      }
      else if(exp.op == 1){
        expInstruction("SUB", 0, 0, 1);
      }
      else if(exp.op == 2){
        expInstruction("MUL", 0, 0, 1);
      }
      else if(exp.op == 3){
        expInstruction("DIV", 0 , 0, 1);
      }
      else if(exp.op == 5 || exp.op == 6 || exp.op == 7 || exp.op == 8 || exp.op == 9 || exp.op == 10){
        expInstruction("SUB", 0, 0, 1);
      }
      
  }

  public void visit( ReadExp exp, int level, boolean isAddr ) {
  }

  public void visit( RepeatExp exp, int level , boolean isAddr) {
    if(exp.test instanceof OpExp){
      int testLine = this.assemblyLine;
      OpExp test = (OpExp) exp.test;
      exp.test.accept(this, level, isAddr);
      //bracketInstruction("ST", 0, -currentOffset, 5);
      //currentOffset++;
      int jumpLine = this.assemblyLine;
      assemblyLine++;
      if(exp.exps != null)
        exp.exps.accept(this,level,isAddr);
      int unconditionalJump = this.assemblyLine;
      assemblyLine++;
      
      int uncondOff = (testLine-unconditionalJump-1);
      this.outString+="\n"+unconditionalJump+":    LDA  7,"+uncondOff+"(7)";
      if(test.op == 5){
        this.outString+= "\n"+jumpLine+":    JGE  0,"+(unconditionalJump-jumpLine)+"(7)  ----------";
      }
      else if(test.op == 6){
        this.outString+= "\n"+jumpLine+":    JLE  0,"+(unconditionalJump-jumpLine)+"(7)  ----------";
      }
      else if(test.op == 7){
        this.outString+= "\n"+jumpLine+":    JLT  0,"+(unconditionalJump-jumpLine)+"(7)  ----------";
      }
      else if(test.op == 8){
        this.outString+= "\n"+jumpLine+":    JGT  0,"+(unconditionalJump-jumpLine)+"(7)  ----------";
      }
      else if(test.op == 9){
        this.outString+= "\n"+jumpLine+":    JEQ  0,"+(unconditionalJump-jumpLine)+"(7)  ----------";
      }
      else if(test.op == 10){
        this.outString+= "\n"+jumpLine+":    JNE  0,"+(unconditionalJump-jumpLine)+"(7)  ----------";
      }
    }
     
  }

  public void visit( VarExp exp, int level, boolean isAddr ) {
      
  }

  public void visit( WriteExp exp, int level, boolean isAddr ) {
  }

  public void visit( VarArrExp exp, int level, boolean isAddr){
    int varLoc = -1;
    int varSize = 0;
    boolean isParam = false;
    for(int i = 0; i <= this.curr;i++){
      if(this.varLocation.get(i).get(exp.name.name) != null){
        isParam = this.varLocation.get(i).get(exp.name.name).isParam;
        varLoc = this.varLocation.get(i).get(exp.name.name).location;
        varSize = this.varArrSize.get(i).get(exp.name.name);
      }
    }
    if(exp.num == null){
      if(varSize > 1){
        bracketInstruction("LDA", 0, -varLoc, 5);
      }
      else{
        bracketInstruction("LD", 0, -varLoc, 5);
      }
      
        
    }
    else{
      if(exp.num instanceof IntExp){
        int offset = Integer.parseInt(((IntExp) exp.num).value);
        bracketInstruction("LD", 0, -varLoc-offset, 5);
      }
      if(exp.num instanceof VarArrExp){
        VarArrExp num = (VarArrExp) exp.num;
        int offset = -4;
        for(int i = 0; i <= this.curr;i++){
          if(this.varLocation.get(i).get(num.name.name) != null){
            
            offset = this.varLocation.get(i).get(num.name.name).location;
          }
        }
        if(isParam){
          bracketInstruction("LD", 0, -varLoc, 5);
          bracketInstruction("LD", 1, -offset, 5);
          expInstruction("SUB", 0, 0, 1, "Is Param true");
          bracketInstruction("LD", 0, 0, 0);
          //bracketInstruction("LD", 0, -offset, 0, "Is param is true");
        }
        else{
          bracketInstruction("LD", 0, -varLoc-offset, 5);
        }
      }
    }
    bracketInstruction("ST", 0, -this.currentOffset, 5);
        this.currentOffset++;
}

  public void visit( ArgList list, int level, boolean isAddr){
    this.newStack = this.currentOffset;
    this.currentOffset+=2;
    while( list != null && list.head != null) {
      list.head.accept( this, level, true );

      list = list.tail;
    }
  }

  public void visit( CallExp exp, int level, boolean isAddr){
    String name = exp.name.name;
    this.previousOffset = this.currentOffset;
    exp.list.accept(this, level, isAddr);
    this.currentOffset = 2;
    
    int prevFp = this.fp;
    this.fp = this.fp - previousOffset;
    int funcLine;
    if(name.equals("input")){
      funcLine = 3;
    }
    else if(name.equals("output")){
      funcLine = 6;
    }
    else{
      funcLine = this.funcLocation.get(0).get(name);
    }
    
    bracketInstruction("ST", 5, -previousOffset, 5);
    bracketInstruction("LDA", 5, -previousOffset, 5);
    bracketInstruction("LDA", 0, 1, 7);
    bracketInstruction("LDA", 7, -(assemblyLine - funcLine), 7);
    bracketInstruction("LD", 5, 0, 5);
    this.currentOffset = this.previousOffset;
    this.fp = prevFp;
  }

  public void visit( ReturnExp exp, int level, boolean isAddr){
    if(exp.ex instanceof IntExp){
      bracketInstruction("LDC", 0, Integer.parseInt(((IntExp)exp.ex).value), 0);
    }
    if(exp.ex instanceof VarArrExp){
      VarArrExp rhs = (VarArrExp) exp.ex;
          int rhsOffset = -1;
          for(int i = 0;i<=this.curr;i++){
            if(this.varLocation.get(i).get(rhs.name.name) != null){
              rhsOffset= this.varLocation.get(i).get(rhs.name.name).location;
            }
          }
          if(rhsOffset!= -1){
            bracketInstruction("LD", 0, -rhsOffset, 5, "return "+rhs.name.name);
          }
    }
  }

  public void visit( ExpStmt stmt,int level, boolean isAddr){
    if(stmt.ex != null){
      stmt.ex.accept(this,level,isAddr);
    }
  }

  public void visit( VarDecExp exp, int level, boolean isAddr){
    if(level > 0){
        numLocalVars++;
    }
    else{
      if(exp.num != null){
        int inc = Integer.parseInt(exp.num.value);
        numGlobalVars+= inc;
      }
      else{
        numGlobalVars++;
      }
       
        
      
    }
    
   
    
    String name = exp.name.name;

    if(exp.type.equals("void")){
      this.errorList.add("ERROR: variable "+name+" declared as void at line "+(exp.row+1)+", column "+exp.col + ". Converting to type int");
      exp.type = "int";
    }
    
    if(exp.num != null){

      if(this.table.get(this.curr).get(name) == null){
          this.table.get(this.curr).put(name,new NodeType(name, exp.type,true,exp.row,exp.col));
      }
      if(this.varLocation.get(this.curr).get(name)==null){

        this.varLocation.get(this.curr).put(name, new VarType(name,this.currentOffset,false));
        this.varArrSize.get(this.curr).put(name, Integer.parseInt(exp.num.value));
        this.currentOffset+= Integer.parseInt(exp.num.value);
      }
    }
    else{
      if(this.varLocation.get(this.curr).get(name)==null){
        this.varLocation.get(this.curr).put(name, new VarType(name,this.currentOffset,false));
        this.varArrSize.get(this.curr).put(name, 1);
        this.currentOffset++;
      }
      if(this.table.get(this.curr).get(name) == null){

          this.table.get(this.curr).put(name,new NodeType(name, exp.type,false,exp.row,exp.col));
      }
      else{
          //Variable already declared
        NodeType type = this.table.get(this.curr).get(name);

        this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+" variable previously declared at line "+(type.row+1)+" column "+type.col);
        //System.err.println("ERROR: variable already declared at line "+(exp.row+1)+" column "+exp.col+" : '"+type.type+" "+type.name+"'");
      }
    }
    
    
        
    

  }

  public void visit(CompoundStmt stmt, int level, boolean isAddr){
    
    if(level != 0){
        

        this.table.add(new HashMap<String,NodeType>());
        this.varLocation.add(new HashMap<String, VarType>());
        this.varArrSize.add(new HashMap<String,Integer>());
        //indent(this.curr+1);

        this.curr++;
        
    }
    level++;
    if(stmt.lhs.head != null){
      stmt.lhs.accept(this,level,isAddr);
    }
    if(stmt.rhs.head != null){
      if(this.hasRet == false){
        if(stmt.rhs.head instanceof ReturnExp){

          this.hasRet = true;
        }
      }
      stmt.rhs.accept(this,level,isAddr);
      
    }
    //showSymbolTable();
    
    if(level != 1){
        //indent(this.curr);

    }
    else{
        //indent(this.curr);

        //System.out.println("Leaving the function scope");
    }
    //System.out.println(this.table.get(this.curr));
    this.table.remove(curr);
    this.curr--;
  }

  public void visit(DecList list, int level, boolean isAddr){
    // try{
    //     FileWriter newFile = new FileWriter(this.fileName);
    //     newFile.write();
    //     newFile.close();

    // } 
    // catch(IOException e){
    //     System.err.println("youre wrong");
    //}
    while( list != null && list.head != null) {
        list.head.accept( this, level,false );
      
        list = list.tail;
    }
    bracketInstruction("ST", 5, -this.numGlobalVars, 5); 
    bracketInstruction("LDA", 5, -this.numGlobalVars, 5);
    bracketInstruction("LDA", 0, 1, 7);
    bracketInstruction("LDA", 7, -(this.assemblyLine - this.mainAccessLine+1), 7);
    bracketInstruction("LD", 5, 0, 5);
    expInstruction("HALT", 0, 0, 0);

    try{
           FileWriter newFile = new FileWriter(this.fileName);
           newFile.write(this.outString);
           newFile.close();
  
       } 
       catch(IOException e){
           System.err.println("youre wrong");
       }
   
  }

  public void visit(Param p, int level, boolean isAddr){
    String name = p.exp.name;
    
    
    if(level == 0){
        System.out.print(p.type+", ");
        
    }
    else if(level == -1){
        System.out.print(p.type+')');
        
    }
    else{
        if(p.type.equals("void")){
          this.errorList.add("ERROR: at line "+p.row+1+" column "+p.col+", parameter types cannot be void");
          //System.err.println("Error: at line "+p.row+1+" column "+p.col+", parameter types cannot be void");
        }
        else{
            numLocalVars++;

            if(this.table.get(this.curr).get(name) == null){
              
            //System.out.println(p.type+" "+ p.exp.name+" "+p.isArray+" "+p.row+" "+p.col);
                
                this.table.get(this.curr).put(name,new NodeType(name, p.type, p.isArray, p.row, p.col));
                this.varLocation.get(this.curr).put(name, new VarType(name,this.currentOffset,true));
                this.varArrSize.get(this.curr).put(name,1);
                currentOffset++;
                //System.out.println(this.table.get(this.curr).get(name).name+" "+this.table.get(this.curr).get(name).isArray);
            }
            else{
                this.errorList.add("ERROR: at line "+(p.row+1)+" column "+p.col+" parameter already exists");
            }
        }
        
    }

  }

  public void visit(ParamList list, int level, boolean isAddr){
    //indent(level);
    //System.out.println("ParamList:");
    
    while( list != null ) {
      
      if(level == 0 && list.tail != null){
        list.head.accept(this, 0,isAddr);
      }
      else if(level == 0){
        list.head.accept(this, -1,isAddr);
      }
      else {
        if(list.head != null){
            list.head.accept( this, level,isAddr );
        }
      
      }

      list = list.tail;
    } 
  }
  
  public void visit(FuncDec fd, int level, boolean isAddr){
    String name = fd.name.name;
    if(!this.fpHasSet){
      this.fp -= this.numGlobalVars;
      this.fpHasSet = true;
    }
    
    int currentInstructions = this.assemblyLine;
    level++;
    assemblyLine++;
    comment("* processing function " + name);
    comment("* jump around function body here");

    if(name.equals("main")) {
        this.mainAccessLine = currentInstructions+1;
    }
    bracketInstruction("ST", 0, -1, 5, "store return");
    
    if(this.table.get(this.curr).get(name) == null){
        this.funcLocation.get(this.curr).put(name, currentInstructions);
        this.table.get(this.curr).put(name,new NodeType(name, fd.type, fd.list, fd.row,fd.col));
        this.currType = fd.type;
        // if(fd.type.equalsIgnoreCase("int")){
        //     this.varLocation.get(this.curr).put(name, currentInstructions);
        // }
    }
    else{
        //Handle errors
        NodeType type = this.table.get(this.curr).get(name);
        this.errorList.add("ERROR: function at line "+(fd.row+1)+" column "+fd.col+" already declared at line "+(type.row+1)+" column "+type.col);
        //System.err.println("ERROR: function at "+fd.row+" column "+fd.col+" already declared at line "+type.row+" column "+type.col);

    }
    fd.name.accept(this, level,isAddr);
    
    //indent(this.curr+1);
    //System.out.println("Entering the scope for function "+name);
    this.table.add(new HashMap<String,NodeType>());
    this.varLocation.add(new HashMap<String,VarType>());
    this.varArrSize.add(new HashMap<String, Integer>());
    this.curr++;
    if(fd.list != null){
        if(fd.list.head != null){
          fd.list.accept(this,level,isAddr);
        }
    } 
    if(fd.stmt != null){
      fd.stmt.accept(this,0,isAddr);
      //System.out.println("_______________________________ "+this.hasRet +" "+fd.type);
      if(this.hasRet == false && fd.type.equals("int")){
        this.errorList.add("ERROR: Missing a return statement for function "+fd.name.name);
      }
      else{
        this.hasRet = false;
      }
        
    }
    this.currType = "";
    
    bracketInstruction("LD", 7, -1, 5);
    this.currentOffset = 2;
    int endLine = this.assemblyLine - 1;
    //System.out.println("0000000000000000000000000000000 this is the end line"+endLine);
    this.outString+="\n"+currentInstructions+":    LDA  7,"+(endLine-currentInstructions)+"(7)";

  }

    public void bracketInstruction(String instruction, int r, int d, int s, String comment){
        if(this.outString.equals("")){
            this.outString = this.assemblyLine+":    "+instruction+"  "+r+','+d+'('+s+')'+"  "+comment;
        }
        else{
            this.outString = this.outString+'\n'+this.assemblyLine+":    "+instruction+"  "+r+','+d+'('+s+')'+"  "+comment;
        }  
        this.assemblyLine++;
    }
    public void bracketInstruction(String instruction, int r, int d, int s){
        bracketInstruction(instruction, r, d, s, "");
    }
    public void expInstruction(String instruction, int r, int s, int t, String comment ){
        if(this.outString.equals("")){
            this.outString = this.assemblyLine+":    "+instruction+"  "+r+','+s+','+t+"  "+comment;
        }
        else{
            this.outString = this.outString+'\n'+this.assemblyLine+":    "+instruction+"  "+r+','+s+','+t+"  "+comment;
        }  
        this.assemblyLine++;
    }
    public void expInstruction(String instruction, int r, int s, int t){
        expInstruction(instruction, r, s, t, "");
    }
    public void comment(String comment){
        if(this.outString.equals("")){
            this.outString = "* "+ comment;
        }
        else{
            this.outString = this.outString + "\n* "+ comment;
        }
    }
}
