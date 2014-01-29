package game.framework.planning;

import game.framework.planning.interfaces.graph.IEdge;

import java.util.Iterator;

public class GraphEdgeIterator implements Iterator<Object>
{
  private Iterator<IEdge> _currentEdge;

  public GraphEdgeIterator(Graph graph, int nodeIndex)
  {
    _currentEdge = graph.getNodeEdges(nodeIndex).iterator();
  }

  @Override
  public boolean hasNext()
  {
    return _currentEdge.hasNext();
  }

  @Override
  public Object next()
  {
    return _currentEdge.next();
  }

  @Override
  public void remove()
  {
    _currentEdge.remove();
  }

}
