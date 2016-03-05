package com.speedy.skytrain;

public class Hashtag {

	private String text;
	private int[] indices;
	
	public Hashtag(String txt, int[] idx) {
		this.text = txt;
		this.indices = idx;
	}
	
	
	public String getText(){
		return this.text;
	}
	
	public int[] getIndices(){
		return this.indices;
	}

}
