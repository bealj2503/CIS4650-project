package absyn;

public class ExpStmt extends Exp {
  public Exp ex;

  public ExpStmt( int row, int col, Exp ex ) {
    this.row = row;
    this.col = col;
    this.ex = ex;
  }

  public void accept( AbsynVisitor visitor, int level, boolean isAddr ) {
    visitor.visit( this, level, isAddr);
  }
}
