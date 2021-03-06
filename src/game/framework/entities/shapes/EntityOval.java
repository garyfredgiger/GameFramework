package game.framework.entities.shapes;

import java.awt.Color;
import java.awt.Graphics2D;

import game.framework.entities.EntityShape;
import game.framework.utilities.GameEngineConstants;

public class EntityOval extends EntityShape
{
  /*
   * Default Constructor
   */
  public EntityOval()
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED, DEFAULT_COLOR, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /*
   * Constructor specifying the color of the shape entity
   */
  public EntityOval(Color color)
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED, DEFAULT_COLOR, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /*
   * Constructor specifying the type of the shape entity
   */
  public EntityOval(GameEngineConstants.EntityTypes type)
  {
    this(type, DEFAULT_COLOR, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /*
   * Constructor specifying the color and dimensions of the shape entity
   */
  public EntityOval(Color color, int width, int height)
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED, color, width, height);
  }

  /*
   * Constructor specifying the type and dimensions of the shape entity
   */
  public EntityOval(GameEngineConstants.EntityTypes type, int width, int height)
  {
    this(type, DEFAULT_COLOR, width, height);
  }

  /*
   * Constructor specifying the type, color and dimensions of the shape entity
   */
  public EntityOval(GameEngineConstants.EntityTypes type, Color color, int width, int height)
  {
    super(type, color);

    this.width = width;
    this.height = height;
  }

  @Override
  public void draw(Graphics2D g)
  {
    if (isAlive() && isVisible())
    {
      g.setColor(color);
      if (fill)
      {        
        g.fillOval((int) position.x, (int) position.y, width, height);        
      }
      else
      {
        g.fillOval((int) position.x, (int) position.y, width, height);
      }
    }
  }
}
