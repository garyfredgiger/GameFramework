package game.framework.entities;

import game.framework.primitives.Position2D;
import game.framework.primitives.Vector2D;
import game.framework.utilities.GameEngineConstants;

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
// The answer is YES

public class Entity2D implements Cloneable
{
  /*
   * Class member variables (i.e., constants)
   */

  // TODO: Not sure if this is needed.
  private static final double               DEGREES_IN_A_CIRCLE = 360;

  /*
   *  Class instance variables
   */

  // Variables that are common for all 2D entities 
  private static int                        nextId              = 0;  // Used to assign a unique id to each entity that is created 
  protected int                             entityId            = 0;  // Every entity should have a unique id

  protected Position2D                      position;                 // Position of the entity
  protected Vector2D                        velocity;                 // The dx and dy of the velocity. These are added to the x-y component of the positon to move the entity 

  protected int                             width, height;

  protected boolean                         alive;                    // Simple, the entity is either alive or dead
  protected boolean                         visible;                  // An entity can be alive, but not visible (allows for invisible/blinking entities during game play)

  protected GameEngineConstants.EntityState entityState;
  protected GameEngineConstants.EntityTypes entityType;

  protected double                          moveAngle, faceAngle, rotationRate;

  protected int                             lifeSpan, lifeAge;                 // Used if entities are to appear for a timed period (e.g., powerups)

  // DEBUG VARIABLES
  //  private boolean                           showDirectionVector = true;

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
  public Entity2D()
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED);
  }

  /*
   * Constructor specifying the type of the entity
   */
  public Entity2D(GameEngineConstants.EntityTypes type)
  {
    // NOTE: Since every image and shape will have a position and velocity, these will likely stay in this class.
    position = new Position2D();  // This will need to be changed
    velocity = new Vector2D();

    width = 0;
    height = 0;
    
    // NOTE: These will likely stay here as well since they can apply to both image and shape entities.
    entityId = nextId++;
    entityType = type;
    entityState = GameEngineConstants.EntityState.NORMAL;

    lifeSpan = 0;
    lifeAge = 0;

    // Make the entity alive and visible    
    /*
     *  NOTE: Rather than calling the method reset() to set both the flags alive and visible, they will be set individually.
     *        The reason? Well, if a subclass extends this class, overrides the reset() method and references an
     *        object in the reset() method, a null pointer exception will occur even if the object is instantiated inside of 
     *        the subclass's constructor. Why? Well, java requires that the parent class's constructor be called first
     *        inside of the sub classes constructor. Since in this case this constructor calls the reset() method and since
     *        the subclass overrides the reset() method, the inner most instance of the reset() method is called first (in 
     *        this case the sub class's overridden reset() method). When the subclass's reset() method is called first from 
     *        within the parent class's constructor, it references the object in the subclass before it can be instantiated
     *        in the suclass's constructor. This results in a null pointer exception.
     */
    //this.reset();
    this.visible = true;
    this.alive = true;
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
   * Set the alive status of the entity
   */
  public void setAlive(boolean alive)
  {
    this.alive = alive;
  }

  /*
   * Set the visible status of the entity
   */
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
  public void setEntityType(GameEngineConstants.EntityTypes type)
  {
    entityType = type;
  }

  /*
   * Position setters
   */
  public void setPosition(Position2D p)
  {
    position.set(p);
  }

  /*
   * Sets the entity's position using doubles for both the X and Y components
   */
  public void setPosition(double x, double y)
  {
    position.set(x, y);
  }

  /*
   * Sets the X component of the entity's position
   */
  public void setPositionX(double x)
  {
    position.x = x;
  }

  /*
   * Sets the Y component of the entity's position
   */
  public void setPositionY(double y)
  {
    position.y = y;
  }

  /*
   * Set the dimensions of the entity
   */
  public void setDimensions(int w, int h)
  {
    width = w;
    height = h;
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

  public int getLifespan()
  {
    return lifeSpan;
  }

  public int getLifeage()
  {
    return lifeAge;
  }

  /*
   * Returns the width of the entity
   */
  public int getWidth()
  {
    return width;
  }

  /*
   * Returns the height of the entity
   */
  public int getHeight()
  {
    return height;
  }

  public Position2D getCenter()
  {
    return new Position2D(getCenterX(), getCenterY());
  }

  /*
   * Returns the x component of the center of the image to the caller.
   */
  public double getCenterX()
  {
    return position.x + (width / 2);
  }

  /*
   * Returns the y component of the center of the image to the caller. 
   */
  public double getCenterY()
  {
    return position.y + (height / 2);
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
  //    ____      _ _ _     _             
  //   / ___|___ | | (_)___(_) ___  _ __  
  //  | |   / _ \| | | / __| |/ _ \| '_ \ 
  //  | |__| (_) | | | \__ \ | (_) | | | |
  //   \____\___/|_|_|_|___/_|\___/|_| |_|
  //                                      
  //   __  __      _   _               _     
  //  |  \/  | ___| |_| |__   ___   __| |___ 
  //  | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //  | |  | |  __/ |_| | | | (_) | (_| \__ \
  //  |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
  //                                         
  /////////////////////////////////////////////////////////////////////////////
  
  /*
   *  Get the current bounding rectangle of the entity based on the entity's position
   */
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
  public boolean collidesWith(Rectangle otherEntityBoundingRectangle)
  {
    return (otherEntityBoundingRectangle.intersects(getBoundingRectangle()));
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
    //    if (isAlive() && isVisible())
    //    {
    //      g.setColor(color);
    //
    //      //      if (showDirectionVector)
    //      //      {
    //      //        g.drawLine((int)getCenterX(), (int)getCenterY(), (int)(getCenterX() + velocity.x), (int)(getCenterY() + velocity.y));
    //      //      }
    //
    //      g.fillRect((int) position.x, (int) position.y, width, height);
    //    }
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
    entitySnapshot += "Position: " + position + "\n";
    entitySnapshot += "Velocity: " + velocity + "\n";
    entitySnapshot += "Width: " + width + "\n";
    entitySnapshot += "Height: " + height + "\n";
    entitySnapshot += "Face Angle: " + faceAngle + "\n";
    entitySnapshot += "Move Angle: " + moveAngle + "\n";
    entitySnapshot += "Rotation Rate: " + rotationRate + "\n";

    return entitySnapshot;
  }
}
