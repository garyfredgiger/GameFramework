package game.framework.utilities;

import java.util.Random;

import game.framework.entities.Entity;
import game.framework.primitives.Position2D;
import game.framework.primitives.Vector2D;

/**
 * 
 * @author ggiger
 * 
 *         A list of issues to address that could cause problems in the future
 * 
 *         TODO: Determine another way to access the screen width and height rather then referencing the default values in the methods warp and collideWalls since it may be redefined.
 * 
 */

public class GameUtility
{
  public static final Random random = new Random();

  /*
   * Cause sprite to warp around the edges of the screen
   */
  public static void warp(Entity entity)
  {
    // Create some shortcut variables
    double w = entity.getWidth() - 1;
    double h = entity.getHeight() - 1;

    // Wrap the sprite around the screen edges
    if (entity.getPositionX() < 0 - w)
    {
      entity.setPositionX(GameEngineConstants.DEFAULT_CANVAS_WIDTH);
    }
    else if (entity.getPositionX() > GameEngineConstants.DEFAULT_CANVAS_WIDTH)
    {
      entity.setPositionX(0 - w);
    }
    if (entity.getPositionY() < 0 - h)
    {
      entity.setPositionY(GameEngineConstants.DEFAULT_CANVAS_HEIGHT);
    }
    else if (entity.getPositionY() > GameEngineConstants.DEFAULT_CANVAS_HEIGHT)
    {
      entity.setPositionY(0 - h);
    }
  }

  // Check for collision with wall
  public static void collideWalls(Entity atom)
  {
    int width = atom.getWidth();
    int height = atom.getHeight();
    Position2D p1 = atom.getPosition();
    Vector2D v1 = atom.getVelocity();

    if (p1.x < 0)
    {
      p1.x = 0;
      v1.x = -v1.x;
    }

    if (p1.y < 0)
    {
      p1.y = 0;
      v1.y = -v1.y;
    }

    if (p1.x > GameEngineConstants.DEFAULT_CANVAS_WIDTH - width)
    {
      p1.x = GameEngineConstants.DEFAULT_CANVAS_WIDTH - width;
      v1.x = -v1.x;
    }

    if (p1.y > GameEngineConstants.DEFAULT_CANVAS_HEIGHT - height)
    {
      p1.y = GameEngineConstants.DEFAULT_CANVAS_HEIGHT - height;
      v1.y = -v1.y;
    }

    atom.setPosition(p1.x, p1.y);
    atom.setVelocity(v1.x, v1.y);
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

  // Pad a positive integer with zeros
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
