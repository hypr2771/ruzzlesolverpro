package com.giacomodrago.ruzzlesolverpro;

import com.giacomodrago.ruzzlesolverpro.dictionary.Dictionary;
import com.giacomodrago.ruzzlesolverpro.solver.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public final class RuzzleSolverPro {

  private static final String PROGRAM_NAME    = "Ruzzle Solver PRO";
  private static final String PROGRAM_VERSION = "v1.0 beta 7";

  private static final boolean EXTENDED_DEBUG = false;
  private static final int     GAME_SIZE      = 4; // 4x4
  private static final int     N_CELLS        = GAME_SIZE * GAME_SIZE; // 16

  private static final String LANGUAGES_DIRECTORY  = "/languages";
  private static final String SETTINGS_FILE_NAME   = "/settings.ini";
  private static final String DICTIONARY_FILE_NAME = "dictionary.txt";
  private static final String SCORES_FILE_NAME     = "scores.txt";
  private static final String DEFAULT_LANGUAGE     = "English";

  private static final char BONUS_DL_CHAR   = '1';
  private static final char BONUS_TL_CHAR   = '2';
  private static final char BONUS_DW_CHAR   = '3';
  private static final char BONUS_TW_CHAR   = '4';
  private static final char BONUS_NONE_CHAR = '0';

  private static final int     BOARD_SIZE              = 500;
  private static final int     BOARD_FONT_SIZE         = 50;
  private static final int     WORD_FONT_SIZE          = 48;
  private static final Color   START_LETTER_COLOR      = Color.GREEN;
  private static final Color   MIDDLE_LETTER_COLOR     = Color.YELLOW;
  private static final Color   END_LETTER_COLOR        = Color.RED;
  private static final int     PATHS_LIST_SIZE         = 140;
  private static final int     SPACING                 = 10;
  private static final int     CLEAR_BUTTON_HEIGHT     = 40;
  private static final int     CONTROLS_FONT_SIZE      = 14;
  private static final int     LABELS_FONT_SIZE        = 12;
  private static final int     DEFAULT_MIN_WORD_LENGTH = 3;
  private static final int     DEFAULT_MAX_WORD_LENGTH = 16;
  private static final String  HELP_TEXT               = "Keys for bonuses:\u2003"
                                                         + BONUS_DL_CHAR + " = DL\u2003" + BONUS_TL_CHAR + " = TL\u2003"
                                                         + BONUS_DW_CHAR + " = DW\u2003" + BONUS_TW_CHAR + " = TW\u2003"
                                                         + BONUS_NONE_CHAR + " = none\u2003";
  private static final String  WEBSITE_URI             = "http://8t88.biz/RSP";
  private static final boolean MULTITHREADED_SOLVER    = true;

  private final Solver      solver;
  private final Set<String> languages;

  private final JFrame         window;
  private final Cell[]         cells;
  private final JTextField[]   cellTextFields;
  private final JButton        solveButton;
  private final JLabel         currentWordLabel;
  private final DrawingPanel   drawingPanel;
  private final BonusesPanel   bonusesPanel;
  private final JList          pathsList;
  private final PathsListModel pathsListModel;
  private final JComboBox      languageSelector;
  private final Preferences    preferences = Preferences.userRoot().node(
    this.getClass().getName());

  public RuzzleSolverPro() {

    Properties settings = new Properties();

    try {
      InputStream in = getClass().getResourceAsStream(SETTINGS_FILE_NAME);
      settings.load(in);
      in.close();
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

    languages = new TreeSet<>();

    try {
      languages.addAll(Arrays.stream(Objects.requireNonNull(new File(getClass().getResource(LANGUAGES_DIRECTORY)
                                                                               .toURI()).listFiles()))
                             .map(File::getName)
                             .collect(
                               Collectors.toList())
      );
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    // Set current language
    String language = preferences.get("language", DEFAULT_LANGUAGE);
    try {
      setLanguage(language);
    } catch (FileNotFoundException ex1) {
      // Try to set default language
      try {
        setLanguage(DEFAULT_LANGUAGE);
      } catch (IOException ex2) {
        throw new RuntimeException(ex2);
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    cells = new Cell[N_CELLS];
    for (int index = 0; index < N_CELLS; index++) {
      cells[index] = new Cell();
      cells[index].setIndex(index);
    }

    JLayeredPane gamePanel  = new JLayeredPane();
    JPanel       cellsPanel = new JPanel();
    cellsPanel.setLayout(new GridLayout(GAME_SIZE, GAME_SIZE));

    cellTextFields = new JTextField[N_CELLS];
    FocusListener focusListener = new CellTextFieldFocusListener();
    for (int i = 0; i < N_CELLS; i++) {
      JTextField textField    = new JTextField();
      Font       originalFont = textField.getFont();
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
    gamePanel.add(cellsPanel, Integer.valueOf(1));

    drawingPanel = new DrawingPanel(GAME_SIZE, bounds);
    gamePanel.add(drawingPanel, Integer.valueOf(2));

    bonusesPanel = new BonusesPanel(GAME_SIZE, bounds, cells);
    gamePanel.add(bonusesPanel, Integer.valueOf(3));

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
    languageSelector = new JComboBox(
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

    pathsList = new JList();
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
                        .addKeyEventDispatcher(e -> {
                          if (e.getID() == KeyEvent.KEY_PRESSED &&
                              e.getKeyCode() == KeyEvent.VK_F1) {
                            about();
                            return true;
                          }
                          return false;
                        });

  }

  public static void main(String[] args) {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }

    SwingUtilities.invokeLater(() -> {
      RuzzleSolverPro application = new RuzzleSolverPro();
      application.showUI();
    });

  }

  private void setLanguage(String language) throws IOException {

    String languageDirectory = LANGUAGES_DIRECTORY + File.separator
                               + language;

    String dictionaryFilePath = languageDirectory + File.separator
                                + DICTIONARY_FILE_NAME;
    String scoresFilePath = languageDirectory + File.separator
                            + SCORES_FILE_NAME;

    // Unload previous dictionary to allow garbage collection
    solver.setDictionary(null);

    long       startTime   = System.currentTimeMillis();
    Dictionary dictionary  = new Dictionary(dictionaryFilePath);
    long       elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println("Loaded dictionary \"" + dictionaryFilePath
                       + "\" in " + elapsedTime + " ms");

    ScoreCalculator scoreCalculator = new ScoreCalculator(scoresFilePath);

    solver.setDictionary(dictionary);
    solver.setScoreCalculator(scoreCalculator);

    preferences.put("language", language);

  }

  private void about() {
    try {
      Desktop.getDesktop().browse(new URI(WEBSITE_URI));
    } catch (IOException | URISyntaxException e) {
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

    long       startTime = System.currentTimeMillis();
    List<Path> paths     = solver.solve(cells);
    long       elapsed   = System.currentTimeMillis() - startTime;

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
    currentWordLabel.setText(path.getWord());
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

  public void showUI() {
    window.setVisible(true);
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

    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_SPACE
          || e.getKeyCode() == KeyEvent.VK_ENTER) {
        robot.keyPress(KeyEvent.VK_DOWN);
      }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

  }

  private final static class PathsListModel implements ListModel<Path> {

    private final List<ListDataListener> listeners;
    private       List<Path>             pathsList;

    public PathsListModel() {
      listeners = new ArrayList();
    }

    public void addListDataListener(ListDataListener listener) {
      listeners.add(listener);
    }

    public Path getElementAt(int index) {
      return pathsList.get(index);
    }

    public int getSize() {
      if (pathsList == null) {
        return 0;
      }
      return pathsList.size();
    }

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

  private final class WindowActionListener implements ActionListener {
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

      String language = (String) languageSelector.getItemAt(languageSelector
                                                              .getSelectedIndex());

      clear(false);
      Cursor defaultCursor = window.getCursor();
      window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      try {
        setLanguage(language);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }

      window.setCursor(defaultCursor);

      cellTextFields[0].requestFocus();

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

    public void keyPressed(KeyEvent e) {

      int        keyCode   = e.getKeyCode();
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

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {

      int keyCode = e.getKeyCode();

      if (keyCode == KeyEvent.VK_ALT || e.isAltDown()) {
        return;
      }

      JTextField textField       = (JTextField) e.getSource();
      String     previousContent = textField.getText();

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

    public void focusGained(FocusEvent e) {
      JTextField textField = (JTextField) e.getSource();
      textField.setCaretPosition(0);
    }

    public void focusLost(FocusEvent e) {
    }

  }

}
