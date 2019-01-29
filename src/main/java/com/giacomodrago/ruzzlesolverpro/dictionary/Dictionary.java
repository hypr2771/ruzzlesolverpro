package com.giacomodrago.ruzzlesolverpro.dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Dictionary {

	protected final Node rootNode;
	protected final DictionaryStatsListener statsListener;

	public Dictionary(String filePath, DictionaryStatsListener statsListener)
			throws IOException {
		
		this.rootNode = new Node(null);
		this.statsListener = statsListener;
		
		if (statsListener != null) {
			statsListener.createdOrAccessedNode(rootNode);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			this.getClass().getResourceAsStream(filePath)));

		String word;
		while ((word = br.readLine()) != null) {
			word = word.trim();
			if (!word.isEmpty()) {
				add(word);
			}
		}

		br.close();
		
	}
	
	public Dictionary(String filePath) throws IOException {
		this(filePath, null);
	}

	public void add(String word) {

		word = word.toLowerCase();

		Node currentNode = rootNode;
		
		for (int i = 0; i < word.length(); i++) {
			char letter = word.charAt(i);
			currentNode = currentNode.addChild(letter);
			if (statsListener != null) {
				statsListener.createdOrAccessedNode(currentNode);
			}
		}
		
		currentNode.setWordEnding(true);

	}

	public Node getRootNode() {
		return rootNode;
	}

	public Node find(String sequence) {
		
		Node currentNode = rootNode;
		
		for (int i = 0; i < sequence.length() && currentNode != null; i++) {
			char letter = sequence.charAt(i);
			currentNode = currentNode.getChild(letter);
		}
		
		return currentNode;
		
	}

}
