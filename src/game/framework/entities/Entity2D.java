package game.framework.entities;

import java.awt.Rectangle;

import game.framework.primitives.Position2D;
import game.framework.utilities.GameEngineConstants;

public class Entity2D extends Entity
{
  // Add 2D entities will have a width and height
  protected int width, height;

  /*
   * Default Constructor
   */
  public Entity2D()
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED);
  }

  /*
   * Constructor specifying the type of the entity
   */
  public Entity2D(GameEngineConstants.EntityTypes type)
  {
    super(type);

    // TODO: Should these be set to zero by default, then defined either in the subclass or by the programmer
    width = 0;
    height = 0;
  }

  /*
   * Set the dimensions of the entity
   */
  // NOTE: Both images and shapes will have dimensions
  public void setDimensions(int w, int h)
  {
    width = w;
    height = h;
  }

  /*
   * Returns the width of the entity
   */
  // NOTE: All Entities will have dimensions
  public int getWidth()
  {
    return width;
  }

  /*
   * Returns the height of the entity
   */
  // NOTE: All Entities will have dimensions
  public int getHeight()
  {
    return height;
  }

  public Position2D getCenter()
  {
    return new Position2D(getCenterX(), getCenterY());
  }

  /**
   * Returns the x component of the center of the image to the caller.
   * 
   * @return The x component of the image center
   */
  public double getCenterX()
  {
    return position.x + (width / 2);
  }

  /**
   * Returns the y component of the center of the image to the caller.
   * 
   * @return The y component of the image center
   */
  public double getCenterY()
  {
    return position.y + (height / 2);
  }

  /*
   *  Get the current bounding rectangle of the entity based on the entity's position
   */
  // NOTE: All entities will have a bounding rectangle
  public Rectangle getBoundingRectangle()
  {
    return new Rectangle((int) position.x, (int) position.y, width, height);
  }

  /*
   * Checks if this this entities bounding rectangle intersects with the other entities bounding rectangle.
   * 
   * True is returned if both rectangles intersect (a collision).
   * False is returned otherwise.
   */
  // NOTE: All entities will need to be tested for collisions
  public boolean collidesWith(Rectangle otherEntityBoundingRectangle)
  {
    return (otherEntityBoundingRectangle.intersects(getBoundingRectangle()));
  }

  /////////////////////////////////////////////////////////////////////////////
  //   ____       _                   __  __      _   _               _     
  //  |  _ \  ___| |__  _   _  __ _  |  \/  | ___| |_| |__   ___   __| |___ 
  //  | | | |/ _ \ '_ \| | | |/ _` | | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //  | |_| |  __/ |_) | |_| | (_| | | |  | |  __/ |_| | | | (_) | (_| \__ \
  //  |____/ \___|_.__/ \__,_|\__, | |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
  //                          |___/                                         
  //
  /////////////////////////////////////////////////////////////////////////////

  public String toString()
  {
    String entitySnapshot = super.toString();

    entitySnapshot += "Width: " + width + "\n";
    entitySnapshot += "Height: " + height + "\n";

    return entitySnapshot;
  }
}
