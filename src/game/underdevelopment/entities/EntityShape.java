package game.underdevelopment.entities;

import game.framework.entities.Entity2D;
import game.framework.utilities.GameEngineConstants;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

// Not Sure this will be needed right now since the Entity class will draw shapes by default, at least for now.
public class EntityShape extends Entity2D
{
  private Shape           shape;
  private Color           color;
  private AffineTransform at;

  public EntityShape(Shape s, int height, int width, Color c, GameEngineConstants.EntityTypes type)
  {
    //    super(type);
    //
    //    // TODO: Should we throw an exception if the shape is null?
    //    if (s == null)
    //    {
    //      return;
    //    }
    //
    //    this.shape = s;
    //    this.color = c;
    //    this.entityType = type;

    //    this.setDimensions(width, height); 
    //    position.x = GameEngineConstants.DEFAULT_CANVAS_WIDTH / 2 - width / 2;
    //    position.y = GameEngineConstants.DEFAULT_CANVAS_HEIGHT / 2 - height / 2;

    // Set the affine transform for the image
    //    at = AffineTransform.getTranslateInstance(position.x, position.y);

    this(s, 0, 0, height, width, c, type);
  }

  public EntityShape(Shape s, int x, int y, int height, int width, Color c, GameEngineConstants.EntityTypes type)
  {
    super(type);

    // TODO: Should we throw an exception if the shape is null?
    if (s == null)
    {
      return;
    }

    this.shape = s;
    this.color = c;
    this.entityType = type;

    this.setDimensions(width, height);
    position.x = x;
    position.y = y;

    // Set the affine transform for the image
    //at = AffineTransform.getTranslateInstance(position.x, position.y);
    at = new AffineTransform();
  }

  public void setShape(Shape shape)
  {
    this.shape = shape;
  }

  public void transform()
  {
    at.setToIdentity();
    at.translate((int) position.x + width / 2, (int) position.y + height / 2);

    at.rotate(Math.toRadians(getFaceAngle()));
    at.translate(-width / 2, -height / 2);
  }

  @Override
  public void draw(Graphics2D g)
  {
    //System.out.println("The Entity Shape draw method is being called.");

    if (isAlive() && isVisible())
    {
      // Create our transform and modify it based on the line we use.
      // Uncomment the lines below to see their effect

      //AffineTransform at = new AffineTransform(); // This line works fine
      at.setToIdentity(); // This line works fine

      // These do not result in the correct positioning of the shape on the screen.
      //at.translate(position.x + width/2, position.y + height/2);
      //at.translate(position.x, position.y);

      // This causes the correct positioning of the shape on the screen
      at.rotate(Math.toRadians(getFaceAngle()), position.x + width / 2, position.y + height / 2);
      //at.scale(modifier / 100.0, modifier/ 100.0);
      //at.translate(modifier, modifier);

      // Transform the shape and draw it to screen
      //Graphics2D g2d = (Graphics2D) g;
      g.setColor(this.color);
      g.draw(at.createTransformedShape(shape));
    }
  }
}
