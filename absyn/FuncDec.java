package absyn;

public class FuncDec extends Exp {
    public String type;
    public VarExp name;
    public ParamList list;
    public Exp stmt;
    public int funaddr;
    public FuncDec( int row, int col, String type, VarExp name, ParamList list, Exp stmt, int funaddr) {
      this.row = row;
      this.col = col;
      this.type = type;
      this.name = name;
      this.list = list;
      this.stmt = stmt;
      this.funaddr = funaddr;
    }
    public FuncDec( int row, int col, String type, VarExp name, ParamList list, Exp stmt) {
      this.row = row;
      this.col = col;
      this.type = type;
      this.name = name;
      this.list = list;
      this.stmt = stmt;
    }
    public void accept( AbsynVisitor visitor, int level , boolean isAddr) {
      visitor.visit( this, level, isAddr);
    }
  }
  