package game.framework.utilities;

import java.util.Random;

import game.framework.entities.Entity2D;
import game.framework.primitives.Position2D;
import game.framework.primitives.Vector2D;

/**
 * This class provides a set of methods that are commonly used in many game to make the game developer's job easier
 * 
 * A list of issues to address that could cause problems in the future
 * 
 * TODO: Determine another way to access the screen width and height rather then referencing the default values in the methods warp and collideWalls since it may be redefined.
 */
public class GameUtility
{
  public static final Random random = new Random();

  /*
   * Cause sprite to warp around the edges of the screen
   */
  public static void warp(Entity2D entity, int screenWidth, int screenHeight)
  {
    // Create some shortcut variables
    double entityWidth = entity.getWidth() - 1;
    double entityHeight = entity.getHeight() - 1;

    // Wrap the sprite around the screen edges
    if (entity.getPositionX() < 0 - entityWidth)
    {
      entity.setPositionX(screenWidth);
    }
    else if (entity.getPositionX() > screenWidth)
    {
      entity.setPositionX(0 - entityWidth);
    }
    if (entity.getPositionY() < 0 - entityHeight)
    {
      entity.setPositionY(screenHeight);
    }
    else if (entity.getPositionY() > screenHeight)
    {
      entity.setPositionY(0 - entityHeight);
    }
  }

  // Check for collision with wall (i.e edge of the screen)
  public static void collideWalls(Entity2D entity, int screenWidth, int screenHeight)
  {
    int entityWidth = entity.getWidth();
    int entityHeight = entity.getHeight();
    Position2D p1 = entity.getPosition();
    Vector2D v1 = entity.getVelocity();

    // Check if the x component of the position hit the left side of the screen
    if (p1.x < 0)
    {
      p1.x = 0;
      v1.x = -v1.x;
    }

    // Check if the y component of the position hit the top of the screen
    if (p1.y < 0)
    {
      p1.y = 0;
      v1.y = -v1.y;
    }

    // Check if the x component of the position hit the right side of the screen 
    if (p1.x > screenWidth - entityWidth)
    {
      p1.x = screenWidth - entityWidth;
      v1.x = -v1.x;
    }

    // Check if the y component of the position hit the bottom of the screen. 
    if (p1.y > screenHeight - entityHeight)
    {
      p1.y = screenHeight - entityHeight;
      v1.y = -v1.y;
    }

    entity.setPosition(p1.x, p1.y);
    entity.setVelocity(v1.x, v1.y);
  }

  public static Vector2D computeRandomVelocity()
  {
    return computeRandomVelocity(1.0);
  }

  public static Vector2D computeRandomVelocity(double scale)
  {
    // Compute a random velocity for both x-y components between [0.0, 1.0)
    double vx = random.nextDouble();
    double vy = random.nextDouble();

    // Determine the sign of each component 
    vx = (random.nextBoolean() ? -vx : vx);
    vy = (random.nextBoolean() ? -vy : vy);

    //System.out.println("(" + vx + ", " + vy + ")");

    Vector2D unitVelocity = new Vector2D(vx, vy);
    unitVelocity.scaleThisVector(scale);

    //System.out.println(unitVelocity.unitVector().toString());

    return unitVelocity.createUnitVector();
  }

  public static Vector2D computeVectorBetweenTwoPositions(Position2D fromPosition, Position2D toPosition)
  {
    Vector2D fromVector = new Vector2D(fromPosition.x, fromPosition.y);
    Vector2D toVector = new Vector2D(toPosition.x, toPosition.y);

    toVector.subtract(fromVector);

    return toVector;
  }

  public static Vector2D computeUnitVectorBetweenTwoPositions(Position2D fromPosition, Position2D toPosition)
  {
    return computeVectorBetweenTwoPositions(fromPosition, toPosition).createUnitVector();
  }

  public static double calcAngleMoveX(double angle)
  {
    return (double) (Math.cos(angle * Math.PI / 180));
  }

  public static double calcAngleMoveY(double angle)
  {
    return (double) (Math.sin(angle * Math.PI / 180));
  }

  /*
   *  Pad an integer with zeros. This can be used in situations where fixed width 
   *  numbers make display look aesthetically pleasing (e.g., when current level
   *  is displayed so 01 through 09 take up same width as double digit levels 10,
   *  11, 12, etc...).
   */
  public static String lPadZero(int in, int fill)
  {

    boolean negative = false;
    int value, len = 0;

    if (in >= 0)
    {
      value = in;
    }
    else
    {
      negative = true;
      value = -in;
      in = -in;
      len++;
    }

    if (value == 0)
    {
      len = 1;
    }
    else
    {
      for (; value != 0; len++)
      {
        value /= 10;
      }
    }

    StringBuilder sb = new StringBuilder();

    if (negative)
    {
      sb.append('-');
    }

    for (int i = fill; i > len; i--)
    {
      sb.append('0');
    }

    sb.append(in);

    return sb.toString();
  }

}
