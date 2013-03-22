package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.Comparator;

import com.giacomodrago.ruzzlesolverpro.solver.Path;

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
