package game.framework.planning.utils;

import game.framework.planning.Graph;
import game.framework.planning.interfaces.graph.IEdge;
import game.framework.planning.interfaces.graph.IEdgeFactory;
import game.framework.planning.interfaces.graph.INode;
import game.framework.planning.interfaces.graph.INodeFactory;
import game.framework.primitives.Position2D;


public class GraphGenerator
{
  private static IEdgeFactory edgeFactory;
  private static INodeFactory nodeFactory;
  
  private static boolean generateFourNeighbor = false;
  
  // TODO: Remove nodeRadius parameter
  public static Graph generateGridGraph(Graph g, INodeFactory nFactory, IEdgeFactory eFactory, int nodeRadius, int startNodeCol, int startNodeRow, int nodeSpacingCol, int nodeSpacingRow, int rows, int cols, boolean digraph, boolean fourNeighbor)
  {
    edgeFactory = eFactory;
    nodeFactory = nFactory;
    
    generateFourNeighbor = fourNeighbor;
    
    GraphHelper_CreateGrid(g, startNodeCol, startNodeRow, nodeSpacingCol, nodeSpacingRow, rows, cols);

    return g;
  }

  /*
   * Returns true if x,y is a valid position in the map
   */
  private static boolean ValidNeighbor(int x, int y, int NumCellsX, int NumCellsY)
  {
    return !((x < 0) || (x >= NumCellsX) || (y < 0) || (y >= NumCellsY));
  }

  /*
   * Adds the eight neighboring edges of a graph node that is positioned in a grid layout
   */
  private static void GraphHelper_AddAllNeighboursToGridNode(Graph graph, int row, int col, int NumCellsX, int NumCellsY)
  {
    for (int i = -1; i < 2; i++)
    {
      for (int j = -1; j < 2; j++)
      {
        // Skip if equal to this node
        if ((i == 0) && (j == 0))
          continue;
       
        if (generateFourNeighbor)
        {
          // Ignore the upper left corner
          if ((i == -1) && (j == -1))
          {
            continue;
          }
          
          // Ignore the upper right corner
          if ((i == -1) && (j == 1))
          {
            continue;
          }
          
          // Ignore the lower left corner
          if ((i == 1) && (j == -1))
          {
            continue;
          }
          
          // Ignore the lower right corner
          if ((i == 1) && (j == 1))
          {
            continue;
          }
          
        }
        
        int neighborNodeX = col + j;
        int neighborNodeY = row + i;
        int nodeIndex = row * NumCellsX + col;
        int neighborNodeIndex = neighborNodeY * NumCellsX + neighborNodeX;
        
        // check to see if this is a valid neighbor
        if (ValidNeighbor(neighborNodeX, neighborNodeY, NumCellsX, NumCellsY))
        {
          // calculate the distance to this node
          Position2D PosNode = graph.getNode(nodeIndex).centerGet();
          Position2D PosNeighbour = graph.getNode(neighborNodeIndex).centerGet();

          double dist = PosNode.distance(PosNeighbour);

          // this neighbor is okay so it can be added
          IEdge newEdge = edgeFactory.createEdge(nodeIndex, neighborNodeIndex, dist); 
          
          newEdge.setEndPoints(PosNode, PosNeighbour);
          graph.AddEdge(newEdge);

          // if graph is not a digraph then an edge needs to be added going
          // in the other direction
          if (graph.isUndirected())
          {
            //IEdge oppositeNewEdge = new EntityEdge(neighborNodeIndex, nodeIndex, dist);
            IEdge oppositeNewEdge = edgeFactory.createEdge(neighborNodeIndex, nodeIndex, dist); 
            
            oppositeNewEdge.setEndPoints(PosNeighbour, PosNode);
            graph.AddEdge(oppositeNewEdge);
          }
        }
      }
    }
  }

  /*
   * Creates a graph based on a grid layout. This function requires the dimensions of the environment and the number of cells required horizontally and vertically.
   */
  //private static void GraphHelper_CreateGrid(Graph graph, int cySize, int cxSize, int NumCellsY, int NumCellsX)
  private static void GraphHelper_CreateGrid(Graph graph, int startCol, int startRow, int colSpacing, int rowSpacing, int NumCellsY, int NumCellsX)  
  {
    // Need some temporaries to help calculate each node center

    // First create all the nodes
    for (int row = 0; row < NumCellsY; row++)
    {
      for (int col = 0; col < NumCellsX; col++)
      {
        // NOTE: Since the entity node here will be part of the actual project, there is no concept of a dimension. Therefore, another interface 
        //       may need to go here that gets implemented by the project and creates an actual EntityNode, but returns it as an INode.
        // 
        INode node = nodeFactory.createNode(graph.getNextFreeNodeIndex(), new Position2D(startCol + (col * colSpacing), startRow + (row * rowSpacing)));
        graph.AddNode(node);
      }
    }

    // Now to calculate the edges. (A position in a 2d array [x][y] is the
    // same as [y*NumCellsX + x] in a 1d array). Each cell has up to eight
    // Neighbors.
    for (int row = 0; row < NumCellsY; row++)
    {
      for (int col = 0; col < NumCellsX; col++)
      {
        GraphHelper_AddAllNeighboursToGridNode(graph, row, col, NumCellsX, NumCellsY);
      }
    }    
  }
}

