package game.deprecated;

import game.framework.entities.Entity2D;
import game.framework.primitives.Vector2D;
import game.framework.utilities.GameEngineConstants;
import game.framework.utilities.GameUtility;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @author ggiger
 * 
 *         List of Action Items for this class
 * 
 *         - Separate the game class from the JFrame so the game class can be added to other application types.
 * 
 */
public abstract class GameJFrame extends JFrame
{
  /*
   * Enumerations
   */

  /*
   * Class member variables (i.e., Constants)
   */

  /**
   * 
   */
  private static final long                      serialVersionUID = 1L;

  // Protected variables to be accessed
  protected static GameEngineConstants.GameState state;                                           // current state of the game
  long                                           deltaLoopTime, loopSleepTime;
  /*
   *  Instance variables for the specific game
   */

  /*
   *  This will remain here, but rather than having all entities in one class, it might be better to have a player entity, 
   *  an enemy entity vector, a player shot vector and an enemy shot vector. When collision detection occurs the following 
   *  comparisons will be made:
   *  
   *  Player to all enemies
   *  Player Shot vector to all enemies
   *  Enemy Shot vector to player
   *  Possibly a power-up vector that can be compared to the player 
   */

  // Implicit state variables that are common to all games  
  private boolean                                gamePaused;
  private boolean                                gameRunning;

  // Variables to keep track of the different game entities including enemies, enemy shots, player shots and the player ship
  private Entity2D                                 player;
  private LinkedList<Entity2D>                     enemies;
  private LinkedList<Entity2D>                     playerShots;
  private LinkedList<Entity2D>                     enemyShots;

  // Used for keeping track of time that elapsed between game loop iterations
  long                                           lastLoopTime;
  long                                           currentLoopTime;
  protected double                               delta;

  // Variables to indicate which entity lists to clean up (remove dead entities)
  private boolean                                cleanEnemyShotsEntityList;
  private boolean                                cleanPlayerShotsEntityList;
  private boolean                                cleanEnemiesEntityList;

  // Statistics Variables for game loop
  long                                           numberOfCollisionComparisons;
  private long                                   frameCount;
  private long                                   frameRate;

  // Debug Variables
  protected boolean                              displayDebugInfo = false;

  private DecimalFormat                          nanoFormat       = new DecimalFormat("0.000000");  // this will helps you to always keeps in two decimal places

  /*
   * Section that contains variables for the buffer strategy
   */

  private BufferStrategy                         bs;                                              // Used to render the screen insteasd of the built in paint/paintComponent methods 
  public GameCanvas                              screen;                                          // Handle for the custom drawing panel

  protected int                                  screenWidth = 800, screenHeight = 600;
  
  // Constructor to initialize the UI components and game objects
  public GameJFrame()
  {
    //this.setIgnoreRepaint(true); // Ignore the repainting mechanism for this JFrame since BufferStrategy will be used

    // Initialize the game objects
    // NOTE: This method calls the user defined game init method

    // GAME CLASS: This method is part of the game class 
    gameInit();

    // Setup the JFrame and panel used in this game
    screen = new GameCanvas();
    screen.setPreferredSize(new Dimension(screenWidth, screenHeight));
    //screen.setIgnoreRepaint(true); // Only required if BufferStrategy is used
    this.setContentPane(screen);
    this.pack();
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setTitle("Game Framework");
    this.setVisible(true);

    // Setup the rendering
    // Comment out these if the original paint component is used
    //createBufferStrategy(2);
    //bs = getBufferStrategy();

    // Start the game.
    // NOTE: This method calls the user defined game start method.
    // GAME CLASS: This method is part of the game class 
    gameStart();
  }

  /////////////////////////////////////////////////////////////////////////////
  //      _    _         _                  _     __  __      _   _               _     
  //     / \  | |__  ___| |_ _ __ __ _  ___| |_  |  \/  | ___| |_| |__   ___   __| |___ 
  //    / _ \ | '_ \/ __| __| '__/ _` |/ __| __| | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //   / ___ \| |_) \__ \ |_| | | (_| | (__| |_  | |  | |  __/ |_| | | | (_) | (_| \__ \
  //  /_/   \_\_.__/|___/\__|_|  \__,_|\___|\__| |_|  |_|\___|\__|_| |_|\___/ \__,_|___/                                                                              
  //
  /////////////////////////////////////////////////////////////////////////////

