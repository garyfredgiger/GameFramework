package game.framework.entities.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/*
 * This class simply displays text to the screen. This class is different from normal entities
 * in that it does not need to be added to an entity list to be displayed using the default
 * game loop draw mechanism. This class can be instantiated and its display methods can be called
 * directly from the game loop pre or post draw user defined methods.
 * 
 * There is no error checking for the x-y coordinates of the font to ensure they are within the
 * screen bounds. The reason, in case the font is displayed off the edge of the screen.
 */
public class StaticText
{
  /*
   * Constants
   */

  // Default font attributes
  public static final int    DEFAULT_FONT_SIZE  = 18;
  public static final int    DEFAULT_FONT_STYLE = Font.PLAIN;
  public static final String DEFAULT_FONT_NAME  = "Arial";

  /*
   * Static variables
   */
  private static int         screenWidth        = -1;
  private static int         screenHeight       = -1;
  private static Rectangle2D bounds;

  private int                x, y;                                 // The specified x-y position of the font
  protected Color            color;

  private String             str                = "Default String";
  private boolean            centerHorizontal   = false;
  private boolean            centerVertical     = false;

  private int                drawX, drawY;                         // Stores the computed x-y position of the font
  private Font               font;

  public StaticText(String msg, Color color, String fontName, int fontStyle, int fontSize, int screenWidth, int screenHeight)
  {
    this(msg, -1, -1, color, fontName, fontStyle, fontSize, screenWidth, screenHeight);
    centerHorizontally();
    centerVertically();
  }
  
  public StaticText(String msg, Color color, Font font, int screenWidth, int screenHeight)
  {
    this(msg, -1, -1, color, font.getName(), font.getStyle(), font.getSize(), screenWidth, screenHeight);
    centerHorizontally();
    centerVertically();
  }
  
  public StaticText(String msg, int x, int y, Color color, Font font, int screenWidth, int screenHeight)
  {
    this(msg, x, y, color, font.getName(), font.getStyle(), font.getSize(), screenWidth, screenHeight);
  }
  
  public StaticText(String msg, int x, int y, Color color, String fontName, int fontStyle, int fontSize, int screenWidth, int screenHeight)
  {
    this.str = msg;

    this.x = x;
    this.y = y;
    this.color = color;

    StaticText.screenWidth = screenWidth;
    StaticText.screenHeight = screenHeight;

    font = new Font(fontName, fontStyle, fontSize);
  }

  public void centerHorizontally()
  {
    centerHorizontal = true;
  }

  public void centerVertically()
  {
    centerVertical = true;
  }

  public void draw(Graphics2D g)
  {
    // Determine the position of the string
    g.setFont(font);
    bounds = g.getFontMetrics().getStringBounds(str, g);
    drawX = (centerHorizontal ? (int) ((screenWidth - bounds.getWidth()) / 2) : x);
    drawY = (centerVertical ? (int) ((screenHeight - bounds.getHeight()) / 2) : y);

    // Draw the string
    g.setColor(color);
    g.drawString(str, drawX, drawY);
  }
}
