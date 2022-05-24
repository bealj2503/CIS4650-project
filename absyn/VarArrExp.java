package absyn;

public class VarArrExp extends Exp {
  public VarExp name;
  public Exp num;
  public int nestLevel;
  public int offset;
  public VarArrExp( int row, int col, VarExp name, Exp num) {
    this.row = row;
    this.col = col;
    this.name = name;
    this.num = num;
  }
  public VarArrExp( int row, int col, VarExp name, Exp num, int nestLevel, int offset) {
    this.row = row;
    this.col = col;
    this.name = name;
    this.num = num;
    this.nestLevel = nestLevel;
    this.offset = offset;
  }

  public void accept( AbsynVisitor visitor, int level, boolean isAddr ) {
    visitor.visit( this, level,isAddr );
  }
}
