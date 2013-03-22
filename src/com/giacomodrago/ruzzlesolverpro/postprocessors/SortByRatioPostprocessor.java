package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;

public class SortByRatioPostprocessor implements Postprocessor {

	private final static CostCalculator scoreCalculator = new LengthCostCalculator();

	private final static Comparator<Path> comparator = new PathRatioComparator(
			scoreCalculator);

	@Override
	public void execute(Dictionary dictionary, ScoreCalculator scoreCalculator,
			List<Path> paths) {

		Collections.sort(paths, comparator);

	}

}
