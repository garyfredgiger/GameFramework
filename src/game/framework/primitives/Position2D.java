package game.framework.primitives;

/*********************************************************
 * Beginning Java Game Programming, 2nd Edition by Jonathan S. Harbour Point2D Class
 **********************************************************/
public class Position2D
{
  public double x;   // The position's x component
  public double y;   // The position's y component

  /**
   * Default constructor
   */
  public Position2D()
  {
    this(0, 0);
  }

  /**
   * int constructor
   */
  public Position2D(int x, int y)
  {
    this((double) x, (double) y);
  }

  /**
   * float constructor
   */
  public Position2D(float x, float y)
  {
    this((double) x, (double) y);
  }

  /**
   * double constructor
   */
  public Position2D(double x, double y)
  {
    this.x = x;
    this.y = y;
  }

  /**
   * Position constructor
   */
  public Position2D(Position2D p)
  {
    this(p.x, p.y);
  }

  /**
   * Set the components of this Vector.
   * 
   * @param x The vector's x component
   * @param y the vector's y component
   */
  public void set(int x, int y)
  {
    set((double) x, (double) y);
  }

  /**
   * Set the components of this Vector.
   * 
   * @param x The vector's x component
   * @param y the vector's y component
   */
  public void set(float x, float y)
  {
    set((double) x, (double) y);
  }

  /**
   * Set the components of this Vector.
   * 
   * @param x The vector's x component
   * @param y the vector's y component
   */
  public void set(double x, double y)
  {
    this.x = x;
    this.y = y;
  }

  /**
   * Set the components of this Vector to those of v.
   * 
   * @param v The source Vector
   */
  public void set(Position2D p)
  {
    set(p.x, p.y);
  }

  /**
   * Reset the position
   */
  public void reset()
  {
    set(0, 0);
  }
  
  /**
   * Return this Vector's String representation.
   */
  public String toString()
  {
    return "(" + this.x + ", " + this.y + ")";
  }
}
