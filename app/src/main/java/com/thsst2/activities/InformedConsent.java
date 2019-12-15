package com.thsst2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thsst2.R;

public class InformedConsent extends AppCompatActivity {

    DrawingView drawView;
    EditText editName;
    EditText editName2;
    Button btnSigned;

    int studentID;
    int schoolID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informed_consent);

        this.editName = (EditText) findViewById(R.id.editName);
        this.editName2 = (EditText) findViewById(R.id.editName2);
        this.drawView = (DrawingView) findViewById(R.id.drawing);
        this.btnSigned = (Button) findViewById(R.id.btnSigned);

        Intent intent = getIntent();
        this.studentID = intent.getIntExtra("StudentID", -1);
        this.schoolID = intent.getIntExtra("SchoolID", -1);
    }

    public void signed(View view) {
        if((!this.editName.getText().toString().equals("") && !this.editName2.getText().toString().equals(""))
                && this.drawView.getIsSigned()) {
            Intent intent = new Intent(this, PSCQuestions.class);
            intent.putExtra("StudentID", this.studentID);
            intent.putExtra("SchoolID", this.schoolID);
            startActivity(intent);
        }
    }
}
