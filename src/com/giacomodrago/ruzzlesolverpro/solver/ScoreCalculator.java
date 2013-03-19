package com.giacomodrago.ruzzlesolverpro.solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ScoreCalculator {

	protected final Map<Character, Integer> scores;

	public ScoreCalculator(String scoresFilePath) {

		this.scores = new TreeMap<Character, Integer>();

		try {
			Scanner scanner = new Scanner(new File(scoresFilePath));
			while (scanner.hasNext()) {
				char letter = scanner.next().charAt(0);
				int score = scanner.nextInt();
				scores.put(letter, score);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

	public int calculate(List<Cell> traversedCells) {

		int wordLength = traversedCells.size();

		int score = 0;

		List<Bonus> bonuses = new ArrayList<Bonus>();

		for (Cell cell : traversedCells) {
			int letterScore = scores.get(cell.getLetter());
			Bonus bonus = cell.getBonus();
			if (bonus != null) {
				if (bonus == Bonus.DL) {
					letterScore *= 2;
				} else if (bonus == Bonus.TL) {
					letterScore *= 3;
				} else {
					bonuses.add(cell.getBonus());
				}
			}
			score += letterScore;
		}

		for (Bonus bonus : bonuses) {
			switch (bonus) {
			case TW:
				score *= 3;
				break;
			case DW:
				score *= 2;
				break;
			}
		}

		switch (wordLength) {
		case 9:
			score += 25;
			break;
		case 8:
			score += 20;
			break;
		case 7:
			score += 15;
			break;
		case 6:
			score += 10;
			break;
		case 5:
			score += 5;
			break;
		}

		return score;

	}

}
