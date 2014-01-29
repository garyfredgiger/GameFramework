package game.framework.planning.interfaces.graph;

public interface IEdgeFactory
{
  public IEdge createEdge(int sourceNodeIndex, int destinationNodeIndex);
  
  public IEdge createEdge(int sourceNodeIndex, int destinationNodeIndex, double cost);
}
