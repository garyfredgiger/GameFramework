package game.framework;

import game.framework.entities.Entity;
import game.framework.interfaces.IRender;
import game.framework.utilities.GameEngineConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;

/*
 * 
 * "We should forget about small efficiencies, say about 97% of the time: premature optimization 
 * is the root of all evil." The performance of instanceof probably won't be an issue, so don't 
 * waste your time coming up with exotic workarounds until you're sure that's the problem.
 * 
 *                                                            - Donald Knuth
 */

public abstract class GameEngine
{
  /*
   *  Instance variables for the game engine.
   */

  protected GameEngineConstants.GameState state;                                              // current state of the game
  protected long                          updateAndRenderLoopTime,
      loopSleepTime;

  // Implicit state variables that are common to all games  
  private boolean                         gamePaused;
  private boolean                         gameRunning;

  // Variables to keep track of the different game entities including enemies, enemy shots, player shots and the player ship
  private Entity                          player;
  private LinkedList<Entity>              enemies;
  private LinkedList<Entity>              playerShots;
  private LinkedList<Entity>              enemyShots;
  private LinkedList<Entity>              powerups;
  
  // TODO: Possibly add a power-up entity list. 

  // Used for keeping track of time (in ms) that elapsed between game loop iterations
  long                                    lastLoopTime;
  long                                    currentLoopTime;
  protected double                        delta;

  // Flags to indicate which entity lists to clean up (remove dead entities) during game play.
  private boolean                         cleanEnemyShotsEntityList;
  private boolean                         cleanPlayerShotsEntityList;
  private boolean                         cleanEnemiesEntityList;
  private boolean                         cleanPowerupsEntityList;

  // Statistics variables for game loop
  private long                            frameCount;                                         // Tracks the number of frames that occur each second  
  private long                            frameRate;                                          // used to display the current frame rate when debugging is enabled

  /*
   * Used to render the screen.
   *
   * NOTE: An instance of the interface IRender, which contains the method renderScreen(). This 
   *       method, invoked by the game loop , is responsible for rendering each frame. Since the 
   *       game engine does not know the type of Java application in which it will be used, it
   *       does not know how to render a game frame. This is why an interface is used in this 
   *       case. The game engine knows that when it calls the interface method renderScreen(),
   *       the current frame of the game loop will be rendered to the screen without knowing how 
   *       the rendering will happen.
   */
  private IRender                         screenRenderer;
  protected int                           screenWidth, screenHeight;

  // Debug Variables
  public boolean                          displayDebugInfo = false;
  private DecimalFormat                   decimalPlaces9   = new DecimalFormat("0.000000000");

  public GameEngine(IRender renderer)
  {
    this(renderer, GameEngineConstants.DEFAULT_CANVAS_WIDTH, GameEngineConstants.DEFAULT_CANVAS_HEIGHT);
  }

  public GameEngine(IRender renderer, int userDefinedScreenWidth, int userDefinedScreenHeight)
  {
    screenRenderer = renderer;
    
    // Check that the default screen width and height are not zero or negative
    if ((userDefinedScreenWidth <= 0) || (userDefinedScreenHeight <= 0))
    {
      // If either screen dimensions are either 0 or negative, then use the default screen width and height.
      System.out.println("Warning: Either the user defined screen width or height is zero or negative (" + userDefinedScreenWidth + ", " + userDefinedScreenHeight + "). Reverting to default screen width and height (" + GameEngineConstants.DEFAULT_CANVAS_WIDTH + ", " + GameEngineConstants.DEFAULT_CANVAS_HEIGHT+ ").");

      screenWidth = GameEngineConstants.DEFAULT_CANVAS_WIDTH;
      screenHeight = GameEngineConstants.DEFAULT_CANVAS_HEIGHT;
    }
    else
    {
      screenWidth = userDefinedScreenWidth;
      screenHeight = userDefinedScreenHeight;
    }
  }
  
