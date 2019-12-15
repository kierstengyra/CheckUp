package com.thsst2.processes;

import android.util.Log;

import java.util.ArrayList;

/**
 * Type: Process
 * Question is the class type for all pre-defined questions.
 */
public class Question {

    private int number;
	private ArrayList<Integer> pixelList;
	private ArrayList<Integer> scoreList;
	private ArrayList<String> answerList;
	private double avgPixels;
	private int answerCnt;
	private String question;
	private int page;

	private double x;
	private double y;
	private double width;
	private double height;

	public Question(int number, String question) {
		this.pixelList = new ArrayList<Integer>();
		this.scoreList = new ArrayList<Integer>();
		this.answerList = new ArrayList<String>();

		this.setNumber(number);
		this.setQuestion(question);
		this.setAnswerCnt(0);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getAvgPixels() {
		return avgPixels;
	}

	public void setAvgPixels() {
		int sum = 0;

		for(int i = 0; i < this.pixelList.size(); i++)
			sum += this.pixelList.get(i);

		this.avgPixels = sum/this.pixelList.size();
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getAnswerCnt() {
		return answerCnt;
	}

	public void addAnswerCnt() {
		this.answerCnt++;
	}

	public void setAnswerCnt(int answerCnt) {
		this.answerCnt = answerCnt;
	}

	public ArrayList<Integer> getPixelList() {
		return this.pixelList;
	}

	public int getPixel(int index) {
		return this.pixelList.get(index);
	}

	public void setPixelList(ArrayList<Integer> pixelList) {
		this.pixelList = pixelList;
	}

	public void addPixel(int pixel) {
		this.pixelList.add(pixel);
	}

	public void addScore(int answer) {
		this.scoreList.add(answer);

		switch(answer) {
			case 0: this.answerList.add("Hindi nangyayari");
				break;
			case 1: this.answerList.add("Minsan nangyayari");
				break;
			case 2: this.answerList.add("Madalas nangyayari");
				break;
			default: break;
		}
	}

	public int getScore(int index) {
		return this.scoreList.get(index);
	}

	public String getAnswer(int index) {
		return this.answerList.get(index);
	}

	public ArrayList<Integer> getScoreList() {
		return this.scoreList;
	}

	public ArrayList<String> getAnswerList() {
		return this.answerList;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return this.page;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return this.x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return this.y;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return this.width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getHeight() {
		return this.height;
	}

}
