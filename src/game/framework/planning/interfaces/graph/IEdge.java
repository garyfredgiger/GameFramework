package game.framework.planning.interfaces.graph;

import game.framework.primitives.Position2D;

public interface IEdge
{  
  /*
   * Setters
   */
  public void setIndex(int index);  // This might not be needed
  
  public void setSource(int nodeId);
  
  public void setDestination(int nodeId);
  
  public void setCost(double cost);
 
  public void setEndPoints(double x1, double y1, double x2, double y2);
  
  public void setEndPoints(Position2D p1, Position2D p2);
  
  /*
   * Getters
   */
  public int getIndex();  // This might not be needed
  
  public int getSource();
  
  public int getDestination();

  public double getCost();
 
  /*
   * Other methods 
   */
  public boolean isEqual(IEdge edge);
  
  public void update(int type, int action);
}
