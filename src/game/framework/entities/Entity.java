package game.framework.entities;

import game.framework.primitives.Position2D;
import game.framework.primitives.Vector2D;
import game.framework.utilities.GameEngineConstants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 
 * @author ggiger
 * 
 *         In this version of the entity class it will only deal with simple 2D shapes.
 * 
 *         Proposed Additions in future versions:
 * 
 *         Ability to represent both shapes and images Ability to handle animated images via animation 77frames
 * 
 */

// TODO: Should the Entity class be re-factored so the notion of position, velocity, visible, alive and update logic are separate from the render logic? Reason, images and shapes need to be rendered differently

public class Entity implements Cloneable
{
  /*
   * Class member variables (i.e., constants)
   */

  // TODO: Not sure if this is needed.
  private static final double               DEGREES_IN_A_CIRCLE = 360;

  /*
   *  Class instance variables
   */

  // Variables that are common for both images and shapes
  private static int                        nextId              = 0;  // Used to assign a unique id to each entity that is created 
  protected int                             entityId            = 0;  // Every entity should have an id

  // TODO: Should the position be its own class Point rather than using Vector2D?
  protected int                             width, height;            // These will likely stay here since both images and shapes have a width and height
  //protected Vector2D                        position, velocity;       // This will likely stay here since an entity needs to exist somewhere on the screen.
  protected Position2D                      position;
  protected Vector2D                        velocity;                 // This will likely stay here since an entity needs to exist somewhere on the screen.

  protected boolean                         alive;                    // Simple, the entity is either alive or dead
  protected boolean                         visible;                  // My reasoning, an entity can be alive, but not visible (allows for invisible entities during game play or blinking entities)

  protected GameEngineConstants.EntityState entityState;
  protected GameEngineConstants.EntityTypes entityType;

  protected double                          moveAngle, faceAngle, rotationRate; // These will be needed for the image (and possibly applied to the shapes). They are here as place holders for now.

  protected int                             lifeSpan, lifeAge;

  // Variables that only apply to shapes
  protected Color                           color;                             // TODO: This will likely end up in a sub class when the entity class is broken up into two sub classes, imageEntity and shapeEntity

  // DEBUG VARIABLES
  private boolean                           showDirectionVector = true;

  /////////////////////////////////////////////////////////////////////////////  
  //    ____                _                   _                 
  //   / ___|___  _ __  ___| |_ _ __ _   _  ___| |_ ___  _ __ ___ 
  //  | |   / _ \| '_ \/ __| __| '__| | | |/ __| __/ _ \| '__/ __|
  //  | |__| (_) | | | \__ \ |_| |  | |_| | (__| || (_) | |  \__ \
  //   \____\___/|_| |_|___/\__|_|   \__,_|\___|\__\___/|_|  |___/
  //                                                              
  /////////////////////////////////////////////////////////////////////////////

