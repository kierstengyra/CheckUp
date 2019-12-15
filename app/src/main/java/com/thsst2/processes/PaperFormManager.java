package com.thsst2.processes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Type: Process
 * PaperFormManager handles the paper form module.
 */
public class PaperFormManager {

    private static PaperFormManager instance = null;
    private ArrayList<FieldManager> allPages;
    private int finalScore;
    private ArrayList<String> finalAnswers;
    private ArrayList<Question> questionList;
    private ArrayList<Bitmap> photos;

    protected PaperFormManager() {
        this.photos = new ArrayList<Bitmap>();
        this.allPages = new ArrayList<FieldManager>();
        this.questionList = new ArrayList<Question>();
    }

    public static PaperFormManager getInstance() {
        if(instance == null)
            instance = new PaperFormManager();

        return instance;
    }

    public void summarize(Context ctx, String studentName, String studentLastName, String schoolName) {
//        this.summarize1();

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(816, 1056, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Typeface linux = Typeface.createFromAsset(ctx.getAssets(), "fonts/LinLibertine_R.ttf");
        Typeface linuxBold =Typeface.createFromAsset(ctx.getAssets(), "fonts/LinLibertine_RB.ttf");

        Paint heading = new Paint();
        heading.setTypeface(linuxBold);
        heading.setTextSize(25.0f);
        heading.setTextAlign(Paint.Align.CENTER);
        heading.setColor(Color.rgb(23,158,154));
        canvas.drawText("PEDIATRIC SYMPTOM CHECKLIST", 408, 96, heading);
        heading.setColor(Color.BLACK);
        canvas.drawText("PHYSICAL FORM RESULTS", 408, 126, heading);
        heading.setTextSize(18.0f);
        heading.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Student Name: "+studentName, 48, 176, heading);

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

        Paint pictures = new Paint();

        int initY = 236;

        for(int m = 0; m < this.allPages.size(); m++)
                this.allPages.get(m).setAnswers();

        for(int i = 0; i < this.questionList.size(); i++) {
            canvas.drawText((i+1)+". "+this.getQuestion(i).getQuestion(), 85, initY+15, questions);

            for(int j = 0; j < this.getQuestion(i).getScoreList().size(); j++) {
                switch(this.getQuestion(i).getScore(j)) {
                    case 2: legend.setColor(Color.rgb(95,159,159));
                        canvas.drawRect(48, initY, 78, initY+32, legend);
                        canvas.drawText("Madalas nangyayari", 85, initY+32, results);
                        break;
                    case 1: legend.setColor(Color.rgb(150,205,205));
                        canvas.drawRect(48, initY, 78, initY+32, legend);
                        canvas.drawText("Minsan nangyayari", 85, initY+32, results);
                        break;
                    case 0: legend.setColor(Color.rgb(209,238,238));
                        canvas.drawRect(48, initY, 78, initY+32, legend);
                        canvas.drawText("Hindi nangyayari", 85, initY+32, results);
                        break;
                    default: break;
                }
                Log.e("PaperFormManager", "Question "+i+" | Answer: "+this.getQuestion(i).getScore(j));
                initY += 15;
            }

            initY += 47;

            if(initY >= 1056-48) {
                document.finishPage(page);
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();

                initY = 96;
            }
        }

        document.finishPage(page);

//        for(int iphoto = 0; iphoto < this.photos.size(); iphoto++) {
//            page = document.startPage(pageInfo);
//
//            float ratio = Math.min(
//                    (float) 720 / this.photos.get(iphoto).getWidth(),
//                    (float) 960 / this.photos.get(iphoto).getHeight());
//            int width = Math.round((float) ratio * this.photos.get(iphoto).getWidth());
//            int height = Math.round((float) ratio * this.photos.get(iphoto).getHeight());
//
//            Bitmap newBitmap = Bitmap.createScaledBitmap(this.photos.get(iphoto), width,
//                    height, false);
//
//            canvas.drawBitmap(newBitmap, 48, 48, pictures);
//            document.finishPage(page);
//        }

        try {
            String timeStampFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());

            File root = new File(Environment.getExternalStorageDirectory(), "Results/"+schoolName+"/"+studentLastName+"/"+timeStampFolder);
            if(!root.exists()) {
                root.mkdirs();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File resultsFile = new File(root, studentLastName+"_PHY_"+timeStamp+".pdf");
            document.writeTo(new FileOutputStream(resultsFile));

            byte[] pdfBytes = readFully(new FileInputStream(resultsFile));
            Log.e("PaperFormManager", "Encrypted");
            byte[] pdfEncryptedBytes = encryptPDF(pdfBytes);
            Log.e("PaperFormManager", "Encrypted string");

            FileOutputStream fos = new FileOutputStream(root.getAbsolutePath()+studentLastName+"_PHY_"+timeStamp+"/Encrypted.txt");
            fos.write(pdfEncryptedBytes);
            fos.close();

            File encryptedFile = new File(root.getAbsolutePath()+studentLastName+"_PHY_"+timeStamp+"/Encrypted.txt");
            byte[] bytes = new byte[(int) encryptedFile.length()];
            FileInputStream fis = new FileInputStream(encryptedFile);
            fis.read(bytes);


//            byte[] decrypted = decryptPDF(bytes);
//            File data = new File(root, "Decrypted.pdf");
//            OutputStream op = new FileOutputStream(data);
//            op.write(decrypted);
//            op.flush();
//            op.close();
//            Log.e("DECRYPT", "Finished decrypting.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        document.close();

        String timeStampFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());
        for(int iphoto = 0; iphoto < this.photos.size(); iphoto++) {
            FileOutputStream out = null;
            try {
                File root = new File(Environment.getExternalStorageDirectory(), "Results/"+schoolName+"/"+studentLastName+"/"+timeStampFolder);
                if(!root.exists()) {
                    root.mkdirs();
                }
                // Create a media file name
                File mediaFile;
                mediaFile = new File(root.getAbsolutePath() + "/" + studentName+"_PHY_"+ timeStampFolder + "PAGE_" + (iphoto + 1) + ".jpg");

                out = new FileOutputStream(mediaFile);
                this.photos.get(iphoto).compress(Bitmap.CompressFormat.PNG, 100, out);

                Log.e("GYRA", "Created successfully");
            }
            catch(Exception e) {
                Log.e("GYRA", "Exception");
                e.printStackTrace();
            }
            finally {
                try {
                    if(out != null)
                        out.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static byte[] encryptPDF(byte[] pdf) throws Exception {
        byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',
                        'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };
        Key key = new SecretKeySpec(keyValue, "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(pdf);

        return encVal;
    }

//    public byte[] decryptPDF(byte[] pdfEncrypt) throws Exception {
//        byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',
//                'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };
//        Key key = new SecretKeySpec(keyValue, "AES");
//        Cipher c = Cipher.getInstance("AES");
//        c.init(Cipher.DECRYPT_MODE, key);
//        byte[] decValue = c.doFinal(pdfEncrypt);
//        Log.e("DECRYPT", "decValue");
//
//        return decValue;
//    }

    public static byte[] readFully(InputStream stream) throws IOException {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while((bytesRead = stream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        return baos.toByteArray();
    }

    public void summarize1() {

        Log.e("PaperFormManager", "SUMMARY");

        for(int i = 0; i < this.allPages.size(); i++) {
            this.allPages.get(i).selectPossibleAnswers();
            Log.e("PaperFormManager", "PAGE "+(i+1));

            int prevNo = 0;
            for(int j = 0; j < this.allPages.get(i).getFieldList().size(); j++) {
                Field f = this.allPages.get(i).getField(j);
                Log.e("PaperFormManager", "Field "+j+" is selected: "+f.isSelected());
                int qNo = f.getQuestion();

                if(f.isSelected()) {
                    if(prevNo != qNo) {
                        Log.e("PaperFormManager", "No. "+qNo+": "+this.questionList.get(qNo-1).getQuestion());
                    }

                    Log.e("PaperFormManager", "Answer: "+f.getAnswer());
                    prevNo = qNo;
                }
            }
            Log.e("PaperFormManager", "||||||||||||||||||||||||||");
        }
    }

    public void addPhoto(Bitmap photo) {
        this.photos.add(photo);
    }

    public ArrayList<Bitmap> getPhotos() {
        return this.photos;
    }

    public Bitmap getPhoto(int index) {
        return this.photos.get(index);
    }

    public boolean isComplete() {
        int ctr = 0;

        for(int i = 0; i < this.allPages.size(); i++) {
            if(this.allPages.get(i).containsPicture())
                ctr++;
        }

        if(ctr == 6)
            return true;
        else
            return false;
    }

    public ArrayList<FieldManager> getAllPages() {
        return allPages;
    }

    public FieldManager getPage(int index) {
        return this.allPages.get(index);
    }

    public void setAllPages(ArrayList<FieldManager> allPages) {
        this.allPages = allPages;
    }

    public void addPage(FieldManager fm) {
        this.allPages.add(fm);
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public ArrayList<String> getFinalAnswers() {
        return finalAnswers;
    }

    public void setFinalAnswers(ArrayList<String> finalAnswers) {
        this.finalAnswers = finalAnswers;
    }

    public ArrayList<Question> getQuestionList() {
        return questionList;
    }

    public Question getQuestion(int index) {
        return this.questionList.get(index);
    }

    public void addQuestion(Question question) {
        this.questionList.add(question);
    }

    public void setQuestionList(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }

}
