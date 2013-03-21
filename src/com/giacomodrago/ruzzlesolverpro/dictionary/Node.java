package com.giacomodrago.ruzzlesolverpro.dictionary;

import java.util.Map;
import java.util.TreeMap;

public class Node {

	protected final Character letter;
	protected Map<Character, Node> children;
	protected boolean wordEnding;
	
	public Node(Character letter) {
		this.letter = letter;
	}

	public Character getLetter() {
		return letter;
	}

	public Node addChild(Character letter) {
		if (children == null) {
			children = new TreeMap<Character, Node>();
		}
		if (!children.containsKey(letter)) {
			Node dictionaryNode = new Node(letter);
			children.put(dictionaryNode.getLetter(), dictionaryNode);
			return dictionaryNode;
		} else {			
			return getChild(letter);
		}
	}
	
	public Node getChild(Character letter) {
		if (children == null) {
			return null;
		} else {
			return children.get(letter);
		}
	}
	
	public boolean isWordEnding() {
		return wordEnding;
	}

	public void setWordEnding(boolean wordEnding) {
		this.wordEnding = wordEnding;
	}
	
}
