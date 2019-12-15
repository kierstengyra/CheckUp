package com.thsst2.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thsst2.processes.DBHelper;
import com.thsst2.R;
import com.thsst2.processes.DigitalFormManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Type: Activity
 * PSCQuestions displays the Digital Form.
 * */

public class PSCQuestions extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //Properties
    RelativeLayout psc_layout;
    ListView listSummary;
    ArrayAdapter<String> listAnswers;

    ImageView btnHindi;
    ImageView btnMinsan;
    ImageView btnMadalas;
    Button btnSkip;

    Button btnConfirmAns;

    TextView txtHindi;
    TextView txtMinsan;
    TextView txtMadalas;

    EditText txtRemarks;

    TextView txtQuestion;
    TextView txtQuestionNo;

    Button prevQuestion;
    Button nextQuestion;
    Button confirmTnx;
    Button confirmGets;
    Button confirmNotGets;
    Button btnEditOK;

    DBHelper db;

    ArrayList<Bitmap> pscDrawings;
    ArrayList<String> pscQuestions;
    ArrayList<Integer> pscAnswers;
    ArrayList<String> pscAnswersStr;
    ArrayList<String> pscRemarks;
    String[] testQuestions;
    ArrayList<Integer> testAnswers;
    ArrayList<String> testRemarks;

    int questionCtr;
    int testQuestionCtr;
    int optionCtr;
    int selected;
    boolean isSelected;
    boolean isSelectedTest;

    String studentName;
    String studentLastName;
    String schoolName;

    int studentID;
    int schoolID;

    boolean isBackPressed;
    boolean isChangedAnswer;
    boolean isChangedRemark;

    int prevAnswer;
    String prevRemark;

    int newAnswer;
    String newRemark;

    boolean isTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psc);

        Intent intent = getIntent();
        this.studentID = intent.getIntExtra("StudentID", -1);
        this.schoolID = intent.getIntExtra("SchoolID", -1);

        this.initComponents();
        Toast.makeText(this, "Student ID: "+this.studentID+" | School ID: "+this.schoolID, Toast.LENGTH_SHORT).show();
    }

    //This method initializes the properties.
    public void initComponents() {
        this.isTutorial = true;

        this.selected = 0;
        this.isSelected = false;
        this.isSelectedTest = false;
        this.isBackPressed = false;
        this.isChangedAnswer = false;
        this.isChangedRemark = false;

        this.prevAnswer = 0;
        this.prevRemark = "";
        this.newAnswer = 0;
        this.newRemark = "";

        this.psc_layout = (RelativeLayout) findViewById(R.id.psc_layout);
        this.listSummary = (ListView) findViewById(R.id.listSummary);

        this.btnHindi = (ImageView) findViewById(R.id.btnHindi);
        this.btnMinsan = (ImageView) findViewById(R.id.btnMinsan);
        this.btnMadalas = (ImageView) findViewById(R.id.btnMadalas);
        this.btnSkip = (Button) findViewById(R.id.btnSkip);
        this.btnConfirmAns = (Button) findViewById(R.id.btnConfirmAns);
        this.btnEditOK = (Button) findViewById(R.id.btnEditOK);

        this.txtHindi = (TextView) findViewById(R.id.txtHindi);
        this.txtMinsan = (TextView) findViewById(R.id.txtMinsan);
        this.txtMadalas = (TextView) findViewById(R.id.txtMadalas);

        this.txtRemarks = (EditText) findViewById(R.id.txtRemarks);

        this.txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        this.txtQuestionNo = (TextView) findViewById(R.id.txtQuestionNo);

        this.prevQuestion = (Button) findViewById(R.id.btnPrevQ);
        this.nextQuestion = (Button) findViewById(R.id.btnNext);
        this.confirmTnx = (Button) findViewById(R.id.btnTnxOK);
        this.confirmGets = (Button) findViewById(R.id.btnTnxGets);
        this.confirmNotGets = (Button) findViewById(R.id.btnTnxNotGets);

        this.db = new DBHelper(this);
        this.getQuestionsAndDrawings();
        this.studentName = this.db.getStudentName(studentID);
        this.schoolName = this.db.getSchoolName(this.schoolID);
        this.studentLastName = this.db.getStudentLastName(studentID);

        this.pscDrawings = new ArrayList<Bitmap>();
        this.pscQuestions = new ArrayList<String>();
        this.pscAnswers = new ArrayList<Integer>();
        this.pscAnswersStr = new ArrayList<String>();
        this.pscRemarks = new ArrayList<String>();
        this.testQuestions = new String[5];
        this.testAnswers = new ArrayList<Integer>();
        this.testRemarks = new ArrayList<String>();

        this.questionCtr = 0;
        this.testQuestionCtr = 0;
        this.optionCtr = 3;

        this.pscQuestions = DigitalFormManager.getInstance().getPscQuestions();
        this.pscDrawings = DigitalFormManager.getInstance().getPscDrawings();

        this.testQuestions[0] = "Mapag-isa ang aking anak.";
        this.testQuestions[1] = "Mahirap kausapin.";
        this.testQuestions[2] = "Maraming kaibigan ang aking anak.";
        this.testQuestions[3] = "Hindi bumabagsak sa eskwelahan.";

        for(int i = 0; i < this.pscQuestions.size(); i++) {
            this.pscAnswers.add(-2);
            this.pscAnswersStr.add("");
            this.pscRemarks.add("");
        }

        for(int i = 0; i <this.testQuestions.length; i++) {
            this.testAnswers.add(-2);
            this.testRemarks.add("");
        }

        this.txtRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!isTutorial)
                    pscRemarks.set(questionCtr, editable.toString());
                else
                    testRemarks.set(testQuestionCtr, editable.toString());
            }
        });

        this.btnHindi.setImageBitmap(pscDrawings.get(0));
        this.btnMinsan.setImageBitmap(pscDrawings.get(1));
        this.btnMinsan.setImageBitmap(pscDrawings.get(2));
    }

    public void highlightAnswer(View view) {
        this.isSelected = true;
        this.isSelectedTest = true;

        if(this.btnSkip.isPressed()) {
            this.txtHindi.setBackgroundColor(Color.WHITE);
            this.txtMinsan.setBackgroundColor(Color.WHITE);
            this.txtMadalas.setBackgroundColor(Color.WHITE);
            this.btnSkip.setBackgroundColor(Color.rgb(23, 158, 154));
            this.btnSkip.setTextColor(Color.WHITE);

            if(!this.isTutorial) {
                this.pscAnswers.set(this.questionCtr, -1);
                this.pscAnswersStr.set(this.questionCtr, "Hindi ko masasagot");
            }
            else {
                this.testAnswers.set(this.testQuestionCtr, -1);
            }
        }

        if(this.btnHindi.isPressed() || this.txtHindi.isPressed()) {
            this.txtHindi.setBackgroundColor(Color.rgb(23, 158, 154));
            this.txtMinsan.setBackgroundColor(Color.WHITE);
            this.txtMadalas.setBackgroundColor(Color.WHITE);
            this.btnSkip.setBackgroundColor(Color.rgb(204, 204, 204));
            this.btnSkip.setTextColor(Color.rgb(23, 158, 154));

            if(!this.isTutorial) {
                this.pscAnswers.set(this.questionCtr, 0);
                this.pscAnswersStr.set(this.questionCtr, "Hindi nangyayari");
            }
            else {
                this.testAnswers.set(this.testQuestionCtr, 0);
            }
        }

        if(this.btnMinsan.isPressed() || this.txtMinsan.isPressed()) {
            this.txtHindi.setBackgroundColor(Color.WHITE);
            this.txtMinsan.setBackgroundColor(Color.rgb(23, 158, 154));
            this.txtMadalas.setBackgroundColor(Color.WHITE);
            this.btnSkip.setBackgroundColor(Color.rgb(204, 204, 204));
            this.btnSkip.setTextColor(Color.rgb(23, 158, 154));

            if(!this.isTutorial) {
                this.pscAnswers.set(this.questionCtr, 1);
                this.pscAnswersStr.set(this.questionCtr, "Minsan nangyayari");
            }
            else {
                this.testAnswers.set(this.testQuestionCtr, 1);
            }
        }

        if(this.btnMadalas.isPressed() || this.txtMadalas.isPressed()) {
            this.txtHindi.setBackgroundColor(Color.WHITE);
            this.txtMinsan.setBackgroundColor(Color.WHITE);
            this.txtMadalas.setBackgroundColor(Color.rgb(23, 158, 154));
            this.btnSkip.setBackgroundColor(Color.rgb(204, 204, 204));
            this.btnSkip.setTextColor(Color.rgb(23, 158, 154));

            if(!this.isTutorial) {
                this.pscAnswers.set(this.questionCtr, 2);
                this.pscAnswersStr.set(this.questionCtr, "Madalas nangyayari");
            }
            else {
                this.testAnswers.set(this.testQuestionCtr, 2);
            }
        }
    }

    public void getQuestionsAndDrawings() {
        Cursor questions = this.db.getAllQuestions();
        Cursor drawings = this.db.getAllPSCOptionDrawings();

        if(questions.getCount() == 0 || drawings.getCount() == 0)
            Toast.makeText(this, "Could not retrieve questions or drawings.", Toast.LENGTH_SHORT).show();
        else {
            while(questions.moveToNext())
                DigitalFormManager.getInstance().addQuestion(questions.getString(questions.getColumnIndex("col_question_tag")));

            while(drawings.moveToNext()) {
                byte[] imgarray = drawings.getBlob(drawings.getColumnIndex("col_pscoptions_img"));
                DigitalFormManager.getInstance().addDrawing(BitmapFactory.decodeByteArray(imgarray, 0, imgarray.length, null));
            }
        }
    }

    public void prevQuestion(View view) {
        if(!this.isTutorial) {
            this.isSelected = true;

            this.questionCtr--;
            this.optionCtr -= 3;

            if(this.questionCtr == 0)
                this.prevQuestion.setVisibility(View.INVISIBLE);

            this.btnHindi.setImageBitmap(this.pscDrawings.get(this.optionCtr));
            this.btnMinsan.setImageBitmap(this.pscDrawings.get(this.optionCtr+1));
            this.btnMadalas.setImageBitmap(this.pscDrawings.get(this.optionCtr+2));
            this.txtQuestion.setText(this.pscQuestions.get(this.questionCtr));

            this.showSelAnswers(this.testQuestionCtr);

            this.txtQuestionNo.setText((this.questionCtr+1)+".");
        }
        else {
            this.isSelectedTest = true;
            this.testQuestionCtr--;

            if(this.testQuestionCtr == 0) {
                this.prevQuestion.setVisibility(View.INVISIBLE);
            }

            this.showSelAnswers(this.testQuestionCtr);
        }
    }

    //This method displays the next question.
    public void nextQuestion(View view) {
        if(!this.isTutorial) {
            this.isTutorial = false;
            if(this.isSelected) {
                if(this.questionCtr+1 < pscQuestions.size()) {
                    if(this.optionCtr < pscDrawings.size()) {
                        this.prevQuestion.setVisibility(View.VISIBLE);
                        this.questionCtr++;

                        this.txtQuestionNo.setText((this.questionCtr+1)+".");
                        this.changeOptions(optionCtr);
                        this.txtQuestion.setText(this.pscQuestions.get(this.questionCtr));
                        this.optionCtr += 3;

                        if(this.pscAnswers.get(this.questionCtr) > -2) {
                            this.showSelAnswers(this.questionCtr);
                        }
                        else {
                            this.isSelected = false;
                            this.txtRemarks.setText("");
                            this.txtRemarks.setHint("Maglagay ng paliwanag");
                            this.clearAnswers();
                        }
                    }
                }
                else {
                    this.showSummary();
//                    String answers = "";
//                    int sum = 0;
//
//                    for(int i = 0; i < pscAnswers.size(); i++) {
//                        if(pscAnswers.get(i) > -2)
//                            sum += pscAnswers.get(i);
//
//                        switch(pscAnswers.get(i)) {
//                            case 2: answers += "Madalas";
//                                break;
//                            case 1: answers += "Minsan";
//                                break;
//                            case 0: answers += "Hindi";
//                                break;
//                            case -1: answers += "Hindi masagot";
//                                break;
//                            default: break;
//                        }
//
//                        if(i < pscAnswers.size()-1)
//                            answers += ",";
//                    }
//
//                    boolean result = this.db.insertAssessment(answers, sum, this.studentID);
//                    if(result) {
//                        this.selected = 0;
//                        this.optionCtr = 3;
//                        this.questionCtr = 1;


//                    }
//                    else
//                        Toast.makeText(this, "Could not insert PSC Answers", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            if(this.isSelectedTest) {
                this.testQuestionCtr++;
                if(this.testQuestionCtr < 4) {
                    this.txtQuestion.setText(this.testQuestions[this.testQuestionCtr]);
                    this.prevQuestion.setVisibility(View.VISIBLE);
                    this.txtRemarks.setText("");
                    this.txtRemarks.setHint("Maglagay ng paliwanag");

                    if(this.testAnswers.get(this.testQuestionCtr) > -2) {
                        this.showSelAnswers(this.testQuestionCtr);
                    }
                    else {
                        this.isSelectedTest = false;
                        this.txtRemarks.setText("");
                        this.txtRemarks.setHint("Maglagay ng paliwanag");
                        this.clearAnswers();
                    }

                }
                else {
                    this.hideQuestions();
                    this.txtQuestion.setText("Naintindihan po ba?");
                    this.confirmGets.setVisibility(View.VISIBLE);
                    this.confirmNotGets.setVisibility(View.VISIBLE);
                }

                this.txtRemarks.setHint("Magalagay ng paliwanag");
            }
        }
    }

    public void changeOptions(int option) {
        this.btnHindi.setImageBitmap(this.pscDrawings.get(option));
        this.btnMinsan.setImageBitmap(this.pscDrawings.get(option+1));
        this.btnMadalas.setImageBitmap(this.pscDrawings.get(option+2));
    }

    public void showSelAnswers(int questionNo) {
        int answer = this.testAnswers.get(questionNo);

        switch(answer) {
            case 0: this.txtHindi.setBackgroundColor(Color.rgb(23, 158, 154));
                this.txtMinsan.setBackgroundColor(Color.WHITE);
                this.txtMadalas.setBackgroundColor(Color.WHITE);
                this.btnSkip.setBackgroundColor(Color.rgb(204, 204, 204));
                this.btnSkip.setTextColor(Color.rgb(23, 158, 154));
                break;
            case 1: this.txtHindi.setBackgroundColor(Color.WHITE);
                this.txtMinsan.setBackgroundColor(Color.rgb(23, 158, 154));
                this.txtMadalas.setBackgroundColor(Color.WHITE);
                this.btnSkip.setBackgroundColor(Color.rgb(204, 204, 204));
                this.btnSkip.setTextColor(Color.rgb(23, 158, 154));
                break;
            case 2: this.txtHindi.setBackgroundColor(Color.WHITE);
                this.txtMinsan.setBackgroundColor(Color.WHITE);
                this.txtMadalas.setBackgroundColor(Color.rgb(23, 158, 154));
                this.btnSkip.setBackgroundColor(Color.rgb(204, 204, 204));
                this.btnSkip.setTextColor(Color.rgb(23, 158, 154));
                break;
            case -1: this.txtHindi.setBackgroundColor(Color.WHITE);
                this.txtMinsan.setBackgroundColor(Color.WHITE);
                this.txtMadalas.setBackgroundColor(Color.WHITE);
                this.btnSkip.setBackgroundColor(Color.rgb(23, 158, 154));
                this.btnSkip.setTextColor(Color.WHITE);
                break;
            default: break;
        }
        this.txtRemarks.setText(this.testRemarks.get(questionNo));
    }

    public void showQuestions() {
        this.btnHindi.setVisibility(View.VISIBLE);
        this.btnMinsan.setVisibility(View.VISIBLE);
        this.btnMadalas.setVisibility(View.VISIBLE);
        this.btnSkip.setVisibility(View.VISIBLE);

        this.txtHindi.setVisibility(View.VISIBLE);
        this.txtMinsan.setVisibility(View.VISIBLE);
        this.txtMadalas.setVisibility(View.VISIBLE);
        this.txtRemarks.setVisibility(View.VISIBLE);
    }

    public void hideQuestions() {
        this.btnHindi.setVisibility(View.INVISIBLE);
        this.btnMinsan.setVisibility(View.INVISIBLE);
        this.btnMadalas.setVisibility(View.INVISIBLE);
        this.btnSkip.setVisibility(View.INVISIBLE);

        this.txtHindi.setVisibility(View.INVISIBLE);
        this.txtMinsan.setVisibility(View.INVISIBLE);
        this.txtMadalas.setVisibility(View.INVISIBLE);
        this.txtRemarks.setVisibility(View.INVISIBLE);

        this.prevQuestion.setVisibility(View.INVISIBLE);
        this.nextQuestion.setVisibility(View.INVISIBLE);
    }

    public void showSummary() {
        this.txtQuestionNo.setText("");
        this.btnHindi.setVisibility(View.INVISIBLE);
        this.btnMinsan.setVisibility(View.INVISIBLE);
        this.btnMadalas.setVisibility(View.INVISIBLE);
        this.btnSkip.setVisibility(View.INVISIBLE);

        this.txtHindi.setVisibility(View.INVISIBLE);
        this.txtMinsan.setVisibility(View.INVISIBLE);
        this.txtMadalas.setVisibility(View.INVISIBLE);
        this.txtRemarks.setVisibility(View.INVISIBLE);

        this.prevQuestion.setVisibility(View.INVISIBLE);
        this.nextQuestion.setVisibility(View.INVISIBLE);

        this.btnConfirmAns.setVisibility(View.VISIBLE);

        this.listAnswers = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, this.pscAnswersStr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(pscQuestions.get(position));
                text2.setText(pscAnswersStr.get(position));
                return view;
            }
        };

        this.listSummary.setAdapter(this.listAnswers);
        this.listSummary.setOnItemClickListener(this);
        this.listSummary.setVisibility(View.VISIBLE);

        this.txtQuestion.setText("Tignan muli ang iyong mga sagot. Kung may nais kang baguhin,"+
                "pindutin lamang ang tanong na iyon.");
    }

    public void editOK(View view) {
        this.btnEditOK.setVisibility(View.INVISIBLE);
        this.showSummary();
    }

    public void okGets(View view) {
        this.confirmGets.setVisibility(View.INVISIBLE);
        this.confirmNotGets.setVisibility(View.INVISIBLE);

        if(this.confirmGets.isPressed()) {
            this.isTutorial = false;
            this.txtQuestion.setText(this.pscQuestions.get(this.questionCtr));
            this.txtQuestionNo.setText((this.questionCtr+1)+".");

            this.prevQuestion.setVisibility(View.INVISIBLE);
            this.confirmGets.setVisibility(View.INVISIBLE);
            this.confirmNotGets.setVisibility(View.INVISIBLE);
        }

        if(this.confirmNotGets.isPressed()) {
            this.testQuestionCtr = 0;
            this.txtQuestion.setText(this.testQuestions[this.testQuestionCtr]);

            for(int i = 0; i < this.testQuestions.length; i++) {
                testAnswers.set(i, -2);
                testRemarks.set(i, "");
            }
        }

        this.btnHindi.setVisibility(View.VISIBLE);
        this.btnMinsan.setVisibility(View.VISIBLE);
        this.btnMadalas.setVisibility(View.VISIBLE);
        this.btnSkip.setVisibility(View.VISIBLE);

        this.txtHindi.setVisibility(View.VISIBLE);
        this.txtMinsan.setVisibility(View.VISIBLE);
        this.txtMadalas.setVisibility(View.VISIBLE);
        this.txtRemarks.setVisibility(View.VISIBLE);

        this.clearAnswers();

        this.prevQuestion.setVisibility(View.INVISIBLE);
        this.nextQuestion.setVisibility(View.VISIBLE);
    }

    public void okTnx(View view) {
        this.createFile();

        Intent intent = new Intent(this, FinalMenu.class);
        intent.putExtra("StudentID", this.studentID);
        intent.putExtra("SchoolID", this.schoolID);
        intent.putExtra("PSCAnswers", this.pscAnswers);
        intent.putExtra("Mode", "DAP");

        startActivity(intent);
    }

    public void createFile() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(816, 1056, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Typeface linux = Typeface.createFromAsset(getAssets(), "fonts/LinLibertine_R.ttf");
        Typeface linuxBold =Typeface.createFromAsset(getAssets(), "fonts/LinLibertine_RB.ttf");

        Paint heading = new Paint();
        heading.setTypeface(linuxBold);
        heading.setTextSize(25.0f);
        heading.setTextAlign(Paint.Align.CENTER);
        heading.setColor(Color.rgb(23,158,154));
        canvas.drawText("PEDIATRIC SYMPTOM CHECKLIST", 408, 96, heading);
        heading.setColor(Color.BLACK);
        canvas.drawText("DIGITAL FORM RESULTS", 408, 126, heading);
        heading.setTextSize(18.0f);
        heading.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Student Name: "+this.studentName, 48, 176, heading);

        Paint legend = new Paint();
        legend.setStyle(Paint.Style.FILL);

        legend.setColor(Color.rgb(95,159,159));
        canvas.drawRect(48, 200, 78, 215, legend);
        canvas.drawText("Madalas nangyayari", 85, 216, heading);

        legend.setColor(Color.rgb(150,205,205));
        canvas.drawRect(285, 200, 315, 215, legend);
        canvas.drawText("Minsan nangyayari", 322, 216, heading);

        legend.setColor(Color.rgb(209,238,238));
        canvas.drawRect(522, 200, 552, 215, legend);
        canvas.drawText("Hindi nangyayari", 559, 216, heading);

        Paint questions = new Paint();
        questions.setTypeface(linux);
        questions.setTextSize(18.0f);
        questions.setTextAlign(Paint.Align.LEFT);
        questions.setColor(Color.BLACK);

        Paint results = new Paint();
        results.setTypeface(linux);
        results.setTextSize(18.0f);
        results.setTextAlign(Paint.Align.LEFT);
        results.setColor(Color.BLACK);

        int initY = 236;
        for(int i = 0; i < this.pscAnswersStr.size(); i++) {
            switch(this.pscAnswersStr.get(i)) {
                case "Madalas nangyayari": legend.setColor(Color.rgb(95,159,159));
                                           canvas.drawRect(48, initY, 78, initY+32, legend);
                                           break;
                case "Minsan nangyayari": legend.setColor(Color.rgb(150,205,205));
                                          canvas.drawRect(48, initY, 78, initY+32, legend);
                                          break;
                case "Hindi nangyayari": legend.setColor(Color.rgb(209,238,238));
                                         canvas.drawRect(48, initY, 78, initY+32, legend);
                                         break;
                case "Hindi ko masasagot": legend.setColor(Color.WHITE);
                                            canvas.drawRect(48, initY, 78, initY+32, legend);
                                            break;
            }

            canvas.drawText((i+1)+". "+this.pscQuestions.get(i), 85, initY+15, questions);
            canvas.drawText(this.pscAnswersStr.get(i), 85, initY+32, results);

            initY += 47;

            if(initY >= 1056-48) {
                document.finishPage(page);
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();

                initY = 96;
            }
        }

        document.finishPage(page);

        String timeStampFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Results/"+schoolName+"/"+studentLastName+"/"+timeStampFolder);
            if(!root.exists()) {
                root.mkdirs();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File resultsFile = new File(root, this.studentName+"_DIG_"+timeStamp+".pdf");
            document.writeTo(new FileOutputStream(resultsFile));

//            FileWriter writer = new FileWriter(resultsFile);
//
//            writer.append(this.studentName+"\n\n");
//
//            for(int i = 0; i < pscAnswersStr.size(); i++) {
//                writer.append(this.pscQuestions.get(i)+"\n");
//                writer.append(this.pscAnswersStr.get(i)+"\n\n");
//            }
//
//            writer.flush();
//            writer.close();
        }
        catch(IOException e) {
            Toast.makeText(this, "OOPS", Toast.LENGTH_LONG).show();
           e.printStackTrace();
        }

        document.close();
    }

    public void clearAnswers() {
        this.txtHindi.setBackgroundColor(Color.WHITE);
        this.txtMinsan.setBackgroundColor(Color.WHITE);
        this.txtMadalas.setBackgroundColor(Color.WHITE);
        this.btnSkip.setBackgroundColor(Color.rgb(204, 204, 204));
        this.btnSkip.setTextColor(Color.rgb(23, 158, 154));

        this.selected = 0;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        this.listSummary.setVisibility(View.INVISIBLE);
        this.btnConfirmAns.setVisibility(View.INVISIBLE);

        this.questionCtr = position;

        this.txtQuestionNo.setText((position+1)+".");
        this.txtQuestion.setText(this.pscQuestions.get(position));

        this.btnEditOK.setVisibility(View.VISIBLE);
        Log.e("PSCQuestions","Showing button");

        this.changeOptions(position*3);
        Log.e("PSCQuestions","Showing options "+position);

        this.showQuestions();
        Log.e("PSCQuestions","Showing questions");

        Log.e("PSCQuestions","Showing sel answers");
        this.showSelAnswers(position);
    }
}
