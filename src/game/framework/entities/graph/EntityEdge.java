package game.framework.entities.graph;

import java.text.DecimalFormat;

import game.framework.entities.shapes.EntityLine;
import game.framework.interfaces.graph.IEdge;

public class EntityEdge extends EntityLine implements IEdge
{
  private int    index;
  private int    sourceNodeId;
  private int    destinationNodeId;
  private double cost;

  public EntityEdge()
  {
    this(-1, -1, 1.0);
  }

  public EntityEdge(int source, int destination)
  {
    this(source, destination, 1.0);
  }

  public EntityEdge(int source, int destination, double cost)
  {
    this.sourceNodeId = source;
    this.destinationNodeId = destination;
    this.cost = cost;
    this.index = -1;
  }

  public String toString()
  {
    //String entitySnapshot = super.toString();
    String entitySnapshot = "";
    DecimalFormat fmt = new DecimalFormat("0.00");
    entitySnapshot += "E(" + this.sourceNodeId + ", " + this.destinationNodeId + ") [" + fmt.format(this.cost) + "]";
    //entitySnapshot += "x1: " + position.x + " y1: " + position.y + " x2: " + width + " y2: " + height;

    return entitySnapshot;
  }

  @Override
  public void setSource(int nodeId)
  {
    this.sourceNodeId = nodeId;
  }

  @Override
  public void setDestination(int nodeId)
  {
    this.destinationNodeId = nodeId;
  }

  @Override
  public int getSource()
  {
    return sourceNodeId;
  }

  @Override
  public int getDestination()
  {
    // TODO Auto-generated method stub
    return destinationNodeId;
  }
  
  @Override
  public double getCost()
  {
    return cost;
  }

  @Override
  public void setCost(double cost)
  {
    this.cost = cost;
  }

  @Override
  public boolean isEqual(IEdge edge)
  {
    return (this.destinationNodeId == edge.getDestination() && this.sourceNodeId == edge.getSource() && this.cost == edge.getCost());
  }

  // This might not be needed
  @Override
  public int getIndex()
  {
    return this.index;
  }

  // This might not be needed
  @Override
  public void setIndex(int index)
  {
    this.index = index;
  }
}

