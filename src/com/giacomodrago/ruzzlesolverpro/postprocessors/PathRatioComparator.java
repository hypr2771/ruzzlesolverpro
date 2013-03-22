package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.Locale;

import com.giacomodrago.ruzzlesolverpro.solver.Path;

class PathRatioComparator implements Comparator<Path> {

	private final static Comparator<Path> scoreComparator = new PathScoreComparator();

	private static final double NEGLIGIBLE_DIFF_THRESHOLD = 0.01;
	private static final DecimalFormat decimalFormat;
	static {
		decimalFormat = new DecimalFormat("0.00",
				DecimalFormatSymbols.getInstance(Locale.US));
	}

	private final CostCalculator costCalculator;
	
	public PathRatioComparator(CostCalculator costCalculator) {
		this.costCalculator = costCalculator;
	}

	@Override
	public int compare(Path path1, Path path2) {
		
		double ratio1 = (double) path1.getScore() / costCalculator.calculateCost(path1);
		double ratio2 = (double) path2.getScore() / costCalculator.calculateCost(path2);

		path1.setAnnotation(decimalFormat.format(ratio1));
		path2.setAnnotation(decimalFormat.format(ratio2));

		double diff = ratio2 - ratio1;

		if (Math.abs(diff) < NEGLIGIBLE_DIFF_THRESHOLD) {
			// Difference is negligible: use standard score-based comparator
			return scoreComparator.compare(path1, path2);
		} else if (diff > 0.0) {
			return 1;
		} else {
			return -1;
		}

	}

}
