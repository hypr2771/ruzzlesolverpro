package com.giacomodrago.ruzzlesolverpro.dictionary;

import java.util.Map;
import java.util.TreeMap;

public class DictionaryNode {

	private final Character letter;
	private final Map<Character, DictionaryNode> children;
	private boolean wordEnding;
	
	public DictionaryNode(Character letter) {
		this.letter = letter;
		this.children = new TreeMap<Character, DictionaryNode>();
	}

	public Character getLetter() {
		return letter;
	}

	public DictionaryNode addChild(Character letter) {
		if (!children.containsKey(letter)) {
			DictionaryNode dictionaryNode = new DictionaryNode(letter);
			children.put(dictionaryNode.getLetter(), dictionaryNode);
			return dictionaryNode;
		} else {			
			return getChild(letter);
		}
	}
	
	public DictionaryNode getChild(Character letter) {
		return children.get(letter);
	}
	
	public boolean isWordEnding() {
		return wordEnding;
	}

	public void setWordEnding(boolean wordEnding) {
		this.wordEnding = wordEnding;
	}
	
}
