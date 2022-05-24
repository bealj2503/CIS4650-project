package absyn;

public class ParamList extends Absyn {
  public Exp head;
  public ParamList tail;

  public ParamList( Exp head, ParamList tail ) {
    this.head = head;
    this.tail = tail;
  }

  public void accept( AbsynVisitor visitor, int level, boolean isAddr ) {
    visitor.visit( this, level, isAddr);
  }
}
