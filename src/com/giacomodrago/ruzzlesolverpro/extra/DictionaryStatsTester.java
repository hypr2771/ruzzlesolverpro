package com.giacomodrago.ruzzlesolverpro.extra;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.dictionary.DictionaryStatsListener;
import com.giacomodrago.ruzzlesolverpro.dictionary.Node;

public class DictionaryStatsTester {

	private static final String DICTIONARY_PATH = "languages/Italiano/dictionary.txt";

	public static void main(String[] args) throws IOException {

		final Set<Node> nodes = new HashSet<Node>();

		DictionaryStatsListener statsListener = new DictionaryStatsListener() {
			@Override
			public void createdOrAccessedNode(Node node) {
				nodes.add(node);
			}
		};

		long startTime = System.currentTimeMillis();
		new Dictionary(DICTIONARY_PATH, statsListener);
		long elapsedTime = System.currentTimeMillis() - startTime;

		System.out.println("Elapsed time: " + elapsedTime + " ms");

		int noNodes = nodes.size();

		int noNodesWithoutChildren = 0;
		int noNodesWithOneChild = 0;
		int noNodesWithMoreThanOneChild = 0;

		for (Node node : nodes) {
			Map<Character, Node> children = node.getChildren();
			if (children.size() == 0) {
				noNodesWithoutChildren++;
			} else if (children.size() == 1) {
				noNodesWithOneChild++;
			} else {
				noNodesWithMoreThanOneChild++;
			}
		}

		System.out.println("# nodes: " + noNodes);
		System.out.println("-> # nodes without children: "
				+ noNodesWithoutChildren);
		System.out.println("-> # nodes with 1 child: " + noNodesWithOneChild);
		System.out.println("-> # nodes with >1 child "
				+ noNodesWithMoreThanOneChild);

	}

}
