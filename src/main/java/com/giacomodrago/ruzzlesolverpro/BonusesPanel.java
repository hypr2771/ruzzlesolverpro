package com.giacomodrago.ruzzlesolverpro;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.giacomodrago.ruzzlesolverpro.solver.Bonus;
import com.giacomodrago.ruzzlesolverpro.solver.Cell;

final class BonusesPanel extends OverlayPanel {

	private static final long serialVersionUID = 2729471927988520384L;
	
	private static final double LABEL_SIZE_FACTOR = 4.0;
	private static final int LABEL_CELL_PADDING = 4;
	private static final int FONT_SIZE = 12;
	
	private final Cell[] cells;
	private final JLabel[] bonusLabels;
	
	public BonusesPanel(int gameSize, Rectangle bounds, Cell[] cells) {
		
		super(gameSize, bounds);
		
		this.cells = cells;
		this.bonusLabels = new JLabel[cells.length];
		
		int cellSize = getWidth() / getGameSize();
		int labelSize = (int) Math.floor((double) cellSize / LABEL_SIZE_FACTOR);
		
		for (int i = 0; i < bonusLabels.length; i++) {
			JLabel bonusLabel = new JLabel("x");
			Point offset = getCellCoordinates(i);
			int posX = offset.x + cellSize - labelSize - LABEL_CELL_PADDING;
			int posY = offset.y + LABEL_CELL_PADDING;
			bonusLabel.setBorder(BorderFactory.createLineBorder(Color.black));
			bonusLabel.setOpaque(true);
			bonusLabel.setBounds(posX, posY, labelSize, labelSize);
			bonusLabel.setHorizontalAlignment(SwingConstants.CENTER);
			bonusLabel.setVerticalAlignment(SwingConstants.CENTER);
			bonusLabel.setVisible(false);
			bonusLabel.setFont(
					new Font(bonusLabel.getFont().getName(), Font.BOLD, FONT_SIZE));
			add(bonusLabel);
			bonusLabels[i] = bonusLabel;
		}
		
	}
	
	public void refresh(int id) {
		JLabel bonusLabel = bonusLabels[id];
		Bonus bonus = cells[id].getBonus();
		if (bonus == null) {
			bonusLabel.setText("");
			bonusLabel.setBackground(Color.WHITE);
			bonusLabel.setVisible(false);
		} else {
			bonusLabel.setText(bonus.toString());
			Color backgroundColor = null;
			Color textColor = null;
			switch (bonus) {
			case DL:
				backgroundColor = Color.GREEN;
				textColor = Color.BLACK;
				break;
			case TL:
				backgroundColor = Color.BLUE;
				textColor = Color.WHITE;
				break;
			case DW:
				backgroundColor = Color.ORANGE;
				textColor = Color.BLACK;
				break;
			case TW:
				backgroundColor = Color.RED;
				textColor = Color.WHITE;
				break;
			}
			bonusLabel.setBackground(backgroundColor);
			bonusLabel.setForeground(textColor);
			bonusLabel.setVisible(true);
		}
	}
	
	public void refreshAll() {
		for (int id = 0; id < bonusLabels.length; id++) {
			refresh(id);
		}
	}

}
