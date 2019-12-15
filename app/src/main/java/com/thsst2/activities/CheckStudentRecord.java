package com.thsst2.activities;

/**
 * Type: Activity
 * CheckStudentRecord handles the screen for determining
 * the availability of the student record.
 * */

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thsst2.R;
import com.thsst2.processes.DBHelper;
import com.thsst2.processes.DigitalFormManager;
import com.thsst2.processes.Field;
import com.thsst2.processes.FieldManager;
import com.thsst2.processes.PaperFormManager;
import com.thsst2.processes.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CheckStudentRecord extends AppCompatActivity {

    //Properties
    TextView txtSchoolName;
    int school_id;
    String school_name;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);

        Intent intent = getIntent();
        this.school_id = intent.getIntExtra("SchoolID", -1);

        this.dbHelper = new DBHelper(this);
        this.school_name = this.dbHelper.getSchoolName(this.school_id);

        this.txtSchoolName = (TextView) findViewById(R.id.txtSchoolName);
        this.txtSchoolName.setText(this.school_name);

        this.readQuestions();
        this.readCSV();
        this.reset();
    }

    //This method starts the CreateStudentRecord Activity.
    public void createRecord(View view) {
        Intent intent = new Intent(this, CreateStudentRecord.class);
        intent.putExtra("SchoolID", this.school_id);

        startActivity(intent);
    }

    //This method starts the ChooseStudentRecord Activity.
    public void selectRecord(View view) {
        Intent intent = new Intent(this, ChooseStudentRecord.class);
        intent.putExtra("SchoolID", this.school_id);

        startActivity(intent);
    }

    private void reset() {
        if(DigitalFormManager.getInstance().getPscQuestions().size() != 0) {
            DigitalFormManager.getInstance().getPscQuestions().clear();
        }

        if(DigitalFormManager.getInstance().getPscDrawings().size() != 0) {
            DigitalFormManager.getInstance().getPscDrawings().clear();
        }
    }

//    private void getQuestionsAndDrawings() {
//        Cursor questions = this.dbHelper.getAllQuestions();
//        Cursor drawings = this.dbHelper.getAllPSCOptionDrawings();
//
//        if(questions.getCount() == 0 || drawings.getCount() == 0)
//            Toast.makeText(this, "Could not retrieve questions or drawings.", Toast.LENGTH_SHORT).show();
//        else {
//            while(questions.moveToNext())
//                DigitalFormManager.getInstance().addQuestion(questions.getString(questions.getColumnIndex("col_question_tag")));
//
//            while(drawings.moveToNext()) {
//                byte[] imgarray = drawings.getBlob(drawings.getColumnIndex("col_pscoptions_img"));
//                DigitalFormManager.getInstance().addDrawing(BitmapFactory.decodeByteArray(imgarray, 0, imgarray.length, null));
//            }
//        }
//    }

    //This method retrieves the questions from the database
    //and creates an object for each.
    private void readQuestions() {
        Cursor result = this.dbHelper.getAllQuestions();

        if(result.getCount() == 0) {
            Log.e("CheckStudentRecord", "No questions found.");
        }
        else {
            while(result.moveToNext()) {
                int num = result.getInt(result.getColumnIndex("col_psc_id"));
                String question = result.getString(result.getColumnIndex("col_question_tag"));

                Question q = new Question(num, question);
                PaperFormManager.getInstance().addQuestion(q);
            }
        }

        AssetManager assetManager = getAssets();
        String[] list;
        BufferedReader reader = null;

        try {
            list = assetManager.list("csv");
            String splitter = ",";

            if(list.length > 0) {
                for(String csvfile : list) {
                    InputStream is = assetManager.open("csv/"+csvfile);
                    InputStreamReader isr = new InputStreamReader(is);
                    String line = "";

                    if(csvfile.startsWith("QuestionBlocks") && csvfile.endsWith("csv")) {
                        reader = new BufferedReader(isr);
                        int q = 0;

                        while((line = reader.readLine()) != null) {
                            String[] info = line.split(splitter);
                            PaperFormManager.getInstance().getQuestion(q).setX(Double.parseDouble(info[1]));
                            PaperFormManager.getInstance().getQuestion(q).setY(Double.parseDouble(info[2]));
                            PaperFormManager.getInstance().getQuestion(q).setWidth(Double.parseDouble(info[3]));
                            PaperFormManager.getInstance().getQuestion(q).setHeight(Double.parseDouble(info[4]));
                            PaperFormManager.getInstance().getQuestion(q).setPage(Integer.parseInt(info[5])-1);

                            q++;
                        }
                    }
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //This method creates the Field objects.
    private void readCSV() {
        AssetManager assetManager = getAssets();
        String[] list;
        BufferedReader reader = null;

        try {
            list = assetManager.list("csv");
            String splitter = ",";

            if(list.length > 0) {
                for(String csvfile : list) {
                    InputStream is = assetManager.open("csv/"+csvfile);
                    InputStreamReader isr = new InputStreamReader(is);
                    String line = "";

                    if(csvfile.startsWith("Page") && csvfile.endsWith("csv") && !csvfile.contains("Block")) {
                        FieldManager fm = new FieldManager();
                        reader = new BufferedReader(isr);

                        while((line = reader.readLine()) != null) {
                            String[] info = line.split(splitter);
                            Field field = new Field(Double.parseDouble(info[1]), Double.parseDouble(info[2]), Double.parseDouble(info[3]), Double.parseDouble(info[4]), Integer.parseInt(info[5]), Integer.parseInt(info[6]));
                            fm.addField(field);
                        }

                        PaperFormManager.getInstance().addPage(fm);
                    }
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
