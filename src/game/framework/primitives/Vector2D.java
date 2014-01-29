package game.framework.primitives;

public class Vector2D implements Cloneable
{
  public double x;   // The Vector's x component
  public double y;   // The Vector's y component

  /** Construct a null Vector, <0, 0> by default. */
  public Vector2D()
  {
    this(0.0, 0.0);
  }

  /**
   * Construct a new Vector from two integers.
   * 
   * @param x The vector's x component
   * @param y The vector's y component
   */
  public Vector2D(int x, int y)
  {
    this((double) x, (double) y);
  }

  /**
   * Construct a new Vector from two floats.
   * 
   * @param x The vector's x component
   * @param y The vector's y component
   */
  public Vector2D(float x, float y)
  {
    this((double) x, (double) y);
  }

  /**
   * Construct a new Vector from two doubles.
   * 
   * @param x The vector's x component
   * @param y The vector's y component
   */
  public Vector2D(double x, double y)
  {
    this.x = x;
    this.y = y;
  }

  /**
   * Construct a new Vector from an existing one.
   * 
   * @param v The source Vector
   */

  public Vector2D(Vector2D v)
  {
    this(v.x, v.y);
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
  public void set(Vector2D v)
  {
    set(v.x, v.y);
  }

  /**
   * Return the scalar norm (length) of this Vector.
   * 
   * @return the norm of this Vector
   */
  public double norm()
  {
    return Math.hypot(x, y);
  }

  /**
   * Add the given Vector to this Vector; return this Vector.
   * 
   * @param v
   *          the given Vector
   * @return the sum
   */
  public Vector2D add(Vector2D v)
  {
    this.x += v.x;
    this.y += v.y;
    return this;
  }

  /**
   * Subtract the given Vector from this Vector; return this Vector.
   * 
   * @param v
   *          the given Vector
   * @return the difference
   */
  public Vector2D subtract(Vector2D v)
  {
    this.x -= v.x;
    this.y -= v.y;
    return this;
  }

  /**
   * Multiply the given Vector by this Vector; return the scalar product.
   * 
   * @param v
   *          the given Vector
   * @return the scalar (dot) product
   */
  public double dot(Vector2D v)
  {
    return (this.x * v.x) + (this.y * v.y);
  }

  /**
   * Scale this Vector by the given scale factor. Note that this method DOES modify this vector's x and y components.
   * 
   * @param scale The value by which to scale the vector.
   */
  public void scaleThisVector(double scale)
  {
    this.set(scale * this.x, scale * this.y);
  }

  /**
   * This method creates a new scaled 2D vector and returns it to the caller. Note that this method does not modify this vector's x and y components.
   * 
   * @param scale The value by which to scale the vector.
   * @return A new vector scaled by the value specified in the parameter scale.
   */
  public Vector2D createScaledVector(double scale)
  {
    return new Vector2D(scale * this.x, scale * this.y);
  }

  /**
   * This method computes this 2D vectors unit vector and returns it to the caller.
   * Things to note:
   * - This method does not modify this vectors x and y components.
   * - The smallest value for either the x or y component is Double.MIN_NORMAL
   * - A check is performed first to ensure division by zero does not occur when creating the unit vector.
   * - If the magnitude of the vector is zero (thus preventing the computation of the unit vector), then the original vector is returned.
   * 
   * TODO: Consider throwing an exception if the magnitude of the vector is zero
   * 
   * @return the this Vector, scaled to unit length
   */
  public Vector2D createUnitVector()
  {
    double xUnit = this.x;
    double yUnit = this.y;
    double d = norm();

    if (d > Double.MIN_NORMAL)
    {
      xUnit /= d;
      yUnit /= d;
    }

    return new Vector2D(xUnit, yUnit);
  }

  /**
   * This method convert this 2D vector into its unit vector.
   * Things to note:
   * - This method DOES modify this vectors x and y components.
   * - The smallest value for either the x or y component is Double.MIN_NORMAL
   * - A check is performed first to ensure division by zero does not occur when creating the unit vector.
   * - If the magnitude of the vector is zero (thus preventing the computation of the unit vector), then no conversion takes place.
   */
  public void convertToUnitVector()
  {
    this.set(this.createUnitVector());
  }

  /**
   * This method computes the perpendicular vector to this vector.
   * 
   * @return A new vector perpendicular to this vector
   */
  public Vector2D PerpendicularVector()
  {
    return new Vector2D(-this.y, this.x);
  }

  public double computeHeadingAngle()
  {
    Vector2D unitVector = this.createUnitVector();

    return Math.atan2(unitVector.y, unitVector.x);
  }

  public float distance(Vector2D other)
  {
    return distance(other.x, other.y);
  }

  public float distance(double x, double y)
  {
    double distX = this.x - x;
    double distY = this.y - y;
    return (float) Math.sqrt(distX * distX + distY * distY);
  }
  
  /**
   * Return this Vector's String representation.
   */
  public String toString()
  {
    return "(" + this.x + ", " + this.y + ")";
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#clone()
   * 
   * Copy the entity
   */
  @Override
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      return null;
    }
  }
}