  // GAME CLASS: All methods in this sections are part of the game class 

  // Initializes any game specific logic before the game begins. This method is only executed once.
  public abstract void userGameInit();

  // Contains any game specific logic when the game starts. 
  public abstract void userGameStart();

  // Any logic that needs to occur before the game engine updates specific entities goes in this method
  public abstract void userGamePreUpdate();

  // Any logic that needs to occur when each entity is updated goes here.
  public abstract void userGameUpdateEntity(Entity2D entity);

  // When a collision is detected between two entities, let the user handle the specific collision
  public abstract void userHandleEntityCollision(Entity2D entity1, Entity2D entity2);

  // Allow the user to handle any user specific input during the game loop execution
  public abstract void userProcessInput();

  /*
   * Logic is called from this method to draw anything to the screen before the the entities are drawn.
   *
   * Typically, the current score, player health, number of player lives or current power-ups are some
   * of the items that are drawn to the screen in this method.
   */
  public abstract void userGamePreDraw(Graphics2D g); // Changed from graphics

  public abstract void userGamePostDraw(Graphics2D g);  // Changed from graphics

  public abstract void userGameShutdown();

  /*
   * Methods to handle game specific keyboard input
   */
  public abstract void gameKeyPressed(int keyCode);

  public abstract void gameKeyReleased(int keyCode);

  public abstract void gameKeyTyped(int keyCode);

  /////////////////////////////////////////////////////////////////////////////
  //    ____                        _                      
  //   / ___| __ _ _ __ ___   ___  | |    ___   ___  _ __  
  //  | |  _ / _` | '_ ` _ \ / _ \ | |   / _ \ / _ \| '_ \ 
  //  | |_| | (_| | | | | | |  __/ | |__| (_) | (_) | |_) |
  //   \____|\__,_|_| |_| |_|\___| |_____\___/ \___/| .__/ 
  //                                                |_|    
  //    
  /////////////////////////////////////////////////////////////////////////////

  // GAME CLASS: This method is part of the game class 
  // Run the game loop here.
  private void gameLoop()
  {
    //    // Regenerate and reset the game objects for a new game
    //    if (state == GameEngineConstants.GameState.INTRODUCTION || state == GameEngineConstants.GameState.GAMEOVER)
    //    {
    //      // Abstract method to handle any user game specific logic when the game starts (to be implemented by the subclass).

    gameRunning = true;
    userGameStart();
    //
    //      state = GameEngineConstants.GameState.PLAYING;
    //    }

    // Variables to computer the delta
    lastLoopTime = System.currentTimeMillis();

    // Game loop
    long frameRateCountStartTime = System.currentTimeMillis();
    long beginLoopTime, endLoopTime, timeTaken, timeToUpdate, timeToRender; // deltaLoopTime; //, loopSleepTime;
    while (gameRunning)
    {
      // Required for the game loop delay mechanism
      beginLoopTime = System.nanoTime();

      // Required for the delta
      // TODO: This probably can be combined with the beginTime variable above 
      currentLoopTime = System.currentTimeMillis();
      delta = (currentLoopTime - lastLoopTime) * 0.001; // The 0.001 converts the time in ms to seconds
      lastLoopTime = currentLoopTime;

      //System.out.println("Delta: " + delta);

      // Required to measure the game frame rate. This may not be needed in future versions of this framework or if timing it essential and overhead code needs to be removed.
      frameCount++;
      if (System.currentTimeMillis() > 1000 + frameRateCountStartTime)
      {
        // Keep track of the frame count. This is used primarily for debugging and game loop stats.
        frameRate = frameCount;
        frameCount = 0;

        // Clear any dead entities from the entity lists
        removeDeadEntities();

        frameRateCountStartTime = System.currentTimeMillis();
      }

      if (!gamePaused) // && !gameOver)
      {
        gameUpdate(delta);
        //gameUpdate(1);
      }

      // This is here for benchmarking purposes
      //timeToUpdate = System.nanoTime() - beginLoopTime;

      // Refresh the display

      // GAME CLASS: This can be changed to call specific game class method (possible a method in an interface) that can be called from the respective application framework's draw/render method 
      repaint();
      //render();
      //screen.render();

      endLoopTime = System.nanoTime();

      // Delay timer to provide the necessary delay to meet the target rate
      //timeTaken = endLoopTime - beginLoopTime;
      //timeToRender = timeTaken - timeToUpdate;
      //deltaLoopTime = (long) ((GameEngineConstants.DEFAULT_UPDATE_PERIOD - timeTaken) * 0.000001); // 1000000;  // in milliseconds
      deltaLoopTime = endLoopTime - beginLoopTime;
      //System.out.println("Delta Loop Time: " + nanoFormat.format(deltaLoopTime * 0.000001) + "ms");

      //System.out.println();
      //System.out.println("timeLeft: " + timeLeft + "ms");
      //System.out.println("Time To Update: " + (timeToUpdate * 0.000001) + "ms\t\tTime to Render: " + (timeToRender * 0.000001) + "ms");

      //      if (timeLeft < 5)
      //      {
      //        //System.out.println("TimeLeft less than 10ms");
      //        timeLeft = 5;  // set a minimum
      //      }

      if (deltaLoopTime > GameEngineConstants.DEFAULT_UPDATE_PERIOD)
      {
        System.out.println("Too Late");
      }
      else
      {
        try
        {
          loopSleepTime = (long) ((GameEngineConstants.DEFAULT_UPDATE_PERIOD - deltaLoopTime) * 0.000001);
          //System.out.println("loopSleepTime : " + nanoFormat.format(loopSleepTime));
          // Provides the necessary delay and also yields control so that other thread can do work.
          //Thread.sleep(deltaLoopTime);
          Thread.sleep(loopSleepTime);
          //Thread.sleep(1);
        }
        catch (InterruptedException ex)
        {}
      }
    }

    // GAME CLASS: This method is part of the game class 
    gameShutdown();
  }

