package game.framework.planning;

import game.framework.entities.graph.EntityEdge;

import java.util.Iterator;

public class GraphEdgeIterator implements Iterator<Object>
{
  private Iterator<EntityEdge> _currentEdge;

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
