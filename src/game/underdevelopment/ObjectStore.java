package game.underdevelopment;

import java.util.Stack;

/**
 * 
 * @author ggiger
 * 
 * @param <T>
 * 
 *          Experimental class whose purpose is to provided an object to the game from a stack of discarded objects.
 *          The idea is, since garbage collection can be costly (especially while playing a game). When an object is
 *          no longer needed, rather than removing it from the list and let the GC return it to the heap, it can be
 *          stored on the stack in this class. When a new instance is needed the object can be popped off the stack
 *          and returned to the caller for use in the game.
 */
public class ObjectStore<T>
{
  private Stack<T> objectStack = new Stack<T>();

  public ObjectStore()
  {}

  public T getObject()
  {
    if (objectStack.isEmpty())
    {
      // TODO: Find out how to return a new instance for a generic type. Some ways to do this can be found here:
      //
      //  http://stackoverflow.com/questions/1090458/instantiating-a-generic-class-in-java
      //
      //return new T();
    }

    return objectStack.pop();
  }
}
