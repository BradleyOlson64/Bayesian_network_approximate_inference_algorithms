package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This is a general purpose class that maps configurations (of n boolean random variables) to a numerical value.
 * The meaning of the numerical value depends upon the usage. The numerical value could be:
 * 
 * - A tally (e.g. for direct sampling or rejection sampling)
 * - A weight (e.g. for likelihood weighting)
 * - A probability (e.g. for a CPT)
 * 
 * Thus, if n=2 and the numerical values were probabilities, the weighted set would contain the following
 * configurations:
 * 
 * 				{TT, TF, FT, FF}
 * 
 * and each assignment would be associated with a probability. 
 *  
 * 
 * @author Bradley Olson
 * @version 3/16/19
 *
 */
public class WeightedSet {
	private int numVars;
	private HashMap<BitVector,Double> set;
	
	/**
	 * Creates a new weighted set that contains all possible configurations of n boolean random variables
	 * 
	 * @param n
	 * 			The number of boolean random variables 
	 * @pre 
	 * 			The weighted set contains all possible configurations
	 */
	public WeightedSet(int n) {	
			numVars = n;
			set = new HashMap<>();
			AssignmentIterator iterate = new AssignmentIterator(n);
			while(iterate.hasNext()) {
				BitVector next = iterate.next();
				set.put(next, 0.0);
			}
	}

	/**
	 * Adds the event with corresponding weight to the set
	 * @param event
	 * 				An event
	 * @param weight
	 * 				A numerical weight
	 */
	public void addEvent(BitVector event, double weight) {
		set.put(event, weight);	
	}

	/**
	 * Increments the weight of the event by the specified amount
	 * 
	 * @param event
	 * 				An event
	 * @param amount
	 * 				An amount by which to increase the weight
	 */
	public void increment(BitVector event, double amount) {
		Double curr = set.get(event);
		Double next = curr + amount;
		set.put(event,next);
	}

	/**
	 * Returns the weight of the particular event
	 * 
	 * @param event
	 * 				An event
	 * @return The weight of the specified event
	 */
	public double getWeight(BitVector event) {
		return set.get(event);
	}

	/**
	 * Returns the set of all events 
	 * @return The set of all events
	 */
	public Set<BitVector> getEvents() {
		return set.keySet();
	}

	/**
	 * Normalizes the weights so that they sum to 1
	 */
	public void normalizeWeights() {
		ArrayList<BitVector> keys = new ArrayList<>(set.keySet());
		ArrayList<Double> values = new ArrayList<>(set.values());
		Double sum = 0.000;
		for(Double value : values) {
			sum += value;
		}
		if(sum > 0.000) {
			for(int i=0;i<values.size();i++) {
				set.put(keys.get(i), values.get(i)/sum);
			}
		}
	}
	/**
	 * This method returns a string representation of the weighted set.
	 */
	@Override
	public String toString() {
		String returnString = "";
		ArrayList<BitVector> keys = new ArrayList<>(set.keySet());
		ArrayList<Double> values = new ArrayList<>(set.values());
		for(int i=0;i<keys.size();i++) {
			returnString += keys.get(i).toString() + " --> " + values.get(i) + "\n";
		}
		return returnString;
	}
	
	public static void main(String[] args) {
		WeightedSet test = new WeightedSet(2);
//		System.out.println(test.toString());
		ArrayList<BitVector> keys = new ArrayList<>(test.getEvents());
		test.addEvent(keys.get(0), 1);
		test.addEvent(keys.get(1), 2);
		test.addEvent(keys.get(2), 3);
		test.addEvent(keys.get(3), 4);
		test.normalizeWeights();
		System.out.println(test.toString());
	}

}
