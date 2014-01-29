package game.framework.planning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import game.framework.planning.interfaces.graph.IEdge;
import game.framework.planning.interfaces.graph.IEdgeFactory;
import game.framework.planning.interfaces.graph.INode;

public class GraphUndirected extends Graph
{
  IEdgeFactory edgeFactory;
  
  /*
   * Default Constructor
   */
  public GraphUndirected(IEdgeFactory edgeFactory)
  {
    super();
    this.edgeFactory = edgeFactory;
  }

  public boolean isUndirected()
  {
    return true;
  }

  // DONE, BUT NEEDS TO BE TESTED
  /*
   * Adds a new node to the current graph
   */
  public int AddNode(INode node)
  {
    // If the current node already exists in the hash map, simply return its index
    if (isNodePresent(node.getIndex()))
    {
      return node.getIndex();
    }

    // Add the node to the hash map and assign an empty edge list to that node in the corresponding graph edge adjacency list
    nodes.put(node.getIndex(), node);
    _numActiveNodes++;  // NOTE: This will need to be decremented when everytime a node is removed

    // TODO: Determine if ArrayList is the best List implementation to use for the edges
    adjacentcyListEdges.put(node.getIndex(), new ArrayList<IEdge>(0));

    // TODO: Add logic here to update the adjacentcyListNodes hash map

    // Since the node did not previously exist, return the index of the new node and increment it.
    return _mNextFreeNodeIndex++;
  }

  // DONE, BUT NEEDS TO BE TESTED
  /*
   * Remove the node with the specified index
   */
  public void removeNode(int nodeIndex)
  {
    // If the node does not exist, exit without doing anything
    if (!isNodePresent(nodeIndex))
    {
      return;
    }

    /*
     * Since this is an undirected graph, all edges pointing to this node need to be removed
     */
    // First, get the list of edges for the node to be removed.
    // TODO: Can these two lines be combined into one line?
    List<IEdge> nodeEdges = adjacentcyListEdges.get(nodeIndex);  // We must Get the list of edges before...
    Iterator<IEdge> iteratorEdges = nodeEdges.iterator();        // ...getting the iterator.

    // Second, iterate through all edges, removing each edge that point to this node.
    while (iteratorEdges.hasNext())
    {
      IEdge edge = iteratorEdges.next();
      removeEdge(edge.getDestination(), edge.getSource());
    }

    // Third, clear the list of edges from the adjacency list of edges of the node to delete
    adjacentcyListEdges.get(nodeIndex).clear();

    // Forth, remove the node from the list. The node object is removed from the actual hashmap,
    // but the node index remains in case the node is added again at some point in the future.
    nodes.put(nodeIndex, null);

    // Last, update the number of active nodes
    _numActiveNodes--;
  }

  // DONE, BUT NEEDS TO BE TESTED
  /*
   * Adds an edge to the graph
   */
  public void AddEdge(IEdge edge)
  {
    int sourceIndex = edge.getSource();
    int destinationIndex = edge.getDestination();

    // If either of the source or destination nodes do not exist, exit since an edge cannot exist between non-existent nodes.
    if (!isNodePresent(sourceIndex) || !isNodePresent(destinationIndex))
    {
//      System.out.println("Either node with index " + sourceIndex + " or " + destinationIndex + " does not exist in the graph. Exiting AddEdge...");
      return;
    }

//    System.out.println("Nodes with both indices " + sourceIndex + " and " + destinationIndex + " exists in the graph. Continuing to add edge.");

    /*
     *  First, making sure it is unique (i.e., it does not already exist) add the edge 
     */
    IEdge existingEdge = uniqueEdge(sourceIndex, destinationIndex);

    // If the edge does not already exist, add the edge
    if (existingEdge == null)
    {
//      System.out.println("Edge " + edge + " does not already exist in the graph. Adding edge.");
//      System.out.println("\nDEBUG: Size of _mEdges(" + sourceIndex + "): " + adjacentcyListEdges.get(sourceIndex).size() + " BEFORE Adding Edge.\n");
      adjacentcyListEdges.get(sourceIndex).add(edge);
      // TODO: Add logic here to update the adjacentcyListNodes hash map
//      System.out.println("\nDEBUG: Size of _mEdges(" + sourceIndex + "): " + adjacentcyListEdges.get(sourceIndex).size() + " AFTER Adding Edge.\n");
    }
    else
    {
      // Since the edge already exists, simply update the edge with the cost
//      System.out.println("Edge " + edge + " already exists in the graph. Only updating cost.");
      // TODO: Should I check if the cost differs at all? Would adding a condition increase runtime since I need to always check first?
      // NOTE: Since the edge already exists, the uniqueEdge() method returns a reference to the edge
      existingEdge.setCost(edge.getCost());
    }

    // Add another edge in the opposite direction (e.g., (destination node id, source node id) )
    // Check to make sure the edge is unique before adding
    IEdge returningEdge = uniqueEdge(destinationIndex, sourceIndex);
    if (returningEdge == null)
    {
      // Create a new edge and make it point from the From node back to the To node.
      // TODO: May need to add a factory pattern here
      //IEdge newEdge = new EntityEdge(destinationIndex, sourceIndex, edge.getCost());
      IEdge newEdge = edgeFactory.createEdge(destinationIndex, sourceIndex, edge.getCost());      

//      System.out.println("Digraph Edge " + newEdge + " does not already exist in the graph. Adding edge.");
//      System.out.println("Edge " + edge + " does not already exist in the graph. Adding edge.");
      adjacentcyListEdges.get(destinationIndex).add(newEdge);
      // TODO: Add logic here to update the adjacentcyListNodes hash map
//      System.out.println("\nDEBUG: Size of _mEdges(" + sourceIndex + "): " + adjacentcyListEdges.get(sourceIndex).size() + " AFTER Adding Edge.\n");
    }
    else
    {
      // Since the edge already exists, simply update the edge with the cost
//      System.out.println("Digraph Edge " + returningEdge + " already exists in the graph. Only updating cost.");
      returningEdge.setCost(edge.getCost());
    }

    // Debugging code that needs to go away 
    //    System.out.println("RECAP:");
    //    System.out.println();
    //    System.out.println("Source Node " + sourceIndex + "Edges:");
    //
    //    ArrayList<EntityEdge> sourceNodeEdges = (ArrayList<EntityEdge>) adjacentcyListEdges.get(sourceIndex);
    //    Iterator<EntityEdge> currentSourceEdges = (Iterator<EntityEdge>) sourceNodeEdges.iterator();
    //    while (currentSourceEdges.hasNext())
    //    {
    //      System.out.println("\t" + currentSourceEdges.next() + "\n");
    //    }
    //
    //    System.out.println();
    //    System.out.println("Destination Node " + sourceIndex + "Edges:");
    //
    //    ArrayList<EntityEdge> destNodeEdges = (ArrayList<EntityEdge>) adjacentcyListEdges.get(destinationIndex);
    //    Iterator<EntityEdge> currentDestEdges = (Iterator<EntityEdge>) destNodeEdges.iterator();
    //    while (currentDestEdges.hasNext())
    //    {
    //      System.out.println("\t" + currentDestEdges.next() + "\n");
    //    }
  }

