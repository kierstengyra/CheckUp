package com.thsst2.processes;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.thsst2.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Type: Process
 * DBHelper is a helper class which handles all database operations.
 * */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "wellbeingapp.db";

    public static final String TABLE_SESSION = "tbl_session";
    public static final String COL_SESSION_ID = "col_session_id";
    public static final String COL_SESSION_PASSWORD = "col_session_password";
    public static final String COL_SESSION_SCHEDULE = "col_session_schedule";
    public static final String COL_SESSION_IS_EXPIRED = "col_session_is_expired";

    public static final String TABLE_SCHOOL = "tbl_school";
    public static final String COL_SCHOOL_ID = "col_school_id";
    public static final String COL_SCHOOL_NAME = "col_school_name";
    public static final String COL_SCHOOL_ADDRESS = "col_school_address";

    public static final String TABLE_STUDENT = "tbl_student";
    public static final String COL_STUDENT_ID = "col_student_id";
    public static final String COL_STUDENT_FNAME = "col_student_fname";
    public static final String COL_STUDENT_MNAME = "col_student_mname";
    public static final String COL_STUDENT_LNAME = "col_student_lname";
    public static final String COL_STUDENT_SUFFIX = "col_student_suffix";
    public static final String COL_STUDENT_BIRTHDAY = "col_student_birthday";
    public static final String COL_STUDENT_AGE = "col_student_age";
    public static final String COL_STUDENT_SEX = "col_student_sex";
    public static final String COL_STUDENT_GRADE = "col_student_grade";
    public static final String COL_STUDENT_SECTION = "col_student_sec";

    public static final String TABLE_DAP = "tbl_dap";
    public static final String COL_DAP_ID = "col_dap_id";
    public static final String COL_IMAGE = "col_image";
    public static final String COL_OBJECTS = "col_objects";

    public static final String TABLE_ASSESSMENT = "tbl_assessment";
    public static final String COL_ASSESSMENT_ID = "col_assessment_id";
    public static final String COL_ANSWERS_PSC = "col_answers_psc";
    public static final String COL_SCORE_PSC = "col_score_psc";
    public static final String COL_ASSESSMENT_REMARKS = "col_assessment_remarks";
