package absyn;

public class VarDecExp extends Exp {
  public String type;
  public VarExp name;
  public IntExp num;
  public VarDecExp( int row, int col, String type, VarExp name, IntExp num) {
    this.row = row;
    this.col = col;
    this.type = type;
    this.name = name;
    this.num = num;
  }
  public void accept(AbsynVisitor visitor, int level, boolean isAddr){
    visitor.visit(this,level, isAddr);
  }
  // void accept( AbsynVisitor visitor, int level, int offset) {
  //   visitor.visit( this, level, offset);
  // }
}
