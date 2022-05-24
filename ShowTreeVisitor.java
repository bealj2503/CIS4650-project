import java.beans.Expression;

import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 1;

  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( "|   " );
  }

  public void visit( ExpList expList, int level, boolean isAddr ) {
    while( expList != null && expList.head != null ) {
      expList.head.accept( this, level,isAddr );
      expList = expList.tail;
    } 
  }

  public void visit( AssignExp exp, int level, boolean isAddr ) {
    indent( level );
    System.out.println( "AssignExp:" );
    level++;
    exp.lhs.accept( this, level,isAddr );
    exp.rhs.accept( this, level,isAddr );
  }

  public void visit( IfExp exp, int level , boolean isAddr) {
    indent( level );
    System.out.println( "IfExp:" );
    level++;
    if(exp.test != null){
      exp.test.accept( this, level,isAddr );
    }
      
    exp.thenpart.accept( this, level,isAddr );

    if (exp.elsepart != null ){
       level--;
       indent(level);
       System.out.println("ElseExp:");
       level++;
       exp.elsepart.accept( this, level ,isAddr);
    }
  }

  public void visit( IntExp exp, int level , boolean isAddr) {
    if(exp.value != null){
      indent( level );
      System.out.println( "IntExp: " + exp.value );
    }
     
  }

  public void visit( OpExp exp, int level , boolean isAddr) {
    indent( level );
    System.out.print( "OpExp:" ); 
    switch( exp.op ) {
      case OpExp.PLUS:
        System.out.println( " + " );
        break;
      case OpExp.MINUS:
        System.out.println( " - " );
        break;
      case OpExp.TIMES:
        System.out.println( " * " );
        break;
      case OpExp.OVER:
        System.out.println( " / " );
        break;
      case OpExp.EQ:
        System.out.println( " = " );
        break;
      case OpExp.LT:
        System.out.println( " < " );
        break;
      case OpExp.GT:
        System.out.println( " > " );
        break;
      case OpExp.GTE:
        System.out.println(" >= ");
        break;
      case OpExp.LTE:
        System.out.println(" <= ");
        break;
      case OpExp.NEQ:
        System.out.println(" != ");
      case OpExp.EQE:
        System.out.println(" == ");
        break;
      default:
        System.out.println( "Unrecognized operator at line " + exp.row + " and column " + exp.col);
    }
    level++;
    exp.left.accept( this, level,isAddr );
    exp.right.accept( this, level,isAddr );
  }

  public void visit( ReadExp exp, int level, boolean isAddr ) {
    indent( level );
    System.out.println( "ReadExp:" );
    exp.input.accept( this, ++level,isAddr );
  }

  public void visit( RepeatExp exp, int level, boolean isAddr ) {
    indent( level );
    System.out.println( "WhileExp:" );
    level++;
    if (exp.test!=null){
    exp.test.accept( this, level,isAddr );
    }
    exp.exps.accept( this, level,isAddr );
     
  }

  public void visit( VarExp exp, int level, boolean isAddr ) {
    indent( level );
    System.out.println( "VarExp: " + exp.name );
  }

  public void visit( WriteExp exp, int level , boolean isAddr) {
    indent( level );
    System.out.println( "WriteExp:" );
    exp.output.accept( this, ++level,isAddr );
  }

  public void visit( VarArrExp exp, int level, boolean isAddr){
    
    if(exp.num != null){
      indent(level);
      System.out.println("VarArrExp:");
      level++;
      exp.name.accept(this,level,isAddr);
      exp.num.accept(this,level,isAddr);
    }
    else{
      exp.name.accept(this,level,isAddr);
    }
    
  }

  public void visit( ArgList list, int level, boolean isAddr){
    indent(level);
    System.out.println("ArgList:");
    level++;
    while( list != null && list.head != null) {
      list.head.accept( this, level,isAddr );
      list = list.tail;
    }
  }

  public void visit( CallExp exp, int level, boolean isAddr){
    indent(level);
    System.out.println("Call:");
    level++;
    exp.name.accept(this, level,isAddr);
    exp.list.accept(this, level,isAddr);
  }

  public void visit( ReturnExp exp, int level, boolean isAddr){
    indent(level);
    if(exp.ex == null)
      System.out.println("Return");
    else{
      System.out.println("Return: ");
      level++;
      exp.ex.accept(this,level,isAddr);
    }
  }

  public void visit( ExpStmt stmt,int level, boolean isAddr){
    if(stmt.ex != null){
      indent(level);
      System.out.println("ExpressionStmt:");
      level++;
      stmt.ex.accept(this,level,isAddr);
    }
  }

  public void visit( VarDecExp exp, int level, boolean isAddr){
    indent(level);
    System.out.println("VarDecExp:");
    level++;
    indent(level);
    System.out.println("Type: "+exp.type);
    exp.name.accept(this,level,isAddr);
    if(exp.num != null)
      exp.num.accept(this,level,isAddr);


  }

  public void visit(CompoundStmt stmt, int level, boolean isAddr){
    indent(level);
    System.out.println("CompoundStmt:");
    level++;
    if(stmt.lhs.head != null){
      indent(level);
      System.out.println("LocalDecs:");
      level++;
      stmt.lhs.accept(this,level,isAddr);
    }
      
    if(stmt.rhs.head != null){
      indent(level);
      System.out.println("StmtList:");
      level++;
      stmt.rhs.accept(this,level,isAddr);
    }
  }

  public void visit(DecList list, int level, boolean isAddr){
    indent(level);
    System.out.println("DecList:");
    level++;
    while( list != null && list.head != null) {
      list.head.accept( this, level ,isAddr);
      list = list.tail;
    } 
  }

  public void visit(Param p, int level, boolean isAddr){
    indent(level);
    System.out.println("Param:");
    level++;
    indent(level);
    System.out.println("Type: "+p.type);
    p.exp.accept(this,level,isAddr);
    indent(level);
    System.out.println("Is Array:"+p.isArray);
  }

  public void visit(ParamList list, int level, boolean isAddr){
    indent(level);
    System.out.println("ParamList:");
    level++;
    while( list != null ) {
      list.head.accept( this, level, isAddr );
      list = list.tail;
    } 
  }
  
  public void visit(FuncDec fd, int level, boolean isAddr){
    indent(level);
    System.out.println("FuncDec:");
    level++;
    indent(level);
    System.out.println("Type: "+fd.type);
    fd.name.accept(this, level,isAddr);
    if(fd.list != null){
      if(fd.list.head != null){
        fd.list.accept(this,level, isAddr);
      }
      else{
        indent(level);
        System.out.println("Param: void");
      }
    }
    
    if(fd.stmt != null)
      fd.stmt.accept(this,level,isAddr);
  }
}
