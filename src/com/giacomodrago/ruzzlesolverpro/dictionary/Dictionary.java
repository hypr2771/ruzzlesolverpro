package com.giacomodrago.ruzzlesolverpro.dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Dictionary {

	protected final Node rootNode;

	public Dictionary(String filePath) throws IOException {

		rootNode = new Node(null);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath)));

		String word;
		while ((word = br.readLine()) != null) {
			word = word.trim();
			if (!word.isEmpty()) {
				add(word);
			}
		}

		br.close();

	}

	public void add(String word) {

		word = word.toLowerCase();

		Node currentNode = rootNode;
		
		for (int i = 0; i < word.length(); i++) {
			char letter = word.charAt(i);
			currentNode = currentNode.addChild(letter);
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
