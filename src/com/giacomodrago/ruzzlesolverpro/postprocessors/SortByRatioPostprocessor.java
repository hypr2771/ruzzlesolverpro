package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;

public class SortByRatioPostprocessor implements Postprocessor {

	private final static CostCalculator costCalculator = new LengthCostCalculator();

	private final static Comparator<Path> comparator = new PathRatioComparator(
			costCalculator);

	@Override
	public void execute(int gameSize, int minWordLen, int maxWordLen,
			Dictionary dictionary, ScoreCalculator scoreCalculator,
			List<Path> paths) {

		Collections.sort(paths, comparator);

	}

}
