package game.framework.entities.graph;

import java.text.DecimalFormat;

import game.framework.entities.shapes.EntityLine;
import game.framework.interfaces.graph.IEdge;

public class EntityEdge extends EntityLine implements IEdge
{
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
  }

  public String toString()
  {
    DecimalFormat fmt = new DecimalFormat("0.00");
    return "E(" + this.sourceNodeId + ", " + this.destinationNodeId + ") [" + fmt.format(this.cost) + "] |";
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
}

