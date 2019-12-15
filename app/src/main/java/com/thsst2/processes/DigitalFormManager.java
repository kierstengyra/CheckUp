package com.thsst2.processes;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Type: Process
 * DigitalFormManager handles the digital form module.
 */
public class DigitalFormManager {

    private static DigitalFormManager instance = null;
    private ArrayList<Bitmap> pscDrawings;
    private ArrayList<String> pscQuestions;

    protected DigitalFormManager() {
        this.pscDrawings = new ArrayList<Bitmap>();
        this.pscQuestions = new ArrayList<String>();
    }

    public static DigitalFormManager getInstance() {
        if(instance == null)
            instance = new DigitalFormManager();

        return instance;
    }

    public ArrayList<Bitmap> getPscDrawings() {
        return this.pscDrawings;
    }

    public void addDrawing(Bitmap pscDrawing) {
        this.pscDrawings.add(pscDrawing);
    }

    public ArrayList<String> getPscQuestions() {
        return this.pscQuestions;
    }

    public void addQuestion(String pscQuestion) {
        this.pscQuestions.add(pscQuestion);
    }
}
