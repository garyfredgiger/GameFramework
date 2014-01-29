package game.framework.planning.interfaces.graph;

import game.framework.primitives.Position2D;

public interface INode
{
  /*
   * Getters
   */
  
  public int getIndex();
  
  // NOTE: Since this method is similar to the one in the Entity2D class, it was given a different name
  public Position2D positionGet();
  
  public Position2D centerGet();
  
  /*
   * Setters
   */
  public void setIndex(int nodeId);

  // NOTE: Since this method is similar to the one in the Entity2D class, it was given a different name
  public void positionSet(Position2D position);

  public void update(int type, int action);
}
