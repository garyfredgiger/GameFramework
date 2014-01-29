package game.framework.planning;

import game.framework.planning.interfaces.graph.IEdge;
import game.framework.planning.primitive.Tuple;
import game.framework.planning.utils.AStarPriorityQueue;
import game.framework.planning.utils.AStarPriorityQueueComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AStar
{
  public static final int TYPE_ASTAR_NODE = 0;
  public static final int TYPE_ASTAR_EDGE = 1;
  
  public static final int ACTION_LABEL_START_NODE = 0;
  public static final int ACTION_LABEL_TARGET_NODE = 1;
  
  private Graph                 _mGraph;

  // TODO: May need to wrap each node in a path node class that stores the cost between this node
  // and another node (previous node/parent node) in the list since costs are relative
  // depending on the current nodes relation to other nodes.

  private int                         _mSource;
  private int                         _mTarget;

  AStarPriorityQueue<Integer, Double> _openSet;

  // The closed set is implemented as a HashSet storing the id of the nodes already visited.
  // A hash set implementation is used since it provides O(1) lookup time.
  private Set<Integer>                _closedSet;

  // Hash maps are used in order to keep the amount of space to a minimum
  // NOTE: To increase performance, these maps may need to be sufficiently sized.
  // Since for very large graphs (1000's of nodes), it might not be wise to
  // allocate space for each node in the graph since it would require twice
  // the space (one entry in the F Cost list and one in the G cost list).
  private Map<Integer, Double>        _mFCosts;
  private Map<Integer, Double>        _mGCosts;

  // Back points of path node Ids (is a path exists).
  private Map<Integer, Integer>       _cameFrom      = new HashMap<Integer, Integer>();

  // Path variables
  private List<Integer>               _pathOfNodeIds = new ArrayList<Integer>();

  public AStar(Graph graph, int source, int target)
  {
    _mGraph = graph;
    _mSource = source;
    _mTarget = target;

    if (_mGraph == null)
    {
      return;
    }

    // Check if both source and destination nodes exist.
    if (!_mGraph.isNodePresent(_mSource) || !_mGraph.isNodePresent(_mTarget))
    {
      return;
    }

    _mGraph.getNode(_mSource).update(TYPE_ASTAR_NODE, ACTION_LABEL_START_NODE);
    _mGraph.getNode(_mTarget).update(TYPE_ASTAR_NODE, ACTION_LABEL_TARGET_NODE);

    // Create an indexed priority queue of node indexes and their F score. The
    // nodes with the lowest overall F cost (G+H) are positioned at the front.
    _openSet = new AStarPriorityQueue<Integer, Double>(_mGraph.numNodes(), new AStarPriorityQueueComparator());

    // Keep a set of node indices that have already been visited.
    _closedSet = new HashSet<Integer>(graph.numNodes());

    _mFCosts = new HashMap<Integer, Double>(_mGraph.numNodes() / 2);
    _mGCosts = new HashMap<Integer, Double>(_mGraph.numNodes() / 2);

    Search();
  }

  private void reconstructPath(int currentNodeId)
  {
    if (currentNodeId == _mSource)
    {
      _pathOfNodeIds.add(_mSource);
      return;
    }

    reconstructPath(_cameFrom.get(currentNodeId));
    _pathOfNodeIds.add(currentNodeId);
  }

  private void Search()
  {
    _mGCosts.put(_mSource, 0.0);
    _mFCosts.put(_mSource, _mGCosts.get(_mSource) + _mGraph.getNode(_mSource).positionGet().distance(_mGraph.getNode(_mTarget).positionGet()));

    _openSet.add(_mSource, _mFCosts.get(_mSource));

    while (!_openSet.isEmpty())
    {
      Tuple<Integer, Double> nextLowestNodeFScorePair = _openSet.remove();
      int currentNodeIndex = nextLowestNodeFScorePair.getFirst();

      // If current node index with next lowest F score is the same as the target index, a path was found.
      if (currentNodeIndex == _mTarget)
      {
        // TODO: Call method that will reconstruct path
        _pathOfNodeIds.clear();
        reconstructPath(_mTarget);
        // Collections.reverse(_pathOfNodeIds);
        break;
      }

      // Add the index of the current node to the closed list
      // NOTE: Only the index for the node needs to be stored since it can be used to get the actual node from the graph
      _closedSet.add(currentNodeIndex);

      // TODO: Get the neighbors of the current node
      GraphEdgeIterator edgeIterator = new GraphEdgeIterator(_mGraph, currentNodeIndex);
      while (edgeIterator.hasNext())
      {
        IEdge edgeToCurrentNeighbor = (IEdge) edgeIterator.next();
        int currentNeighborIndex = edgeToCurrentNeighbor.getDestination();

        // NOTE: The edge cost was already computed when the graph was made. This works in this case since the neighbors are adjacent.
        double tentativeGScore = _mGCosts.get(currentNodeIndex) + edgeToCurrentNeighbor.getCost();

        if (_closedSet.contains(currentNeighborIndex))
        {
          if (tentativeGScore >= _mGCosts.get(currentNeighborIndex))
          {
            continue;
          }
        }

        // Check if current current neighbor index already exists in open list
        if ((!_openSet.contains(currentNeighborIndex)) || (tentativeGScore < _mGCosts.get(currentNeighborIndex)))
        {
          _cameFrom.put(currentNeighborIndex, currentNodeIndex);
          _mGCosts.put(currentNeighborIndex, tentativeGScore);
          _mFCosts.put(currentNeighborIndex, _mGCosts.get(currentNeighborIndex) + _mGraph.getNode(currentNeighborIndex).positionGet().distance(_mGraph.getNode(_mTarget).positionGet()));

          if (!_openSet.contains(currentNeighborIndex))
          {
            _openSet.add(currentNeighborIndex, _mFCosts.get(currentNeighborIndex));
          }
        }
      }
    }
  }

  // private void displayCameFromList()
  // {
  // for (Map.Entry entry : _cameFrom.entrySet())
  // {
  // System.out.println(entry.getKey() + ", " + entry.getValue());
  // }
  // }

  public ArrayList<Integer> getPathAsListOfNodeIds()
  {
    return (ArrayList<Integer>) _pathOfNodeIds;
  }
}

