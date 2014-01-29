package game.framework.planning.utils;

import game.framework.entities.graph.EntityEdge;
import game.framework.entities.graph.EntityNode;
import game.framework.planning.Graph;
import game.framework.primitives.Position2D;


public class GraphGenerator
{
  public static Graph generateGridGraph(Graph g, int startNodeCol, int startNodeRow, int nodeSpacingCol, int nodeSpacingRow, int rows, int cols, boolean digraph)
  {
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
                
        int neighborNodeX = col + j;
        int neighborNodeY = row + i;
        int nodeIndex = row * NumCellsX + col;
        int neighborNodeIndex = neighborNodeY * NumCellsX + neighborNodeX;
        
        // check to see if this is a valid neighbor
        if (ValidNeighbor(neighborNodeX, neighborNodeY, NumCellsX, NumCellsY))
        {
          // calculate the distance to this node
          Position2D PosNode = graph.getNode(nodeIndex).getPosition();
          Position2D PosNeighbour = graph.getNode(neighborNodeIndex).getPosition();
          
          double dist = PosNode.distance(PosNeighbour);

//          System.out.println("Node " + graph.getNode(nodeIndex).getIndex() + " (" + row + ", " + col + ")"); 
//          System.out.print("\tProcess node neighbors (" + neighborNodeX + ", " + neighborNodeY + ")...");
//          System.out.println("Creating Edge between Node " + graph.getNode(nodeIndex).getIndex() + " " + PosNode + " and Node " + graph.getNode(neighborNodeIndex).getIndex() + " " + PosNeighbour + ".");
          
          // this neighbor is okay so it can be added
          EntityEdge NewEdge = new EntityEdge(nodeIndex, neighborNodeIndex, dist);
          NewEdge.setPosition(PosNode);
          NewEdge.setDimensions((int)PosNeighbour.x, (int)PosNeighbour.y);
//          System.out.println("\tNewEdge Edge:" + NewEdge);
          graph.AddEdge(NewEdge);

          // if graph is not a digraph then an edge needs to be added going
          // in the other direction
          if (graph.isUndirected())
          {
            EntityEdge oppositeNewEdge = new EntityEdge(neighborNodeIndex, nodeIndex, dist);
            oppositeNewEdge.setPosition(PosNeighbour);
            oppositeNewEdge.setDimensions((int)PosNode.x, (int)PosNode.y);
//            System.out.println("\toppositeNewEdge Edge:" + oppositeNewEdge);
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
//    double CellWidth = (double) cySize / (double) NumCellsX;
//    double CellHeight = (double) cxSize / (double) NumCellsY;
//
//    double midX = CellWidth / 2;
//    double midY = CellHeight / 2;

    // First create all the nodes
    for (int row = 0; row < NumCellsY; row++)
    {
      for (int col = 0; col < NumCellsX; col++)
      {
        graph.AddNode(new EntityNode(graph.getNextFreeNodeIndex(), new Position2D(startCol + (col * colSpacing), startRow + (row * rowSpacing))));
      }
    }

    // Now to calculate the edges. (A position in a 2d array [x][y] is the
    // same as [y*NumCellsX + x] in a 1d array). Each cell has up to eight
    // Neighbors.
    for (int row = 0; row < NumCellsY; row++)
    {
      for (int col = 0; col < NumCellsX; col++)
      {
//        System.out.println();
//        System.out.println();
        GraphHelper_AddAllNeighboursToGridNode(graph, row, col, NumCellsX, NumCellsY);
      }
    }
    
//    GraphDisplay.DisplayGraphNodesToConsole(graph, true);
//    System.out.println();
//    System.out.println();
//    GraphDisplay.DisplayGraphToConsole(graph, true);
  }
}

