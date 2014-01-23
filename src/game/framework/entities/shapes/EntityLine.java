package game.framework.entities.shapes;

import java.awt.Color;
import java.awt.Graphics2D;

import game.framework.entities.EntityShape;
import game.framework.utilities.GameEngineConstants;

public class EntityLine extends EntityShape
{
  /*
   * Default Constructor
   */
  public EntityLine()
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED, DEFAULT_COLOR, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /*
   * Constructor specifying the color of the shape entity
   */
  public EntityLine(Color color)
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED, DEFAULT_COLOR, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /*
   * Constructor specifying the type of the shape entity
   */
  public EntityLine(GameEngineConstants.EntityTypes type)
  {
    this(type, DEFAULT_COLOR, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  /*
   * Constructor specifying the color and dimensions of the shape entity
   */
  public EntityLine(Color color, int x2, int y2)
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED, color, x2, y2);
  }

  /*
   * Constructor specifying the type and dimensions of the shape entity
   */
  public EntityLine(GameEngineConstants.EntityTypes type, int x2, int y2)
  {
    this(type, DEFAULT_COLOR, x2, y2);
  }

  /*
   * Constructor specifying the type, color and dimensions of the shape entity
   */
  public EntityLine(GameEngineConstants.EntityTypes type, Color color, int x2, int y2)
  {
    super(type, color);

    this.width = x2;
    this.height = y2;
  }

  @Override
  public void draw(Graphics2D g)
  {
    if (isAlive() && isVisible())
    {
      g.setColor(color);
      g.drawLine((int) position.x, (int) position.y, width, height);
    }
  }
}
