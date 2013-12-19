package game.deprecated;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import game.framework.primitives.Position2D;

public abstract class GameApplet extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener
{
  /*
   * Class Constants
   */
  public final static int            DEFAULT_SCREEN_WIDTH  = 800;
  public final static int            DEFAULT_SCREEN_HEIGHT = 600;
  public final static int            DEFAULT_FRAME_RATE    = 60;

  //the main game loop thread
  private Thread                     gameloop;

  //internal list of sprites
  private LinkedList<AnimatedSprite> _sprites;

  public LinkedList<AnimatedSprite> sprites()
  {
    return _sprites;
  }

  //screen and double buffer related variables
  private BufferedImage backbuffer;
  private Graphics2D    g2d;
  private int           screenWidth, screenHeight;

  //keep track of mouse position and buttons
  private Position2D    mousePos       = new Position2D(0, 0);
  private boolean       mouseButtons[] = new boolean[4];

  //frame rate counters and other timing variables
  private int           _frameCount    = 0;
  private int           _frameRate     = 0;
  private int           desiredRate;
  private double        gameLoopSleepTime;
  private long          startTime      = System.currentTimeMillis();

  // Variables to keep statistics
  long                  frameRateSum;
  long                  numberLoopIterations;
  double                averageFrameRate;

  long                  currentLoopTime;
  long                  lastLoopTime;
  double                loopTime;

  //local applet object
  public Applet applet()
  {
    return this;
  }

  //game pause state
  private boolean _gamePaused = false;

  public boolean gamePaused()
  {
    return _gamePaused;
  }

  public void pauseGame()
  {
    _gamePaused = true;
  }

  public void resumeGame()
  {
    _gamePaused = false;
  }

  // Declare the game event methods that sub-class must implement

  // Called from within the init method
  public abstract void gameStartup();

  public abstract void gameTimedUpdate();

  public abstract void gameRefreshScreen();

  public abstract void gameShutdown();

  public abstract void userUpdateSprite(AnimatedSprite sprite);

  public abstract void userDrawSprite(AnimatedSprite sprite);

  public abstract void spriteDying(AnimatedSprite sprite);

  public abstract void userSpriteCollision(AnimatedSprite spr1, AnimatedSprite spr2);

  // User input methods
  public abstract void gameKeyDown(int keyCode);

  public abstract void gameKeyUp(int keyCode);

  public abstract void gameMouseDown();

  public abstract void gameMouseUp();

  public abstract void gameMouseMove();

  /*****************************************************
   * constructor
   *****************************************************/
  public GameApplet(int frameRate, int width, int height)
  {
    desiredRate = frameRate;
    screenWidth = width;
    screenHeight = height;
    gameLoopSleepTime = 1000 / desiredRate;
  }

  //return g2d object so sub-class can draw things
  public Graphics2D graphics()
  {
    return g2d;
  }

  //current frame rate
  public int frameRate()
  {
    return _frameRate;
  }

  public double getLoopTime()
  {
    return loopTime;
  }

  //mouse buttons and movement
  public boolean mouseButton(int btn)
  {
    return mouseButtons[btn];
  }

  public Position2D mousePosition()
  {
    return mousePos;
  }

  /*
   * (non-Javadoc)
   * @see java.applet.Applet#init()
   * 
   * Initializes the game engine including:
   * - Setting up the buffers for drawing 
   * - Creating the data structures used by the game loop
   * - Adding the keyboard and mouse listeners to handle user input
   * - Calling the user defined method to perform any game specific
   *   initializations before the game loop starts 
   * 
   * NOTE: This method is the first method called when the Applet starts and is
   * only called once. This method is always called before the first call to
   * the start method.
   */
  public void init()
  {
    // Create the back buffer and drawing surface
    backbuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
    g2d = backbuffer.createGraphics();

    // Create the internal sprite list
    _sprites = new LinkedList<AnimatedSprite>();

    // Start the input listeners
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);

    numberLoopIterations = 0;

