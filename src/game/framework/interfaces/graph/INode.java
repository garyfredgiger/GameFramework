package game.framework.interfaces.graph;

import game.framework.primitives.Position2D;

public interface INode
{
  public int getIndex();
  
  // NOTE: Since this method is similar to the one in the Entity2D class, it was given a different name
  public Position2D getNodePosition();
  
  public void setIndex(int nodeId);

  // NOTE: Since this method is similar to the one in the Entity2D class, it was given a different name
  public void setNodePosition(Position2D position);
}
