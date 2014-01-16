package game.framework.entities.shapes;

import java.awt.Color;
import java.awt.Graphics2D;

import game.framework.entities.EntityShape;
import game.framework.utilities.GameEngineConstants;

public class EntityRectangle extends EntityShape
{
  public static final int DEFAULT_WIDTH = 16;
  public static final int DEFAULT_HEIGHT = 16;

  /*
   * Default Constructor
   */
  public EntityRectangle()
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED, Color.RED, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  public EntityRectangle(Color color, int width, int height)
  {
    this(GameEngineConstants.EntityTypes.UNDEFINED, color, width, height);
  }
  
  public EntityRectangle(GameEngineConstants.EntityTypes type)
  {
    this(type, Color.RED, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }  
    
  /*
   * Constructor specifying the type and color of the shape entity
   */
  public EntityRectangle(GameEngineConstants.EntityTypes type, Color color, int width, int height)
  {
    super(type);

    this.color = color;
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    if (isAlive() && isVisible())
    {
      g.setColor(color);
      g.fillRect((int) position.x, (int) position.y, width, height);
    }
  }
}
