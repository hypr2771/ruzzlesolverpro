package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.Comparator;

import com.giacomodrago.ruzzlesolverpro.solver.Path;

class PathRatioComparator implements Comparator<Path> {

	private final static Comparator<Path> scoreComparator = new PathScoreComparator();
	
	private static final double NEGLIGIBLE_DIFF_THRESHOLD = 0.01;
	
	@Override
	public int compare(Path path1, Path path2) {
		
		double ratio1 = (double) path1.getScore() /
				(double) path1.getTraversedCells().size();
		double ratio2 = (double) path2.getScore() /
				(double) path2.getTraversedCells().size();
		
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
