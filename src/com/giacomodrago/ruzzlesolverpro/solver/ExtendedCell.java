package com.giacomodrago.ruzzlesolverpro.solver;

import java.util.List;

class ExtendedCell extends Cell {

	private List<ExtendedCell> neighbors;

	public List<ExtendedCell> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<ExtendedCell> neighbors) {
		this.neighbors = neighbors;
	}

	public ExtendedCell(Cell cell) {
		this.setIndex(cell.getIndex());
		this.setLetter(cell.getLetter());
		this.setBonus(cell.getBonus());
	}

}
