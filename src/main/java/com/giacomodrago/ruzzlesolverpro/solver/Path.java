package com.giacomodrago.ruzzlesolverpro.solver;

import java.util.List;

public class Path {

	private List<Cell> traversedCells;
	private String word;
	private int score;

	public List<Cell> getTraversedCells() {
		return traversedCells;
	}

	public void setTraversedCells(List<Cell> traversedCells) {
		this.traversedCells = traversedCells;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return getWord() + " [" + getScore() + "]";
	}

}