  /////////////////////////////////////////////////////////////////////////////
  //      _    _         _                  _     __  __      _   _               _     
  //     / \  | |__  ___| |_ _ __ __ _  ___| |_  |  \/  | ___| |_| |__   ___   __| |___ 
  //    / _ \ | '_ \/ __| __| '__/ _` |/ __| __| | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
  //   / ___ \| |_) \__ \ |_| | | (_| | (__| |_  | |  | |  __/ |_| | | | (_) | (_| \__ \
  //  /_/   \_\_.__/|___/\__|_|  \__,_|\___|\__| |_|  |_|\___|\__|_| |_|\___/ \__,_|___/                                                                              
  //
  /////////////////////////////////////////////////////////////////////////////

  // Initializes any game specific logic before the game begins. This method is only executed once.
  public abstract void userGameInit();

  // Contains any game specific logic when the game starts. 
  public abstract void userGameStart();

  // Any logic that needs to occur before the game engine updates specific entities goes in this method
  public abstract void userGamePreUpdate();

  // Any logic that needs to occur when each entity is updated goes here.
  public abstract void userGameUpdateEntity(Entity entity);

  // When a collision is detected between two entities, let the user handle the specific collision
  public abstract void userHandleEntityCollision(Entity entity1, Entity entity2);

  // Allow the user to handle any user specific input during the game loop execution
  public abstract void userProcessInput();

  /*
   * Logic is called from this method to draw anything to the screen before the the entities are drawn.
   *
   * Typically, the current score, player health, number of player lives or current power-ups are some
   * of the items that are drawn to the screen in this method.
   */
  public abstract void userGamePreDraw(Graphics2D g);

  public abstract void userGamePostDraw(Graphics2D g);

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