;
    public static final String TABLE_PSC = "tbl_psc";
    public static final String COL_PSC_ID = "col_psc_id";
    public static final String COL_QUESTION_ENG = "col_question_eng";
    public static final String COL_QUESTION_TAG = "col_question_tag";

    public static final String TABLE_PSC_DRAWING = "tbl_psc_drawing";
    public static final String COL_PSCDRAW_ID = "col_pscdraw_id";
    public static final String COL_PSCDRAW_IMG = "col_pscdraw_img";

    public static final String TABLE_PSC_OPTIONS = "tbl_psc_options";
    public static final String COL_PSCOPTIONS_ID = "col_pscoptions_id";
    public static final String COL_PSCOPTIONS_IMG = "col_pscoptions_img";

    public Context passedContext;
    private AssetManager assetManager;
    private int pscSize;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.passedContext = context;
        this.assetManager = this.passedContext.getAssets();
        this.pscSize = 35;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tbl_school:
        db.execSQL("CREATE TABLE "+TABLE_SCHOOL+
                "("+COL_SCHOOL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COL_SCHOOL_NAME+" VARCHAR NOT NULL,"+
                COL_SCHOOL_ADDRESS+" TEXT);");

        // Insert values for tbl_school:
        db.execSQL("INSERT INTO "+TABLE_SCHOOL+"("+COL_SCHOOL_NAME+", "+COL_SCHOOL_ADDRESS+")"+
                " VALUES ('De La Salle University - Laguna Campus', 'Brgy. Malamig, Binan, Laguna')," +
                "('Inchican Elementary School', 'Brgy. Inchican, Sta. Rosa, Laguna')," +
                "('Mary Help of Christians College', 'Brgy. Canlubang, Calamba City, Laguna')," +
                "('Don Bosco Canlubang', 'Brgy. Canlubang, Calamba City, Laguna')," +
                "('Don Bosco Children''s Center', 'Brgy. Mayapa, Calamba City, Laguna');");

        // Create tbl_session:
        db.execSQL("CREATE TABLE "+TABLE_SESSION+
                "("+COL_SESSION_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COL_SESSION_PASSWORD+" VARCHAR NOT NULL,"+
                COL_SESSION_SCHEDULE+" DATETIME NOT NULL,"+
                COL_SESSION_IS_EXPIRED+" BOOLEAN NOT NULL,"+
                COL_SCHOOL_ID+" INTEGER NOT NULL,"+
                "FOREIGN KEY("+COL_SCHOOL_ID+") REFERENCES "+TABLE_SCHOOL+"("+COL_SCHOOL_ID+"));");

        // Insert values for tbl_session:
        db.execSQL("INSERT INTO " + TABLE_SESSION + "(" + COL_SESSION_PASSWORD + ", " + COL_SESSION_SCHEDULE + "," +
                COL_SESSION_IS_EXPIRED + "," + COL_SCHOOL_ID + ")" +
                " VALUES ('fvM41]0:', '2016-02-29 09:00', 0, 3)," +
                "('8+kA_7%~', '2014-10-17 07:30', 0, 1)," +
                "('0v(SVFyG', '2018-01-11 08:15', 0, 5)," +
                "('wk?UFdIm', '2018-07-27 15:00', 0, 1)");

        // Create tbl_students:
        db.execSQL("CREATE TABLE "+TABLE_STUDENT+
                "("+COL_STUDENT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COL_STUDENT_FNAME+" VARCHAR NOT NULL,"+
                COL_STUDENT_MNAME+" VARCHAR,"+
                COL_STUDENT_LNAME+" VARCHAR NOT NULL,"+
                COL_STUDENT_SUFFIX+" VARCHAR,"+
                COL_STUDENT_BIRTHDAY+" DATE NOT NULL,"+
                COL_STUDENT_AGE+" INTEGER NOT NULL,"+
                COL_STUDENT_SEX+" CHAR NOT NULL,"+
                COL_STUDENT_GRADE+" INTEGER NOT NULL,"+
                COL_STUDENT_SECTION+" VARCHAR NOT NULL,"+
                COL_SCHOOL_ID+" INTEGER NOT NULL,"+
                "FOREIGN KEY("+COL_SCHOOL_ID+") REFERENCES "+TABLE_SCHOOL+"("+COL_SCHOOL_ID+"));");

        //Insert values for tbl_students:
        db.execSQL("INSERT INTO "+TABLE_STUDENT+"("+COL_STUDENT_FNAME+","+COL_STUDENT_MNAME+","+COL_STUDENT_LNAME+","+COL_STUDENT_SUFFIX+","+
                COL_STUDENT_BIRTHDAY+","+COL_STUDENT_AGE+","+COL_STUDENT_SEX+","+COL_SCHOOL_ID+","+COL_STUDENT_GRADE+","+COL_STUDENT_SECTION+")"+
                " VALUES ('Katrina', 'Fuente', 'Lazarte', null, '2012-09-18', 5, 'F', 1, 5, 'Emerald'),"+
                "('Candy', 'Herminado', 'Espulgar', null, '2012-02-02', 6, 'F', 1, 5, 'Amethyst'),"+
                "('Jazmine', 'Manongsong', 'Sace', null, '1998-07-09', 19, 'F', 5, 5, 'Ruby'),"+
                "('Anzel Gavrie', 'Laugico', 'Raymundo', null, '2013-09-30', 4, 'M', 1, 5, 'Sapphire'),"+
                "('Jeruel', 'Garrido', 'Trinidad', 'Jr.', '2013-02-13', 19, 'M', 5, 6, 'Opal'),"+
                "('Luis', 'Quibol', 'Madrigal', null, '2012-05-18', 6, 'M', 1, 6, 'Garnet'),"+
                "('Neil', 'Villagracia', 'Romblon', null, '1997-11-19', 20, 'M', 4, 6, 'Diamond');");

        // Create tbl_dap:
        db.execSQL("CREATE TABLE "+TABLE_DAP+
                "("+COL_DAP_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COL_IMAGE+" BLOB NOT NULL,"+
                COL_OBJECTS+" TEXT NOT NULL,"+
                COL_STUDENT_ID+" INTEGER NOT NULL,"+
                "FOREIGN KEY("+COL_STUDENT_ID+") REFERENCES "+TABLE_STUDENT+"("+COL_STUDENT_ID+"));");

        // Create tbl_assessment:
        db.execSQL("CREATE TABLE "+TABLE_ASSESSMENT+
                "("+COL_ASSESSMENT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COL_ANSWERS_PSC+" TEXT,"+
                COL_SCORE_PSC+" INTEGER,"+
                COL_ASSESSMENT_REMARKS+" TEXT,"+
                COL_STUDENT_ID+" INTEGER NOT NULL,"+
                COL_DAP_ID+" INTEGER,"+
                "FOREIGN KEY("+COL_STUDENT_ID+") REFERENCES "+TABLE_STUDENT+"("+COL_STUDENT_ID+"),"+
                "FOREIGN KEY("+COL_DAP_ID+") REFERENCES "+TABLE_DAP+"("+COL_DAP_ID+"));");

        // Create tbl_psc:
        db.execSQL("CREATE TABLE "+TABLE_PSC+
                "("+COL_PSC_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COL_QUESTION_TAG+" TEXT NOT NULL,"+
                COL_QUESTION_ENG+" TEXT NOT NULL);");

        // Insert values for tbl_psc:
        db.execSQL("INSERT INTO " + TABLE_PSC + "(" + COL_QUESTION_TAG + "," + COL_QUESTION_ENG + ")" +
                " VALUES ('Nagsasabi na may nararamdamang kirot o sakit sa katawan.', 'Complaints about aches and pains')," +
                "('Mas gustong mapag-isa.', 'Spends more time alone')," +
                "('Madaling mapagod, hindi maliksi.', 'Tires easily, has little energy')," +
                "('Di mapakali, di makaupo nang matagal sa isang lugar.', 'Fidgety, unable to sit still')," +
                "('Nagkakaroon ng problema sa titser.', 'Has trouble with teacher')," +
                "('Hindi masyadong interesado sa pag-aaral.', 'Less interested in school')," +
                "('Sobrang likot.', 'Acts as if driven by motor')," +
                "('Madalas nangangarap nang gising.', 'Daydreams too much')," +
                "('Mabilis malipat ang atensyon.','Distracted easily')," +
                "('Takot sa mga bagong sitwasyon.', 'Is afraid of new situations')," +
                "('Malungkutin.','Feels sad, unhappy')," +
                "('Iritable, madalas magalit.','Is irritable, angry')," +
                "('Nakakaramdam ng kawalan ng pag-asa.', 'Feels hopeless')," +
                "('Hirap ituon ang pansin sa iisang bagay lamang.', 'Has trouble concentrating')," +
                "('Kaunti ang interes makipagkaibigan.', 'Less interested in friends')," +
                "('Nakikipag-away sa ibang mga bata.','Fights with other children')," +
                "('Lumiliban o nag-aabsent sa klase', 'Absent from school')," +
                "('Bumababa ang mga marka sa eskwelahan.','School grades dropping')," +
                "('Walang tiwala sa sarili.', 'Is down on him or herself')," +
                "('Kumukonsulta sa doktor na wala namang nakikitang problema ang doktor.','Visits the doctor with doctor finding nothing wrong')," +
                "('Hirap matulog.','Has trouble sleeping')," +
                "('Palaging nag-aalala.', 'Worries a lot')," +
                "('Hirap humiwalay mula sa iyo kumpara sa dati.', 'Wants to be with you more than before')," +
                "('Pakiramdam niya siya ay masama.', 'Feels he or she is bad')," +
                "('Gumagawa ng mga bagay o aksyon na walang pag-iingat.','Takes unnecessary risks')," +
                "('Palaging nasasaktan.', 'Gets hurt frequently')," +
                "('Parang palagi siyang kulang sa kasiyahan.','Seems to be having less fun')," +
                "('Kumikilos na parang mas bata sa kanyang totoong edad.', 'Acts younger than children his or her age')," +
                "('Hindi nakikinig o sumusunod sa mga patakaran.', 'Does not listen to rules')," +
                "('Hindi ipinapakita ang nararamdaman.', 'Does not show feelings')," +
                "('Hindi naiintindihan ang damdamin ng ibang tao.','Does not understand people''s feelings')," +
                "('Nanunukso ng iba.', 'Teases others')," +
                "('Sinisisi sa iba ang mga masasamang nangyayari sa kanya.', 'Blames others for his or her troubles')," +
                "('Kumukuha ng mga bagay na hindi sa kanya.', 'Takes things that do not belong to him or her')," +
                "('Madamot.', 'Refuses to share');");

        //Create tbl_psc_drawings
        db.execSQL("CREATE TABLE " + TABLE_PSC_DRAWING +
                "(" + COL_PSCDRAW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_PSCDRAW_IMG + " BLOB,"
                + COL_PSC_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY(" + COL_PSC_ID + ") REFERENCES " + TABLE_PSC + "(" + COL_PSC_ID + "));");

        //Insert values to tbl_psc_drawings
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q3), 3);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q5), 5);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q8), 8);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q9), 9);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q11), 11);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q12), 12);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q18), 18);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q21), 21);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q26), 26);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q32), 32);
//        this.insertToPSCDrawings(db, convertToByteArray(R.drawable.q34), 34);

        //Create tbl_psc_options
        db.execSQL("CREATE TABLE " + TABLE_PSC_OPTIONS +
                "(" + COL_PSCOPTIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_PSCOPTIONS_IMG + " BLOB,"
                + COL_PSC_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY(" + COL_PSC_ID + ") REFERENCES " + TABLE_PSC + "(" + COL_PSC_ID + "));");

        int questionCounter = 1;
        int optionCounter = 0;

        for(int i = 1; i <= pscSize*3; i++) {
            optionCounter++;

            Log.e("DBHelper", "Q"+questionCounter+"-"+optionCounter+".jpg");

            ContentValues cv = new ContentValues();
            cv.put(COL_PSCOPTIONS_IMG, this.convertToByteArray("Q"+questionCounter+"-"+optionCounter+".jpg"));
            cv.put(COL_PSC_ID, questionCounter);
            db.insert(TABLE_PSC_OPTIONS, null, cv);

            if(i%3 == 0) {
                optionCounter = 0;
                questionCounter++;
            }
        }
    }

    public byte[] convertToByteArray(String filename) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            InputStream is = this.assetManager.open("pscoptions/" + filename);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }

