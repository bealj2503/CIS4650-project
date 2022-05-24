package absyn;

public interface AbsynVisitor {

  public void visit( ExpList exp, int level, boolean isAddr);

  public void visit( AssignExp exp, int level, boolean isAddr );

  public void visit( IfExp exp, int level, boolean isAddr);

  public void visit( IntExp exp, int level, boolean isAddr );

  public void visit( OpExp exp, int level, boolean isAddr );

  public void visit( ReadExp exp, int level, boolean isAddr );

  public void visit( RepeatExp exp, int level, boolean isAddr );

  public void visit( VarExp exp, int level, boolean isAddr );

  public void visit( WriteExp exp, int level, boolean isAddr );
  
  public void visit( VarArrExp exp, int level, boolean isAddr);

  public void visit( ArgList list, int level, boolean isAddr);

  public void visit( CallExp exp, int level, boolean isAddr);

  public void visit (ReturnExp exp, int level, boolean isAddr);

  public void visit(ExpStmt stmt, int level, boolean isAddr);

  public void visit(VarDecExp exp, int level, boolean isAddr);

  public void visit(CompoundStmt stmt, int level, boolean isAddr);

  public void visit(DecList list, int level, boolean isAddr);

  public void visit(Param p, int level, boolean isAddr);

  public void visit(ParamList pl, int level, boolean isAddr);

  public void visit(FuncDec fd, int level, boolean isAddr);
}
