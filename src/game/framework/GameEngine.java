package game.framework;

import game.framework.entities.Entity;
import game.framework.interfaces.IRender;
import game.framework.utilities.GameEngineConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class GameEngine
{
  /*
   *  Instance variables for the specific game
   */

  protected GameEngineConstants.GameState state;                                           // current state of the game
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

  // Used for keeping track of time that elapsed between game loop iterations
  long                                    lastLoopTime;
  long                                    currentLoopTime;
  protected double                        delta;

  // Variables to indicate which entity lists to clean up (remove dead entities)
  private boolean                         cleanEnemyShotsEntityList;
  private boolean                         cleanPlayerShotsEntityList;
  private boolean                         cleanEnemiesEntityList;

  // Statistics Variables for game loop
  long                                    numberOfCollisionComparisons;
  private long                            frameCount;
  private long                            frameRate;

  private IRender                         screenRenderer;

  private BufferStrategy                  bs;                                              // Used to render the screen insteasd of the built in paint/paintComponent methods 

  // Debug Variables
  public boolean                          displayDebugInfo = false;
  private DecimalFormat                   nanoFormat       = new DecimalFormat("0.000000");  // this will helps you to always keeps in two decimal places

  public GameEngine(IRender renderer)
  {
    screenRenderer = renderer;
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

  // DONE
  /*
   * The main game loop for this game engine
   */
  private void gameLoop()
  {
    gameRunning = true;

    // Call the user game start method to execute any user specific logic before the game loop begins   
    userGameStart();

    // Variables to computer the delta
    lastLoopTime = System.currentTimeMillis();

    // Game loop
    long frameRateCountStartTime = System.currentTimeMillis();
    long beginLoopTime, endLoopTime;
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
      delta = (currentLoopTime - lastLoopTime) * 0.001; // The 0.001 converts the time in ms to seconds
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
        //gameUpdate(1);  // NOTE: If we switch to a variable frame rate game loop, then this update method will need to be called.
      }

      // Update the display with the newly computed positions of each entity 
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
        System.out.println("Update and Render Took Too Much Time, Skipping sleeping this game loop iteration.");
      }
      else
      {
        try
        {
          // Compute the amount of time to sleep. Note that the measured time is in nano seconds and must be converted to milliseconds for the sleep operation.
          loopSleepTime = (long) ((GameEngineConstants.DEFAULT_UPDATE_PERIOD - updateAndRenderLoopTime) * 0.000001);
          //System.out.println("loopSleepTime : " + nanoFormat.format(loopSleepTime));

          // Provides the necessary delay and also yields control so that other threads can do work.
          Thread.sleep(loopSleepTime);
          //Thread.sleep(1);
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
    player.setPosition(GameEngineConstants.DEFAULT_CANVAS_WIDTH / 2, GameEngineConstants.DEFAULT_CANVAS_HEIGHT / 2);
    player.setVelocity(0.0, 0.0);
    player.setAlive(false);
    player.setVisible(false);

    enemies = new LinkedList<Entity>();
    playerShots = new LinkedList<Entity>();
    enemyShots = new LinkedList<Entity>();

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
     *  Last, compare the player sprite with all enemies to check for any collisions
     */
    for (int enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++)
    //for (int enemyIndex = 0; enemyIndex < entities.size(); enemyIndex++)
    {
      Entity currentEnemy = (Entity) enemies.get(enemyIndex);
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

  // DONE
  // Refresh the display. Called back via repaint(), which invoke the paintComponent().

  // GARY NOTE: Make this method draw the complete sprite list and call an abstract method that allows 
  //            the user to perform game specific sprite drawing that can be implemented in the sub class.
  //            The abstract method will be called userGameDraw()
  public void gameDraw(Graphics2D g) // Changed from graphics
  {
    // Clear the background to avoid trailers
    g.setBackground(GameEngineConstants.DEFAULT_BACKGROUND_COLOR);  // may use an image for background    
    g.fillRect(0, 0, GameEngineConstants.DEFAULT_CANVAS_WIDTH, GameEngineConstants.DEFAULT_CANVAS_HEIGHT);

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
      Entity currentEntity = enemies.get(i);
      currentEntity.draw(g);
    }

    // Draw the player shots 
    for (int i = 0; i < playerShots.size(); i++)
    {
      Entity currentPlayerShot = playerShots.get(i);
      currentPlayerShot.draw(g);
    }

    // Draw the player
    player.draw(g);

    userGamePostDraw(g);
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
  public void addEnemy(Entity entity)
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
    g.drawString("Delta Loop Time: " + probf.format(updateAndRenderLoopTime * 0.000001), 20, line);
    line += 16;
    g.drawString("Loop Sleep Time: " + loopSleepTime, 20, line);
  }
}
