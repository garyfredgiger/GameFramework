package game.deprecated;

/*********************************************************
 * Base game entity class
 **********************************************************/

// Obsolete Class that will eventually go away and be replaced with the class Entity

public class BaseGameEntity
{
  // Class member variables
  protected boolean alive;
  protected double  x, y;
  protected double  velX, velY;
  protected double  moveAngle, faceAngle;

  /**
   * Default Constructor
   */
  BaseGameEntity()
  {
    setAlive(false);
    setX(0.0);
    setY(0.0);
    setVelX(0.0);
    setVelY(0.0);
    setMoveAngle(0.0);
    setFaceAngle(0.0);
  }

  ///////////////////////////////////////////////////////////////////////////
  //     _                                        
  //    / \   ___ ___ ___  ___ ___  ___  _ __ ___ 
  //   / _ \ / __/ __/ _ \/ __/ __|/ _ \| '__/ __|
  //  / ___ \ (_| (_|  __/\__ \__ \ (_) | |  \__ \
  // /_/   \_\___\___\___||___/___/\___/|_|  |___/
  //                                             
  ///////////////////////////////////////////////////////////////////////////

  public boolean isAlive()
  {
    return alive;
  }

  public double getX()
  {
    return x;
  }

  public double getY()
  {
    return y;
  }

  public double getVelX()
  {
    return velX;
  }

  public double getVelY()
  {
    return velY;
  }

  public double getMoveAngle()
  {
    return moveAngle;
  }

  public double getFaceAngle()
  {
    return faceAngle;
  }

  ///////////////////////////////////////////////////////////////////////////
  //     __  __       _        _                 
  //    |  \/  |_   _| |_ __ _| |_ ___  _ __ ___ 
  //    | |\/| | | | | __/ _` | __/ _ \| '__/ __|
  //    | |  | | |_| | || (_| | || (_) | |  \__ \
  //    |_|  |_|\__,_|\__\__,_|\__\___/|_|  |___/
  //
  ///////////////////////////////////////////////////////////////////////////

  public void setAlive(boolean alive)
  {
    this.alive = alive;
  }

  public void setX(double x)
  {
    this.x = x;
  }

  public void incX(double i)
  {
    this.x += i;
  }

  public void setY(double y)
  {
    this.y = y;
  }

  public void incY(double i)
  {
    this.y += i;
  }

  public void setVelX(double velX)
  {
    this.velX = velX;
  }

  public void incVelX(double i)
  {
    this.velX += i;
  }

  public void setVelY(double velY)
  {
    this.velY = velY;
  }

  public void incVelY(double i)
  {
    this.velY += i;
  }

  public void setFaceAngle(double angle)
  {
    this.faceAngle = angle;
  }

  public void incFaceAngle(double i)
  {
    this.faceAngle += i;
  }

  public void setMoveAngle(double angle)
  {
    this.moveAngle = angle;
  }

  public void incMoveAngle(double i)
  {
    this.moveAngle += i;
  }
}
