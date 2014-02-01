package game.framework.entities.text;

import java.awt.Color;
import java.awt.Graphics2D;

public class StaticTextFade extends StaticText
{
  private boolean      fadeIn;
  private boolean      fadeDone;
  private long         delay;
  private long         delayTimer;

  private int currentAlpha;
  
  public StaticTextFade(String msg, int x, int y, Color color, String fontName, int fontStyle, int fontSize, int screenWidth, int screenHeight, boolean fadeIn, long delay)
  {
    super(msg, x, y, color, fontName, fontStyle, fontSize, screenWidth, screenHeight);

    this.fadeIn = fadeIn;
    
    // Set the fade in value for alpha
    currentAlpha = (fadeIn ? 0 : 255);
    
    this.fadeDone = false;

    this.delay = delay;
    if (delay < 0)
    {
      delay = 0;
    }
    delayTimer = System.currentTimeMillis();
  }

  @Override
  public void draw(Graphics2D g)
  {
    if (System.currentTimeMillis() > (delayTimer + delay))
    {      
      // Fade in the text
      if (fadeIn && !fadeDone)
      {
        currentAlpha += 16;
        if (currentAlpha >= 255)
        {
          currentAlpha = 255;
          fadeDone = true;
        }
      }
      // Fade out the text
      else if (!fadeIn && !fadeDone)
      {
        currentAlpha -= 16;
        if (currentAlpha <= 0)
        {
          currentAlpha = 0;
          fadeDone = true;
        }
      }
      delayTimer = System.currentTimeMillis();
    }
    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), currentAlpha);

    super.draw(g);
  }
}
