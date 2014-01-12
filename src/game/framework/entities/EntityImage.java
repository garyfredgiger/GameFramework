package game.framework.entities;

import game.framework.utilities.GameEngineConstants;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.net.URL;

public class EntityImage extends Entity
{
  protected Image           image;        // Image to display
  protected ImageObserver   imageObserver;
  protected AffineTransform at;

  // TODO: Possibly remove the EntityTypes parameter
  public EntityImage(ImageObserver imageObserver, GameEngineConstants.EntityTypes type)
  {
    super();
    this.imageObserver = imageObserver;
    this.entityType = type;
  }

  /**
   * Returns the currently assigned image to the caller
   * 
   * @return The currently assigned image
   */
  public Image getImage()
  {
    return image;
  }

  /**
   * When an image is assigned to an instance of this class, the following actions occur:
   * 
   * - The image is assigned a default position at the center of the applet window.
   * 
   * - An AffineTransform object represents a translation transformation for the image is created with the x and y values just defined.
   * 
   * Note that before the image is displayed, its initial position should be changed to another location in the applet windows or all images will be displayed at the center of the applet window when
   * the canvas is redrawn.
   * 
   * @param image
   */
  public void setImage(Image image)
  {
    if (image == null)
    {
      return;
    }

    // Assign the image, set the dimensions and the default position
    this.image = image;
    this.setDimensions(this.image.getWidth(imageObserver), this.image.getHeight(imageObserver));

    // NOTE: Since entities have no knowledge of the display environment, this code should not exist in here.

    // Start the entity in the upper left corner of the display by default
    //position.x = 0;
    //position.y = 0;

    // Set the affine transform for the image
    at = AffineTransform.getTranslateInstance(position.x, position.y);
  }

  /**
   * Returns the width of the image if it is defined.
   * 
   * @return Returns image width it defined, otherwise a 0 is returned
   */
  @Override
  public int getWidth()
  {
    return ((image != null) ? width : 0);
  }

  /**
   * Returns the height of the image if it is defined.
   * 
   * @return Returns image height it defined, otherwise a 0 is returned
   */
  @Override
  public int getHeight()
  {
    return ((image != null) ? height : 0);
  }

  /**
   * Translates and/or rotates the currently displayed image. This is usually done right after the x-y coordinates or the facing angle are modified. When the image is drawn to the canvas, these new
   * changes will take effect.
   */
  public void transform()
  {
    at.setToIdentity();
    at.translate((int) position.x + width / 2, (int) position.y + height / 2);

    // TODO: Possible Speedup, only convert to radians when the face angle is changed
    at.rotate(Math.toRadians(getFaceAngle()));
    at.translate(-width / 2, -height / 2);
  }

  /**
   * Loads the image specified by the filename. When the image is loaded from the specified filename, the following actions occur:
   * 
   * - This method will wait until the image is loaded before proceeding. with the rest of the execution.
   * 
   * - Once the image is loaded, its x and y coordinates are defined to be in the center of the applet window.
   * 
   * - An AffineTransform object represents a translation transformation for the image is created with the x and y values just defined.
   * 
   * @param filename
   *          - The filename of the image to load
   * 
   *          TODO - After the image is loaded, consider calling the setImage method since the later part if this code is already duplicated in this method.
   */
  public void load(String filename)
  {
    Toolkit tk = Toolkit.getDefaultToolkit();

    // TODO: The image store should go here to reduce the overhead of loading the images from files each and everytime.s

    try
    {
      image = tk.getImage(getURL(filename));
      
      if (image == null)
      {
        throw new NullPointerException("Image with filename ' + filename + ' could not be loaded.");
      }
      
      while (getImage().getWidth(imageObserver) <= 0);

      this.setDimensions(this.image.getWidth(imageObserver), this.image.getHeight(imageObserver));

      // Start the entity in the upper left corner of the display by default
      position.x = 0;
      position.y = 0;

      at = AffineTransform.getTranslateInstance(position.x, position.y);
    }
    catch (NullPointerException e)
    {
      System.out.println(e.getMessage());
    }
  }

  private URL getURL(String filename)
  {
    URL url = null;
    try
    {
      // TODO: Add the image loader class here

      url = this.getClass().getClassLoader().getResource(filename);
    }
    catch (Exception e)
    {}

    return url;
  }

  /*
   * (non-Javadoc)
   * @see game.entities.Entity#draw(java.awt.Graphics2D)
   */
  @Override
  public void draw(Graphics2D g)
  {
    if (isAlive() && isVisible())
    {
      this.transform();
      g.drawImage(getImage(), at, imageObserver);
    }
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
    String entitySnapshot = super.toString();

    entitySnapshot += "Image Height: " + image.getHeight(imageObserver) + "\n";
    entitySnapshot += "Image Width: " + image.getWidth(imageObserver) + "\n";
    entitySnapshot += "Image Width: " + image.toString() + "\n";

    return entitySnapshot;
  }
}
