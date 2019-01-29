package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.List;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;

public interface Postprocessor {

	public void execute(int gameSize, int minWordLen, int maxWordLen,
			Dictionary dictionary,
			ScoreCalculator scoreCalculator,
			List<Path> paths);
	
}
