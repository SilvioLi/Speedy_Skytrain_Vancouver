package com.speedy.skytrain;

import java.util.ArrayList;

import android.content.Context;

/*
 * Returns a list of nodes relating to each station
 * also returns a list of edge objects relating to the times between stations
 * The times in the edge objects are the weights, if you wanted unweighted graph then
 * simply ignore the data
 */
public class NodeManager {

	private ArrayList<Node> nodes;
	private ArrayList<ArrayList<String>> edges;
	private ArrayList<EdgeObject> edgeObjects;
	private ArrayList<FirstLastTrain> flt;
	private MatsAwesomeXmlParser maxp;
	
	
	public NodeManager(Context ctx) {
		
	
		
		maxp = new MatsAwesomeXmlParser(ctx);
		nodes = maxp.getVertices();
		edges = maxp.getEdges();
		edgeObjects = maxp.getEdgeObjects();
		flt = maxp.getFirstLastTrain();
	}
	
	public ArrayList<EdgeObject> getWeightedEdges(){
		return this.edgeObjects;
	}
	
	public ArrayList<Node> getNodes(){
		return this.nodes;
	}
	
	public ArrayList<FirstLastTrain> getFirstLastTrain(){
		return this.flt;
	}
	
	

}
