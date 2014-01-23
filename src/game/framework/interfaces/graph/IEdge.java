package game.framework.interfaces.graph;

public interface IEdge
{
  public void setSource(int nodeId);
  
  public void setDestination(int nodeId);
  
  public void setCost(double cost);
  
  public int getSource();
  
  public int getDestination();

  public double getCost();
  
  public boolean isEqual(IEdge edge);
}
