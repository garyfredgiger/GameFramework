package game.framework.planning;

import game.framework.planning.interfaces.graph.IEdge;
import game.framework.planning.interfaces.graph.INode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public abstract class Graph
{
  // TODO: This graph uses an adjacency list implementation, but it should be abstracted once more level to hide the specific graph implementation.

  /*
   *  Stores the nodes given the index of a particular node.
   */
  protected HashMap<Integer, INode>       nodes               = new HashMap<Integer, INode>();

  /*
   *  Classic adjacency list construct where each row is indexed by the node id and each entry is a List os edges 
   */
  protected HashMap<Integer, List<IEdge>> adjacentcyListEdges = new HashMap<Integer, List<IEdge>>();

  /*
   *  Classic adjacency list construct where each row is indexed by the node id and each entry is a set of adjacent node ids
   *  TODO: Incorporate this into the current graph implementation.  
   */
  protected HashMap<Integer, HashSet<Integer>> adjacentcyListNodes = new HashMap<Integer, HashSet<Integer>>();

  protected int                                _numActiveNodes;                                               // Bookkeeping to track number of active nodes
  protected int                                _mNextFreeNodeIndex;

  protected static int                         invalid_node_index  = -1;

  public Graph()
  {
    _mNextFreeNodeIndex = 0;
    _numActiveNodes = 0;
  }

  /**
   * Indicates whether or not the graph is empty
   * 
   * @return
   */
  // TODO: This will stay in the Graph class
  public boolean isEmpty()
  {
    return nodes.isEmpty();
  }

  /**
   * 
   * @return
   */
  // TODO: This will stay in the Graph class
  public int numActiveNodes()
  {
    return _numActiveNodes;
  }

  /**
   * 
   * @return
   */
  // TODO: This will stay in the Graph class
  public int numNodes()
  {
    return nodes.size();
  }

  /**
   * 
   * @return
   */
  // TODO: This will stay in the Graph class
  public int getNextFreeNodeIndex()
  {
    return _mNextFreeNodeIndex;
  }

  /*
   * Checks if a node with the specified index already exists in the graph
   */
  // TODO: This will most likely stay in the Graph class since this operation is the same for all graph types 
  public boolean isNodePresent(int nodeIndex)
  {
    // Check if the key exists in the hash map and that its value is not null
    // Note that a key can exist and its value can be null. When a node is
    // removed from the graph rather than deleting the node from the hash map, its
    // value will be set to null thus preserving the key. This is to ensure that
    // when a node is added and removed, its index is preserved so that if the
    // same node is added again in the future, it will not interfere with any
    // other nodes etc...
    if (nodes.containsKey(nodeIndex) && (nodes.get(nodeIndex) != null))
    {
      return true;
    }

    return false;
  }

  /**
   * 
   * @param nodeIndex
   * @return
   */
  // TODO: This will most likely stay in the Graph class since this operation is the same for all graph types 
  public INode getNode(int nodeIndex)
  {
    if (isNodePresent(nodeIndex))
    {
      return nodes.get(nodeIndex);
    }

    return null;
  }

  /**
   * Active nodes are returned in an unordered list with regard to node index.
   * 
   * @return
   */
  // TODO: This will most likely stay in the Graph class since this operation is the same for all graph types 
  public List<INode> getNodes()
  {
    // Create the list of nodes to return
    List<INode> listOfActiveNodes = new ArrayList<INode>(this.numActiveNodes());

    Iterator<Entry<Integer, INode>> iteratorNodes = nodes.entrySet().iterator();
    while (iteratorNodes.hasNext())
    {
      Entry<Integer, INode> nodes = iteratorNodes.next();
      INode node = (INode) nodes.getValue();

      if (node != null)
      {
        listOfActiveNodes.add(node);
      }
    }

    return listOfActiveNodes;
  }

  /**
   * Active nodes are returned ordered by their index in ascending order.
   * 
   * @return
   */
  // TODO: This will most likely stay in the Graph class since this operation is the same for all graph types 
  public List<INode> getOrderedNodes()
  {
    // Create the list of nodes to return
    List<INode> listOfActiveNodes = new ArrayList<INode>(this.numActiveNodes());

    for (int currentNodeIndex = 0; currentNodeIndex < this.numNodes(); currentNodeIndex++)
    {
      if (this.isNodePresent(currentNodeIndex))
      {
        listOfActiveNodes.add(getNode(currentNodeIndex));
      }
    }

    return listOfActiveNodes;
  }

  /**
   * Checks if an edge already exists in the graph. This method assumes that both nodes already exist since this method is called within the AddEdge method.
   * 
   * @param from
   * @param to
   * @return
   * 
   *         TODO Should this method be private? If the method is public, then this method may need to test if both nodes already exist
   * 
   *         TODO Find a more efficient way to implement finding the "To" node when looking for the edge. Currently it is linear time O(n).
   * 
   */

  /* TODO: Possibly use a data structure like the one below fot the method uniqueEdge
   * 
   *   private HashMap<Integer, HashSet<Integer>> _nodeNeighbors     = new HashMap<Integer, HashSet<Integer>>();
   *   
   * Stores a set of node indices that are connected (via edges) to the particular node index.
   * 
   * This will be used for quick lookup when determining which nodes are connected to the specified node.
   * For instance the uniqueEdge method. 
   */
  // TODO: NOT SURE WHERE THIS WILL GO.
  protected IEdge uniqueEdge(int source, int destination)
  {
    IEdge existingEdge = null;
    Iterator<IEdge> edgesIterator = adjacentcyListEdges.get(source).iterator();

    while (edgesIterator.hasNext())
    {
      IEdge edge = edgesIterator.next();

      if (edge.getDestination() == destination)
      {
        existingEdge = edge;
        break;
      }
    }

    return existingEdge;
  }

  abstract public boolean isUndirected();
  
  abstract public int AddNode(INode node);

  abstract public void removeNode(int nodeIndex);

  abstract public void AddEdge(IEdge edge);

  abstract public void removeEdge(int from, int to);

  abstract public boolean updateEdgeCost(int from, int to, double cost);

  abstract public IEdge getEdge(int from, int to);
  
  abstract public List<IEdge> getEdges();

  abstract public List<IEdge> getNodeEdges(int nodeIndex);

  abstract public boolean isEdgePresent(int from, int to);
}
