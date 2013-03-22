package com.giacomodrago.ruzzlesolverpro.postprocessors;

import com.giacomodrago.ruzzlesolverpro.solver.Path;

class LengthCostCalculator implements CostCalculator {
	
	private static final double POW_EXPONENT = 1.1;
	
	public double calculateCost(Path path) {
		return Math.pow((double) path.getTraversedCells().size(),
				POW_EXPONENT);
	}

}
