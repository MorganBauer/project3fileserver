package team3.src.util;
/**
 * Allows for transport of two object from a method call.
 * @author Joir-dan Gumbs
 *
 */
public class Pair<T1, T2> {
	/** The first element. */
	private T1 first;
	/**  The second element. */
	private T2 last;
	
	/** Accessor for first element 
	 * @return last element*/
	public T1 getFirst(){ return first; }
	
	/** Accessor for second element.
	 *  @return last element */
	public T2 getLast(){ return last; }
	
	/** Constructor for pair object */
	public Pair(T1 first, T2 last){
		this.first = first;
		this.last = last;
	}

}