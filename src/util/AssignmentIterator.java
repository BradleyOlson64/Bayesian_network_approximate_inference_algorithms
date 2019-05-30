package util;

import java.util.Iterator;

import util.BitVector;

/**
 * Iterates over all possible configurations of n boolean random variables. For example, if n = 3
 * the iterator returns: 
 * 
 *  111
 *  110
 *  101
 *  100
 *  011
 *  010
 *  001
 *  000
 * 
 * @author alchambers
 * @version spring2019
 *
 */
public class AssignmentIterator implements Iterator<BitVector>{
	private int size;
	private int maxNoSets;
	private int setCounter;

	/**
	 * Creates a new iterator 
	 * @param n
	 * 			The iterator will iterate over all 2^n settings of n boolean random variables 
	 */
	public AssignmentIterator(int n) {
		if(n >= 10) {
			System.out.println("Supports sizes less than or equal to 10");
			throw new IllegalArgumentException("Input parameter size must be <= 10");
		}
		this.size = n;
		this.maxNoSets = (int)Math.pow(2,  size);
		this.setCounter = 0;
	}


	@Override	
	public boolean hasNext() {
		return setCounter < maxNoSets;		
	}

	@Override
	public BitVector next() {
		BitVector assignment = new BitVector(size);
		
		// Iterate over each bit of the setCounter integer variable
		for(int bit = 0; bit < size; bit++) {
			// We are bit shifting the number 1 (which is 000...0001) so that the 1 bit
			// moves to the correct position. We then perform an AND to pull out the 
			// current bit
			
			if((setCounter & (1 << bit)) == 0){ 
				assignment.set(size-bit-1, true);
			}
			else {
				assignment.set(size-bit-1, false);
			}
		}		
		setCounter++;
		return assignment;
	}
	
	public static void main(String[] args) {
		Iterator<BitVector> itr = new AssignmentIterator(3);
		while(itr.hasNext()) {
			BitVector b = itr.next();
			System.out.println(b);
		}
	}
}