  /*
   * The main game loop for this game engine
   */
  private void gameLoop()
  {
    gameRunning = true;

    // Call the user game start method to execute any user specific logic before the game loop begins   
    userGameStart();

    // Used to determine amount of time that update and render operations took so the appropriate sleep time can be computed for current game loop iteration.
    long beginLoopTime, endLoopTime;

    // Game loop
    long frameRateCountStartTime = System.currentTimeMillis();
    lastLoopTime = System.currentTimeMillis();      // Used to computer the delta
    while (gameRunning)
    {
      // Record the start time of the current loop iteration. This will be used to compute the time taken for the 
      // update and render operations, which will then be used to determine the amount of time the current loop
      // iteration will sleep in order to maintain the fixed frame rate.
      beginLoopTime = System.nanoTime();

      // This section records the amount of time that has elapsed since the previous loop iteration was executed.
      // The result (called the delta) will be used in updating each entity's position to ensure smoother animations
      // if the time between loop iterations varies greatly.
      currentLoopTime = System.currentTimeMillis();
      delta = (currentLoopTime - lastLoopTime) * GameEngineConstants.MILLI_TO_BASE; // The 0.001 converts the time in ms to seconds
      lastLoopTime = currentLoopTime;

      // This section serves two purposes, 1) measure the games frame rate (mostly used for debugging purposes
      // during game development) and 2) to remove any dead entities from each of the entity lists once every second 
      frameCount++;
      if (System.currentTimeMillis() > 1000 + frameRateCountStartTime)
      {
        // Keep track of the frame count. This is used primarily for debugging and game loop statistics.
        frameRate = frameCount;
        frameCount = 0;

        // Clear any dead entities from the entity lists
        // NOTE: For this to happen the programmer must set the clean enemies, clean player shots and clean enemy 
        //       shots flags using the respective methods. If not, as game play progresses the speed of the game 
        //       may be severely slowed down since the engine will need to iterate through the different entity 
        //       lists that may contain dead entities. 
        removeDeadEntities();

        // Reset the frame rate count start time
        frameRateCountStartTime = System.currentTimeMillis();
      }

      /*
       * Process user input
       */
      userProcessInput();

      /*
       * Pre-update
       */
      userGamePreUpdate();

      // Plain and simple, only update the entities if the game in not paused
      if (!gamePaused)
      {
        gameUpdate(delta);
      }

      // Update the display with the current entity lists 
      screenRenderer.renderScreen();

      // Record the end time of the current loop iteration and compute the time taken for the update and render operations.
      // This will be used to determine the amount of time the current loop will need to sleep in order to maintain the 
      // fixed frame rate for the game.
      endLoopTime = System.nanoTime();
      updateAndRenderLoopTime = endLoopTime - beginLoopTime;

      // If the time taken to update and render took too long (i.e., is greater than the default update period) then skip 
      // sleeping this game loop iteration. 
      if (updateAndRenderLoopTime > GameEngineConstants.DEFAULT_UPDATE_PERIOD)
      {
        // This is an indication I left in during development to alert me if the game loop is taking too much time for the 
        // render and update operations. This may be commented out when your game is ready for production releases.
        System.out.println("Update and Render Took Too Much Time, Skipping sleeping this game loop iteration.");
      }
      else
      {
        try
        {
          // Compute the amount of time to sleep. Note that the measured time is in nano seconds and must be converted to milliseconds for the sleep operation.
          loopSleepTime = (long) ((GameEngineConstants.DEFAULT_UPDATE_PERIOD - updateAndRenderLoopTime) * GameEngineConstants.NANO_TO_MILLI);

          // Provides the necessary delay and also yields control so that other threads can do work.
          Thread.sleep(loopSleepTime);
        }
        catch (InterruptedException ex)
        {
          // NOTE: If an exception occurs, do nothing.
        }
      }
    }

    gameShutdown();
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

  // DONE
  /*
   *  Initialize all the game components, run only once in the constructor of the class that contains the instance of this game.
   */
  public void gameInit()
  {
    /*
     *  Initialize all entities
     */
    player = new Entity(GameEngineConstants.EntityTypes.PLAYER);
    player.setPosition(screenWidth / 2, screenHeight / 2);
    player.setVelocity(0.0, 0.0);
    player.setAlive(false);
    player.setVisible(false);

    enemies = new LinkedList<Entity>();
    playerShots = new LinkedList<Entity>();
    enemyShots = new LinkedList<Entity>();
    powerups = new LinkedList<Entity>();

    /*
     *  Set default values
     */

    // The game is started as un-paused
    unpauseGame();

    // The game is started in the INTRODUCTION state
    state = GameEngineConstants.GameState.INTRODUCTION;

    // Clear all flags that indicating which entity lists should be cleared.
    cleanEnemyShotsEntityList = false;
    cleanPlayerShotsEntityList = false;
    cleanEnemiesEntityList = false;

    /*
     * Call user defined initialization method to execute any user logic needed for game initilization.
     */
    userGameInit();
  }

  // DONE
  /*
   *  Start the game loop.
   *  NOTE: This method is called from within the constructor of the class that contains an instance of this class
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

  // DONE
  /*
   *  Shutdown the game, clean up code that runs only once.
   */
  public void gameShutdown()
  {
    // Call the user defined method to execute any specific logic relevant to the game shutting down before it exits
    userGameShutdown();

    // TODO: Determine if this should still be called if this game class is inside of a japplet or applet
    System.exit(0);
  }

  // DONE
  /*
  *  Update the state and position of all the game objects, detect collisions and provide responses.
  */
  public void gameUpdate(double delta)
  {
    /*
    * Pre-update
    */

    // Execute any logic that needs to happen before all entity lists are updated
    // Entity Logic updates can go here including state updates, velocity changes or reactions to user input 
    //    userGamePreUpdate();

    /*
    * Update the positions for the different entity types 
    */

    // TODO: Since these loops all seem to do the same actions, but on different lists, consider moving the logic to a method to help clean up the code. 

    // Update positions for the enemies list
    for (int i = 0; i < enemies.size(); i++)
    {
      Entity currentEntity = enemies.get(i);
      currentEntity.updatePosition(delta);
      currentEntity.updateRotation(delta);
      currentEntity.updateLifetime();

      // Call the user defined method to perform any specific updates on each sprite
      userGameUpdateEntity(currentEntity);
    }

    // Update positions for the players shots
    for (int i = 0; i < playerShots.size(); i++)
    {
      // Update the entity
      Entity currentEntity = playerShots.get(i);
      currentEntity.updatePosition(delta);
      currentEntity.updateRotation(delta);
      currentEntity.updateLifetime();

      // Call the user defined method to perform any specific updates on each sprite
      userGameUpdateEntity(currentEntity);
    }

    // Update positions for the enemy shots
    for (int i = 0; i < enemyShots.size(); i++)
    {
      // Update the entity
      Entity currentEntity = enemyShots.get(i);
      currentEntity.updatePosition(delta);
      currentEntity.updateRotation(delta);
      currentEntity.updateLifetime();

      // Call the user defined method to perform any specific updates on each sprite
      userGameUpdateEntity(currentEntity);
    }

    // Update positions for the powerups 
    for (int i = 0; i < powerups.size(); i++)
    {
      // Update the entity
      Entity currentPowerup = powerups.get(i);
      currentPowerup.updatePosition(delta);
      currentPowerup.updateRotation(delta);
      currentPowerup.updateLifetime();

      // Call the user defined method to perform any specific updates on each sprite
      userGameUpdateEntity(currentPowerup);
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

  // DONE
  /*
   * Detect the collisions between the entity lists in the following order
   * 
   * Compare the Player Shot Entity list to Enemy Entity list
   * Check if the Player Entity is dead and if so, exit method since there is no point in continuing. 
   * Compare the Player Entity to the Enemy Shot Entity list
   * Compare the Player Entity to the Enemy Entity list
   * 
   * NOTE: The reason why the player shot entity list is compared to the enemy entity list before the
   *       player entity is checked for the alive status is so that any shots the player made prior to
   *       being killed can still be checked for collisions against the enemy list before a new player
   *       is spawned. 
   *       
   * TODO: Possibly a power-up vector that can be compared to the player
   */
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
      Entity currentPlayerShot = (Entity) playerShots.get(playerShotIndex);

      if (currentPlayerShot.isAlive())
      {
        // Second, compare the player shot vector with all enemies to check for any collisions
        for (int enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++)
        {
          Entity currentEnemy = (Entity) enemies.get(enemyIndex);

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

    // If the player is dead, return to the caller since it is pointless to compare to player entity to both the enemy and enemy shot entity lists.
    if (!player.isAlive())
    {
      return;
    }

    /*
     * Second, Compare the enemy shot vector with the player to check for any collisions
     */
    for (int enemyShotIndex = 0; enemyShotIndex < enemyShots.size(); enemyShotIndex++)
    {
      Entity currentEnemyShot = (Entity) enemyShots.get(enemyShotIndex);

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
     * Third, Compare the powerups vector with the player 
     */
    for (int powerupIndex = 0; powerupIndex < powerups.size(); powerupIndex++)
      {
        Entity currentPowerup = (Entity) powerups.get(powerupIndex);

        if (currentPowerup.isAlive())
        {
          // If the player collides with 
          if (player.collidesWith(currentPowerup.getBoundingRectangle()))
          {
            userHandleEntityCollision(player, currentPowerup);
            break;
          }
        }
      }

    /*
     *  Last, compare the player sprite with all enemies to check for any collisions
     */
    for (int enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++)
    {
      Entity currentEnemy = (Entity) enemies.get(enemyIndex);

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

  // DONE
  // Refresh the display. Called back via repaint(), which invoke the paintComponent().

  // GARY NOTE: Make this method draw the complete sprite list and call an abstract method that allows 
  //            the user to perform game specific sprite drawing that can be implemented in the sub class.
  //            The abstract method will be called userGameDraw()
  public void gameDraw(Graphics2D g) // Changed from graphics
  {
    // Clear the background to avoid trailers
    g.setBackground(GameEngineConstants.DEFAULT_BACKGROUND_COLOR);  // may use an image for background    
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
      Entity currentEnemyShot = enemyShots.get(i);
      currentEnemyShot.draw(g);
    }

    // Draw the enemies 
    for (int i = 0; i < enemies.size(); i++)
    {
      try
      {
      Entity currentEntity = enemies.get(i);      
      currentEntity.draw(g);
      }
      catch(NullPointerException e)
      {
        System.out.println("One of the enemies in the enemies LinkedList<Entity> was null");
        System.out.println(e.getMessage());
      }
    }

    // Draw the player shots 
    for (int i = 0; i < playerShots.size(); i++)
    {
      Entity currentPlayerShot = playerShots.get(i);
      currentPlayerShot.draw(g);
    }

    // Draw the powerups 
    for (int i = 0; i < powerups.size(); i++)
    {
      Entity currentPowerup = powerups.get(i);
      currentPowerup.draw(g);
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

  // DONE
  public void pauseGame()
  {
    gamePaused = true;
  }

  // DONE
  public boolean isGamePaused()
  {
    return gamePaused;
  }

  // DONE
  public void unpauseGame()
  {
    gamePaused = false;
  }

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
  public void setNewPlayerEntity(Entity player)
  {
    this.player = (Entity) player.clone();
    this.player.setEntityType(GameEngineConstants.EntityTypes.PLAYER);
  }

  /*
   * Add entities
   */
  public final void addEnemy(Entity entity)
  {
    addEntity(entity, GameEngineConstants.EntityTypes.ENEMY, enemies);
  }

  public void addPlayerShot(Entity entity)
  {
    addEntity(entity, GameEngineConstants.EntityTypes.PLAYER_SHOT, playerShots);
  }

  public void addEnemyShot(Entity entity)
  {
    addEntity(entity, GameEngineConstants.EntityTypes.ENEMY_SHOT, enemyShots);
  }

  public void addPowerup(Entity entity)
  {
    addEntity(entity, GameEngineConstants.EntityTypes.POWER_UP, powerups);
  }

  private void addEntity(Entity entity, GameEngineConstants.EntityTypes type, LinkedList<Entity> entityList)
  {
    // Set the type of the enemy
    entity.setEntityType(type);

    // Add the enemy to the list
    entityList.add(entity);
  }

  /*
   * Get the different entity lists
   */

  public Entity getPlayer()
  {
    return player;
  }

  public LinkedList<Entity> getEnemies()
  {
    return enemies;
  }

  public LinkedList<Entity> getEnemyShots()
  {
    return enemyShots;
  }

  public LinkedList<Entity> getPlayerShot()
  {
    return playerShots;
  }

  public LinkedList<Entity> getPowerups()
  {
    return powerups;
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

  public void clearPowerups()
  {
    powerups.clear();
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

  public void doNotRemoveDeadPlayerShotsFromEntityList()
  {
    cleanPlayerShotsEntityList = false;
  }

  public void removeDeadPowerupsFromEntityList()
  {
    cleanPowerupsEntityList = true;
  }

  public void doNotRemoveDeadPowerupsFromEntityList()
  {
    cleanPowerupsEntityList = false;
  }
  
  // DONE
  /*
   * If the flags are specified, remove the dead entities from the respective entitiy lists.
   */
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

    if (cleanPowerupsEntityList)
    {
      removeDeadEntitiesFromEntityList(powerups);
    }
  }

  // DONE
  /*
   * The dead entities are removed from the linked list as shown in the links below
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
  private void removeDeadEntitiesFromEntityList(LinkedList<Entity> entities)
  {
    Iterator<Entity> entityIterator = entities.iterator();

    while (entityIterator.hasNext())
    {
      if (!entityIterator.next().isAlive())
      {
        entityIterator.remove();
      }
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

  // DONE
  private void displayDebugInfo(Graphics2D g) // Changed from graphics
  {
    int line = 300;

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
    g.drawString("Num Powerup: " + powerups.size(), 20, line);
    line += 16;
    g.drawString("Delta: " + decimalPlaces9.format(delta), 20, line);
    line += 16;
    g.drawString("DEFAULT_UPDATE_PERIOD: " + GameEngineConstants.DEFAULT_UPDATE_PERIOD, 20, line);
    line += 16;
    g.drawString("Delta Loop Time: " + decimalPlaces9.format(updateAndRenderLoopTime * GameEngineConstants.NANO_TO_MILLI), 20, line);
    line += 16;
    g.drawString("Loop Sleep Time: " + loopSleepTime, 20, line);
  }
}
