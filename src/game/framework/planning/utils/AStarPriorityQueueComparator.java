package game.framework.planning.utils;

import game.framework.planning.primitive.Tuple;
import java.util.Comparator;

public class AStarPriorityQueueComparator implements Comparator<Tuple<Integer, Double>>
{
  @Override
  public int compare(Tuple<Integer, Double> cost1, Tuple<Integer, Double> cost2)
  {
    if (cost1.getSecond() < cost2.getSecond())
    {
      return -1;
    }

    if (cost1.getSecond() > cost2.getSecond())
    {
      return 1;
    }

    return 0;
  }
}

