package com.speedy.skytrain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;


/*
 * On construction, will parse the xml code and create objects to hold the data
 * calling each method will return that data type
 * 
 */
public class MatsAwesomeXmlParser {

	private static final String STATION_NAME = "name";
	private static final String LINE_ONE = "line1";
	private static final String LINE_TWO = "line2";
	private static final String LINE_THREE = "line3";
	private static final String FARE_ZONE = "zone";
	private static final String LONGITUDE = "long";
	private static final String LATITUDE = "lat";
	private static final String TOURIST = "info";
	
	private Context context;
	
	private ArrayList<Node> vertices;
	private ArrayList<ArrayList<String>> edgeArray;
	private ArrayList<EdgeObject> edgeObjects;
	private ArrayList<FirstLastTrain> firstLastTrain;
	
	
	MatsAwesomeXmlParser(Context ctx){
		//context is passed in order to reference the assets folder for xml parsing, if this doesn't work you can create
		//a constructor which passes the input stream through the constructor
		this.context = ctx;
		doXMLStuff();
	}
	
	
	
	private void doXMLStuff() {
		vertices = new ArrayList<Node>();
		edgeArray = new ArrayList<ArrayList<String>>();
		edgeObjects = new ArrayList<EdgeObject>();
		firstLastTrain = new ArrayList<FirstLastTrain>();
		
		try {
			//Log.d("my application", "wow this works");
			boolean linesLoaded;
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();
			InputStream is = context.getApplicationContext().getAssets()
					.open("node.xml"); // will access the firefrom the assets
										// directory. NOT THE RES/RAW

			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(is, null);
			linesLoaded = true; 
			int eventType = parser.getEventType();
			String data;
			while (eventType != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
				case XmlPullParser.START_TAG:
					data = parser.getName();

					if (data.equals("station")) {

						String name = null;
						String[] stations = new String[3];
						int farezone = -1;
						double longitude = 0.0;
						double latitude = 0.0;
						String info = null;
						
						
						for(int i = 0; i < parser.getAttributeCount(); i++){
							String value = parser.getAttributeName(i);

							if(value.equals(STATION_NAME)){
								name = parser.getAttributeValue(i);
							} else if(value.equals(LINE_ONE)){
								stations[0] = parser.getAttributeValue(i);
							} else if(value.equals(LINE_TWO)){
								stations[1] = parser.getAttributeValue(i);
							} else if(value.equals(LINE_THREE)){
								stations[2] = parser.getAttributeValue(i);
							} else if(value.equals(FARE_ZONE)){
								farezone = Integer.parseInt(parser.getAttributeValue(i));
							} else if(value.equals(LONGITUDE)){
								longitude = Double.parseDouble(parser.getAttributeValue(i));
							} else if(value.equals(LATITUDE)){
								latitude = Double.parseDouble(parser.getAttributeValue(i));
							} else if(value.equals(TOURIST)){
								info = parser.getAttributeValue(i);
							}
							else {
								
							}
							
							
							
						}
						
						Node n = new Node(name, stations, farezone, longitude, latitude, info);
						
						vertices.add(n);
						
					} else if (data.equals("edge")) {

						ArrayList<String> edge = new ArrayList<String>();
						edge.add(parser.getAttributeValue(0));
						edge.add(parser.getAttributeValue(1));
						edgeArray.add(edge);
						
						Node s1 = null;
						Node s2 = null;
						int time = -1;
						for(Node n: vertices){
							if(n.getName().equals(parser.getAttributeValue(0))){
								s1 = n;
							}
							if(n.getName().equals(parser.getAttributeValue(1))){
								s2 = n;
							}
						}
						time = Integer.parseInt(parser.getAttributeValue(2));
						EdgeObject eo = new EdgeObject(s1, s2, time);
						edgeObjects.add(eo);
						

					} else if(data.equals("firsttrain") || data.equals("lasttrain")){
						
						FirstLastTrain flt;
						boolean firstTrain = data.equals("firsttrain");
						String line = parser.getAttributeValue(0);
						String to = parser.getAttributeValue(1);
						String from = parser.getAttributeValue(2);
						String week = parser.getAttributeValue(3);
						String sat = parser.getAttributeValue(4);
						String holiday = parser.getAttributeValue(5);
						
						flt = new FirstLastTrain(firstTrain,line,to,from,week,sat,holiday);
						
						firstLastTrain.add(flt);
						
					}

					break;

				case XmlPullParser.END_TAG:
					break;

				default:
				}

				eventType = parser.next();

			}
		/*	
			 for(ArrayList<String> ar: edgeArray){
			 Log.d("my application", ar.toString());
			 }
			
			 for(Node n: vertices){
			 Log.d("my program!", n.getName());
			 }
			*/

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/*
	 * returns an object which holds all of the vertices parsed from xml
	 */
	public ArrayList<Node> getVertices(){
		
		return this.vertices;
	}
	
	/*
	 * returns an object which holds all of the edges parsed from xml
	 */
	public ArrayList<ArrayList<String>> getEdges(){
		return this.edgeArray;
	}
	
	public ArrayList<EdgeObject> getEdgeObjects(){
		return this.edgeObjects;
	}
	
	public ArrayList<FirstLastTrain> getFirstLastTrain(){
		return this.firstLastTrain;
	}
	
}
