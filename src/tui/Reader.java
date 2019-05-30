package tui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import bn.Node;
import util.AssignmentIterator;
import util.BitVector;
import util.WeightedSet;

/**
 * Responsible for reading and parsing Bayesian network files (with extension .bn) 
 * @author alchambers
 *
 */
public class Reader {
	
	public static Node[] read(String filename) {
		Node[] nodes = null;
		
		try {
			Scanner scanner = new Scanner(new File(filename));

			// The first line in the file is the number of nodes in the Bayesian Network
			int numNodes = Integer.parseInt(scanner.nextLine());
			nodes = new Node[numNodes];
			HashMap<String, Node> mapping = new HashMap<>();
			
			
			// The remaining lines in the file specify the CPT
			int nodeId = 0;
			while(scanner.hasNext()) {
				String line = scanner.nextLine().toLowerCase();
				Node node = null;

				// Node has no parents in the BN
				if(!line.contains("|")) {
					String prob = scanner.nextLine();
					WeightedSet cpt = new WeightedSet(1);
					cpt.addEvent(BitVector.TRUE, Double.parseDouble(prob));
					// TODO: THERE IS AN INCONSISTENCY HERE
					// THE WEIGHTED SET CONTAINS TWO EVENTS {TRUE, FALSE}
					// HOWEVER, WE HAVE UPDATED THE PROBABILITY OF TRUE 
					// BUT WE LEFT THE PROBABILITY OF FALSE TO BE THE DEFAULT 0.0
					// WHAT TO DO?
					BitVector falseVec = new BitVector(1);
					falseVec.set(0, false);
					Double falseVal = 1.000 - Double.parseDouble(prob);
					cpt.addEvent(falseVec, falseVal);
					node = new Node(line, cpt);
				}
				else {
					String[] parts = line.split("\\|");
					if(parts.length != 2) {
						// throw error
					}			
					String childName = parts[0];			
					String[] parentNames = parts[1].split(",");

					Node[] parents = new Node[parentNames.length];
					for(int i = 0; i < parentNames.length; i++) {												
						parents[i] = mapping.get(parentNames[i]);						
					}									
					
					WeightedSet cpt = new WeightedSet(parents.length + 1);					
					Iterator<BitVector> itr = new AssignmentIterator(parents.length + 1);
					while(itr.hasNext()) {						
						BitVector assignment = itr.next();
						double probTrue = Double.parseDouble(scanner.nextLine());
						cpt.addEvent(assignment, probTrue);	
						BitVector assignmentFalse = itr.next();
						cpt.addEvent(assignmentFalse, 1.00 - probTrue);
					}					
					node = new Node(childName, parents, cpt); // has parents
				}
				nodes[nodeId] = node;		
				mapping.put(node.getName(), node);
				nodeId++;
			}
			scanner.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("Error: Could not find file \"" +filename+ "\".");
			System.exit(-1);
		}
		catch(NumberFormatException e) {
			System.err.println("Error: File has incorrect format (double/integer expected)");
			System.exit(-1);
		}		
		return nodes;
	}
}

