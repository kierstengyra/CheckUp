package com.thsst2.processes;

import android.util.Log;

import java.util.ArrayList;

/**
 * Type: Process
 * FieldManager handles all the
 * pre-defined fields per page.
 * */

public class FieldManager {

	private ArrayList<Field> fieldList;
	private int page;
	private int partialScore;
	private ArrayList<String> partialAnswers;
	private double avgPixels;
	private boolean hasPicture;

	public FieldManager() {
		this.fieldList = new ArrayList<Field>();
		this.hasPicture = false;
	}

	public void setAnswers() {
		for(int i = 0; i < this.fieldList.size(); i++) {
			int question = this.fieldList.get(i).getQuestion();
			int answer = this.fieldList.get(i).getScore();

			if(this.fieldList.get(i).isSelected())
				PaperFormManager.getInstance().getQuestion(question-1).addScore(answer);
		}
	}

	public void setAvgPixelsPerQuestion() {
		for(int i = 0; i < this.fieldList.size(); i++) {
			int question = this.fieldList.get(i).getQuestion();
			int nonzero = this.fieldList.get(i).getNonzero_pixels();
			PaperFormManager.getInstance().getQuestion(question-1).addPixel(nonzero);

			if((i+1)%3 == 0) {
				PaperFormManager.getInstance().getQuestion(question-1).setAvgPixels();
			}
		}
	}

	public void selectPossibleAnswers() {
		this.setAvgPixelsPerQuestion();

		for(int i = 0; i < this.fieldList.size(); i++) {
			int question = this.fieldList.get(i).getQuestion();
			double avg = PaperFormManager.getInstance().getQuestion(question-1).getAvgPixels();

			if(this.fieldList.get(i).getNonzero_pixels() > avg) {
				PaperFormManager.getInstance().getQuestion(question-1).addAnswerCnt();
				this.fieldList.get(i).setSelected(true);
			}
		}
	}

	public ArrayList<Field> getFieldList() {
		return fieldList;
	}

	public Field getField(int index) {
		return this.fieldList.get(index);
	}

	public void addField(Field field) {
		this.fieldList.add(field);
	}

	public void setFieldList(ArrayList<Field> fieldList) {
		this.fieldList = fieldList;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPartialScore() {
		return partialScore;
	}

	public void setPartialScore(int partialScore) {
		this.partialScore = partialScore;
	}

	public ArrayList<String> getPartialAnswers() {
		return partialAnswers;
	}

	public void setPartialAnswers(ArrayList<String> partialAnswers) {
		this.partialAnswers = partialAnswers;
	}

	public double getAvgPixels() {
		return avgPixels;
	}

	public void setAvgPixels(double avgPixels) {
		this.avgPixels = avgPixels;
	}

	public boolean containsPicture() {
		return hasPicture;
	}

	public void setHasPicture(boolean hasPicture) {
		this.hasPicture = hasPicture;
	}
}
