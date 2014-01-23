package game.framework.entities.graph;

import game.framework.entities.shapes.EntityOval;
import game.framework.interfaces.graph.INode;
import game.framework.primitives.Position2D;

public class EntityNode extends EntityOval implements INode
{
  protected int index;
 
  public EntityNode()
  {
    this(-1, new Position2D());
  }

  public EntityNode(int index, Position2D position)
  {
    this.index = index;
    this.position.set(position);
  }
  
  @Override
  public int getIndex()
  {
    return index;
  }

  @Override
  public Position2D getNodePosition()
  {
    return this.position;
  }

  @Override
  public void setIndex(int nodeId)
  {
    this.index = nodeId;
  }

  @Override
  public void setNodePosition(Position2D position)
  {
    this.position.set(position);
  }
}
