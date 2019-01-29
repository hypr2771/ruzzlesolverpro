package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;

public class RemoveDuplicatesPostprocessor implements Postprocessor {

	@Override
	public void execute(int gameSize, int minWordLen, int maxWordLen,
			Dictionary dictionary, ScoreCalculator scoreCalculator,
			List<Path> paths) {

		Set<String> foundWords = new HashSet<String>();

		Iterator<Path> it = paths.iterator();

		while (it.hasNext()) {
			Path path = it.next();
			String word = path.getWord();
			if (!foundWords.contains(word)) {
				foundWords.add(word);
			} else {
				it.remove();
			}
		}

	}

}
