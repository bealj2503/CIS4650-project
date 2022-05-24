package absyn;

public class ArgList extends Absyn {
  public Exp head;
  public ArgList tail;

  public ArgList( Exp head, ArgList tail ) {
    this.head = head;
    this.tail = tail;
  }

  public void accept( AbsynVisitor visitor, int level , boolean isAddr) {
    visitor.visit( this, level, isAddr);
  }
}