    // This method implemented by the sub-class
    gameStartup();
  }

  /*****************************************************
   * thread start event - start the game loop running
   *****************************************************/

  /*
   * (non-Javadoc)
   * @see java.applet.Applet#start()
   * 
   * Responsible for starting the game loop (i.e., starts the thread, which
   * invokes the implementation of the run method).
   * 
   * NOTE: This method is called after the init method and each time the Applet
   * on the web page is visited (i.e., when another web page is left and a user 
   * returns to the web page containing the Applet).
   * 
   */
  public void start()
  {
    gameloop = new Thread(this);
    gameloop.start();
  }

  /*
   * (non-Javadoc)
   * @see java.awt.Container#update(java.awt.Graphics)
   * 
   * This method is actually in the Container class, a parent class of the
   * Applet class. This method is called by the repaint method (from the 
   * Component class), which in-turn calls the paint method.
   */
  public void update(Graphics g)
  {
    // Calculate frame rate
    _frameCount++;
    if (System.currentTimeMillis() > startTime + 1000)
    {
      startTime = System.currentTimeMillis();
      _frameRate = _frameCount;
      _frameCount = 0;

      // Keep track of these variables to compute the average frame rate for the duration of the game
      frameRateSum += _frameRate;
      numberLoopIterations++;

      // Once every second all dead sprites are deleted
      purgeSprites();
    }
    // This method implemented by sub-class
    gameRefreshScreen();

    // Draw the internal list of sprites
    if (!gamePaused())
    {
      drawSprites();
    }

    // Explicitly call the paint method
    // NOTE: Since the paint method is overridden and it simply draws the back 
    //       buffer, the call to draw the back buffer can be done here, the 
    //       overridden paint method can be removed from this class and the AWT
    //       can simply call the paint method from the parent class. I am not
    //       sure which is preferred from a performance standpoint.
    //paint(g);

    //g.drawImage(backbuffer, 0, 0, this);
  }

  /*
   * (non-Javadoc)
   * @see java.awt.Container#paint(java.awt.Graphics)
   * 
   * Simply draw the graphics buffer
   * 
   * This method is actually in the Container class, a parent class of the
   * Applet class. It paints the container.
   */
  //  public void paint(Graphics g)
  //  {
  //    g.drawImage(backbuffer, 0, 0, this);
  //  }

  /*****************************************************
   * thread run event (game loop)
   *****************************************************/

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   * 
   * This method contains the game loop
   * 
   */
  public void run()
  {
    // Acquire the current thread
    Thread t = Thread.currentThread();

    // Add particles here
    lastLoopTime = System.currentTimeMillis();
    //lastLoopTime = System.nanoTime();

    // Process the main game loop thread
    while (t == gameloop)
    {
      currentLoopTime = System.currentTimeMillis();
      //currentLoopTime = System.nanoTime();
      loopTime = (currentLoopTime - lastLoopTime) * 0.001;
      lastLoopTime = currentLoopTime;

      try
      {
        // Set a consistent frame rate
        //Thread.sleep(1000 / desiredRate);
        Thread.sleep((int) gameLoopSleepTime);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }

      // Update the internal list of sprites
      if (!gamePaused())
      {
        updateSprites(loopTime);
        testCollisions();
      }

      // Allow main game to update if needed
      gameTimedUpdate();

      // Refresh the screen. By calling the repaint method, this calls the
      // update method which in turn calls the paint method. 
      repaint();
    }
  }

  /*****************************************************
   * thread stop event
   *****************************************************/

  /*
   * (non-Javadoc)
   * @see java.applet.Applet#stop()
   * 
   * This method is responsible for stopping the game loop and calling the user
   * defined method gameShutdown.
   * 
   * NOTE: This method is called when the web page containing the Applet is 
   *       replaced by another web page. It is also called prior to the destroy
   *       method.
   * 
   */
  public void stop()
  {
    //kill the game loop
    gameloop = null;

    //this method implemented by sub-class
    gameShutdown();

    averageFrameRate = frameRateSum / numberLoopIterations;
    System.out.println("Average Frame Rate:" + averageFrameRate);
  }

  /*****************************************************
   * key listener events
   *****************************************************/
  public void keyTyped(KeyEvent k)
  {}

  public void keyPressed(KeyEvent k)
  {
    gameKeyDown(k.getKeyCode());
  }

  public void keyReleased(KeyEvent k)
  {
    gameKeyUp(k.getKeyCode());
  }

  /*****************************************************
   * checkButtons stores the state of the mouse buttons
   *****************************************************/
  private void checkButtons(MouseEvent e)
  {
    switch (e.getButton())
    {
      case MouseEvent.BUTTON1:
        mouseButtons[1] = true;
        mouseButtons[2] = false;
        mouseButtons[3] = false;
        break;
      case MouseEvent.BUTTON2:
        mouseButtons[1] = false;
        mouseButtons[2] = true;
        mouseButtons[3] = false;
        break;
      case MouseEvent.BUTTON3:
        mouseButtons[1] = false;
        mouseButtons[2] = false;
        mouseButtons[3] = true;
        break;
    }
  }

  /*****************************************************
   * mouse listener events
   *****************************************************/
  public void mousePressed(MouseEvent e)
  {
    checkButtons(e);
    mousePos.x = e.getX();
    mousePos.y = e.getY();
    gameMouseDown();
  }

  public void mouseReleased(MouseEvent e)
  {
    checkButtons(e);
    mousePos.x = e.getX();
    mousePos.y = e.getY();
    gameMouseUp();
  }

  public void mouseMoved(MouseEvent e)
  {
    checkButtons(e);
    mousePos.x = e.getX();
    mousePos.y = e.getY();
    gameMouseMove();
  }

  public void mouseDragged(MouseEvent e)
  {
    checkButtons(e);
    mousePos.x = e.getX();
    mousePos.y = e.getY();
    gameMouseDown();
    gameMouseMove();
  }

  public void mouseEntered(MouseEvent e)
  {
    mousePos.x = e.getX();
    mousePos.y = e.getY();
    gameMouseMove();
  }

  public void mouseExited(MouseEvent e)
  {
    mousePos.x = e.getX();
    mousePos.y = e.getY();
    gameMouseMove();
  }

  //this event is not needed
  public void mouseClicked(MouseEvent e)
  {}

  /*****************************************************
   * X and Y velocity calculation functions
   *****************************************************/
  protected double calcAngleMoveX(double angle)
  {
    return (double) (Math.cos(angle * Math.PI / 180));
  }

  protected double calcAngleMoveY(double angle)
  {
    return (double) (Math.sin(angle * Math.PI / 180));
  }

  /*****************************************************
   * update the sprite list from the game loop thread
   *****************************************************/
  protected void updateSprites(double loopTime)
  {
    for (int n = 0; n < _sprites.size(); n++)
    {
      AnimatedSprite spr = (AnimatedSprite) _sprites.get(n);
      if (spr.alive())
      {
        spr.updatePosition(loopTime);
        spr.updateRotation();
        spr.updateAnimation();
        userUpdateSprite(spr);
        spr.updateLifetime();
        if (!spr.alive())
        {
          spriteDying(spr);
        }
      }
    }
  }

  /*****************************************************
   * perform collision testing of all active sprites
   *****************************************************/
  protected void testCollisions()
  {
    //iterate through the sprite list, test each sprite against
    //every other sprite in the list
    for (int first = 0; first < _sprites.size(); first++)
    {

      //get the first sprite to test for collision
      AnimatedSprite spr1 = (AnimatedSprite) _sprites.get(first);
      if (spr1.alive())
      {

        //look through all sprites again for collisions
        for (int second = 0; second < _sprites.size(); second++)
        {

          //make sure this isn't the same sprite
          if (first != second)
          {

            //get the second sprite to test for collision
            AnimatedSprite spr2 = (AnimatedSprite) _sprites.get(second);
            if (spr2.alive())
            {
              if (spr2.collidesWith(spr1))
              {
                userSpriteCollision(spr1, spr2);
                break;
              }
              else
                spr1.setCollided(false);

            }
          }
        }
      }
    }
  }

  /*****************************************************
   * draw all active sprites in the sprite list sprites lower in the list are drawn on top
   *****************************************************/
  protected void drawSprites()
  {
    //draw sprites in reverse order (reverse priority)
    for (int n = 0; n < _sprites.size(); n++)
    {
      AnimatedSprite spr = (AnimatedSprite) _sprites.get(n);
      if (spr.alive())
      {
        spr.updateFrame();
        spr.transform();
        spr.draw();
        userDrawSprite(spr);
      }
    }
  }

  /*****************************************************
   * once every second during the frame update, this method is called to remove all dead sprites from the linked list
   *****************************************************/
  private void purgeSprites()
  {
    for (int n = 0; n < _sprites.size(); n++)
    {
      AnimatedSprite spr = (AnimatedSprite) _sprites.get(n);
      if (!spr.alive())
      {
        _sprites.remove(n);
      }
    }
  }

}
