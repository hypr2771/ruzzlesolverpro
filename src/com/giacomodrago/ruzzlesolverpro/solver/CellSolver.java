package com.giacomodrago.ruzzlesolverpro.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.dictionary.DictionaryNode;

class CellSolver implements Runnable {

	protected final Dictionary dictionary;
	protected final int maxWordLen;
	protected final int minWordLen;
	protected final ExtendedCell cell;
	protected final Collection<Path> paths;

	public CellSolver(Dictionary dictionary, int maxWordLen, int minWordLen,
			ExtendedCell cell, Collection<Path> paths) {
		super();
		this.dictionary = dictionary;
		this.maxWordLen = maxWordLen;
		this.minWordLen = minWordLen;
		this.cell = cell;
		this.paths = paths;
	}

	@Override
	public void run() {

		DictionaryNode dictNode = dictionary.getRootNode().getChild(
				cell.getLetter());
		LinkedList<Cell> currentPath = new LinkedList<Cell>();
		currentPath.add(cell);
		Set<Cell> traversedCellsSet = new HashSet<Cell>();
		traversedCellsSet.add(cell);
		findPaths(cell, dictNode, currentPath, traversedCellsSet, paths);

	}
	

	protected void findPaths(ExtendedCell currentCell, DictionaryNode dictNode,
			LinkedList<Cell> currentPath, Set<Cell> traversedCellsSet,
			Collection<Path> paths) {

		if (currentPath.size() > maxWordLen) {
			return;
		}

		// Test this word
		if (currentPath.size() >= minWordLen) {
			if (dictNode.isWordEnding()) { // this is an ending node, so it's a word
				StringBuffer word = new StringBuffer();
				for (Cell cell : currentPath) {
					word.append(cell.getLetter());
				}
				Path path = new Path();
				path.setWord(word.toString());
				path.setTraversedCells(new ArrayList<Cell>(currentPath));
				paths.add(path);
			}
		}

		// Make it longer
		for (ExtendedCell nextCell : currentCell.getNeighbors()) {
			if (!currentPath.contains(nextCell)) {
				char nextLetter = nextCell.getLetter();
				DictionaryNode nextDictNode = dictNode.getChild(nextLetter);
				if (nextDictNode != null) {
					currentPath.add(nextCell);
					traversedCellsSet.add(nextCell);
					findPaths(nextCell, nextDictNode, currentPath, traversedCellsSet, paths);
					currentPath.removeLast();
					traversedCellsSet.remove(nextCell);
				}
			}
		}

	}

}
