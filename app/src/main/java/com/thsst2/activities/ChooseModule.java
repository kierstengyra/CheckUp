package com.thsst2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.thsst2.R;

import org.opencv.android.OpenCVLoader;

/**
 * Type: Activity
 * ChooseModule handles the screen for choosing which
 * module to use. The two modules are the Digital Form
 * and the Paper Form.
 */
public class ChooseModule extends AppCompatActivity {

    //Properties
    Button btnPSC;
    Button btnDAP;
    int studentID;
    int schoolID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_module);

        if(!OpenCVLoader.initDebug())
            OpenCVLoader.initDebug();

        Intent intent = getIntent();
        this.studentID = intent.getIntExtra("StudentID", -1);
        this.schoolID = intent.getIntExtra("SchoolID", -1);

        this.initComponents();
    }

    //This method initializes the properties.
    public void initComponents() {
        this.btnPSC = (Button) findViewById(R.id.btnPSC);
        this.btnDAP = (Button) findViewById(R.id.btnDAP);
    }

    //This method loads the PSCQuestions Activity.
    public void loadPSC(View view) {
        Intent intent = new Intent(this, InformedConsent.class);
        intent.putExtra("StudentID", this.studentID);
        intent.putExtra("SchoolID", this.schoolID);
        startActivity(intent);
    }

    //This method loads the CameraOverlay Activity.
    public void loadCamera(View view) {
        Intent intent = new Intent(this, CameraOverlay.class);
        intent.putExtra("StudentID", this.studentID);
        intent.putExtra("SchoolID", this.schoolID);
        startActivity(intent);
    }
}
