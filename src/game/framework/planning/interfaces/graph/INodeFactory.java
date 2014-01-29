package game.framework.planning.interfaces.graph;

import game.framework.primitives.Position2D;

public interface INodeFactory
{
  public INode createNode(int index, Position2D position);
}
