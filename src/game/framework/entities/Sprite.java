package game.framework.entities;

import java.awt.Graphics2D;

// TODO: Not sure if this is the way to go regarding a way to handle the drawing of both images and shapes
public abstract class Sprite
{
  abstract public void draw(Graphics2D g);
}