  public long getCurrentLoopTime()
  {
    return currentLoopTime;
  }

  /////////////////////////////////////////////////////////////////////////////
  //    ____                        _                      
  //   / ___| __ _ _ __ ___   ___  | |    ___   ___  _ __  
  //  | |  _ / _` | '_ ` _ \ / _ \ | |   / _ \ / _ \| '_ \ 
  //  | |_| | (_| | | | | | |  __/ | |__| (_) | (_) | |_) |
  //   \____|\__,_|_| |_| |_|\___| |_____\___/ \___/| .__/ 
  //                                                |_|    
  //   __  __      _   _               _     
  //  |  \/  | ___| |_| |__   ___   __| |___ 
  //  | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //  | |  | |  __/ |_| | | | (_) | (_| \__ \
  //  |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
  //                                         
  /////////////////////////////////////////////////////////////////////////////

  // GAME CLASS: This method is part of the game class 
  /*
   *  Initialize all the game components, run only once in the constructor of the main class.
   */
  public void gameInit()
  {
    /*
     *  Initialize all entities
     */
    player = new Entity2D(GameEngineConstants.EntityTypes.PLAYER);
    player.setPosition(screenWidth / 2, screenHeight / 2);
    player.setVelocity(0.0, 0.0);
    player.setAlive(false);
    player.setVisible(false);

    enemies = new LinkedList<Entity2D>();
    playerShots = new LinkedList<Entity2D>();
    enemyShots = new LinkedList<Entity2D>();

    /*
     *  Set default values
     */

    // The game is started as un-paused
    unpauseGame();

    // The game is started in the INTRODUCTION state
    state = GameEngineConstants.GameState.INTRODUCTION;

    // Clear all flags that indicated if entity lists should be cleared.
    cleanEnemyShotsEntityList = false;
    cleanPlayerShotsEntityList = false;
    cleanEnemiesEntityList = false;

    /*
     * Call user defined init method
     */
    userGameInit();
  }

  // GAME CLASS: This method is part of the game class 
  /*
   *  To start the game.
   *  This method is called from within the constructor of this class
   */
  public void gameStart()
  {
    // Create a new thread
    Thread gameThread = new Thread()
    {
      // Override run() to provide the running behavior of this thread.
      @Override
      public void run()
      {
        gameLoop();
      }
    };

    // Start the thread. start() calls run(), which in turn calls gameLoop().
    gameThread.start();
  }

