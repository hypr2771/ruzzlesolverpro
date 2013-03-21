package com.giacomodrago.ruzzlesolverpro;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.Bonus;
import com.giacomodrago.ruzzlesolverpro.solver.Cell;
import com.giacomodrago.ruzzlesolverpro.solver.Path;
import com.giacomodrago.ruzzlesolverpro.solver.ScoreCalculator;
import com.giacomodrago.ruzzlesolverpro.solver.Solver;

public final class RuzzleSolverPro {

	private static final String PROGRAM_NAME = "Ruzzle Solver PRO";
	private static final String PROGRAM_VERSION = "v1.0 beta 6";

	private static final boolean EXTENDED_DEBUG = false;
	private static final int GAME_SIZE = 4; // 4x4
	private static final int N_CELLS = GAME_SIZE * GAME_SIZE; // 16

	private static final String LANGUAGES_DIRECTORY = "languages";
	private static final String SETTINGS_FILE_NAME = "settings.ini";
	private static final String DICTIONARY_FILE_NAME = "dictionary.txt";
	private static final String SCORES_FILE_NAME = "scores.txt";
	private static final String DEFAULT_LANGUAGE = "English";

	private static final char BONUS_DL_CHAR = '1';
	private static final char BONUS_TL_CHAR = '2';
	private static final char BONUS_DW_CHAR = '3';
	private static final char BONUS_TW_CHAR = '4';
	private static final char BONUS_NONE_CHAR = '0';

	private static final int BOARD_SIZE = 500;
	private static final int BOARD_FONT_SIZE = 50;
	private static final int WORD_FONT_SIZE = 48;
	private static final Color START_LETTER_COLOR = Color.GREEN;
	private static final Color MIDDLE_LETTER_COLOR = Color.YELLOW;
	private static final Color END_LETTER_COLOR = Color.RED;
	private static final int PATHS_LIST_SIZE = 140;
	private static final int SPACING = 10;
	private static final int CLEAR_BUTTON_HEIGHT = 40;
	private static final int CONTROLS_FONT_SIZE = 14;
	private static final int LABELS_FONT_SIZE = 12;
	private static final int DEFAULT_MIN_WORD_LENGTH = 3;
	private static final int DEFAULT_MAX_WORD_LENGTH = 16;
	private static final String HELP_TEXT = "Keys for bonuses:\u2003"
			+ BONUS_DL_CHAR + " = DL\u2003" + BONUS_TL_CHAR + " = TL\u2003"
			+ BONUS_DW_CHAR + " = DW\u2003" + BONUS_TW_CHAR + " = TW\u2003"
			+ BONUS_NONE_CHAR + " = none\u2003";
	private static final String WEBSITE_URI = "http://8t88.biz/RSP";
	private static final boolean MULTITHREADED_SOLVER = true;

	private final Solver solver;
	private final Set<String> languages;

	private final JFrame window;
	private final Cell[] cells;
	private final JTextField[] cellTextFields;
	private final JButton solveButton;
	private final JLabel currentWordLabel;
	private final DrawingPanel drawingPanel;
	private final BonusesPanel bonusesPanel;
	private final JList<Path> pathsList;
	private final PathsListModel pathsListModel;
	private final JComboBox<String> languageSelector;
	private final Preferences preferences = Preferences.userRoot().node(
			this.getClass().getName());

	public RuzzleSolverPro() {

		Properties settings = new Properties();

		try {
			FileInputStream settingsFile = new FileInputStream(
					SETTINGS_FILE_NAME);
			settings.load(settingsFile);
			settingsFile.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		int minWordLength = Integer.parseInt(settings.getProperty(
				"min_word_length", Integer.toString(DEFAULT_MIN_WORD_LENGTH)));
		int maxWordLength = Integer.parseInt(settings.getProperty(
				"max_word_length", Integer.toString(DEFAULT_MAX_WORD_LENGTH)));
		String[] postprocessorsClasses = settings.getProperty("postprocessors",
				"").split(" ");

		solver = new Solver(GAME_SIZE, minWordLength, maxWordLength,
				postprocessorsClasses, MULTITHREADED_SOLVER);

		languages = new TreeSet<String>();
		File languagesDirectory = new File(LANGUAGES_DIRECTORY);
		String[] subDirectories = languagesDirectory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});
		for (String subdirectory : subDirectories) {
			languages.add(subdirectory);
		}

