GameFramework
=============

##Summary

A Java based game framework on which I am working. This is a pet project of mine to see if I can develop an easy to use game framework for myself and others to use to create classic 2D arcade games. This version of the game framework appears to be stable and I have used it to create two games so far, a clone of the classic arcade game Space Invaders (*[here](https://github.com/garyfredgiger/SpaceInvadersClone.git)*) and a clone of the classic arcade game Asteroids (*[here](https://github.com/garyfredgiger/GalacticWarReboot.git)*). As I get more time, I plan on using this game framework to develop additional classic arcade games including a Robotron clone and a Pac-Man style clone.

I also started to put together a set of *[tutorials](https://github.com/garyfredgiger/GameFrameworkTutorials.git)* that show how to use this framework. There are only a few tutorials that show basic concepts such as creating an empty game project, adding a player entity that you can control and working your way up to creating a simple game. As I get more time I will add more tutorials that show how to do more with this framework. For a more complete example of how this framework can be leveraged to create a full game, refer to either the Space Invaders or Asteroids clone mentioned above.

###Long Term Goals

<ul>
<li>Add a simple Ackerman Steering vehicle model so developers can create racing and other games involving vehicles.</li>
<li>AI behaviors so game entities can employ Seek, Flee, Arrive, Pursuit, Evade and Wander behaviors to make games more interesting and challenging.</li>
<li>Possibly add some type of network capability to enable two or more players can play together or compete.</li>
</ul>

###Other Notes

If you are looking for a more complete Java Game library to develop your own games, check out the project *[Lightweight Java Game Library](http://lwjgl.org/)* and *[JGame](http://www.13thmonkey.org/~boris/jgame/)*. These are two great libraries that have been around for quire some time.


##Setting up this project on your Machine for use in Eclipse

Below are a few steps needed for getting this project up and running on your own machine. This project was devloped and built using Eclipse (Kepler Release Build id: 20130614-0229) with Java 1.6. Note: If you are new to Eclipse and need instructions on how to install and set it up, refer to this link *[here](http://wiki.eclipse.org/Eclipse/Installation)*.

NOTE: I included the eclipse project files (.project and .classpath) in this project so you can simply import this project without having to create a new project from your cloned copy of the code. Some suggest it is not a good idea to include project specific files, but I thought I would be a rebel and do it anyway.

###Step 1: Cloning this Project

First, you need to clone this project. Open up a terminal window and in a local directory on your machine, clone this project using the command below.

    git clone https://github.com/garyfredgiger/GameFramework.git

Note that when you clone this project it will only pull the master branch, which contains the lastest release version of the game. At this point there should be two sub folders in your current directory as shown below.

    drwxr--r-- 4 user user 4096 Mar  7 15:06 GameFramework

###Step 2: Importing this Project

After this project is cloned, it needs to be imported into the Eclipse workspace. To do this, follow the steps below:

1. Right-click on *Package Explorer* and select *Import*, then select *General -> Existing Projects into Workspace* and click on Next. The Import dialog will then appear.
2. Click on the Browse button and locate the directory of this project.
3. Once you select this projects click on the OK button, it will then appear in the Projects text area in the Import dialog. Click on the Finish button and it will appear in the Package Explorer.

After importing this projects you should not notice any build errors. If you do, one possible cause coould be the incorrect version of Java is selected. To correct this simply:

1. Right click on the GameFramework project in the Eclispe Package explorer.
2. Select Properties which will bring up the Properties dialog box.
3. On the left hand side select the Java Compiler, on the right side of the dialog box you will see the Java Compiler options.
4. Check that the *Compiller compliance level* is 1.6. If not then select the check the box at the top of the screen labeled *Enable project specific settings* and change the compiler to version 1.6 in the combo box next to *Compiller compliance level*.

After the project builds successfully, you are rady to use it to create your own games. To see it in action check out either of the games that I wrote(*[Space Invaders Clone](https://github.com/garyfredgiger/SpaceInvadersClone.git)* or (*[Asteroids Clone](https://github.com/garyfredgiger/GalacticWarReboot.git)*) or you can start the tutorials which can be found *[here](https://github.com/garyfredgiger/GameFrameworkTutorials.git)*.

## If You Encounter Problems?

If you do find a bug you can submit a new issue under this repo.

...OR...

If you are unable to sumbit a new issue for some unknown reason please email me at garyfredgiger@gmail.com and include in the subject line GALACTIC WAR REBOOT BUG. In the email please provide a detailed description of the bug and the steps that you followed to arrive at the bug. If I am unable to reproduce the problem then I will not be able to fix it.