  // GAME CLASS: This method is part of the game class 
  /*
   *  Shutdown the game, clean up code that runs only once.
   */
  public void gameShutdown()
  {
    userGameShutdown();

    System.exit(0);
  }

  // GAME CLASS: This method is part of the game class 
  /*
   *  Update the state and position of all the game objects, detect collisions and provide responses.
   */
  public void gameUpdate(double delta)
  {
    /*
     *  Process user input
     */
    userProcessInput();

    /*
     * Pre-update
     */

    // Execute any logic that needs to happen before all entity lists are updated
    // Entity Logic updates can go here including state updates, velocity changes or reactions to user input 
    userGamePreUpdate();

    /*
     * Update the positions for the different entity types 
     */

    // TODO: Since these loops all seem to do the same actions, but on different lists, consider moving the logic to a method to help clean up the code. 

    // Update positions for the enemies list
    for (int i = 0; i < enemies.size(); i++)
    {
      Entity2D currentEntity = enemies.get(i);
      currentEntity.updatePosition(delta);
      currentEntity.updateRotation(delta);
      currentEntity.updateLifetime();

      // Call the user defined method to perform any specific updates on each sprites
      userGameUpdateEntity(currentEntity);
    }

    // Update positions for the players shots
    for (int i = 0; i < playerShots.size(); i++)
    {
      // Update the entity
      Entity2D currentEntity = playerShots.get(i);
      currentEntity.updatePosition(delta);
      currentEntity.updateRotation(delta);
      currentEntity.updateLifetime();

      // Call the user defined method to perform any specific updates on each sprites
      userGameUpdateEntity(currentEntity);
    }

    // Update positions for the enemy shots
    for (int i = 0; i < enemyShots.size(); i++)
    {
      // Update the entity
      Entity2D currentEntity = enemyShots.get(i);
      currentEntity.updatePosition(delta);
      currentEntity.updateRotation(delta);
      currentEntity.updateLifetime();

      // Call the user defined method to perform any specific updates on each sprites
      userGameUpdateEntity(currentEntity);
    }

    /*
     * Update the player
     */
    player.updatePosition(delta);
    player.updateRotation(delta);
    player.updateLifetime();
    userGameUpdateEntity(player);

    // TODO: Add a post-update method that is called before each entity list is processed. The method can be called userGamePostUpdate

    /*
     * Detect collisions
     */
    gameDetectCollisions();
  }

  // NOTE: This method should be called in the respective application type's render method.
  // GAME CLASS: This method is part of the game class 
  //  public void render()
  //  {
  //    do
  //    {
  //      // Get hold of a graphics context for the accelerated 
  //      // surface and blank it out
  //      Graphics2D g = (Graphics2D) bs.getDrawGraphics();
  //
  //      // NOTE: This needs to be here in case the buffer is not cleared in implemented sub methods 
  //      g.setColor(GameEngineConstants.DEFAULT_BACKGROUND_COLOR);
  //      g.fillRect(0, 0, GameEngineConstants.DEFAULT_CANVAS_WIDTH, GameEngineConstants.DEFAULT_CANVAS_HEIGHT);
  //
  //      // TODO: Determine if a paused condition should go here?
  //      // NOTE: Same as drawSprites() method in original GalacticWar game 
  //      gameDraw(g);
  //
  //      // finally, we've completed drawing so clear up the graphics
  //      // and flip the buffer over
  //      g.dispose();
  //    }
  //    while (bs.contentsRestored());
  //
  //    // Show the buffer
  //    bs.show();
  //
  //    // Documentation says to do this to prevent tearing. It seems to work.
  //    Toolkit.getDefaultToolkit().sync();
  //  }

