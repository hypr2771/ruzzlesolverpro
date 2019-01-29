package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;

public class SortByScorePostprocessor implements Postprocessor {

	private final static Comparator<Path> comparator = new PathScoreComparator();
	
	@Override
	public void execute(int gameSize, int minWordLen, int maxWordLen,
			Dictionary dictionary, ScoreCalculator scoreCalculator,
			List<Path> paths) {
		
		Collections.sort(paths, comparator);
		
	}

}

class PathScoreComparator implements Comparator<Path> {

	@Override
	public int compare(Path path1, Path path2) {
		int result = path2.getScore() - path1.getScore();
		if (result == 0) {
			result = path1.getWord().compareTo(path2.getWord());
		}
		return result;
	}

}
