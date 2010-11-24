package team3.src.util;
/**
 * This object allows for three objects to be returned from  a method 
 * @author Joir-dan Gumbs
 *
 */
public class Triple<T1, T2, T3> {
	/** First element. */
	private T1 first;
	/** Second element. */
	private T2 second;
	/** Third element. */
	private T3 third;
	
	/** Accessor for first element.
	 * @return first element */
	public T1 getFirst(){ return first; }
	/**  Accessor for second element.
	 * @return second element */
	public T2 getSecond(){ return second; }
	/** Accessor for third element
	 * @return third element */
	public T3 getThird(){ return third; }
	
	/** Constructor for Triple object */
	public Triple(T1 first, T2 second, T3 third){
		this.first = first;
		this.second = second;
		this.third = third;
	}
}
