package game.framework.utilities;

import java.awt.Color;

public class GameEngineConstants
{
  /*
   * Constants for the game
   */
  public static final int   DEFAULT_CANVAS_WIDTH     = 800;                              // width and height of the game screen
  public static final int   DEFAULT_CANVAS_HEIGHT    = 600;
  public static final int   DEFAULT_UPDATE_RATE      = 100;                               // number of game update per second
  public static final long  DEFAULT_UPDATE_PERIOD    = 1000000000L / DEFAULT_UPDATE_RATE;                                                                                                                          // nanoseconds
  public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;

  /*
   * Constants for entities
   */
  public static final int   DEFAULT_ENTITY_WIDTH     = 16;
  public static final int   DEFAULT_ENTITY_HEIGHT    = 16;

  // Represents the states of the game.
  public static enum GameState
  {
    INTRODUCTION, GAME_START, PLAYING, PAUSED, GAMEOVER, LEVEL_NEXT, PLAYER_DEAD, EXIT_PLAYING_GAME, EXIT_GAME, UNDEFINED
  }

  public static enum EntityTypes
  {
    PLAYER("Player"), PLAYER_SHOT("Player Shot"), ENEMY("Enemy"), ENEMY_SHOT("Enemy Shot"), POWER_UP("Power Up"), UNDEFINED("Undefined");

    private String label;

    EntityTypes(String label)
    {
      this.label = label;
    }

    public String toString()
    {
      return label;
    }
  }

  public static enum EntityState
  {
    NORMAL, COLLIDED, EXPLODING
  }
}
