package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;

public class SortByRatioPostprocessor2 implements Postprocessor {

	@Override
	public void execute(int gameSize, int minWordLen, int maxWordLen,
			Dictionary dictionary, ScoreCalculator scoreCalculator,
			List<Path> paths) {

		CostCalculator costCalculator = new EffortCostCalculator(gameSize);

		Comparator<Path> comparator = new PathRatioComparator(
				costCalculator);
		
		Collections.sort(paths, comparator);

	}

}
