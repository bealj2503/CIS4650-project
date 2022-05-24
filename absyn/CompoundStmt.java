package absyn;
public class CompoundStmt extends Exp {
    public ExpList lhs;
    public ExpList rhs;
    public CompoundStmt(int row, int col, ExpList lhs, ExpList rhs){
        this.row = row;
        this.col = col;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    public void accept( AbsynVisitor visitor, int level, boolean isAddr ) {
        visitor.visit( this, level, isAddr );
    }
}
