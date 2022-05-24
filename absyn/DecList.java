package absyn;

public class DecList extends Absyn {
  public Exp head;
  public DecList tail;

  public DecList( Exp head, DecList tail ) {
    this.head = head;
    this.tail = tail;
  }

  public void accept( AbsynVisitor visitor, int level, boolean isAddr ) {
    visitor.visit( this, level, isAddr );
  }
}
