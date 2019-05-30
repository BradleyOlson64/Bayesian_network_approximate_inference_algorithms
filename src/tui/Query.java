package tui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a probabilistic query of the following forms:
 * p(X)
 * p(X | y)
 * p(X | !y)
 * 
 * @author alchambers
 *
 */
public class Query {
	public Set<String> queryVariables;
	public Set<String> evidenceVariables;
	public HashMap<String, Boolean> evidenceValues;
	
	public Query() {
		queryVariables = null;
		evidenceVariables = null;
		evidenceValues = null;
	}

	@Override
	public String toString() {
		String str = "Query variables: \n";
		for(String q : queryVariables) {
			str += "\t" + q;
		}

		if(evidenceVariables != null) {
			str += "\nEvidence variables: \n";
			for(String e : evidenceVariables) {
				str += "\t" + e + " with value " + evidenceValues.get(e);
			}
		}
		return str;
	}
	
	public String constructEvidenceString() {
		String s = "";
		for(String e : evidenceVariables) {
			if(evidenceValues.get(e)) {
				s += e + ", "; 
			}else {
				s += "!" + e + ", ";
			}
		}
		s = s.trim();
		s = s.substring(0, s.length()-1);
		return s;
	}
	
	/**
	 * Constructs a query object from a string representation of the query
	 * 
	 * @param query 
	 * 			A string representation of a query, e.g. p(W|X)
	 * 
	 *  @return
	 *  		A query object or null if the query was ill-formed
	 */
	public static Query processQuery(String query) {
		int openParenIndex = query.indexOf("(");
		int closeParenIndex = query.indexOf(")");
		int pipeIndex = query.indexOf("|");
		
		if(openParenIndex == -1 || closeParenIndex == -1 || (openParenIndex > closeParenIndex)) {
			return null;
		}
		
		Query q = new Query();		
		
		// Store the query variables
		q.queryVariables = new HashSet<String>();
		int endIndex = (pipeIndex == -1) ? closeParenIndex : pipeIndex;		
		String[] variables = query.substring(openParenIndex+1, endIndex).split(",");	
		for(int i = 0; i < variables.length; i++) {
			q.queryVariables.add(variables[i].trim());
		}

		// Store the evidence variables
		if(pipeIndex != -1) {
			q.evidenceVariables = new HashSet<String>();	
			q.evidenceValues = new HashMap<String, Boolean>();
			
			variables = query.substring(pipeIndex+1,closeParenIndex).split(",");
			for(int i = 0; i < variables.length; i++) {
				String name = variables[i].trim();
				boolean value = true;
				if(name.startsWith("!")) {
					name = name.substring(1);
					value = false;
				}
				q.evidenceVariables.add(name);
				q.evidenceValues.put(name, value);
			}
		}
		return q;		
	}
}
