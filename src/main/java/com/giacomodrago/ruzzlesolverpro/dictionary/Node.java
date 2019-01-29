package com.giacomodrago.ruzzlesolverpro.dictionary;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Node {

	protected final Character letter;
	protected Map<Character, Node> children;
	protected boolean wordEnding;
	protected Node firstBorn;
	
	public Node(Character letter) {
		this.letter = letter;
	}

	public Character getLetter() {
		return letter;
	}

	public Node addChild(Character letter) {
		
		Node childNode = getChild(letter);
		
		if (childNode == null) {
			childNode = new Node(letter);
			if (firstBorn == null) {
				firstBorn = childNode;
			} else {
				if (children == null) {
					children = new TreeMap<Character, Node>();
				} else if (!children.containsKey(firstBorn.getLetter())) {
					children.put(firstBorn.getLetter(), firstBorn);
				}
				children.put(childNode.getLetter(), childNode);
			}
		}
		
		return childNode;

	}
	
	public Node getChild(Character letter) {
		if (firstBorn != null && firstBorn.getLetter().equals(letter)) {
			return firstBorn;
		}
		if (children == null) {
			return null;
		} else {
			return children.get(letter);
		}
	}
	
	public Map<Character, Node> getChildren() {
		Map<Character, Node> result;
		if (children != null) {
			result = children;
		} else {
			result = new TreeMap<Character, Node>();
			if (firstBorn != null) {
				result.put(firstBorn.getLetter(), firstBorn);
			}
		}
		return Collections.unmodifiableMap(result);
	}
	
	public boolean isWordEnding() {
		return wordEnding;
	}

	public void setWordEnding(boolean wordEnding) {
		this.wordEnding = wordEnding;
	}
	
}
