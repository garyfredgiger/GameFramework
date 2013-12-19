package game.framework.utilities.input;

import java.awt.event.KeyEvent;

/**
 * 
 * @author ggiger
 * 
 *         Experimental class that keeps track of the current movement key states of the player. This class only works for the arrow key right now. This class currently only supports a single player.
 * 
 *         TODO: Ability to configure movement keys other than the arrow keys
 * 
 */
public class GameInputMovement
{
  //  private int keyLeftMovementValue = KeyEvent.VK_LEFT;
  //  private int keyRightMovementValue = KeyEvent.VK_RIGHT;
  //  private int keyUpMovementValue = KeyEvent.VK_UP;
  //  private int keyDownMovementValue = KeyEvent.VK_DOWN;

  // Used to record current state of user input 
  private boolean keyLeftMovementPressed;
  private boolean keyRightMovementPressed;
  private boolean keyUpMovementPressed;
  private boolean keyDownMovementPressed;

  public GameInputMovement()
  {
    reset();
  }

  public void pressed(int keyCode)
  {
    //    switch (keyCode)
    //    {
    //      case KeyEvent.VK_UP:
    //        keyUpMovementPressed = true;
    //        break;
    //      case KeyEvent.VK_DOWN:
    //        keyDownMovementPressed = true;
    //        break;
    //      case KeyEvent.VK_LEFT:
    //        keyLeftMovementPressed = true;
    //        break;
    //      case KeyEvent.VK_RIGHT:
    //        keyRightMovementPressed = true;
    //        break;
    //    }

    if (keyCode == KeyEvent.VK_UP)
      keyUpMovementPressed = true;

    if (keyCode == KeyEvent.VK_DOWN)
      keyDownMovementPressed = true;

    if (keyCode == KeyEvent.VK_LEFT)
      keyLeftMovementPressed = true;

    if (keyCode == KeyEvent.VK_RIGHT)
      keyRightMovementPressed = true;

  }

  public void released(int keyCode)
  {
    //    switch (keyCode)
    //    {
    //      case KeyEvent.VK_UP:
    //        keyUpMovementPressed = false;
    //        break;
    //      case KeyEvent.VK_DOWN:
    //        keyDownMovementPressed = false;
    //        break;
    //      case KeyEvent.VK_LEFT:
    //        keyLeftMovementPressed = false;
    //        break;
    //      case KeyEvent.VK_RIGHT:
    //        keyRightMovementPressed = false;
    //        break;
    //    }

    if (keyCode == KeyEvent.VK_UP)
      keyUpMovementPressed = false;

    if (keyCode == KeyEvent.VK_DOWN)
      keyDownMovementPressed = false;

    if (keyCode == KeyEvent.VK_LEFT)
      keyLeftMovementPressed = false;

    if (keyCode == KeyEvent.VK_RIGHT)
      keyRightMovementPressed = false;
  }

  public boolean keyUp()
  {
    return keyUpMovementPressed;
  }

  public boolean keyDown()
  {
    return keyDownMovementPressed;
  }

  public boolean keyLeft()
  {
    return keyLeftMovementPressed;
  }

  public boolean keyRight()
  {
    return keyRightMovementPressed;
  }

  public void reset()
  {
    keyLeftMovementPressed = false;
    keyRightMovementPressed = false;
    keyUpMovementPressed = false;
    keyDownMovementPressed = false;
  }
}
