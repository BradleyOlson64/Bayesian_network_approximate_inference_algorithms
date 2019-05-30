package tui;

import java.util.Scanner;
import java.util.Set;

import bn.BayesianNetwork;
import bn.Node;
import util.WeightedSet;
import util.BitVector;

/**
 * This class provides a text user interface for querying a Bayesian network.
 * 
 * @author alchambers
 * @version spring19
 */
public class InferenceEngine {	
	private static final int DIRECT_SAMPLING = 1;
	private static final int REJECTION_SAMPLING = 2;
	private static final int LIKELIHOOD_WEIGHTING = 3;

	private static BayesianNetwork bn;
	private static Scanner scanner;
	private static int numSamples;
	private static int inferenceMethod;	


	// Prints result of a query or error message if query is ill-formed
	private static void printDistribution(Query q, WeightedSet d) {
		Node[] nodes = bn.getNodes();

		String evidence = "";
		if(q.evidenceVariables != null) {
			evidence = q.constructEvidenceString();
		}

		Set<BitVector> events = d.getEvents();
		for(BitVector event : events) {
			String outside = "p(";
			String inside = "";
			int position = 0;				

			for(Node n : nodes) {
				if(q.queryVariables.contains(n.getName())) {
					inside += " " + n.getName() + "=" + event.get(position) + ",";
					position++;
				}
			}		
			inside = inside.trim();
			if(inside.equals("")) {
				printError();
				return;
			}
			inside = inside.substring(0, inside.length()-1);

			if(!evidence.equals("")) {
				inside += " | " + evidence;
			}
			inside += ") = " + d.getWeight(event);
			System.out.println(outside + inside);
		}		
	}

	// Prints an error message
	private static void printError() {
		System.out.println("Invalid Input\n");
	}

	// Reads the user's preferences and sets up the entire system
	private static void readAndSetPreferences(String filename) {
		Node[] nodes = Reader.read(filename);
		bn = new BayesianNetwork(nodes);		
		scanner = new Scanner(System.in);

		// Welcome message
		System.out.println("===== Welcome to the Inference Engine =====");
		System.out.println("The Bayesian network being analyzed is: " + filename );

		System.out.println("\nThe nodes in the network are:");
		for(Node n : nodes) {
			System.out.println(n.getName());
		}		
		System.out.println("\n");

		// Select inference method
		System.out.println("Approximate Inference Methods: ");
		System.out.println("1. Direct sampling (no evidence)");
		System.out.println("2. Rejection sampling (requires evidence)");
		System.out.println("3. Likelihood Weighting");
		System.out.print("Choose an inference method: ");

		inferenceMethod = -1;
		while(inferenceMethod == -1) {
			try{
				inferenceMethod = Integer.parseInt(scanner.nextLine());
			}
			catch(NumberFormatException e) {
				System.out.print("Choose an inference method: ");
			}			
		}

		// Set number of samples
		System.out.print("Enter the number of samples: ");
		numSamples = -1;
		while(numSamples == -1) {
			try {
				numSamples = Integer.parseInt(scanner.nextLine());
			}
			catch(NumberFormatException e) {
				System.out.print("Enter the number of samples: ");
			}		
		}		
		System.out.println();
	}


	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Usage: java InferenceEngine <file>");
			System.exit(-1);
		}
		readAndSetPreferences(args[0]);

		while(true) {
			System.out.print("Enter a query (or type \"quit\"): ");
			String input = scanner.nextLine().toLowerCase();
			if(input.equalsIgnoreCase("quit")) {
				break;
			}			

			// Process the query
			Query q = Query.processQuery(input);
			if(q == null) {
				printError();
				continue;
			}

			// Create a new weighted set to hold the distribution
			WeightedSet d = null;

			// Perform Direct Sampling
			if(inferenceMethod == DIRECT_SAMPLING) {
				if(q.evidenceVariables != null) {
					System.out.println("Error: Your query cannot contain evidence\n");
					continue;
				}
				d = bn.directSample(q, numSamples);								
			}
			
			// Perform Rejection sampling or Likelihood Weighting
			else {
				if(q.evidenceVariables == null) {
					System.out.println("Error: Your query must contain evidence\n");
					continue;
				}
				if(inferenceMethod == REJECTION_SAMPLING) {
					d = bn.rejectionSampling(q, numSamples);										
				}
				else if(inferenceMethod == LIKELIHOOD_WEIGHTING) {
					d = bn.likelihoodWeighting(q, numSamples);
				}
			}
			
			// Print the query distribution
			if(d != null) {
				printDistribution(q, d);
			}
			System.out.println();
		}

		System.out.println("\n===== Exiting the Inference Engine =====");
	}
}
