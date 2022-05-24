
import java.util.ArrayList;
import java.util.HashMap;

import absyn.*;


public class SemanticAnalyzer implements AbsynVisitor {
  public ArrayList<HashMap<String, NodeType>> table; 
  public int curr;
  public String currType;
  public ArrayList<String> errorList;
  public boolean hasRet;
  public SemanticAnalyzer(){
    this.table = new ArrayList<HashMap<String,NodeType>>();
    this.table.add(new HashMap<String,NodeType>());
    this.curr = 0;
    this.currType = "";
    this.errorList = new ArrayList<String>();
    this.hasRet = false;
  }
  final static int SPACES = 1;
  
  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( "|   " );
  }

  public void visit( ExpList expList, int level , boolean isAddr) {
    
    while( expList != null && expList.head != null ) {
      
      if(expList.head.getClass().getSimpleName().equals("ReturnExp") && this.hasRet == false){
        this.hasRet = true;
      }
      expList.head.accept( this, level, isAddr);
      expList = expList.tail;
    } 
  }

  public void visit( AssignExp exp, int level , boolean isAddr) {
    //indent( level );
    //System.out.println( "AssignExp:" );
    //level++;
    if(exp.rhs.getClass().getSimpleName().equals("CallExp")){
      CallExp right = (CallExp) exp.rhs;
      String name = right.name.name;
      NodeType nodeType = this.table.get(0).get(name);
      if(nodeType!= null && nodeType.list != null && nodeType.type.equals("int")){
        exp.lhs.accept( this, level,isAddr);
        exp.rhs.accept( this, level ,isAddr);
      }
      else{
        this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+" right hand side of assignExp must be of type int");
        //System.err.println("ERROR: at line "+(exp.row+1)+" column "+exp.col+" right hand side of assignExp must be of type int");
      }
      
    }
    else{
      exp.lhs.accept( this, level, true );
    
      exp.rhs.accept( this, level, true );
    }
    
    
  }

  public void visit( IfExp exp, int level, boolean isAddr ) {
    //indent( level );
    //System.out.println( "IfExp:" );
    //level++;
    if(exp.test != null){
      exp.test.accept( this, level, isAddr );
    }
      
    exp.thenpart.accept( this, level, isAddr );

    if (exp.elsepart != null ){
       level--;
       //indent(level);
       //System.out.println("ElseExp:");
       //level++;
       exp.elsepart.accept( this, level, isAddr );
    }
  }

  public void visit( IntExp exp, int level, boolean isAddr ) {
    if(exp.value != null){
      //indent( level );
      //System.out.println( "IntExp: " + exp.value );
    }
     
  }

  public void visit( OpExp exp, int level, boolean isAddr ) {
    if(exp.left.getClass().getSimpleName().equals("CallExp")){
      CallExp left = (CallExp) exp.left;
      String name = left.name.name;
      NodeType node = this.table.get(0).get(name);
      if(node == null || node.list == null){ 
        this.errorList.add("ERROR: at line "+(left.row+1)+" col "+left.col+" function "+name+" never defined");
        //System.err.println("ERROR: at line "+left.row+" col "+left.col+" function "+name+" never defined");
      }else if(!node.type.equals("int")){
        this.errorList.add("ERROR: at line "+(left.row+1)+" col "+left.col+" Expected int but found void instead");
        //System.err.println("Error: at line "+left.row+" col "+left.col+" function must be of type int");
      }
      
      else{
        exp.left.accept( this, level , isAddr);
      }
    }
    else{
      exp.left.accept(this,level, isAddr);
    }
    if(exp.right.getClass().getSimpleName().equals("CallExp")){
      CallExp right = (CallExp) exp.right;
      String name = right.name.name;
      NodeType node = this.table.get(0).get(name);
      if(node == null || node.list == null){ 
        this.errorList.add("ERROR: at line "+(right.row+1)+" col "+right.col+" function "+name+" never defined");
        //System.err.println("ERROR: at line "+right.row+" col "+right.col+" function "+name+" never defined");
        
      }else if(!node.type.equals("int")){
        this.errorList.add("ERROR: at line "+(right.row+1)+" col "+right.col+" Expected int but found void instead");
        //System.err.println("Error: at line "+right.row+" col "+right.col+" function must be of type int");
      }
      else{
        exp.right.accept( this, level,isAddr );
      }
    }
    else{
      exp.right.accept( this, level,isAddr );
    }
    
    
  }

  public void visit( ReadExp exp, int level , boolean isAddr) {
    //indent( level );
    //System.out.println( "ReadExp:" );
    exp.input.accept( this, ++level,isAddr);
  }

  public void visit( RepeatExp exp, int level, boolean isAddr ) {
    // //indent( level );
    // System.out.println( "WhileExp:" );
    //level++;
    if (exp.test!=null){
    if(exp.test.getClass().getSimpleName().equals("CallExp")){
      CallExp call = (CallExp) exp.test;
      String name = call.name.name;
      NodeType type = this.table.get(0).get(name);
      if(!type.type.equals("int")){
        this.errorList.add("ERROR: at line "+ (exp.test.row+1)+" column "+ exp.test.col+" Expected a type of int but found void instead");
        //System.err.println("ERROR: at line "+ exp.test.row+" column "+ exp.test.col+" Expected a type of int but found void instead");
      }
    }
    exp.test.accept( this, level,isAddr );
    }
    exp.exps.accept( this, level,isAddr );
     
  }

  public void visit( VarExp exp, int level , boolean isAddr) {
    //indent( level );
    //System.out.println( "VarExp: " + exp.name );
  }

  public void visit( WriteExp exp, int level , boolean isAddr) {
    //indent( level );
    //System.out.println( "WriteExp:" );
    exp.output.accept( this, ++level , isAddr);
  }

  public void visit( VarArrExp exp, int level, boolean isAddr){
    String name = exp.name.name;
    NodeType node = null;
    for(int i = this.curr;i>=0;i--){
      if(this.table.get(i).get(name) != null){
        node = this.table.get(i).get(name);
        break;
      }
    }
    if(node != null){
        
      
      if((this.table.get(0).get(name) != null) && (this.table.get(0).get(name).list != null)){
        //System.err.println("ERROR: at line "+(exp.row+1)+" column "+exp.col+", function "+name+" cannot be on the LHS of assignExp");
        this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+", function "+name+" cannot be on the LHS of assignExp");
      }
      if(exp.num != null){ 
        if(exp.num.getClass().getSimpleName().equals("CallExp")){
          CallExp call = (CallExp) exp.num;
          String callName = call.name.name;
          NodeType type = this.table.get(0).get(callName);
          if(!type.type.equals("int")){
            this.errorList.add("ERROR: at line "+ (exp.num.row+1)+" column "+ exp.num.col+" Expected a type of int but found void instead");
            //System.err.println("ERROR: at line "+ exp.num.row+" column "+ exp.num.col+" Expected a type of int but found void instead");
          }
        }
        
        exp.name.accept(this,level, isAddr);
        exp.num.accept(this,level,isAddr);
      }
      else{
          exp.name.accept(this,level,isAddr);
        
        
      }
    }
    else{
      this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+" variable "+name+" has not been declared");
      //System.err.println("ERROR: at line "+(exp.row+1)+" column "+exp.col+" variable "+name+" has not been declared");
    }
    
    
    
  }

  public void visit( ArgList list, int level, boolean isAddr){
    //indent(level);
    //System.out.println("ArgList:");
    //level++;
    while( list != null && list.head != null) {
      //System.out.println("------------------------"+list.head.getClass().getSimpleName());
      list.head.accept( this, level ,isAddr);

      list = list.tail;
    }
  }

  public void visit( CallExp exp, int level,boolean isAddr){
    //indent(level);
    //System.out.println("Call:");
    //level++;
    String name = exp.name.name;

    NodeType node = this.table.get(0).get(name);
    
    ArgList argList = exp.list;
    int paramCount = 0;
    int argCount = 0;
    //Check if variable is a function
    if(node == null){
      this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+" function "+name+" has not been declared");
    }
    else if(node.list == null){
      this.errorList.add("ERROR: attempted to call variable "+name+" at line "+(exp.row+1)+" column "+exp.col);
      //System.err.println("ERROR: attempted to call variable "+name+" at line "+(exp.row+1)+" column "+exp.col);
    }
    else if(node.list != null){
      ArrayList<Exp> paramList = new ArrayList<Exp>();
      ArrayList<Exp> argumentList = new ArrayList<Exp>();

      while(node.list != null){
        paramList.add(node.list.head);
        paramCount++;
        node.list = node.list.tail;

      }
      ParamList newParamList = null;
      for(int i = paramList.size()-1;i>=0;i--){
        newParamList = new ParamList(paramList.get(i), newParamList);
        
      }
      node.list = newParamList;
      while(argList != null){
        argumentList.add(argList.head);
        argCount++;
        argList = argList.tail;
      }
      if(paramCount != argCount){
        if(paramCount == 1){
          this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+", "+paramCount+" argument expected but "+argCount+" found");
          //System.err.println("ERROR: at line "+(exp.row+1)+" column "+exp.col+", "+paramCount+" argument expected but "+argCount+" found");
        }
        else{
          this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+", "+paramCount+" arguments expected but "+argCount+" found");
          //System.err.println("ERROR: at line "+(exp.row+1)+" column "+exp.col+", "+paramCount+" arguments expected but "+argCount+" found");
        }
        
      }
      else{
        boolean isArray = false;
        for(int i = 0;i<paramList.size();i++){
          //System.out.println(paramList.get(i).isArray+ " "+ paramList.get(i).exp.name);
          Param curr = (Param)paramList.get(i);
          if(curr.isArray && argumentList.get(i).getClass().getSimpleName().equals("VarArrExp")){
            VarArrExp check = (VarArrExp) argumentList.get(i);
            //System.out.println(check.name.name);
            for(int j = this.curr;j >= 0;j--){
              if(this.table.get(j).get(check.name.name)!= null){
                if(this.table.get(j).get(check.name.name).isArray){
                  isArray = true;
                }
                break;
              }
            }
            if(isArray == false){
              this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+" expected type int[]  but found int instead");
              
            }
          }
        }
      }
    }
    
    exp.name.accept(this, level, isAddr);
    exp.list.accept(this, level, isAddr);
  }

  public void visit( ReturnExp exp, int level, boolean isAddr){
    if(exp.ex == null){
      if(!this.currType.equals("void")){
        this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+" Function expects a return type of int");
        //System.err.println("ERROR: Function expects a return type of int");
      }
    }
    else{
      //level++;
      if(exp.ex.getClass().getSimpleName().equals("CallExp")){
        CallExp call = (CallExp) exp.ex;
        String name = call.name.name;
        NodeType type = this.table.get(0).get(name);
        if(!this.currType.equals("void")){
          if(type != null && type.list != null && type.type.equals("int")){
            exp.ex.accept(this,level,isAddr);
          }
          else{
            this.errorList.add("ERROR: at line "+(exp.ex.row+1)+" column "+exp.ex.col+" cannot return "+type.type);
            //System.err.println("ERROR: at line"+exp.ex.row+" column "+exp.ex.col+" cannot return "+type.type);
          }
        }
      } 
      else if(this.currType.equals("void")){
        this.errorList.add("ERROR: at line "+(exp.ex.row+1)+" column " +exp.ex.col+", unexpected return type");
      }
      
       
    }
  }

  public void visit( ExpStmt stmt,int level, boolean isAddr){
    if(stmt.ex != null){
      //indent(level);
      //System.out.println("ExpressionStmt:");
      //level++;
      stmt.ex.accept(this,level,isAddr);
    }
  }

  public void visit( VarDecExp exp, int level, boolean isAddr){
    //indent(level);
    //System.out.println("VarDecExp:");
    //level++;
    //indent(level);
    
    
    String name = exp.name.name;
    //System.out.println(this.curr+' '+name);
    //System.out.println("Type: "+exp.type);
    //exp.name.accept(this,level);
    if(exp.type.equals("void")){
      this.errorList.add("ERROR: variable "+name+" declared as void at line "+(exp.row+1)+", column "+exp.col + ". Converting to type int");
      //System.err.println("ERROR: variable "+name+" declared as void at line "+(exp.row+1)+", column "+exp.col + ". Converting to type int");
      exp.type = "int";
    }
    
    if(exp.num != null){
      if(this.table.get(this.curr).get(name) == null){
          this.table.get(this.curr).put(name,new NodeType(name, exp.type,true,exp.row,exp.col));
      }
        
        //exp.num.accept(this,level);
    }
    else{
      if(this.table.get(this.curr).get(name) == null){
            //System.out.println(name);
          this.table.get(this.curr).put(name,new NodeType(name, exp.type,false,exp.row,exp.col));
      }
      else{
          //Variable already declared
        NodeType type = this.table.get(this.curr).get(name);
          //System.out.println(type.row +" "+ type.col);
        this.errorList.add("ERROR: at line "+(exp.row+1)+" column "+exp.col+" variable previously declared at line "+(type.row+1)+" column "+type.col);
        //System.err.println("ERROR: variable already declared at line "+(exp.row+1)+" column "+exp.col+" : '"+type.type+" "+type.name+"'");
      }
    }
    
    
        
    

  }

  public void visit(CompoundStmt stmt, int level, boolean isAddr){
    
    if(level != 0){
        
        //System.out.println("Incrementing curr: " + this.curr);
        this.table.add(new HashMap<String,NodeType>());
        indent(this.curr+1);
        System.out.println("Entering a new block");
        this.curr++;
        
    }
    level++;
    if(stmt.lhs.head != null){
      stmt.lhs.accept(this,level,isAddr);
    }
    if(stmt.rhs.head != null){
      if(this.hasRet == false){
        if(stmt.rhs.head.getClass().getSimpleName().equals("ReturnExp")){
          //System.out.println("------------------------------------------------");
          this.hasRet = true;
        }
      }
      stmt.rhs.accept(this,level,isAddr);
      
    }
    showSymbolTable();
    
    if(level != 1){
        indent(this.curr);
        System.out.println("Leaving the block");
    }
    else{
        indent(this.curr);

        System.out.println("Leaving the function scope");
    }
    //System.out.println(this.table.get(this.curr));
    this.table.remove(curr);
    this.curr--;
  }

  public void visit(DecList list, int level, boolean isAddr){
    //indent(level);
    //System.out.println("DecList:");
    //level++;
    System.out.println("Entering global scope:");
    while( list != null && list.head != null) {
      list.head.accept( this, level,isAddr);
      
      list = list.tail;
    } 
    //System.out.println(this.table.size());

    showSymbolTable();
    
    System.out.println("Leaving the global scope");

    printErrors();
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
          
          if(this.table.get(this.curr).get(name) == null){
            //System.out.println("---------------"+name);
            //System.out.println(p.type+" "+ p.exp.name+" "+p.isArray+" "+p.row+" "+p.col);
            this.table.get(this.curr).put(name,new NodeType(name, p.type, p.isArray, p.row, p.col));
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
        list.head.accept(this, 0, isAddr);
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
    
    
    // //indent(level);
    // System.out.println("FuncDec:");
    level++;
    // //indent(level);
    //System.out.println("Type: "+fd.type);
    if(this.table.get(this.curr).get(name) == null){
        
        
        this.table.get(this.curr).put(name,new NodeType(name, fd.type, fd.list, fd.row,fd.col));
        this.currType = fd.type;
    }
    else{
        //Handle errors
    
        NodeType type = this.table.get(this.curr).get(name);
        this.errorList.add("ERROR: function at line "+(fd.row+1)+" column "+fd.col+" already declared at line "+(type.row+1)+" column "+type.col);
        //System.err.println("ERROR: function at "+fd.row+" column "+fd.col+" already declared at line "+type.row+" column "+type.col);

    }
    fd.name.accept(this, level,isAddr);

    
    indent(this.curr+1);
    System.out.println("Entering the scope for function "+name);
    this.table.add(new HashMap<String,NodeType>());
    this.curr++;
    if(fd.list != null){
        if(fd.list.head != null){
          fd.list.accept(this,level,isAddr);
        }
    } 
    if(fd.stmt != null){
      fd.stmt.accept(this,0, isAddr);
      //System.out.println("_______________________________ "+this.hasRet +" "+fd.type);
      if(this.hasRet == false && fd.type.equals("int")){
        this.errorList.add("ERROR: Missing a return statement for function "+fd.name.name);
      }
      else{
        this.hasRet = false;
      }
        
    }
    this.currType = "";
  }
  public void showSymbolTable(){
    for(String name : this.table.get(this.curr).keySet()){
      indent(this.curr+1);
      

      if(this.table.get(this.curr).get(name).list != null){

        printFunc(name);
      }
      else{
        System.out.println(name +": "+this.table.get(this.curr).get(name).type);
      }
    }
  }
  public void printFunc(String name){
    ParamList list = this.table.get(this.curr).get(name).list;
    if(list != null){
        if(list.head != null){
          System.out.print(name+" : (");
          list.accept(this,0,true);
          System.out.println(" -> "+this.table.get(this.curr).get(name).type);
        }
        else{
            System.out.println(name+": (void) -> "+this.table.get(this.curr).get(name).type);
        }
    } 
    
  }
  public void printErrors(){
      for(int i = 0;i<this.errorList.size();i++){
        System.out.println(errorList.get(i));
        System.out.println();
      }
  }

  
}
