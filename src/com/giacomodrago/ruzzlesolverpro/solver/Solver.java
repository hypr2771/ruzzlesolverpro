package com.giacomodrago.ruzzlesolverpro.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.postprocessors.Postprocessor;

public class Solver {

	protected final boolean multithreaded;
	protected final int minWordLen;
	protected final int maxWordLen;
	protected final int gameSize;
	protected final List<Postprocessor> postprocessors;

	protected ScoreCalculator scoreCalculator;
	protected Dictionary dictionary;
	protected ExecutorService executor;

	public Solver(int gameSize, int minWordLen, int maxWordLen,
			String[] postprocessorsClasses, boolean multithreaded) {

		this.minWordLen = minWordLen;
		this.maxWordLen = maxWordLen;
		this.gameSize = gameSize;
		this.multithreaded = multithreaded;

		this.postprocessors = new ArrayList<Postprocessor>();
		for (String postprocessorClassName : postprocessorsClasses) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Postprocessor> postprocessorClass = (Class<? extends Postprocessor>) Class
						.forName(postprocessorClassName);
				Postprocessor instance = postprocessorClass.newInstance();
				postprocessors.add(instance);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		if (multithreaded) {
			this.executor = Executors.newFixedThreadPool(Runtime.getRuntime()
					.availableProcessors());
		}

	}

	protected int resolveIndex(int row, int col) {
		return gameSize * row + col;
	}

	public List<Path> solve(Cell[] cells) {

		// Create extended cells
		ExtendedCell[] extendedCells = new ExtendedCell[cells.length];
		for (int i = 0; i < cells.length; i++) {
			extendedCells[i] = new ExtendedCell(cells[i]);
		}

		// Find neighbors
		for (int row = 0; row < gameSize; row++) {
			for (int col = 0; col < gameSize; col++) {
				int index = resolveIndex(row, col);
				ExtendedCell cell = extendedCells[index];
				List<ExtendedCell> neighbors = new ArrayList<ExtendedCell>();
				int startRowAdd = (row == 0) ? 0 : -1;
				int endRowAdd = (row == gameSize - 1) ? 0 : 1;
				int startColAdd = (col == 0) ? 0 : -1;
				int endColAdd = (col == gameSize - 1) ? 0 : 1;
				for (int rowAdd = startRowAdd; rowAdd <= endRowAdd; rowAdd++) {
					for (int colAdd = startColAdd; colAdd <= endColAdd; colAdd++) {
						if (rowAdd == 0 && colAdd == 0) {
							continue;
						}
						int neighborIndex = resolveIndex(row + rowAdd, col
								+ colAdd);
						ExtendedCell neighbor = extendedCells[neighborIndex];
						neighbors.add(neighbor);
					}
				}
				cell.setNeighbors(neighbors);
			}
		}

		List<Path> paths = new ArrayList<Path>();

		if (multithreaded) {

			List<Path> pathsBlocking = Collections.synchronizedList(paths);

			// Generate paths
			Collection<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
			for (ExtendedCell cell : extendedCells) {
				CellSolver task = new CellSolver(dictionary, maxWordLen,
						minWordLen, cell, pathsBlocking);
				tasks.add(Executors.callable(task));
			}

			try {
				executor.invokeAll(tasks);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

		} else {

			for (ExtendedCell cell : extendedCells) {
				CellSolver task = new CellSolver(dictionary, maxWordLen,
						minWordLen, cell, paths);
				task.run();
			}

		}

		// Calculate score
		for (Path path : paths) {
			int score = scoreCalculator.calculate(path.getTraversedCells());
			path.setScore(score);
		}

		// Run postprocessors
		for (Postprocessor postprocessor : postprocessors) {
			postprocessor.execute(dictionary, scoreCalculator, paths);
		}

		return paths;

	}

	public void setScoreCalculator(ScoreCalculator scoreCalculator) {
		this.scoreCalculator = scoreCalculator;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

}