  // GAME CLASS: This method is part of the game class 
  public void gameDetectCollisions()
  {
    /*
     *  First, compare the player shot vector with all enemies to check for any collisions
     *  
     *  NOTE: This is put before the check if the player is dead so even after the player dies,
     *        any remaining player shots can still strike the enemy.
     */
    for (int playerShotIndex = 0; playerShotIndex < playerShots.size(); playerShotIndex++)
    {
      Entity2D currentPlayerShot = (Entity2D) playerShots.get(playerShotIndex);

      if (currentPlayerShot.isAlive())
      {
        // Second, compare the player shot vector with all enemies to check for any collisions
        for (int enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++)
        {
          Entity2D currentEnemy = (Entity2D) enemies.get(enemyIndex);

          if (currentEnemy.isAlive())
          {
            if (currentPlayerShot.collidesWith(currentEnemy.getBoundingRectangle()))
            {
              // Given this collision, both the current player shot and current enemy should be marked as "killed" in the user defined method, but it does not have to.
              userHandleEntityCollision(currentPlayerShot, currentEnemy);
            }
          }
        }
      }
    }

    // If the player is dead, do nothing
    if (!player.isAlive())
    {
      return;
    }

    /*
     * Second, Compare the enemy shot vector with the player to check for any collisions
     */
    for (int enemyShotIndex = 0; enemyShotIndex < enemyShots.size(); enemyShotIndex++)
    {
      Entity2D currentEnemyShot = (Entity2D) enemyShots.get(enemyShotIndex);

      if (currentEnemyShot.isAlive())
      {
        // If the player collides with an enemy shot, exit this method after processing the collision since there is no point processing any further collisions 
        if (player.collidesWith(currentEnemyShot.getBoundingRectangle()))
        {
          userHandleEntityCollision(player, currentEnemyShot);
          return;
        }
      }
    }

    /*
     *  Last, compare the player sprite with all enemies to check for any collisions
     */
    for (int enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++)
    //for (int enemyIndex = 0; enemyIndex < entities.size(); enemyIndex++)
    {
      Entity2D currentEnemy = (Entity2D) enemies.get(enemyIndex);
      //Entity currentEnemy = (Entity) entities.get(enemyIndex);

      if (currentEnemy.isAlive())
      {
        // If the player collides with 
        if (player.collidesWith(currentEnemy.getBoundingRectangle()))
        {
          userHandleEntityCollision(player, currentEnemy);
          break;
        }
      }
    }
  }

  // GAME CLASS: This method is part of the game class 
  // Refresh the display. Called back via repaint(), which invoke the paintComponent().

  // GARY NOTE: Make this method draw the complete sprite list and call an abstract method that allows 
  //            the user to perform game specific sprite drawing that can be implemented in the sub class.
  //            The abstract method will be called userGameDraw()
  private void gameDraw(Graphics2D g) // Changed from graphics
  {
    // Clear the background to avoid trailers
    setBackground(GameEngineConstants.DEFAULT_BACKGROUND_COLOR);  // may use an image for background
    g.fillRect(0, 0, screenWidth, screenHeight);

    /*
     * Draw any user specific items before the entity lists are drawn to the screen 
     */
    userGamePreDraw(g);

    /*
     * Draw debug info
     */
    if (displayDebugInfo)
    {
      displayDebugInfo(g);
    }

    /*
     *  Draw the entities
     */

    // Draw the enemy shots 
    for (int i = 0; i < enemyShots.size(); i++)
    {
      Entity2D currentEnemyShot = enemyShots.get(i);
      currentEnemyShot.draw(g);
    }

    // Draw the enemies 
    for (int i = 0; i < enemies.size(); i++)
    {
      Entity2D currentEntity = enemies.get(i);
      currentEntity.draw(g);
    }

    // Draw the player shots 
    for (int i = 0; i < playerShots.size(); i++)
    {
      Entity2D currentPlayerShot = playerShots.get(i);
      currentPlayerShot.draw(g);
    }

    // Draw the player
    player.draw(g);

    userGamePostDraw(g);
  }

  public void exitGame()
  {
    gameRunning = false;
  }

  /////////////////////////////////////////////////////////////////////////////
  //    ____                        ____                      
  //   / ___| __ _ _ __ ___   ___  |  _ \ __ _ _   _ ___  ___ 
  //  | |  _ / _` | '_ ` _ \ / _ \ | |_) / _` | | | / __|/ _ \
  //  | |_| | (_| | | | | | |  __/ |  __/ (_| | |_| \__ \  __/
  //   \____|\__,_|_| |_| |_|\___| |_|   \__,_|\__,_|___/\___|
  //                                                          
  //   __  __      _   _               _     
  //  |  \/  | ___| |_| |__   ___   __| |___ 
  //  | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //  | |  | |  __/ |_| | | | (_) | (_| \__ \
  //  |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
  //                                         
  /////////////////////////////////////////////////////////////////////////////

