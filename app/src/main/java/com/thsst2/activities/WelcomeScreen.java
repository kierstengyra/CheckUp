package com.thsst2.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.thsst2.processes.DBHelper;
import com.thsst2.R;
import com.thsst2.processes.DigitalFormManager;

/**
 * Type: Activity
 * WelcomeScreen is the first screen seen by the user.
 * This screen asks the user for the password initially
 * given by an administrator.
 * */

public class WelcomeScreen extends AppCompatActivity {

    //Properties
    Button btnStart;
    EditText txtPassword;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        this.btnStart = (Button) findViewById(R.id.btnStart);
        this.txtPassword = (EditText) findViewById(R.id.txtPassword);

        this.dbHelper = new DBHelper(this);
    }

    //This method verifies the password entered.
    public void verifyPassword(View view) {
        Cursor result = this.dbHelper.confirmSession(this.txtPassword.getText().toString());
        if(result.getCount() == 0) {
            Toast.makeText(WelcomeScreen.this, "Invalid password.", Toast.LENGTH_SHORT).show();
        }
        else {
            int school_id = 0;

            while(result.moveToNext()) {
                school_id = result.getInt(result.getColumnIndex("col_school_id"));
            }

            Intent intent = new Intent(this, CheckStudentRecord.class);
            intent.putExtra("SchoolID", school_id);
            startActivity(intent);
        }
    }
}