		// Set current language
		String language = preferences.get("language", DEFAULT_LANGUAGE);
		setLanguage(language);

		cells = new Cell[N_CELLS];
		for (int index = 0; index < N_CELLS; index++) {
			cells[index] = new Cell();
			cells[index].setIndex(index);
		}

		JLayeredPane gamePanel = new JLayeredPane();
		JPanel cellsPanel = new JPanel();
		cellsPanel.setLayout(new GridLayout(GAME_SIZE, GAME_SIZE));

		cellTextFields = new JTextField[N_CELLS];
		FocusListener focusListener = new CellTextFieldFocusListener();
		for (int i = 0; i < N_CELLS; i++) {
			JTextField textField = new JTextField();
			Font originalFont = textField.getFont();
			Font newFont = new Font(originalFont.getName(), Font.BOLD,
					BOARD_FONT_SIZE);
			textField.setFont(newFont);
			textField.setHorizontalAlignment(JTextField.CENTER);
			textField.setHighlighter(null);
			textField.addKeyListener(new CellTextFieldKeyListener(cells[i]));
			textField.addFocusListener(focusListener);
			cellsPanel.add(textField);
			cellTextFields[i] = textField;
		}

		Rectangle bounds = new Rectangle(0, 0, BOARD_SIZE, BOARD_SIZE);

		cellsPanel.setBounds(bounds);
		gamePanel.add(cellsPanel, new Integer(1));

		drawingPanel = new DrawingPanel(GAME_SIZE, bounds);
		gamePanel.add(drawingPanel, new Integer(2));

		bonusesPanel = new BonusesPanel(GAME_SIZE, bounds, cells);
		gamePanel.add(bonusesPanel, new Integer(3));