  public void pauseGame()
  {
    gamePaused = true;
  }

  public boolean isGamePaused()
  {
    return gamePaused;
  }

  public void unpauseGame()
  {
    gamePaused = false;
  }

  /////////////////////////////////////////////////////////////////////////////
  //    ____                         ___                 
  //   / ___| __ _ _ __ ___   ___   / _ \__   _____ _ __ 
  //  | |  _ / _` | '_ ` _ \ / _ \ | | | \ \ / / _ \ '__|
  //  | |_| | (_| | | | | | |  __/ | |_| |\ V /  __/ |   
  //   \____|\__,_|_| |_| |_|\___|  \___/  \_/ \___|_|   
  //                                                     
  //   __  __      _   _               _     
  //  |  \/  | ___| |_| |__   ___   __| |___ 
  //  | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //  | |  | |  __/ |_| | | | (_) | (_| \__ \
  //  |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
  //                                         
  /////////////////////////////////////////////////////////////////////////////

  //  public void setGameOver()
  //  {
  //    gameOver = true;
  //  }
  //
  //  public boolean isGameOver()
  //  {
  //    return gameOver;
  //  }
  //
  //  public void clearGameOver()
  //  {
  //    gameOver = false;
  //  }

  /////////////////////////////////////////////////////////////////////////////
  //   _____       _   _ _           __  __      _   _               _     
  //  | ____|_ __ | |_(_) |_ _   _  |  \/  | ___| |_| |__   ___   __| |___ 
  //  |  _| | '_ \| __| | __| | | | | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //  | |___| | | | |_| | |_| |_| | | |  | |  __/ |_| | | | (_) | (_| \__ \
  //  |_____|_| |_|\__|_|\__|\__, | |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
  //                         |___/                                         
  //
  /////////////////////////////////////////////////////////////////////////////

  /*
   * Set a new player Entity
   */
  public void setNewPlayerEntity(Entity2D player)
  {
    this.player = (Entity2D) player.clone();
    this.player.setEntityType(GameEngineConstants.EntityTypes.PLAYER);
  }

  /*
   * Add entities
   */
  public void addEnemy(Entity2D entity)
  {
    addEntity(entity, GameEngineConstants.EntityTypes.ENEMY, enemies);
  }

  public void addPlayerShot(Entity2D entity)
  {
    addEntity(entity, GameEngineConstants.EntityTypes.PLAYER_SHOT, playerShots);
  }

  public void addEnemyShot(Entity2D entity)
  {
    addEntity(entity, GameEngineConstants.EntityTypes.ENEMY_SHOT, enemyShots);
  }

  private void addEntity(Entity2D entity, GameEngineConstants.EntityTypes type, LinkedList<Entity2D> entityList)
  {
    // Set the type of the enemy
    entity.setEntityType(type);

    // Add the enemy to the list
    entityList.add(entity);
  }

  /*
   * Get the different entity lists
   */

  public Entity2D getPlayer()
  {
    return player;
  }

  public LinkedList<Entity2D> getEnemies()
  {
    return enemies;
  }

  public LinkedList<Entity2D> getEnemyShots()
  {
    return enemyShots;
  }

  public LinkedList<Entity2D> getPlayerShot()
  {
    return playerShots;
  }

  /*
   * Clear the different entity lists
   */
  public void clearEnemies()
  {
    enemies.clear();
  }

  public void clearEnemyShot()
  {
    enemyShots.clear();
  }

  public void clearPlayerShot()
  {
    playerShots.clear();
  }

  public void resetEntityLists()
  {
    clearEnemies();
    clearEnemyShot();
    clearPlayerShot();
  }

  /*
   * Methods responsible for clearing dead entities from entity lists 
   */

  public void removeDeadEnemiesFromEntityList()
  {
    cleanEnemiesEntityList = true;
  }

  public void doNotRemoveDeadEnemiesFromEntityList()
  {
    cleanEnemiesEntityList = false;
  }

  public void removeDeadEnemyShotsFromEntityList()
  {
    cleanEnemyShotsEntityList = true;
  }

