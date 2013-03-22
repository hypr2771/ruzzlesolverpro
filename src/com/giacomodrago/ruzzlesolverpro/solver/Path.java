package com.giacomodrago.ruzzlesolverpro.solver;

import java.util.List;

public class Path {

	private List<Cell> traversedCells;
	private String word;
	private int score;
	private String annotation;

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
	
	public String getAnnotation() {
		return annotation;
	}
	
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	
	@Override
	public String toString() {
		String result = getWord();
		result += " [";
		result += getScore();
		if (annotation != null && !annotation.isEmpty()) {
			result += ", "+annotation;
		}
		result += "]";
		return result;
	}

}