  /*
   * Default constructor
   */
  public Entity()
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED);
  }

  /*
   * Constructor specifying the type of the entity
   */
  public Entity(GameEngineConstants.EntityTypes type)
  {
    // TODO: Since color only applies to shapes, this may need to be moved into a sub class when images are introduced into the entities
    color = Color.RED;

    // TODO: Since both shapes and images have a width and height, these may need to be set to zero here and defined in sub classes (for image entity, when images is loaded the width and height can implicitly be assigned)
    // TODO: Should these be set to zero by default, then defined either in the subclass or by the programmer
    width = GameEngineConstants.DEFAULT_ENTITY_WIDTH;
    height = GameEngineConstants.DEFAULT_ENTITY_HEIGHT;

    // NOTE: Since every image and shape will have a position and velocity, these will likely stay in this class.
    position = new Position2D();  // This will need to be changed
    velocity = new Vector2D();

    // NOTE: These will likely stay here as well since they can apply to both image and shape entities.
    entityId = nextId++;
    entityType = type;
    entityState = GameEngineConstants.EntityState.NORMAL;

    lifeSpan = 0;
    lifeAge = 0;

    // Make the entity alive and visible
    reset();
  }

  /////////////////////////////////////////////////////////////////////////////  
  //   ____       _   _                
  //  / ___|  ___| |_| |_ ___ _ __ ___ 
  //  \___ \ / _ \ __| __/ _ \ '__/ __|
  //   ___) |  __/ |_| ||  __/ |  \__ \
  //  |____/ \___|\__|\__\___|_|  |___/
  //                                   
  /////////////////////////////////////////////////////////////////////////////

  /*
   * Set the color of the entity
   */
  // NOTE: Specific to shape entities
  public void setColor(Color c)
  {
    color = c;
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
   * Set the alive status of the entity
   */
  // NOTE: All Entities will have alive status 
  public void setAlive(boolean alive)
  {
    this.alive = alive;
  }

  /*
   * Set the visible status of the entity
   */
  // NOTE: All Entities will have visible status 
  public void setVisible(boolean visible)
  {
    this.visible = visible;
  }

  /*
   * Sets the entity state. Entity states includes NORMAL, COLLIDED and EXPLODING
   */
  // NOTE: All Entities will have a state 
  public void setEntityState(GameEngineConstants.EntityState state)
  {
    entityState = state;
  }

  /*
   * Sets the entity type. Entity types include PLAYER, PLAYER_SHOT, ENEMY, ENEMY_SHOT, POWER_UP and UNDEFINED
   */
  // NOTE: All Entities will have a type 
  public void setEntityType(GameEngineConstants.EntityTypes type)
  {
    entityType = type;
  }

  /*
   * Position setters
   */

  /*
   * Sets the entity's position using doubles for both the X and Y components
   */
  // NOTE: All Entities will have a position 
  public void setPosition(double x, double y)
  {
    position.set(x, y);
  }

  /*
   * Sets the X component of the entity's position
   */
  // NOTE: All Entities will have a position 
  public void setPositionX(double x)
  {
    position.x = x;
  }

  /*
   * Sets the Y component of the entity's position
   */
  // NOTE: All Entities will have a position 
  public void setPositionY(double y)
  {
    position.y = y;
  }

  /*
   * Velocity setters
   */

  /*
   * Sets the entity's velocity using doubles for both the X and Y components
   */
  // NOTE: All Entities will have a velocity 
  public void setVelocity(double x, double y)
  {
    velocity.set(x, y);
  }

  /*
   * Sets the entity's velocity using a vector object
   */
  // NOTE: All Entities will have a velocity
  public void setVelocity(Vector2D v)
  {
    velocity.set(v);
  }

  /*
   * Sets the X component of the entity's velocity
   */
  // NOTE: All Entities will have a velocity
  public void setVelocityX(double x)
  {
    velocity.x = x;
  }

  /*
   * Sets the Y component of the entity's velocity
   */
  // NOTE: All Entities will have a velocity
  public void setVelocityY(double y)
  {
    velocity.y = y;
  }

  /*
   * Angle Setters
   */
  public void setFaceAngle(double angle)
  {
    this.faceAngle = angle;
  }

  public void setMoveAngle(double angle)
  {
    this.moveAngle = angle;
  }

  public void setRotationRate(double rate)
  {
    rotationRate = rate;
  }

  public void setLifespan(int life)
  {
    lifeSpan = life;
  }

  // TODO: Should the life age be the number of seconds the object should live? Then this number can be multiplied by the frame rate since the life age is incremented each game loop time. 
  public void setLifeage(int age)
  {
    lifeAge = age;
  }

  /////////////////////////////////////////////////////////////////////////////
  //    ____      _   _                
  //   / ___| ___| |_| |_ ___ _ __ ___ 
  //  | |  _ / _ \ __| __/ _ \ '__/ __|
  //  | |_| |  __/ |_| ||  __/ |  \__ \
  //   \____|\___|\__|\__\___|_|  |___/
  //                                 
  /////////////////////////////////////////////////////////////////////////////

  /*
   * Get the color of the entity
   */
  // NOTE: Specific to shape entities
  public Color getColor()
  {
    return color;
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

  /*
   * Returns the ID of the entity. When a new entity is created, it is assigned a unique ID.
   */
  // NOTE: All Entities will have an ID
  public int getId()
  {
    return entityId;
  }

  /*
   * Returns the entity state. Entity states includes NORMAL, COLLIDED and EXPLODING
   */
  // NOTE: All Entities will have a state
  public GameEngineConstants.EntityState getEntityState()
  {
    return entityState;
  }

  /*
   * Returns the entity type. Entity types include PLAYER, PLAYER_SHOT, ENEMY, ENEMY_SHOT, POWER_UP and UNDEFINED
   */
  // NOTE: All Entities will have a type
  public GameEngineConstants.EntityTypes getEntityType()
  {
    return entityType;
  }

  /*
   * Position getters
   */

  /*
   * Returns the position of the entity as a vector object
   */
  // NOTE: All Entities will have a position
  public Position2D getPosition() // THIS COULD BE A PROBLEM
  {
    return position;
  }

  /*
   * Returns the X component of the entity's position
   */
  // NOTE: All Entities will have a position
  public double getPositionX()
  {
    return position.x;
  }

  /*
   * Returns the Y component of the entity's  position
   */
  // NOTE: All Entities will have a position
  public double getPositionY()
  {
    return position.y;
  }

  /*
   * Velocity getters
   */

  /*
   * Returns the velocity of the entity as a vector object 
   */
  // NOTE: All Entities will have a velocity
  public Vector2D getVelocity()
  {
    return velocity;
  }

  /*
   * Returns the X component of the entity's velocity
   */
  //  NOTE: All Entities will have a velocity
  public double getVelocityX()
  {
    return velocity.x;
  }

  /*
   * Returns the Y component of the entities velocity
   */
  // NOTE: All Entities will have a velocity
  public double getVelocityY()
  {
    return velocity.y;
  }

  /*
   * Angle getters
   */
  public double getFaceAngle()
  {
    return faceAngle;
  }

  public double getMoveAngle()
  {
    return moveAngle;
  }

  public double getRotationRate()
  {
    return rotationRate;
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

  public int getLifespan()
  {
    return lifeSpan;
  }

  public int getLifeage()
  {
    return lifeAge;
  }

  /////////////////////////////////////////////////////////////////////////////
  //   _____ _                 
  //  |  ___| | __ _  __ _ ___ 
  //  | |_  | |/ _` |/ _` / __|
  //  |  _| | | (_| | (_| \__ \
  //  |_|   |_|\__,_|\__, |___/
  //                 |___/     
  //
  /////////////////////////////////////////////////////////////////////////////

  /*
   * Indicates if the entity is alive
   */
  // NOTE: All Entities will have an alive status
  public boolean isAlive()
  {
    return this.alive;
  }

  /*
   * Indicates if the entity is visible
   */
  // NOTE: All Entities will have a visible status
  public boolean isVisible()
  {
    return this.visible;
  }

  /////////////////////////////////////////////////////////////////////////////
  //    ___  _   _                 __  __      _   _               _     
  //   / _ \| |_| |__   ___ _ __  |  \/  | ___| |_| |__   ___   __| |___ 
  //  | | | | __| '_ \ / _ \ '__| | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //  | |_| | |_| | | |  __/ |    | |  | |  __/ |_| | | | (_) | (_| \__ \
  //   \___/ \__|_| |_|\___|_|    |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
  //                                                                     
  /////////////////////////////////////////////////////////////////////////////

  /*
   *  Makes the entity not alive and not visible
   */
  // NOTE: Needed to manipulate the alive and visible status
  public void kill()
  {
    setAlive(false);
    setVisible(false);
  }

  /*
   *  Makes the entity alive and visible 
   */
  // NOTE: Needed to manipulate the alive and visible status
  public void reset()
  {
    setAlive(true);
    setVisible(true);
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

  /////////////////////////////////////////////////////////////////////////////
  //   _   _           _       _                         _ 
  //  | | | |_ __   __| | __ _| |_ ___    __ _ _ __   __| |
  //  | | | | '_ \ / _` |/ _` | __/ _ \  / _` | '_ \ / _` |
  //  | |_| | |_) | (_| | (_| | ||  __/ | (_| | | | | (_| |
  //   \___/| .__/ \__,_|\__,_|\__\___|  \__,_|_| |_|\__,_|
  //        |_|                                            
  //   ____                _             _                _      
  //  |  _ \ ___ _ __   __| | ___ _ __  | |    ___   __ _(_) ___ 
  //  | |_) / _ \ '_ \ / _` |/ _ \ '__| | |   / _ \ / _` | |/ __|
  //  |  _ <  __/ | | | (_| |  __/ |    | |__| (_) | (_| | | (__ 
  //  |_| \_\___|_| |_|\__,_|\___|_|    |_____\___/ \__, |_|\___|
  //                                                |___/        
  /////////////////////////////////////////////////////////////////////////////

  /*
   * Updates the position of the entity given the current position and the entity's velocity.
   * NOTE: The delta is the time difference between the last time the game loop executed at the current iteration. Since frame 
   *       rate may vary, the delta value ensures that animation of the entity is consistent regardless of the varying frame
   *       rate. Also note that for frame rates below 20 fps, the entities will seem to jerk or become jittery as they move 
   *       across the screen.
   *       
   * NOTE: The entity's position will only be updated if it is alive.
   */
  public void updatePosition(double delta)
  {
    if (isAlive())
    {
      position.x += velocity.x * delta;
      position.y += velocity.y * delta;
    }
  }

  /*
   * NOTE: Rotation rate can have a negative value to indicate CCW vs positive value which represents CW
   */
  public void updateRotation(double delta)
  {
    if (isAlive())
    {
      faceAngle += rotationRate * delta;

      if (faceAngle < 0)
      {
        faceAngle = DEGREES_IN_A_CIRCLE + rotationRate * delta;
      }

      if (faceAngle > DEGREES_IN_A_CIRCLE)
      {
        faceAngle = rotationRate * delta;
      }
    }
  }

  /*
   * 
   */
  public void updateLifetime()
  {
    // if life span is used, it must be > 0
    if (lifeSpan > 0)
    {
      lifeAge++;
      if (lifeAge > lifeSpan)
      {
        setAlive(false);
        lifeAge = 0;
      }
    }
  }

  /*
   *  Draw the entity
   *  NOTE: The entity will only be drawn if it is alive and visible
   */

  // NOTE: This will be entity type dependent given that an entity can be a shape or an image
  public void draw(Graphics2D g)  // Changed from graphics 
  {
    if (isAlive() && isVisible())
    {
      g.setColor(color);

      //      if (showDirectionVector)
      //      {
      //        g.drawLine((int)getCenterX(), (int)getCenterY(), (int)(getCenterX() + velocity.x), (int)(getCenterY() + velocity.y));
      //      }

      g.fillRect((int) position.x, (int) position.y, width, height);
    }
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
    String entitySnapshot = "";

    entitySnapshot += "Id: " + entityId + "\n";
    entitySnapshot += "EntityType: " + entityType + "\n";
    entitySnapshot += "Alive: " + alive + "\n";
    entitySnapshot += "Visible: " + visible + "\n";
    entitySnapshot += "Width: " + width + "\n";
    entitySnapshot += "Height: " + height + "\n";
    entitySnapshot += "Color: " + color + "\n";
    entitySnapshot += "Position: " + position + "\n";
    entitySnapshot += "Velocity: " + velocity + "\n";
    entitySnapshot += "Face Angle: " + faceAngle + "\n";
    entitySnapshot += "Move Angle: " + moveAngle + "\n";
    entitySnapshot += "Rotation Rate: " + rotationRate + "\n";

    return entitySnapshot;
  }
}
