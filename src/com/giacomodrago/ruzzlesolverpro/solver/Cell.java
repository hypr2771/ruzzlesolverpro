package com.giacomodrago.ruzzlesolverpro.solver;

public class Cell {

	private int index;
	private Character letter;
	private Bonus bonus;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Character getLetter() {
		return letter;
	}

	public void setLetter(Character letter) {
		this.letter = letter;
	}

	public Bonus getBonus() {
		return bonus;
	}

	public void setBonus(Bonus bonus) {
		this.bonus = bonus;
	}

}
