package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.List;

import com.giacomodrago.ruzzlesolverpro.solver.Cell;
import com.giacomodrago.ruzzlesolverpro.solver.Path;

class EffortCostCalculator implements CostCalculator {
	
	private final int gameSize;
	
	public EffortCostCalculator(int gameSize) {
		this.gameSize = gameSize;
	}
	
	private static enum Direction {
		NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST
	}
	
	public double calculateCost(Path path) {
		
		int directionChanges = 0;
		Direction oldDirection = null;
		
		List<Cell> traversedCells = path.getTraversedCells();
		
		for (int i = 0; i < traversedCells.size()-1; i++) {
			Direction newDirection = null;
			Cell cell1 = traversedCells.get(i);
			Cell cell2 = traversedCells.get(i+1);
			int row1 = cell1.getIndex() / gameSize;
			int col1 = cell1.getIndex() % gameSize;
			int row2 = cell2.getIndex() / gameSize;
			int col2 = cell2.getIndex() % gameSize;
			if (row1 == row2) { // on the same row
				if (col2 > col1) { // moving right
					newDirection = Direction.EAST;
				} else { // moving left
					newDirection = Direction.WEST;
				}
			} else if (col1 == col2) { // on the same column
				if (row2 > row1) { // moving down
					newDirection = Direction.SOUTH;
				} else { // moving up
					newDirection = Direction.NORTH;
				}
			} else { // diagonal moving
				if (row2 > row1 && col2 > col1) { // down-right
					newDirection = Direction.SOUTH_EAST;
				} else if (row2 > row1 && col2 < col1) { // down-left
					newDirection = Direction.SOUTH_WEST;
				} else if (row2 < row1 && col2 > col1) { // up-right
					newDirection = Direction.NORTH_EAST;
				} else if (row2 < row1 && col2 < col1) { // up-left
					newDirection = Direction.NORTH_WEST;
				}
			}
			if (!newDirection.equals(oldDirection)) {
				directionChanges++;
			}
			oldDirection = newDirection;
		}
		
		double cost = (double) directionChanges;// / (double) (traversedCells.size()-1);
		
		return cost;
				
	}
	
}
