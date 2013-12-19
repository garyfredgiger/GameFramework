package game.underdevelopment.statemachine;

import java.awt.Graphics;

public interface IGameState
{
  /**
   * First, checks if a transition needs to occur. If it does, the transition is made so that the new state
   * will be executed during the next game loop cycle. If not, the current logic associated with this state
   * will be executed.
   */
  public void updateState();

  /**
   * Draw any items to the screen that are associated with this particular state.
   */
  public void draw(Graphics g);

  /**
   * Initialize any variables used in this state while it is active
   */
  public void initState();

  /**
   * Perform any initializations, clean-up or updates that need to happen before transitioning to the next state.
   */
  public void exitState();

  // Not sure if this is needed?
  public boolean transition();

  // Is this needed since update runs the main code?
  public void executeState();

}
