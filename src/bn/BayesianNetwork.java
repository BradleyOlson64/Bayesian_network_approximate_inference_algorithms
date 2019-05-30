package bn;

import java.util.ArrayList;

import tui.Query;
import tui.Reader;
import util.BitVector;
import util.WeightedSet;

/**
 * Represents a generic Bayesian Network of boolean random variables
 * 
 * @author Bradley Olson
 * @version 3/16/2019
 *
 */
public class BayesianNetwork {	

	private Node[] nodes;
	
	/**
	 * Constructs a new Bayesian network with the given nodes. 
	 * 
	 * @param nodes The nodes in the Bayesian network
	 * 
	 * @pre The nodes are listed in topological order
	 */
	public BayesianNetwork(Node[] nodes) {
		this.nodes = nodes;
	}

	
	/**
	 * Returns the nodes in the Bayesian network
	 * @return The nodes in the Bayesian network
	 */
	public Node[] getNodes() {
		return nodes;
	}	

	
	/**
	 * Approximates the query using direct sampling
	 * 
	 * @param q
	 * 			The query
	 * 
	 * @param numSamples
	 * 			The number of samples for direct sampling
	 * @return
	 * 			A probability distribution over the query variables
	 */
	public WeightedSet directSample(Query q, int numSamples) {
		for(Node node : nodes) {
			System.out.println(node.toString());			
		}
		// Initializing weighted set 
		WeightedSet results = new WeightedSet(q.queryVariables.size());
		BitVector result = new BitVector(q.queryVariables.size());
		// looping through samples
		for(int i=0;i<numSamples;i++) {
			int count = 0;
			for(Node node : nodes) {
				node.sampleAndSet();
				for(String queryVariable: q.queryVariables) {
					if(node.getName().equals(queryVariable)) {
						result.set(count, node.getValue());
						count++;
					}
				}
			}
			results.increment(result, 1);
		}
		results.normalizeWeights();
		return results;
	}

	
	/**
	 * Approximates the query using rejection sampling
	 * 
	 * @param q
	 * 			The query
	 * 
	 * @param numSamples
	 * 			The number of samples for rejection sampling
	 * @return
	 * 			A probability distribution over the query variables
	 */

	public WeightedSet rejectionSampling(Query q, int numSamples) {		
		// Initializing weighted set 
		WeightedSet results = new WeightedSet(q.queryVariables.size());
		BitVector result = new BitVector(q.queryVariables.size());
		// looping through samples
		for(int i=0;i<numSamples;i++) {
			int count = 0;
			Boolean valid = true;
			for(Node node : nodes) {
				node.sampleAndSet();
				for(String evidenceVariable: q.evidenceVariables) {
					if(node.getName().equals(evidenceVariable) && node.getValue() != q.evidenceValues.get(evidenceVariable)) valid = false;
				}
				for(String queryVariable: q.queryVariables) {
					if(node.getName().equals(queryVariable)) {
						result.set(count, node.getValue());
						count++;
					}
				}
			}
			if(valid == true) {
				results.increment(result, 1);
			}
		}
		results.normalizeWeights();
		return results;
	}

	
	/**
	 * Approximates the query using likelihood weighting
	 * 
	 * @param q
	 * 			The query
	 * 
	 * @param numSamples
	 * 			The number of samples for likelihood weighting
	 * @return
	 * 			A probability distribution over the query variables
	 */

	public WeightedSet likelihoodWeighting(Query q, int numSamples) {
		// Initializing weighted set 
		WeightedSet results = new WeightedSet(q.queryVariables.size());
		BitVector result = new BitVector(q.queryVariables.size());
		for(int i=0;i<numSamples;i++) {
			int count = 0;
			Double weight = 1.00;
			for(Node node : nodes) {
				// determining if evidence variable
				Boolean isEvidence = false;
				for(String evidenceVariable: q.evidenceVariables) {
					if(node.getName().equals(evidenceVariable)) {
						Boolean outcome = q.evidenceValues.get(evidenceVariable);
						isEvidence = true;
						if(outcome == true) weight = weight * node.getProbability();
						if(outcome == false) weight = weight * (1- node.getProbability());
						node.setValue(outcome);
					}
				}
				// workin it
				if(!isEvidence){
					node.sampleAndSet();
					for(String queryVariable: q.queryVariables) {
						if(node.getName().equals(queryVariable)) {
							result.set(count, node.getValue());
							count++;
						}
					}
				}
			}
			results.increment(result, weight);
		}
		results.normalizeWeights();
		return results;
	}
	
	public static void main(String[] args) {
		// Forging components
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
		test2.addEvent(keys2.get(1), .75);
		test2.addEvent(keys2.get(2), .3);
		test2.addEvent(keys2.get(3), .25);
		Node child = new Node("Rain", parents, test2);
		System.out.println(test2.toString());
		
		// Creating bn
		Node[] nodes = new Node[2];
		nodes[0] = original;
		nodes[1] = child;
		BayesianNetwork stormRain = new BayesianNetwork(nodes);
		
		// Creating query
		Query q1 = Query.processQuery("p(Storm)");
		Query q2 = Query.processQuery("p(Rain | !Storm)");
		// Testing direct sample
		WeightedSet resultsD = stormRain.directSample(q1, 10000);
		WeightedSet resultsR = stormRain.rejectionSampling(q2, 10000);
		WeightedSet resultsL = stormRain.likelihoodWeighting(q2, 10000);
		System.out.println(resultsD.toString());
		System.out.println(resultsR.toString());
		System.out.println(resultsL.toString());
	}
}
