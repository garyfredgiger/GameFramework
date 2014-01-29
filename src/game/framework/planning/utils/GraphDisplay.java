package game.framework.planning.utils;

import game.framework.entities.graph.EntityEdge;
import game.framework.entities.graph.EntityNode;
import game.framework.planning.Graph;
import game.framework.planning.GraphEdgeIterator;
import game.framework.planning.GraphNodeIterator;


public class GraphDisplay
{
  public static void DisplayGraphNodesToConsole(Graph g, boolean ordered)
  {
    System.out.println("----------------------------------");

    if (g.isEmpty() || g.numActiveNodes() == 0)
    {
      System.out.println("The graph is empty."); 
      System.out.println("----------------------------------");
      return;
    }

    System.out.println("There are " + g.numActiveNodes() + " node(s) in the graph.");
    System.out.println();

    GraphNodeIterator iterator = new GraphNodeIterator(g, ordered);

    while (iterator.hasNext())
    {
      EntityNode node = (EntityNode) iterator.next();

      System.out.println(node.toString());
    }
  }

  /**
   * Display the graph contents to the console window
   */
  public static void DisplayGraphToConsole(Graph g, boolean ordered)
  {
    System.out.println("----------------------------------");

    if (g.isEmpty() || g.numActiveNodes() == 0)
    {
      System.out.println("The graph is empty.");
      System.out.println("----------------------------------");
      return;
    }

    System.out.println("There are " + g.numActiveNodes() + " node(s) in the graph.");
    System.out.println();

    GraphNodeIterator iterator = new GraphNodeIterator(g, ordered);
    while (iterator.hasNext())
    {
      EntityNode node = (EntityNode) iterator.next();

      String graphNodeWithEdges = node.getIndex() + ":\t";

      GraphEdgeIterator edgeIterator = new GraphEdgeIterator(g, node.getIndex());
      while (edgeIterator.hasNext())
      {
        EntityEdge edge = (EntityEdge) edgeIterator.next();
        graphNodeWithEdges += edge.toString() + " ";
      }
      System.out.println(graphNodeWithEdges);
    }

    System.out.println("----------------------------------");
  }
}
