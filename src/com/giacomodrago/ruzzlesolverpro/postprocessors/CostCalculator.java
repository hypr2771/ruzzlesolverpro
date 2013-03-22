package com.giacomodrago.ruzzlesolverpro.postprocessors;

import com.giacomodrago.ruzzlesolverpro.solver.Path;

public interface CostCalculator {

	public double calculateCost(Path path);
	
}
