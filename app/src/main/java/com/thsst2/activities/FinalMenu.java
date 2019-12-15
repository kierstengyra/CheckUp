package com.thsst2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thsst2.R;
import com.thsst2.processes.PaperFormManager;

import java.util.ArrayList;

/**
 * Type: Activity
 * FinalMenu is the last screen of the workflow. It
 * asks the user if there are still other students or
 * close the app instead.
 */
public class FinalMenu extends AppCompatActivity {

    //Properties
    Button btnNextStudent;
    Button btnExit;
    int studentID;
    int schoolID;
    ArrayList<Integer> pscAnswers;
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("PaperFormManager", "FinalMenu Activity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_menu);

        this.initComponents();

        Intent intent = getIntent();
        this.studentID = intent.getIntExtra("StudentID", -1);
        this.schoolID = intent.getIntExtra("SchoolID", -1);
        this.mode = intent.getStringExtra("Mode");
        this.pscAnswers = intent.getIntegerArrayListExtra("PSCAnswers");
    }

    //This method goes back to the CheckStudentRecord Activity.
    public void nextStudent(View view) {
        for(int i = 0; i < PaperFormManager.getInstance().getAllPages().size(); i++)
            PaperFormManager.getInstance().getPage(i).getFieldList().clear();

        PaperFormManager.getInstance().getAllPages().clear();

        Log.e("FinalMenu", "Page Size: "+PaperFormManager.getInstance().getAllPages().size());

        Intent intent = new Intent(this, CheckStudentRecord.class);
        intent.putExtra("SchoolID", this.schoolID);
        startActivity(intent);
    }

    public void exit(View view) {
//        finish();
        System.exit(1);
    }

    //This method initializes the properties.
    public void initComponents() {
        this.btnNextStudent = (Button) findViewById(R.id.btnNextStudent);
        this.btnExit = (Button) findViewById(R.id.btnExit);

        this.pscAnswers = new ArrayList<Integer>();
    }
}
