package absyn;

public class Param extends Exp {
  public String type;
  public VarExp exp;
  public boolean isArray;
  public Param( int row, int col, String type, VarExp exp, boolean isArray) {
    this.row = row;
    this.col = col;
    this.type = type;
    this.exp = exp;
    this.isArray = isArray;
  }

  public void accept( AbsynVisitor visitor, int level, boolean isAddr ) {
    visitor.visit( this, level, isAddr );
  }
}
