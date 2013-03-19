package com.giacomodrago.ruzzlesolverpro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import com.giacomodrago.ruzzlesolverpro.solver.Cell;
import com.giacomodrago.ruzzlesolverpro.solver.Path;

final class DrawingPanel extends OverlayPanel {

	private static final long serialVersionUID = 621489240879011335L;

	private static final float STROKE_WIDTH = 3.0f;
	private static final Color STROKE_COLOR =
			new Color(0.0f, 0.0f, 1.0f, 0.5f);

	private Path wordPath;

	public DrawingPanel(int gameSize, Rectangle bounds) {
		super(gameSize, bounds);
	}

	public void setWordPath(Path wordPath) {
		this.wordPath = wordPath;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (wordPath != null) {
			
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			Stroke stroke = new BasicStroke(STROKE_WIDTH);
			g2.setStroke(stroke);
			g2.setColor(STROKE_COLOR);
			
			List<Point> points = new ArrayList<Point>();
			for (Cell cell : wordPath.getTraversedCells()) {
				Point point = getCellCoordinates(cell.getIndex());
				point.x += getCellWidth() / 2;
				point.y += getCellHeight() / 2;
				points.add(point);
			}

			GeneralPath polyline = new GeneralPath();

			polyline.moveTo(points.get(0).x, points.get(0).y);
			
			for (int i = 1; i <= points.size() - 1; i++) {
				Point p = points.get(i);
				polyline.lineTo(p.x, p.y);
			}
			
			g2.draw(polyline);
			
			g2.dispose();
			
		}

	}

}
