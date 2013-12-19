package game.deprecated;

/*********************************************************
 * Beginning Java Game Programming, 2nd Edition
 * by Jonathan S. Harbour
 * Sprite class
 **********************************************************/

// Not sure how this will fit into the scheme of things with the newly refactored game engine based on the JFrame game class
import game.framework.primitives.Position2D;

import java.awt.*;
import java.applet.*;

public class Sprite
{
  // Class variables 
  private ImageEntity  entity;
  protected Position2D pos;
  protected Position2D vel;
  protected double     rotRate;
  protected int        currentState;
  protected int        sprType;
  protected boolean    _collided;
  protected int        _lifespan, _lifeage;

  /**
   * Default constructor
   * 
   * @param a
   *          - Reference to the applet object
   * @param g2d
   *          - Reference to the graphics object
   */
  public Sprite(Applet a, Graphics2D g2d)
  {
    entity = new ImageEntity(a);
    entity.setGraphics(g2d);
    entity.setAlive(false);
    pos = new Position2D(0, 0);
    vel = new Position2D(0, 0);
    rotRate = 0.0;
    currentState = 0;
    _collided = false;
    _lifespan = 0;
    _lifeage = 0;
  }

  /**
   * Loads the image specified by the filename
   * 
   * @param filename
   */
  public void load(String filename)
  {
    entity.load(filename);
  }

  /**
   * Move the image of the sprite to the current x-y location using the affine transformations.
   * 
   */
  public void transform()
  {
    entity.setX(pos.x);
    entity.setY(pos.y);
    entity.transform();
  }

  /**
   * Draws the image to the canvas.
   */
  public void draw()
  {
    entity.g2d.drawImage(entity.getImage(), entity.at, entity.applet);
  }

  /**
   * For debugging purposes. Draw a bounding rectangle around the sprite
   * 
   * @param c
   *          - The color of the bounding rectangle
   */
  public void drawBounds(Color c)
  {
    entity.g2d.setColor(c);
    entity.g2d.draw(getBounds());
  }

  /**
   * Updates the position based on velocity
   */
  public void updatePosition()
  {
    pos.x = pos.x + vel.x;
    pos.y = pos.y + vel.y;
  }

  /**
   * Updates the position based on velocity
   */
  public void updatePosition(double delta)
  {
    pos.x = pos.x + (vel.x * delta);
    pos.y = pos.y + (vel.y * delta);
  }

  ///////////////////////////////////////////////////////////////////////////
  //     _         _                        _   _      
  //    / \  _   _| |_ ___  _ __ ___   __ _| |_(_) ___ 
  //   / _ \| | | | __/ _ \| '_ ` _ \ / _` | __| |/ __|
  //  / ___ \ |_| | || (_) | | | | | | (_| | |_| | (__ 
  // /_/   \_\__,_|\__\___/|_| |_| |_|\__,_|\__|_|\___|
  //
  //  ____       _        _   _             
  // |  _ \ ___ | |_ __ _| |_(_) ___  _ __  
  // | |_) / _ \| __/ _` | __| |/ _ \| '_ \ 
  // |  _ < (_) | || (_| | |_| | (_) | | | |
  // |_| \_\___/ \__\__,_|\__|_|\___/|_| |_|
  //
  //  _____          _               __  __      _   _               _     
  // |  ___|_ _  ___| |_ ___  _ __  |  \/  | ___| |_| |__   ___   __| |___ 
  // | |_ / _` |/ __| __/ _ \| '__| | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  // |  _| (_| | (__| || (_) | |    | |  | |  __/ |_| | | | (_) | (_| \__ \
  // |_|  \__,_|\___|\__\___/|_|    |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
  //                              
  ///////////////////////////////////////////////////////////////////////////

  /**
   * 
   * 
   * @return
   */
  public double rotationRate()
  {
    return rotRate;
  }

  /**
   * 
   * 
   * @param rate
   */
  public void setRotationRate(double rate)
  {
    rotRate = rate;
  }

  /**
     * 
     */
  public void updateRotation()
  {
    setFaceAngle(faceAngle() + rotRate);
    if (faceAngle() < 0)
      setFaceAngle(360 - rotRate);
    else if (faceAngle() > 360)
      setFaceAngle(rotRate);
  }

  /*
   * Generic sprite state variable (alive, dead, collided, etc)
   */
  public int state()
  {
    return currentState;
  }

  public void setState(int state)
  {
    currentState = state;
  }

  // returns a bounding rectangle
  public Rectangle getBounds()
  {
    return entity.getBounds();
  }

  // sprite position
  public Position2D position()
  {
    return pos;
  }

  public void setPosition(Position2D pos)
  {
    this.pos = pos;
  }

  // sprite movement velocity
  public Position2D velocity()
  {
    return vel;
  }

  public void setVelocity(Position2D vel)
  {
    this.vel = vel;
  }

  // returns the center of the sprite as a Point2D
  public Position2D center()
  {
    return (new Position2D(entity.getCenterX(), entity.getCenterY()));
  }

  // generic variable for selectively using sprites
  public boolean alive()
  {
    return entity.isAlive();
  }

  public void setAlive(boolean alive)
  {
    entity.setAlive(alive);
  }

  // face angle indicates which direction sprite is facing
  public double faceAngle()
  {
    return entity.getFaceAngle();
  }

  public void setFaceAngle(double angle)
  {
    entity.setFaceAngle(angle);
  }

  public void setFaceAngle(float angle)
  {
    entity.setFaceAngle((double) angle);
  }

  public void setFaceAngle(int angle)
  {
    entity.setFaceAngle((double) angle);
  }

  // move angle indicates direction sprite is moving
  public double moveAngle()
  {
    return entity.getMoveAngle();
  }

  public void setMoveAngle(double angle)
  {
    entity.setMoveAngle(angle);
  }

  public void setMoveAngle(float angle)
  {
    entity.setMoveAngle((double) angle);
  }

  public void setMoveAngle(int angle)
  {
    entity.setMoveAngle((double) angle);
  }

  // returns the source image width/height
  public int imageWidth()
  {
    return entity.width();
  }

  public int imageHeight()
  {
    return entity.height();
  }

  // check for collision with a rectangular shape
  public boolean collidesWith(Rectangle rect)
  {
    return (rect.intersects(getBounds()));
  }

  // check for collision with another sprite
  public boolean collidesWith(Sprite sprite)
  {
    return (getBounds().intersects(sprite.getBounds()));
  }

  // check for collision with a point
  public boolean collidesWith(Position2D point)
  {
    return (getBounds().contains(point.x, point.y));
  }

  public Applet applet()
  {
    return entity.applet;
  }

  public Graphics2D graphics()
  {
    return entity.g2d;
  }

  public Image image()
  {
    return entity.image;
  }

  public void setImage(Image image)
  {
    entity.setImage(image);
  }

  public int spriteType()
  {
    return sprType;
  }

  public void setSpriteType(int type)
  {
    sprType = type;
  }

  public boolean collided()
  {
    return _collided;
  }

  public void setCollided(boolean collide)
  {
    _collided = collide;
  }

  public int lifespan()
  {
    return _lifespan;
  }

  public void setLifespan(int life)
  {
    _lifespan = life;
  }

  public int lifeage()
  {
    return _lifeage;
  }

  public void setLifeage(int age)
  {
    _lifeage = age;
  }

  public void updateLifetime()
  {
    // if lifespan is used, it must be > 0
    if (_lifespan > 0)
    {
      _lifeage++;
      if (_lifeage > _lifespan)
      {
        setAlive(false);
        _lifeage = 0;
      }
    }
  }

}