  public void doNotRemoveDeadEnemyShotsFromEntityList()
  {
    cleanEnemyShotsEntityList = false;
  }

  public void removeDeadPlayerShotsFromEntityList()
  {
    cleanPlayerShotsEntityList = true;
  }

  public void toNotRemoveDeadPlayerShotsFromEntityList()
  {
    cleanPlayerShotsEntityList = false;
  }

  // This method is called from within the main game loop
  private void removeDeadEntities()
  {
    if (cleanEnemyShotsEntityList)
    {
      removeDeadEntitiesFromEntityList(enemyShots);
    }

    if (cleanPlayerShotsEntityList)
    {
      removeDeadEntitiesFromEntityList(playerShots);
    }

    if (cleanEnemiesEntityList)
    {
      removeDeadEntitiesFromEntityList(enemies);
    }
  }

  /*
   * NOTE: I decided to remove the dead entities from the linked list as shown in the links below
   * 
   *  Removing elements from a List
   *  http://stackoverflow.com/questions/8174964/java-using-iterator-to-search-an-arraylist-and-delete-matching-objects
   *  
   *  Discusses how iteratpr.remove is the safest way to remove elements from a list while iterating over it 
   *  http://stackoverflow.com/questions/223918/efficient-equivalent-for-removing-elements-while-iterating-the-collection
   *  
   *  Comments in this link mentions how iterator.remove is safe and the most efficient.
   *  http://java67.blogspot.com/2012/12/difference-between-arraylist-vs-LinkedList-java.html
   *  
   */
  private void removeDeadEntitiesFromEntityList(LinkedList<Entity2D> entities)
  {
    Iterator<Entity2D> entityIterator = entities.iterator();

    while (entityIterator.hasNext())
    {
      if (!entityIterator.next().isAlive())
      {
        entityIterator.remove();
      }
    }

    // The old way that was used to remove the dead entities, but apparently not as safe as the iterator method.
    //    for (int n = 0; n < entities.size(); n++)
    //    {
    //      Entity spr = (Entity) entities.get(n);
    //      if (!spr.isAlive())
    //      {
    //        entities.remove(n);
    //      }
    //    }
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

  private void displayDebugInfo(Graphics2D g) // Changed from graphics
  {
    int line = 300;
    DecimalFormat probf = new DecimalFormat("0.000000000");  // this will helps you to always keeps in two decimal places
    //  g.drawString("UFO Probability: " + probf.format(ufoEntityManager.getProbability()), 560, line);

    g.setFont(new Font("Dialog", Font.PLAIN, 14));
    g.setColor(Color.WHITE);
    g.drawString("Frame Rate: " + frameRate, 20, line);
    line += 16;
    g.drawString("Num Enemies: " + enemies.size(), 20, line);
    line += 16;
    g.drawString("Num Enemy Shots: " + enemyShots.size(), 20, line);
    line += 16;
    g.drawString("Num Player Shots: " + playerShots.size(), 20, line);
    line += 16;
    g.drawString("Num Comparisons: " + numberOfCollisionComparisons, 20, line);
    line += 16;
    g.drawString("Delta: " + delta, 20, line);
    line += 16;
    g.drawString("DEFAULT_UPDATE_PERIOD: " + GameEngineConstants.DEFAULT_UPDATE_PERIOD, 20, line);
    line += 16;
    g.drawString("Delta Loop Time: " + probf.format(deltaLoopTime * 0.000001), 20, line);
    line += 16;
    g.drawString("Loop Sleep Time: " + loopSleepTime, 20, line);
  }

  /////////////////////////////////////////////////////////////////////////////
  //   ___                          ____ _               
  //  |_ _|_ __  _ __   ___ _ __   / ___| | __ _ ___ ___ 
  //   | || '_ \| '_ \ / _ \ '__| | |   | |/ _` / __/ __|
  //   | || | | | | | |  __/ |    | |___| | (_| \__ \__ \
  //  |___|_| |_|_| |_|\___|_|     \____|_|\__,_|___/___/
  //                                                     
  //   ____                                     _   _             
  //  |  _ \ ___ _ __  _ __ ___  ___  ___ _ __ | |_(_)_ __   __ _ 
  //  | |_) / _ \ '_ \| '__/ _ \/ __|/ _ \ '_ \| __| | '_ \ / _` |
  //  |  _ <  __/ |_) | | |  __/\__ \  __/ | | | |_| | | | | (_| |
  //  |_| \_\___| .__/|_|  \___||___/\___|_| |_|\__|_|_| |_|\__, |
  //            |_|                                         |___/ 
  //       _ ____                  _ 
  //      | |  _ \ __ _ _ __   ___| |
  //   _  | | |_) / _` | '_ \ / _ \ |
  //  | |_| |  __/ (_| | | | |  __/ |
  //   \___/|_|   \__,_|_| |_|\___|_|
  //                                 
  /////////////////////////////////////////////////////////////////////////////

  //Custom drawing panel, written as an inner class.
  public class GameCanvas extends JPanel implements KeyListener
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // Constructor
    public GameCanvas()
    {
      setFocusable(true);  // so that can receive key-events
      requestFocus();
      addKeyListener(this);
    }

    // Override paintComponent to do custom drawing.
    // Called back by repaint().
    @Override
    public void paintComponent(Graphics g)
    {
      super.paintComponent(g);   // paint background

      // Draw the game objects
      gameDraw((Graphics2D) g);

      Toolkit.getDefaultToolkit().sync();
    }

    //    public void render()
    //    {
    //      do
    //      {
    //        // Get hold of a graphics context for the accelerated 
    //        // surface and blank it out
    //        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
    //
    //        // NOTE: This needs to be here in case the buffer is not cleared in implemented sub methods 
    //        g.setColor(GameEngineConstants.DEFAULT_BACKGROUND_COLOR);
    //        g.fillRect(0, 0, GameEngineConstants.DEFAULT_CANVAS_WIDTH, GameEngineConstants.DEFAULT_CANVAS_HEIGHT);
    //
    //        //        g.setColor(Color.RED);
    //        //        g.drawRect(0, 0, GameEngineConstants.DEFAULT_CANVAS_WIDTH, GameEngineConstants.DEFAULT_CANVAS_HEIGHT);
    //
    //        // TODO: Determine if a paused condition should go here?
    //        // NOTE: Same as drawSprites() method in original GalacticWar game 
    //        gameDraw(g);
    //
    //        // finally, we've completed drawing so clear up the graphics
    //        // and flip the buffer over
    //        g.dispose();
    //      }
    //      while (bs.contentsRestored());
    //
    //      // Show the buffer
    //      bs.show();
    //
    //      // Documentation says to do this to prevent tearing. It seems to work.
    //      Toolkit.getDefaultToolkit().sync();
    //    }

    /////////////////////////////////////////////////////////////////////////////
    //   _  __            _____                 _   
    //  | |/ /___ _   _  | ____|_   _____ _ __ | |_ 
    //  | ' // _ \ | | | |  _| \ \ / / _ \ '_ \| __|
    //  | . \  __/ |_| | | |___ \ V /  __/ | | | |_ 
    //  |_|\_\___|\__, | |_____| \_/ \___|_| |_|\__|
    //            |___/                             
    //   _   _                 _ _               
    //  | | | | __ _ _ __   __| | | ___ _ __ ___ 
    //  | |_| |/ _` | '_ \ / _` | |/ _ \ '__/ __|
    //  |  _  | (_| | | | | (_| | |  __/ |  \__ \
    //  |_| |_|\__,_|_| |_|\__,_|_|\___|_|  |___/
    //                                             
    /////////////////////////////////////////////////////////////////////////////

    // KeyEvent handlers
    @Override
    public void keyPressed(KeyEvent e)
    {
      gameKeyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
      // Process Debug key    
      if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE && e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK)
      {
        displayDebugInfo = !displayDebugInfo;

        if (displayDebugInfo)
        {
          System.out.println("Debugging Enabled.");
        }
        else
        {
          System.out.println("Debugging Disabled.");
        }
      }

      gameKeyReleased(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
      gameKeyTyped(e.getKeyCode());
    }
  }

  //  // main
  //  public static void main(String[] args)
  //  {
  //    // Use the event dispatch thread to build the UI for thread-safety.
  //    SwingUtilities.invokeLater(new Runnable()
  //    {
  //      @Override
  //      public void run()
  //      {
  //        new GameJFrame();
  //      }
  //    });
  //  }
}