  // DONE, BUT NEEDS TO BE TESTED
  /*
   * Remove the edge between the specified source and destination node indices
   */
  public void removeEdge(int source, int destination)
  {
    // If either of the source or destination nodes do not exist, exit since an edge
    // cannot exist between non-existent nodes.
    if (!isNodePresent(source) || !isNodePresent(destination))
    {
      return;
    }

    // Check if the edge does not exist
    IEdge edgeToRemove = uniqueEdge(source, destination);
    if (edgeToRemove != null)
    {
      // TODO: Is there a more efficient way to remove an edge using another list implementation or different data structure?
      adjacentcyListEdges.get(source).remove(edgeToRemove);
    }
  }

  // DONE, BUT NEEDS TO BE TESTED
  /*
   * Update the edge cost 
   */
  public boolean updateEdgeCost(int source, int destination, double cost)
  {
    // If either of the source or destination nodes do not exist, exit since an edge
    // cannot exist between non-existent nodes.
    if (!isNodePresent(source) || !isNodePresent(destination))
    {
      return false;
    }

    // Check if both edges exist
    IEdge edgeToUpdate = uniqueEdge(source, destination);
    IEdge oppositeEdgeToUpdate = uniqueEdge(destination, source);

    // If either edge does not exist abort
    // TODO: Should some type of exception be thrown (e.g., edge inconsistency exception) since this should never be the case with an undirected graph
    if ((edgeToUpdate == null) || (oppositeEdgeToUpdate == null))
    {
      return false;
    }

    edgeToUpdate.setCost(cost);
    oppositeEdgeToUpdate.setCost(cost);

    return true;
  }

  // DONE, BUT NEEDS TO BE TESTED
  /*
   * Get the specified edge
   */
  public IEdge getEdge(int source, int destination)
  {
    // TODO: Should a test be performed to see if its reverse edge exists.
    // TODO: Should some type of exception be thrown (e.g., edge inconsistency exception) since this should never be the case with an undirected graph
    return uniqueEdge(source, destination);
  }

  public List<IEdge> getEdges()
  {
    System.out.println("GraphUndirected::getEdges()");
    ArrayList<IEdge> allEdges = new ArrayList<IEdge>();
    //HashSet<EntityEdge> edges = new HashSet<EntityEdge>();

    // Get the list of node IDs
    for (Integer nodeIndex : nodes.keySet())
    {
      ArrayList<IEdge> nodeEdges = (ArrayList<IEdge>) adjacentcyListEdges.get(nodeIndex);      
      allEdges.addAll(nodeEdges);
    }

    Iterator<IEdge> tmpIterator = allEdges.iterator();
    while(tmpIterator.hasNext())
    {
      IEdge edge = tmpIterator.next();
      System.out.println(edge);
    }

    // TODO: Using each node ID, get the corresponding list of edges

    // TODO: Construct the list of edges
    // NOTE: As edges are added, duplicates need to be removed

    return allEdges;
  }

  // DONE, BUT NEEDS TO BE TESTED
  /*
   * Get all edges for the specified node 
   */
  public List<IEdge> getNodeEdges(int nodeIndex)
  {
    // NOTE: A conditional check is not required here to see if the node index exists in the hash map of edges.
    //       If the index does not exist a null will be returned (Refer to javadoc for details regarding get and hashmap). 
    return adjacentcyListEdges.get(nodeIndex);
  }

  // DONE, BUT NEEDS TO BE TESTED
  /*
   * Test whether the specified edge exists
   */
  public boolean isEdgePresent(int source, int destination)
  {
    // TODO: Should a test be performed to see if its reverse edge exists?
    // TODO: Should some type of exception be thrown (e.g., edge inconsistency exception) since this should never be the case with an undirected graph
    if (uniqueEdge(source, destination) == null)
    {
      return false;
    }

    return true;
  }
}
