package game.deprecated;

/*********************************************************
 * Base game image class for bitmapped game entities
 **********************************************************/
import java.awt.*;
import java.awt.geom.*;
import java.net.*;
import java.applet.*;

// Will eventually be extended from the Entity class

public class ImageEntity extends BaseGameEntity
{
  // Class Variables
  protected Image           image; // Image to display
  protected Applet          applet;		// 
  protected AffineTransform at;
  protected Graphics2D      g2d;

  /**
   * Default constructor
   * 
   * @param a
   *          - Stores the reference to the applet object
   */
  public ImageEntity(Applet a)
  {
    applet = a;
    setImage(null);
    setAlive(true);
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
    this.image = image;
    double x = applet.getSize().width / 2 - width() / 2;
    double y = applet.getSize().height / 2 - height() / 2;
    at = AffineTransform.getTranslateInstance(x, y);
  }

  /**
   * Returns the width of the image if it is defined.
   * 
   * @return Returns image width it defined, otherwise a 0 is returned
   */
  public int width()
  {
    if (image != null)
      return image.getWidth(applet);
    else
      return 0;
  }

  /**
   * Returns the height of the image if it is defined.
   * 
   * @return Returns image height it defined, otherwise a 0 is returned
   */
  public int height()
  {
    if (image != null)
      return image.getHeight(applet);
    else
      return 0;
  }

  /**
   * Returns the x component of the center of the image to the caller.
   * 
   * @return The x component of the image center
   */
  public double getCenterX()
  {
    return getX() + width() / 2;
  }

  /**
   * Returns the y component of the center of the image to the caller.
   * 
   * @return The y component of the image center
   */
  public double getCenterY()
  {
    return getY() + height() / 2;
  }

  /**
   * Stores a reference of the graphics object
   * 
   * @param g
   *          - A reference to the graphics object
   */
  public void setGraphics(Graphics2D g)
  {
    g2d = g;
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

    image = tk.getImage(getURL(filename));
    while (getImage().getWidth(applet) <= 0);
    double x = applet.getSize().width / 2 - width() / 2;
    double y = applet.getSize().height / 2 - height() / 2;
    at = AffineTransform.getTranslateInstance(x, y);
  }

  /**
   * Translates and/or rotates the currently displayed image. This is usually done right after the x-y coordinates or the facing angle are modified. When the image is drawn to the canvas, these new
   * changes will take effect.
   */
  public void transform()
  {
    at.setToIdentity();
    at.translate((int) getX() + width() / 2, (int) getY() + height() / 2);
    at.rotate(Math.toRadians(getFaceAngle()));
    at.translate(-width() / 2, -height() / 2);
  }

  /**
   * Draws the image to the canvas.
   */
  public void draw()
  {
    g2d.drawImage(getImage(), at, applet);
  }

  /**
   * Returns a bounding rectangle to the caller.
   * 
   * @return The bounding rectangle for the current image
   * 
   *         TODO Can reduce from three lines of code to one l
   */
  public Rectangle getBounds()
  {
    Rectangle r;
    r = new Rectangle((int) getX(), (int) getY(), width(), height());
    return r;
  }

}
