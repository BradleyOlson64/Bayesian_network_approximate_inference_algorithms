package bn;


import java.util.ArrayList;
import java.util.Random;

import util.BitVector;
import util.WeightedSet;


/**
 * Represents a boolean random variable in a Bayesian Network
 * 
 * @author Bradley Olson
 * @version 3/16/2019
 *
 */
public class Node {
	
	String name;
	Boolean sampVal;
	Node[] parents;
	WeightedSet cpt;

	/**
	 * Creates a node in a Bayesian network representing a boolean random variable.
	 * The value of the node is initially set to false.
	 * 
	 * @param name
	 * 			The name of the random variable
	 * @param parents
	 * 			The parents of the node in the Bayesian network
	 * @param cpt
	 * 			The conditional probability table
	 * 
	 * @pre The cpt contains an entry for every possible configuration of the parents
	 * @pre The cpt specifies the probability that the node is true given a configuration of the parents  
	 * 
	 */
	public Node(String name, Node[] parents, WeightedSet cpt) {
		this.name = name;
		this.parents = parents;
		this.cpt = cpt;
		sampVal = false;
	}

	/**
	 * Creates a node in a Bayesian network representing a boolean random variable with no parents.
	 * The value of the node is initially set to false.
	 * 
	 * @param name
	 * 			The name of the random variable
	 * @param cpt
	 * 			The conditional probability table
	 * 
	 * @pre The cpt contains exactly 1 entry which is the probability of the random variable being true
	 */
	public Node(String name, WeightedSet cpt) {
		this.name = name;
		this.parents = null;
		this.cpt = cpt;
		sampVal = false;
	}

	/**
	 * Returns the value of the random variable
	 * @return The value of the random variable
	 */
	public boolean getValue() {
		return sampVal;
	}

	/**
	 * Returns the parents of the random variable
	 * @return The parents of the random variable
	 */
	public Node[] getParents() {
		return parents;
	}

	/**
	 * Returns the name of the random variable
	 * @return The name of the random variable
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns p(X = true | parents(X)) 
	 * @return The probability of the random variable being true given the values of its parents 
	 */
	public double getProbability() {
		if(parents != null) {
			// Constructing bit vector to contain values of parents
			BitVector event = new BitVector(parents.length+1);
			for(int i=0;i<parents.length;i++) {
				event.set(i, parents[i].getValue());
			}
			event.set(parents.length, true);
			//Applying bit vector to obtain cpt output
			return cpt.getWeight(event);
		}
		else {
			BitVector event = new BitVector(1);
			event.set(0, true);
			return cpt.getWeight(event);
		}
	}
	
	/**
	 * Sets the value of the random variable to the specified value 
	 * @param newValue
	 * 				The new value of the random variable
	 */
	public void setValue(boolean newValue) {
		sampVal = newValue;
	}

	/**
	 * Samples a value for the random variable conditioned on the configuration of the parents (if parents exist).
	 * If there are no parents, this method samples a value for the random variable from the prior distribution.
	 * 
	 * Sets the value of the node to the outcome of the sample.
	 *  
	 * @pre 
	 * 			sampleAndSet() has already been called on the node's parents (if any exist)
	 * 			
	 * @return
	 * 			The sampled value
	 */
	public boolean sampleAndSet() {
		// Generating random value
		Random r = new Random();
		Double rand = r.nextDouble();
		// Finding probability true
		Double prob = getProbability();
		// Returning
		if(rand <= prob) {
			sampVal = true;
			return true;
		}
		sampVal = false;
		return false;	
	}
	
	/**
	 * Returns a string with the name, value, and cpt of the node.
	 */
	public String toString() {
		String returnString = name + "\n" + "Value:" + sampVal + "\n" + "cpt:" + cpt.toString();
		return returnString;
	}
	
	public static void main(String[] args) {
		// Creating node 1 
		WeightedSet test = new WeightedSet(1);
		ArrayList<BitVector> keys = new ArrayList<>(test.getEvents());
		test.addEvent(keys.get(0), .7);
		test.addEvent(keys.get(1), .3);
		Node original = new Node("Storm", test);
		// Creating node 2 
		Node[] parents = new Node[1];
		parents[0] = original;
		WeightedSet test2 = new WeightedSet(2);
		ArrayList<BitVector> keys2 = new ArrayList<>(test2.getEvents());
		test2.addEvent(keys2.get(0), .7);
		test2.addEvent(keys2.get(1), .3);
		test2.addEvent(keys2.get(2), .75);
		test2.addEvent(keys2.get(3), .25);
		Node child = new Node("Rain", parents, test2);
	}
}
