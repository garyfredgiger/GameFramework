package game.framework.planning;

import game.framework.planning.interfaces.graph.INode;

import java.util.Iterator;

public class GraphNodeIterator implements Iterator<Object>
{
  private Iterator<INode> _currentNode;

  public GraphNodeIterator(Graph graph, boolean ordered)
  {
    if (ordered)
    {
      _currentNode = graph.getOrderedNodes().iterator();
    }
    else
    {
      _currentNode = graph.getNodes().iterator();
    }
  }

  @Override
  public boolean hasNext()
  {
    return _currentNode.hasNext();
  }

  @Override
  public Object next()
  {
    return _currentNode.next();
  }

  @Override
  public void remove()
  {
    _currentNode.remove();
  }
}
