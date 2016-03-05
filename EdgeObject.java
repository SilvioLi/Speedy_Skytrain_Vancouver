package com.speedy.skytrain;

public class EdgeObject {

	private Node st1;
	private Node st2;
	private long timeBetweenStations;
	
	public EdgeObject() {
		//try not to use this constructor
	}
	
	public EdgeObject(Node one, Node two, long time){
		this.st1 = one;
		this.st2 = two;
		this.timeBetweenStations = time;
	}
	
	public Node getStationOne(){
		return st1;
	}
	
	public Node getStationTwo(){
		return st2;
	}
	
	public long getTimeBetweenStations(){
		return timeBetweenStations;
	}
	
	 public void swap() {
		  Node temp = new Node();
		  temp = st1;
		  st1 = st2;
		  st2 = temp;
		 }
	

}
