package com.giacomodrago.ruzzlesolverpro;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

abstract class OverlayPanel extends JPanel {

	private static final long serialVersionUID = -8145114011831133851L;

	protected final int gameSize;
	
	public OverlayPanel(int gameSize, Rectangle bounds) {
		this.gameSize = gameSize;
		setBounds(bounds);
		setOpaque(false);
		setLayout(null);
	}
	
	protected int getGameSize() {
		return gameSize;
	}
	
	protected Point getCellCoordinates(int id) {

		int row = id / getGameSize();
		int col = id % getGameSize();

		int x = col * getCellWidth();
		int y = row * getCellHeight();

		return new Point(x, y);

	}
	
	protected int getCellWidth() {
		int width = getWidth();
		int cellWidth = width / getGameSize();
		return cellWidth;
	}
	
	protected int getCellHeight() {
		int height = getHeight();
		int cellHeight = height / getGameSize();
		return cellHeight;
	}
	
}