//    public byte[] convertToByteArray(int resID) {
//        Drawable d = ContextCompat.getDrawable(this.passedContext, resID);
//        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        return stream.toByteArray();
//    }
//
//    public void insertToPSCDrawings(SQLiteDatabase database, byte[] image, int pscID) {
//        ContentValues cv = new ContentValues();
//        cv.put(COL_PSCDRAW_IMG, image);
//        cv.put(COL_PSC_ID, pscID);
//        database.insert(TABLE_PSC_DRAWING, null, cv);
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHOOL);
        onCreate(db);
    }

    public Cursor confirmSession(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT "+TABLE_SCHOOL+"."+COL_SCHOOL_ID+", "+COL_SCHOOL_NAME+" FROM "+TABLE_SESSION+", "+TABLE_SCHOOL+
                " WHERE "+COL_SESSION_PASSWORD+" = '"+password+"' AND "+TABLE_SCHOOL+"."+COL_SCHOOL_ID+" = "+TABLE_SESSION+"."+COL_SCHOOL_ID, null);
    }

    public Cursor getAllStudentRecordsFromSchool(int schoolID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_STUDENT+" WHERE "+COL_SCHOOL_ID+" = "+schoolID, null);
    }

    public Cursor getStudentID(String fname, String mname, String lname, String suffix, int age, int schoolID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT "+COL_STUDENT_ID+" FROM "+TABLE_STUDENT+
                " WHERE "+COL_STUDENT_FNAME+" = '"+fname+"' AND "+COL_STUDENT_MNAME+" = '"+mname+"' AND "+COL_STUDENT_LNAME+" = '"+lname+"' AND "+
                COL_STUDENT_SUFFIX+" = '"+suffix+"' AND "+COL_STUDENT_AGE+" = "+age+" AND "+COL_SCHOOL_ID+" = "+schoolID, null);
    }

    public String getStudentName(int studentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COL_STUDENT_FNAME+","+COL_STUDENT_MNAME+","+COL_STUDENT_LNAME+" FROM "+TABLE_STUDENT+
                " WHERE "+COL_STUDENT_ID+" = "+studentID, null);
        String studentName = "";

        while(cursor.moveToNext())
            studentName = cursor.getString(cursor.getColumnIndex(COL_STUDENT_FNAME))+" "+
                    cursor.getString(cursor.getColumnIndex(COL_STUDENT_MNAME))+" "+
                    cursor.getString(cursor.getColumnIndex(COL_STUDENT_LNAME));

        return studentName;
    }

    public String getStudentLastName(int studentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COL_STUDENT_LNAME+" FROM "+TABLE_STUDENT+
                " WHERE "+COL_STUDENT_ID+" = "+studentID, null);
        String studentName = "";

        while(cursor.moveToNext())
            studentName = cursor.getString(cursor.getColumnIndex(COL_STUDENT_LNAME));

        return studentName;
    }

    public Cursor getAllQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_PSC, null);
    }

    public String getSchoolName(int schoolId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COL_SCHOOL_NAME+" FROM "+TABLE_SCHOOL+" WHERE "+COL_SCHOOL_ID+" = "+schoolId, null);
        String schoolName = "";

        while(cursor.moveToNext())
            schoolName = cursor.getString(cursor.getColumnIndex(COL_SCHOOL_NAME));

        return schoolName;
    }

    public boolean insertStudentRecord(String fname, String mname, String lname, String suffix, int age, char sex, int year, int day, int month, int grade, String section, int schoolID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_STUDENT_FNAME, fname);
        contentValues.put(COL_STUDENT_MNAME, mname);
        contentValues.put(COL_STUDENT_LNAME, lname);
        contentValues.put(COL_STUDENT_SUFFIX, suffix);
        contentValues.put(COL_STUDENT_AGE, age);
        contentValues.put(COL_STUDENT_SEX, sex+"");
        contentValues.put(COL_STUDENT_BIRTHDAY, year + "-" + month + "-" + day);
        contentValues.put(COL_SCHOOL_ID, schoolID);
        contentValues.put(COL_STUDENT_GRADE, grade);
        contentValues.put(COL_STUDENT_SECTION, section);

        long result = db.insert(TABLE_STUDENT, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllPSCQuestionsWithDrawings() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT "+COL_PSCDRAW_IMG+", "+COL_QUESTION_TAG+
                " FROM "+TABLE_PSC_DRAWING+", "+TABLE_PSC+
                " WHERE "+TABLE_PSC_DRAWING+"."+COL_PSC_ID+" = "+TABLE_PSC+"."+COL_PSC_ID, null);
    }

    public Cursor getAllPSCOptionDrawings() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_PSC_OPTIONS, null);
    }

    public boolean insertAssessment(String answers, int score, int studentID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_STUDENT_ID, studentID);
        contentValues.put(COL_ANSWERS_PSC, answers);
        contentValues.put(COL_SCORE_PSC, score);

        long result = db.insert(TABLE_ASSESSMENT, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }
}
