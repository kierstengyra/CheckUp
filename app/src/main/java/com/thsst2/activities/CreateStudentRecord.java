package com.thsst2.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thsst2.processes.DBHelper;
import com.thsst2.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Type: Activity
 * CreateStudentRecord displays a form to be filled up
 * by the Test Administrator. The information will be saved
 * to the database.
 */
public class CreateStudentRecord extends AppCompatActivity implements OnItemSelectedListener {

    //Properties
    ArrayAdapter<String> monthAdapter;
    ArrayAdapter<Integer> dayAdapter;
    ArrayAdapter<Integer> yearAdapter;

    List<Integer> years;
    List<String> months;
    List<Integer> days;

    Spinner spinMonths;
    Spinner spinDays;
    Spinner spinYears;

    Calendar now;
    private int startYear;
    private int endYear;

    TextView txtEditCreate;
    EditText fname;
    EditText mname;
    EditText lname;
    EditText suffix;
    EditText grade;
    EditText section;

    RadioGroup radioSexGroup;
    RadioButton radioSexButton;

    DBHelper database;

    int schoolID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student_record);

        this.initComponents();
    }

    //This method creates the student record and uploads it to the database.
    public void onSubmit(View view) {
        String firstName = this.fname.getText().toString();
        String middleName = this.mname.getText().toString();
        String lastName = this.lname.getText().toString();
        String suffix = this.suffix.getText().toString();

        int year = Integer.parseInt(spinYears.getSelectedItem().toString());
        int day = Integer.parseInt(spinDays.getSelectedItem().toString());
        int month = spinMonths.getSelectedItemPosition()+1;

        int selectedId = this.radioSexGroup.getCheckedRadioButtonId();
        this.radioSexButton = (RadioButton) findViewById(selectedId);
        char sex = (radioSexButton.getText().toString().equals("Male") ? 'M' : 'F');

        String rawGrade = this.grade.getText().toString();
        String sec = this.section.getText().toString();

        if(isValid(firstName, middleName, suffix, lastName, rawGrade, sec)) {
            int gradeLevel = Integer.parseInt(rawGrade);
            int age = this.getAge();

            boolean isInserted = this.database.insertStudentRecord(firstName, middleName, lastName, suffix, age, sex, year, day, month, gradeLevel, sec, this.schoolID);

            if (isInserted)
                Toast.makeText(this, "Student record added.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Could not create record.", Toast.LENGTH_SHORT).show();

            int studentID = 0;
            Cursor res = this.database.getStudentID(firstName, middleName, lastName, suffix, age, this.schoolID);
            if (res.getCount() == 0 || res.getCount() > 1)
                Toast.makeText(this, "Student not found.", Toast.LENGTH_SHORT).show();
            else {
                while (res.moveToNext()) {
                    studentID = res.getInt(res.getColumnIndex("col_student_id"));
                }
            }

            Intent intent = new Intent(this, ChooseModule.class);
            intent.putExtra("SchoolID", this.schoolID);
            intent.putExtra("StudentID", studentID);

            startActivity(intent);
        }else {
            int count = emptyFieldCount(firstName, middleName, suffix,lastName, rawGrade, sec);
            this.txtEditCreate.setBackgroundColor(Color.rgb(238, 99, 99));
            this.txtEditCreate.setText("There are still " + count + " invalid fields");
        }
    }


    private int emptyFieldCount(String firstName, String middleName, String suffix, String lastName, String gradeLevel, String sec){
        int count = 0;
        if(firstName.equals("")) {
            this.fname.setText("");
            this.fname.setHintTextColor(Color.rgb(255, 0, 0));
            this.fname.setHint("First Name: Required Field.");
            count ++;
        }
        else if(!firstName.matches("[a-zA-Z0-9]+")){
            this.fname.setText("");
            this.fname.setHintTextColor(Color.rgb(255, 0, 0));
            this.fname.setHint("First Name: Special characters are not accepted.");
            count ++;
        }

        if(lastName.equals("")) {
            this.lname.setText("");
            this.lname.setHintTextColor(Color.rgb(255, 0, 0));
            this.lname.setHint("Last Name: Required Field.");
            count ++;
        }

        else if(!lastName.matches("[a-zA-Z0-9]+")){
            this.lname.setText("");
            this.lname.setHintTextColor(Color.rgb(255, 0, 0));
            this.lname.setHint("Last Name: Special characters are not accepted.");
            count ++;
        }

        if(middleName.equals("")) {
            this.mname.setText("");
            this.mname.setHintTextColor(Color.rgb(255, 0, 0));
            this.mname.setHint("Middle Name: Required Field.");
            count ++;
        }

        else if(!middleName.matches("[a-zA-Z0-9]+")){
            this.mname.setText("");
            this.mname.setHintTextColor(Color.rgb(255, 0, 0));
            this.mname.setHint("Middle Name: Special characters are not accepted.");
            count ++;
        }

        if(!suffix.matches("[a-zA-Z0-9]+") && !suffix.equals("")){
            this.suffix.setText("");
            this.suffix.setHintTextColor(Color.rgb(255, 0, 0));
            this.suffix.setHint("Suffix(Optional): Special characters are not accepted.");
            count ++;
        }

        if(gradeLevel.equals("")) {
            this.grade.setText("");
            this.grade.setHintTextColor(Color.rgb(255, 0, 0));
            this.grade.setHint("Grade Level: Required Field.");
            count ++;
        }

        else if(!gradeLevel.matches("[0-9]+")){
            this.grade.setText("");
            this.grade.setHintTextColor(Color.rgb(255, 0, 0));
            this.grade.setHint("Last Name: Must only use numbers.");
            count ++;
        }

        if(sec.equals("")) {
            this.section.setText("");
            this.section.setHintTextColor(Color.rgb(255, 0, 0));
            this.section.setHint("Section: Required Field.");
            count ++;
        }

        else if(!sec.matches("[a-zA-Z0-9]+")){
            this.section.setText("");
            this.section.setHintTextColor(Color.rgb(255, 0, 0));
            this.section.setHint("Section: Special characters are not accepted.");
            count ++;
        }

        return count;
    }

    private boolean isValid(String firstName, String middleName, String suffix, String lastName, String gradeLevel, String sec) {
        boolean isValid = true;

        if(firstName.equals("") || lastName.equals("") || gradeLevel.equals("") || sec.equals("")
                ||middleName.equals("") || !firstName.matches("[a-zA-Z0-9]+") || !lastName.matches("[a-zA-Z0-9]+")
                || !gradeLevel.matches("[0-9]+") || !sec.matches("[a-zA-Z0-9]+")
                || !middleName.matches("[a-zA-Z0-9]+") || (!suffix.matches("[a-zA-Z0-9]+") && !suffix.equals(""))){
            isValid = false;
        }

        return isValid;

    }

    private int getMonthInt(Spinner month){
        switch(month.getSelectedItem().toString()){
            case "January": return 1;
            case "February": return 2;
            case "March": return 3;
            case "April": return 4;
            case "May": return 5;
            case "June":return 6;
            case "July": return 7;
            case "August": return 8;
            case "September": return 9;
            case "October": return 10;
            case "November": return 11;
            case "December": return 12;
        }

        return 0;
    }

    private int getAge(){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        int yearInt, monthInt, dayInt;


        yearInt = Integer.parseInt(this.spinYears.getSelectedItem().toString());
        monthInt = getMonthInt(this.spinMonths);
        dayInt = Integer.parseInt(this.spinDays.getSelectedItem().toString());

        Log.e("YearInt: ", yearInt+"");
        Log.e("MonthInt: ", monthInt+"");
        Log.e("DayInt: ", dayInt+"");

        dob.set(yearInt, monthInt, dayInt);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);

        return ageInt;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        Toast.makeText(parent.getContext(), "Selected: "+item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //This method initializes the properties.
    private void initComponents() {
        // Get school ID from previous activity
        Intent intent = getIntent();
        this.schoolID = intent.getIntExtra("SchoolID", -1);

        // Instantiate DBHelper
        this.database = new DBHelper(this);

        // Instantiate spinners
        this.spinMonths = (Spinner) findViewById(R.id.spinMonth);
        this.spinDays = (Spinner) findViewById(R.id.spinDay);
        this.spinYears = (Spinner) findViewById(R.id.spinYear);

        // Instantiate spinMonths dataset
        this.months = new ArrayList<String>();
        this.months.add("January");
        this.months.add("February");
        this.months.add("March");
        this.months.add("April");
        this.months.add("May");
        this.months.add("June");
        this.months.add("July");
        this.months.add("August");
        this.months.add("September");
        this.months.add("October");
        this.months.add("November");
        this.months.add("December");

        // Instantiate spinDays dataset
        this.days = new ArrayList<Integer>();
        for (int i = 1; i <= 31; i++)
            this.days.add(i);

        // Instantiate spinYears dataset
        this.now = Calendar.getInstance();
        this.endYear = now.get(Calendar.YEAR) - 4;
        this.startYear = this.endYear - 20;
        this.years = new ArrayList<Integer>();
        for (int i = this.endYear; i >= this.startYear; i--)
            this.years.add(i);

        // Get form elements
        this.fname = (EditText) findViewById(R.id.txtFirstName);
        this.mname = (EditText) findViewById(R.id.txtMiddleName);
        this.lname = (EditText) findViewById(R.id.txtLastName);
        this.suffix = (EditText) findViewById(R.id.txtSuffix);
        this.radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        this.grade = (EditText) findViewById(R.id.txtGrade);
        this.section = (EditText) findViewById(R.id.txtSection);
        this.txtEditCreate = (TextView) findViewById(R.id.txtHeadingCreate);

        // Fill spinners with data
        monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMonths.setAdapter(monthAdapter);

        dayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDays.setAdapter(dayAdapter);

        yearAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinYears.setAdapter(yearAdapter);
    }
}
