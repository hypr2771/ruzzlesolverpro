package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;

public class SortByRatioPostprocessor implements Postprocessor {

	private final static Comparator<ExtendedPath> comparator = new PathRatioComparator();

	@Override
	public void execute(int gameSize, int minWordLen, int maxWordLen,
			Dictionary dictionary, ScoreCalculator scoreCalculator,
			List<Path> paths) {

		List<ExtendedPath> extendedPaths = new ArrayList<ExtendedPath>();
		for (Path path : paths) {
			double ratio = (double) path.getScore() / 
					(double) path.getTraversedCells().size();
			ExtendedPath extendedPath = new ExtendedPath(path);
			extendedPath.setRatio(ratio);
			extendedPaths.add(extendedPath);
		}
		
		Collections.sort(extendedPaths, comparator);
		
		paths.clear();
		paths.addAll(extendedPaths);

	}

}

class ExtendedPath extends Path {
	
	private static final DecimalFormat decimalFormat;
	static {
		decimalFormat = new DecimalFormat("0.00",
				DecimalFormatSymbols.getInstance(Locale.US));
	}
	
	private double ratio;
	
	public ExtendedPath(Path path) {
		setWord(path.getWord());
		setTraversedCells(path.getTraversedCells());
		setScore(path.getScore());
	}

	public double getRatio() {
		return ratio;
	}
	
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	
	@Override
	public String toString() {
		String result = getWord();
		result += " [";
		result += getScore();
		result += ", " + decimalFormat.format(ratio);
		result += "]";
		return result;
	}
	
}

class PathRatioComparator implements Comparator<ExtendedPath> {

	private final static Comparator<Path> scoreComparator = new PathScoreComparator();

	private static final double NEGLIGIBLE_DIFF_THRESHOLD = 0.01;
	

	@Override
	public int compare(ExtendedPath path1, ExtendedPath path2) {
		
		double diff = path2.getRatio() - path1.getRatio();

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
