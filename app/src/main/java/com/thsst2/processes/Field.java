package com.thsst2.processes;

/**
 * Type: Process
 * Field is the class type of pre-defined fields.
 * */

public class Field {

	private double x;
	private double y;

	private double width;
	private double height;

	private int question;
	private int score;
	private String answer;

	private boolean isSelected;

	private int nonzero_pixels;

	public Field(double x, double y, double width, double height, int question, int score) {
		this.setX(x);
		this.setY(y);

		this.setWidth(width);
		this.setHeight(height);

		this.setQuestion(question);
		this.setScore(score);

		this.setSelected(false);

		this.setNonzero_pixels(0);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public int getQuestion() {
		return question;
	}

	public void setQuestion(int question) {
		this.question = question;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;

		switch(score) {
			case 0: this.setAnswer("Hindi nangyayari");
				break;
			case 1: this.setAnswer("Paminsan-minsang nangyayari");
				break;
			case 2: this.setAnswer("Madalas mangyari");
				break;
			default: break;
		}
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getNonzero_pixels() {
		return nonzero_pixels;
	}

	public void setNonzero_pixels(int nonzero_pixels) {
		this.nonzero_pixels = nonzero_pixels;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
