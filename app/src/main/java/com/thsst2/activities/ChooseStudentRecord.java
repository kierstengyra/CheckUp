package com.thsst2.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.thsst2.processes.DBHelper;
import com.thsst2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Type: Activity
 * ChooseStudentRecord retrieves all the student records
 * for a particular school and displays all.
 */
public class ChooseStudentRecord extends AppCompatActivity implements OnItemClickListener {

    //Properties
    DBHelper database;
    int schoolID;
    ListView viewStudents;
    List<String> studentList;
    ArrayAdapter<String> studentListAdapter;
    ArrayList<Integer> studentIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_student_record);

        this.initComponents();
    }

    //This method initializes the properties.
    private void initComponents() {
        Intent intent = getIntent();
        this.schoolID = intent.getIntExtra("SchoolID", -1);

        viewStudents = (ListView) findViewById(R.id.listStudents);

        this.database = new DBHelper(this);
        this.studentIDs = new ArrayList<>();

        studentList = new ArrayList<String>();
        Cursor res = this.database.getAllStudentRecordsFromSchool(this.schoolID);
        if(res.getCount() == 0) {
            Toast.makeText(this, "No such student.", Toast.LENGTH_SHORT).show();
        }

        while(res.moveToNext()) {
            studentIDs.add(res.getInt(res.getColumnIndex("col_student_id")));
            String firstname = res.getString(res.getColumnIndex("col_student_fname"));
            String middlename = res.getString(res.getColumnIndex("col_student_mname"));
            String lastname = res.getString(res.getColumnIndex("col_student_lname"));
            String suffixCheck = res.getString(res.getColumnIndex("col_student_suffix"));
            String suffix = (suffixCheck == null || suffixCheck.equals("-") ? " " : suffixCheck);

            studentList.add(lastname+", "+firstname+" "+suffix+" "+middlename+"");
        }

        studentListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentList);
        viewStudents.setAdapter(studentListAdapter);
        viewStudents.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int selected = studentIDs.get(position);

        Intent intent = new Intent(this, ChooseModule.class);
        intent.putExtra("SchoolID", this.schoolID);
        intent.putExtra("StudentID", selected);

        startActivity(intent);
    }
}
