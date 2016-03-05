package com.speedy.skytrain;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.util.Log;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class JungPlay extends MatsAwesomeXmlParser {
	private Graph<Node, EdgeObject> g;
//	private ArrayList<ArrayList<String>> transferLines;
	private ArrayList<Integer> transferStations;

	JungPlay(Context context) {
		super(context);
		addVerticesEdges();
	}

	public ArrayList<Integer> getTransferStations() {
		return transferStations;
	}

//	public ArrayList<ArrayList<String>> getTransferLines() {
//		return transferLines;
//	}

	private void addVerticesEdges() {
		g = new SparseMultigraph<Node, EdgeObject>();
		try {
			for (int i = 0; i < getVertices().size(); i++) {
				g.addVertex(getVertices().get(i));
			}

		} catch (Exception e) {
			//Log.d("ERROR", "VERTICES:: " + e.toString());
		}

		try {
			for (int i = 0; i < getEdgeObjects().size(); i++) {
				g.addEdge(getEdgeObjects().get(i), getEdgeObjects().get(i)
						.getStationOne(), getEdgeObjects().get(i)
						.getStationTwo());
			}
		} catch (Exception e) {
			//Log.d("ERROR", "Edges:: " + e.toString());

		}

	}

	ArrayList<Node> shortestPath(String from, String to) {
		Node From = null, To = null;
		boolean flag = false;
		for (int i = 0; i < getVertices().size(); i++) {
			if (getVertices().get(i).getName().equals(from)) {
				From = getVertices().get(i);
				flag = true;
			}
			if (getVertices().get(i).getName().equals(to)) {
				To = getVertices().get(i);
				if (flag)
					break;
			}
		}
		DijkstraShortestPath<Node, EdgeObject> alg = new DijkstraShortestPath<Node, EdgeObject>(
				g);
		ArrayList<Node> vertices = new ArrayList<Node>();
		ArrayList<EdgeObject> edgePath = new ArrayList<EdgeObject>(alg.getPath(
				From, To));
		EdgePathAdjuster(edgePath, from, to);
		for (EdgeObject a : edgePath) {
			vertices.add(a.getStationOne());
		}
		vertices.add(edgePath.get(edgePath.size() - 1).getStationTwo());
		transferStations(vertices);
		//debugger(vertices, edgePath);
		return vertices;
	}

	/*
	 * We have some issues with the stations order. The order of the stations in
	 * EdgeObjects may be wrong as, basically, the order is hardcoded. Maybe
	 * there's a better way of doing this, but we just enjoy the process :)
	 * Hence I swap the wrongly ordered elements in the following method
	 * (EdgePathAdjuster)
	 */
	private void EdgePathAdjuster(ArrayList<EdgeObject> edgePath, String from,
			String to) {
		if (!edgePath.get(0).getStationOne().getName().equals(from))
			edgePath.get(0).swap();
		for (int i = 1; i < edgePath.size(); i++)
			if (!edgePath.get(i).getStationOne().getName()
					.equals(edgePath.get(i - 1).getStationTwo().getName()))
				edgePath.get(i).swap();
	}

	/*
	 * The following method (thePathTester) is for debugging. Basically, it just
	 * prints the list of station in the log.
	 */
	// @SuppressWarnings("unused")
	private void debugger(ArrayList<Node> vertices, ArrayList<EdgeObject> edges) {
		 Log.d("PathTester", "First: " + vertices.get(0).getName());
		for (EdgeObject a : edges) {
			Log.d("PathTester", "EDGES: " + a.getStationOne().getName());
		}
		Log.d("PathTester", "EDGES: "
				+ edges.get(edges.size() - 1).getStationTwo().getName());
		Log.d("PathTester", "===============================\n"
				+ "===============================\n"
				+ "===============================\n"
				+ "===============================");
		for (Node a : vertices)
			Log.d("PathTester", "VERTICES: " + a.getName());
		Log.d("PathTester", "===============================\n"
				+ "===============================\n"
				+ "===============================\n"
				+ "===============================");
		Log.d("PathTester", "transferStations.size = " + transferStations.size());
		Log.d("PathTester", "Here's the list of all transferStations:");
		for (int i : transferStations) {
			Log.d("PathTester", "Station number " + i + ": "
					+ vertices.get(i).getName());
		}
		Log.d("PathTester", "===============================\n"
				+ "===============================\n"
				+ "===============================\n"
				+ "===============================");
	}

	/*
	 * This method, I believe, is the most complex one. There are simple ways of
	 * doing this, but I'd like to try making the code universal. So it could
	 * work with any city, not just Vancouver. Okay, I tried a few approaches.
	 * This one looks the most interesting one. I know, the architecture is
	 * wrong. We should've change the way the stations, edges and graph are
	 * implemented. Currently we treat a station as a Node(Vertex). The right
	 * way of doing this is to treat the combination of the station and line as
	 * a vertex. It would be beneficial for complex subway systems (I analyzed
	 * the one in New York). Well, inicially we planned to make it work only for
	 * Vancouver Skytrain, so it's fine.
	 */
	// @SuppressWarnings("unused")
	private void transferStations(ArrayList<Node> vertices){
		Integer cb = -1;
		ArrayList<String> linesB = new ArrayList<String>(Arrays.asList(vertices
				.get(0).getLine()));
		//transferLines = new ArrayList<ArrayList<String>>();
		transferStations = new ArrayList<Integer>();
		linesB = intersection(
				new ArrayList<String>(Arrays.asList(vertices.get(0).getLine())),
				new ArrayList<String>(Arrays.asList(vertices.get(1).getLine())));
		for (int i = 1; i < vertices.size() - 1; i++) {
			if (vertices.get(i).getName().equals("Commercial-Broadway"))
				cb = i;
			linesB = intersection(
					linesB,
					new ArrayList<String>(Arrays.asList(vertices.get(i + 1)
							.getLine())));
			if (linesB.size() < 1) {
				//transferLines.add(new ArrayList<String>(linesA));
				transferStations.add((Integer) i);
				linesB = intersection(
						new ArrayList<String>(Arrays.asList(vertices.get(i)
								.getLine())),
						new ArrayList<String>(Arrays.asList(vertices.get(i + 1)
								.getLine())));
			} else {
			}
		}
		//transferLines.add(new ArrayList<String>(linesB));
		if (cb > -1) {
			VancouverOnly(vertices, cb);
		}
	}

	/*
	 * I have to do some extra hardcoding just for Vancouver because of its
	 * weird Commercial-Broadway station. It is weird because it has
	 * semiredundant platforms. Each of which has the Millenium Line. I know
	 * there's a much more elegant way of fixing this, but historically we
	 * didn't really think about the transfer stations...
	 */
	private void VancouverOnly(ArrayList<Node> vertices, Integer cb) {
		ArrayList<String> Names = new ArrayList<String>();
		// I have to do this due to the architecture flaws
		for (Node a : vertices) {
			Names.add(a.getName());
		}
		if (transferStations.contains("Commercial-Broadway"))
			return;// Having more than one return in a method is comfortable.
		// Now we start hardcoding :(
		if ((Names.contains("Main Street-Science World") && Names
				.contains("Renfrew"))
				|| (Names.contains("Main Street-Science World") && Names
						.contains("VCC-Clark")))
			transferStations.add(cb);//One way... Now the other way around...
		if ((Names.contains("Nanaimo") && Names
				.contains("Renfrew"))
				|| (Names.contains("Nanaimo") && Names
						.contains("VCC-Clark")))
			transferStations.add(cb);
	}

	/*
	 * Nice intersection method.
	 */
	private ArrayList<String> intersection(ArrayList<String> list1,
			ArrayList<String> list2) {
		ArrayList<String> result = new ArrayList<String>(list1);
		result.retainAll(list2);
		return result;
	}
}