		gamePanel.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));

		Font defaultFont = UIManager.getDefaults().getFont("TabbedPane.font");
		Font controlsFont = new Font(defaultFont.getFontName(), Font.BOLD,
				CONTROLS_FONT_SIZE);
		Font labelsFont = new Font(defaultFont.getFontName(), 0,
				LABELS_FONT_SIZE);

		ActionListener actionListener = new WindowActionListener();

		JButton clearButton = new JButton("Clear");
		clearButton.setMnemonic('c');
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(actionListener);
		clearButton.setPreferredSize(new Dimension(0, CLEAR_BUTTON_HEIGHT));
		clearButton.setFont(controlsFont);

		JPanel languagePanel = new JPanel();
		languagePanel
				.setLayout(new FlowLayout(FlowLayout.LEFT, SPACING / 2, 0));
		JLabel languageLabel = new JLabel("Game language:");
		languageLabel.setFont(labelsFont);
		languageSelector = new JComboBox<String>(
				languages.toArray(new String[languages.size()]));
		languageSelector.setFont(labelsFont);
		languageSelector.setSelectedItem(language);
		languageSelector.addItemListener(new LanguageSelectorActionListener());

		languagePanel.add(languageLabel);
		languagePanel.add(languageSelector);

		JButton aboutButton = new JButton("About");
		aboutButton.setMnemonic('a');
		aboutButton.setActionCommand("about");
		aboutButton.addActionListener(actionListener);
		aboutButton.setFont(labelsFont);

		JLabel helpLabel = new JLabel(HELP_TEXT);
		helpLabel.setFont(labelsFont);
		helpLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel aboutButtonPanel = new JPanel();
		aboutButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		aboutButtonPanel.add(helpLabel);
		aboutButtonPanel.add(aboutButton);

		JPanel topMenu = new JPanel();
		topMenu.setLayout(new BorderLayout());
		topMenu.add(languagePanel, BorderLayout.WEST);
		topMenu.add(aboutButtonPanel, BorderLayout.EAST);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2, 1));
		topPanel.add(topMenu);
		topPanel.add(clearButton);

		solveButton = new JButton("Solve");
		solveButton.setFont(controlsFont);
		solveButton.setMnemonic('s');
		solveButton.setActionCommand("solve");
		solveButton.addActionListener(actionListener);

		pathsList = new JList<Path>();
		pathsListModel = new PathsListModel();
		pathsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pathsList.setModel(pathsListModel);
		pathsList.addListSelectionListener(new PathsListSelectionListener());
		pathsList.addKeyListener(new PathListKeyListener());

		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BorderLayout(SPACING, SPACING));
		sidePanel.add(solveButton, BorderLayout.WEST);

		JScrollPane pathsListScroll = new JScrollPane(pathsList);
		pathsListScroll.setPreferredSize(new Dimension(PATHS_LIST_SIZE,
				BOARD_SIZE));
		sidePanel.add(pathsListScroll, BorderLayout.CENTER);

		currentWordLabel = new JLabel(" ");
		currentWordLabel.setFont(new Font(currentWordLabel.getFont().getName(),
				Font.BOLD, WORD_FONT_SIZE));
		currentWordLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel mainPanel = new JPanel();
		mainPanel
				.setBorder(new EmptyBorder(SPACING, SPACING, SPACING, SPACING));
		mainPanel.setLayout(new BorderLayout(SPACING, SPACING));

		mainPanel.add(gamePanel, BorderLayout.CENTER);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(currentWordLabel, BorderLayout.SOUTH);
		mainPanel.add(sidePanel, BorderLayout.EAST);

		window = new JFrame(PROGRAM_NAME + " " + PROGRAM_VERSION);
		window.add(mainPanel);
		window.setResizable(false);
		window.getContentPane();
		window.pack();
		window.setLocationRelativeTo(null);

		cellTextFields[0].requestFocus();

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JOptionPane.setDefaultLocale(Locale.US);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED && 
						e.getKeyCode() == KeyEvent.VK_F1) {
					about();
					return true;
				}
				return false;
			}
		});

	}

	private void setLanguage(String language) {

		String languageDirectory = LANGUAGES_DIRECTORY + File.separator
				+ language;

		String dictionaryFilePath = languageDirectory + File.separator
				+ DICTIONARY_FILE_NAME;
		String scoresFilePath = languageDirectory + File.separator
				+ SCORES_FILE_NAME;
		
		// Unload previous dictionary to allow garbage collection
		solver.setDictionary(null);
		
		Dictionary dictionary = null;
		try {
			long startTime = System.currentTimeMillis();
			dictionary = new Dictionary(dictionaryFilePath);
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println("Loaded dictionary \"" + dictionaryFilePath
					+ "\" in " + elapsedTime + " ms");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		ScoreCalculator scoreCalculator = new ScoreCalculator(scoresFilePath);

		solver.setDictionary(dictionary);
		solver.setScoreCalculator(scoreCalculator);

		preferences.put("language", language);

	}

	private void about() {
		try {
			Desktop.getDesktop().browse(new URI(WEBSITE_URI));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private void clearColors() {
		for (JTextField cell : cellTextFields) {
			cell.setBackground(Color.WHITE);
		}
	}

	private void clear(boolean clearCells) {

		clearColors();

		if (clearCells) {
			for (JTextField textField : cellTextFields) {
				textField.setText("");
			}
			for (Cell cell : cells) {
				cell.setLetter(null);
				cell.setBonus(null);
			}
		}

		currentWordLabel.setText(" ");
		pathsListModel.setPathsList(null);
		drawingPanel.setWordPath(null);
		drawingPanel.repaint();
		bonusesPanel.refreshAll();

		if (clearCells) {
			cellTextFields[0].requestFocus();
		}

	}

	private void clear() {
		clear(true);
	}

	private void solve() {

		// Check all cells are populated
		for (int i = 0; i < N_CELLS; i++) {
			if (cells[i].getLetter() == null) {
				cellTextFields[i].requestFocus();
				JOptionPane.showMessageDialog(window,
						"Please fill in all the boxes.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		Cursor defaultCursor = window.getCursor();
		window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		long startTime = System.currentTimeMillis();
		List<Path> paths = solver.solve(cells);
		long elapsed = System.currentTimeMillis() - startTime;

		window.setCursor(defaultCursor);

		System.out.println("Found " + paths.size() + " paths in " + elapsed
				+ " ms");

		pathsList.clearSelection();

		if (!paths.isEmpty()) {
			if (EXTENDED_DEBUG) {
				for (Path path : paths) {
					System.out.println(path);
				}
			}
			pathsListModel.setPathsList(paths);
			pathsList.setSelectedIndex(0);
			pathsList.requestFocus();
		} else {
			pathsListModel.setPathsList(null);
			JOptionPane.showMessageDialog(window, "No words found.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private void displayPath(Path path) {
		clearColors();
		currentWordLabel.setText(path.toString());
		int counter = 0;
		for (Cell node : path.getTraversedCells()) {
			int index = node.getIndex();
			if (counter == 0) {
				cellTextFields[index].setBackground(START_LETTER_COLOR);
			} else if (counter == path.getTraversedCells().size() - 1) {
				cellTextFields[index].setBackground(END_LETTER_COLOR);
			} else {
				cellTextFields[index].setBackground(MIDDLE_LETTER_COLOR);
			}
			counter++;
		}
		drawingPanel.setWordPath(path);
		drawingPanel.repaint();
	}

	private final class WindowActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String actionCommand = event.getActionCommand();
			if ("clear".equals(actionCommand)) {
				clear();
			} else if ("solve".equals(actionCommand)) {
				solve();
			} else if ("about".equals(actionCommand)) {
				about();
			}
		}
	}

	private final class LanguageSelectorActionListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {

			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}

			String language = languageSelector.getItemAt(languageSelector
					.getSelectedIndex());

			clear(false);
			Cursor defaultCursor = window.getCursor();
			window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			setLanguage(language);

			window.setCursor(defaultCursor);

			cellTextFields[0].requestFocus();

		}

	}

	private static final class PathListKeyListener implements KeyListener {

		private final Robot robot;

		public PathListKeyListener() {
			try {
				robot = new Robot();
			} catch (AWTException ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE
					|| e.getKeyCode() == KeyEvent.VK_ENTER) {
				robot.keyPress(KeyEvent.VK_DOWN);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

	}

	private final class PathsListSelectionListener implements
			ListSelectionListener {
		public void valueChanged(ListSelectionEvent le) {
			if (le.getValueIsAdjusting()) {
				return;
			}
			int index = pathsList.getSelectedIndex();
			if (index != -1) {
				Path path = pathsListModel.getElementAt(index);
				displayPath(path);
			}
		}
	}

	private final class CellTextFieldKeyListener implements KeyListener {

		private final Cell cell;

		public CellTextFieldKeyListener(Cell cell) {
			this.cell = cell;
		}

		@Override
		public void keyPressed(KeyEvent e) {

			int keyCode = e.getKeyCode();
			JTextField textField = (JTextField) e.getSource();

			if (keyCode == KeyEvent.VK_LEFT) {
				goToPrevious();
			} else if (keyCode == KeyEvent.VK_RIGHT) {
				goToNext();
			} else if (keyCode == KeyEvent.VK_UP) {
				goUp();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				goDown();
			} else if (keyCode == KeyEvent.VK_DELETE
					|| keyCode == KeyEvent.VK_BACK_SPACE) {
				cell.setLetter(null);
				cell.setBonus(null);
				textField.setText("");
				bonusesPanel.refresh(cell.getIndex());
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {

			int keyCode = e.getKeyCode();

			if (keyCode == KeyEvent.VK_ALT || e.isAltDown()) {
				return;
			}

			JTextField textField = (JTextField) e.getSource();
			String previousContent = textField.getText();

			char letter = e.getKeyChar();

			if ((letter >= 'A' && letter <= 'Z')
					|| (letter >= 'a' && letter <= 'z')) {

				cell.setLetter(letter);
				String text = ("" + letter).toUpperCase();
				textField.setText(text);

				if (cell.getIndex() < N_CELLS - 1) {
					int nextCellId = cell.getIndex() + 1;
					if (cellTextFields[nextCellId].getText().isEmpty()) {
						cellTextFields[nextCellId].requestFocus();
					}
				} else if (previousContent.isEmpty()) {
					solveButton.requestFocus();
				}

				clear(false);

			} else {
				
				boolean clear = true;

				switch (letter) {
				case BONUS_DL_CHAR:
					cell.setBonus(Bonus.DL);
					break;
				case BONUS_TL_CHAR:
					cell.setBonus(Bonus.TL);
					break;
				case BONUS_DW_CHAR:
					cell.setBonus(Bonus.DW);
					break;
				case BONUS_TW_CHAR:
					cell.setBonus(Bonus.TW);
					break;
				case BONUS_NONE_CHAR:
					cell.setBonus(null);
					break;
				default:
					clear = false;
				}

				if (clear) {
					clear(false);
				}

			}

			e.consume();

		}

		private void goToPrevious() {
			if (cell.getIndex() > 0) {
				int prevCellId = cell.getIndex() - 1;
				cellTextFields[prevCellId].requestFocus();
			}
		}

		private void goToNext() {
			if (cell.getIndex() < N_CELLS - 1) {
				int nextCellId = cell.getIndex() + 1;
				cellTextFields[nextCellId].requestFocus();
			}
		}

		private void goUp() {
			int upCellIndex = cell.getIndex() - GAME_SIZE;
			if (upCellIndex >= 0) {
				cellTextFields[upCellIndex].requestFocus();
			}
		}

		private void goDown() {
			int downCellIndex = cell.getIndex() + GAME_SIZE;
			if (downCellIndex <= N_CELLS - 1) {
				cellTextFields[downCellIndex].requestFocus();
			}
		}

	}

	private final class CellTextFieldFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			JTextField textField = (JTextField) e.getSource();
			textField.setCaretPosition(0);
		}

		@Override
		public void focusLost(FocusEvent e) {
		}

	}

	private final static class PathsListModel implements ListModel<Path> {

		private final List<ListDataListener> listeners;
		private List<Path> pathsList;

		public PathsListModel() {
			listeners = new ArrayList<ListDataListener>();
		}

		@Override
		public void addListDataListener(ListDataListener listener) {
			listeners.add(listener);
		}

		@Override
		public Path getElementAt(int index) {
			return pathsList.get(index);
		}

		@Override
		public int getSize() {
			if (pathsList == null) {
				return 0;
			}
			return pathsList.size();
		}

		@Override
		public void removeListDataListener(ListDataListener listener) {
			listeners.remove(listener);
		}

		public void setPathsList(List<Path> pathsList) {
			this.pathsList = pathsList;
			ListDataEvent event = new ListDataEvent(this,
					ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1);
			for (ListDataListener listener : listeners) {
				listener.contentsChanged(event);
			}
		}

	}

	public void showUI() {
		window.setVisible(true);
	}

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedLookAndFeelException e) {
			throw new RuntimeException(e);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				RuzzleSolverPro application = new RuzzleSolverPro();
				application.showUI();
			}
		});

	}

}
