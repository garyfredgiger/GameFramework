package game.framework.entities;

import game.framework.primitives.Position2D;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

public class StaticImage
{
  private Position2D           position;
  private int                  width, height;
  private Image                image;
  private static ImageObserver imageObserver;

  // TODO: Possibly remove the EntityTypes parameter
  public StaticImage(Image i, int x, int y, boolean centerImage, ImageObserver io)
  {
    imageObserver = io;

    try
    {
      if (i == null)
      {
        throw new NullPointerException("Image is null.");
      }

      // Assign the image, set the dimensions and the default position
      this.image = i;
      while (image.getWidth(imageObserver) <= 0);

      width = image.getWidth(imageObserver);
      height = image.getHeight(imageObserver);

      if (centerImage)
      {
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);

        position = new Position2D(x - width / 2, y - height / 2);
      }
      else
      {
        position = new Position2D(x, y);
      }
    }
    catch (NullPointerException e)
    {
      System.out.println(e.getMessage());
    }

    System.out.println(toString());
  }

  /*
   * (non-Javadoc)
   * @see game.entities.Entity#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D g)
  {
    g.drawImage(image, (int) position.x, (int) position.y, imageObserver);
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
    String entitySnapshot = "Image Height: " + image.getHeight(imageObserver) + "\n";
    entitySnapshot += "Image Width: " + image.getWidth(imageObserver) + "\n";
    entitySnapshot += "Image Position: " + position.toString() + "\n";

    return entitySnapshot;
  }
}
