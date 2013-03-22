package com.giacomodrago.ruzzlesolverpro.postprocessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;

public class HumanizePostprocessor implements Postprocessor {

	protected static final int MEDIUM_WORD_MIN_LEN = 4;
	protected static final int MEDIUM_WORD_MAX_LEN = 5;
	protected static final double HUMANIZE_PROBABILITY = 0.25;

	@Override
	public void execute(int gameSize, int minWordLen, int maxWordLen,
			Dictionary dictionary, ScoreCalculator scoreCalculator,
			List<Path> paths) {

		List<Path> mediumLengthWords = new ArrayList<Path>();
		for (Path path : paths) {
			if (isMediumLengthWord(path.getWord())) {
				mediumLengthWords.add(path);
			}
		}

		Iterator<Path> mediumWordsIterator = mediumLengthWords.iterator();
		List<Path> humanizedList = new ArrayList<Path>();
		Random random = new Random();

		Set<String> insertedMediumWords = new HashSet<String>();

		for (Path path : paths) {
			String word = path.getWord();
			if (isMediumLengthWord(word)) {
				if (!insertedMediumWords.contains(word)) {
					humanizedList.add(path);
					insertedMediumWords.add(word);
				}
			} else {
				if (random.nextDouble() < HUMANIZE_PROBABILITY) {
					while (mediumWordsIterator.hasNext()) {
						Path mediumWordPath = mediumWordsIterator.next();
						String mediumWord = mediumWordPath.getWord();
						if (!insertedMediumWords.contains(mediumWord)) {
							humanizedList.add(mediumWordPath);
							insertedMediumWords.add(mediumWord);
							break;
						}
					}
				}
				humanizedList.add(path);
			}
		}

		paths.clear();
		paths.addAll(humanizedList);

	}

	protected boolean isMediumLengthWord(String word) {
		int len = word.length();
		return len >= MEDIUM_WORD_MIN_LEN && len <= MEDIUM_WORD_MAX_LEN;
	}

}
