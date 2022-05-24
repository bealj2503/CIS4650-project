package absyn;

public class CallExp extends Exp {
  public VarExp name;
  public ArgList list;
  public CallExp( int row, int col, VarExp name, ArgList list) {
    this.row = row;
    this.col = col;
    this.name = name;
    this.list = list;
  }

  public void accept( AbsynVisitor visitor, int level, boolean isAddr ) {
    visitor.visit( this, level, isAddr);
  }
}
