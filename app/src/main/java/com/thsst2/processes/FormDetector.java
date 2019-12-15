package com.thsst2.processes;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Type: Process
 * FormDetector separates the detected paper from the background
 * and uses four-point transform to straighten its perspective.
 */
public class FormDetector {

//    private Mat src;
    private Mat srcOrig;
    private Mat srcGray;
//    private Mat output_mat;
    private Bitmap output_bmp;

//    private int scaleFactor;

    private Point ref_tl;
    private Point ref_tr;
    private Point ref_br;
    private Point ref_bl;

    public Mat extract(Bitmap source) {
        this.convertToMat(source);
        this.transform();
        this.drawRectangle();

//        this.output_bmp = Bitmap.createBitmap(this.output_mat.cols(), this.output_mat.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(this.output_mat, this.output_bmp);
//        return this.output_bmp;
        return this.srcOrig;
    }

    private void convertToMat(Bitmap source) {
        Bitmap copy = source.copy(Bitmap.Config.ARGB_8888, true);
        this.srcOrig = new Mat(source.getHeight(), source.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(copy, this.srcOrig);

        Log.e("FormDetector", "Scaled");

        this.ref_tl = new Point(0, 0);
        this.ref_tr = new Point(this.srcOrig.cols(), 0);
        this.ref_br = new Point(this.srcOrig.cols(), this.srcOrig.rows());
        this.ref_bl = new Point(0, this.srcOrig.rows());

        this.srcGray = this.srcOrig.clone();
//        this.output_mat = this.srcOrig.clone();
    }

    private void transform() {
        Imgproc.cvtColor(this.srcOrig, srcGray, Imgproc.COLOR_BGR2GRAY);
//        Photo.fastNlMeansDenoising(srcGray, srcGray);
        Imgproc.adaptiveThreshold(srcGray, srcGray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 21, 2);

        Log.e("FormDetector", "Preprocessed");
    }

    private void drawRectangle() {

        //Find all contours
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(this.srcGray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Log.e("FormDetector", "Contours " + contours.size());

        ArrayList<MatOfPoint> filtered = new ArrayList<MatOfPoint>();
        for(int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));

            if(area > 140)
                filtered.add(contours.get(i));
        }
        Log.e("FormDetector", "Filtered");

        //Get candidate contours
        List<Rect> detected = new ArrayList<Rect>();
        detected = this.detectRectangles(filtered);
        Log.e("FormDetector", "Detected " + detected.size());

        //Identify four corner points
        List<Point> rectangle = this.getFourCornerPts(detected);
        Log.e("FormDetector", "Rectangle "+rectangle.size());

        //Apply four-point transform
        this.srcOrig = this.getFourPointTransform(rectangle, this.srcOrig);
//        this.srcGray.convertTo(this.srcGray, -1, 1.5, 1);
        Log.e("FormDetector", "Transformed");
    }

    private List<Rect> detectRectangles(List<MatOfPoint> c) {
        List<Rect> rectGroup = new ArrayList<Rect>();
        MatOfPoint approx = new MatOfPoint();
        MatOfPoint2f approx2f = new MatOfPoint2f();

        for(MatOfPoint contour : c) {
            MatOfPoint2f contour2f = new MatOfPoint2f();
            contour.convertTo(contour2f, CvType.CV_32FC2);

            double perimeter = Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approx2f, perimeter*0.04, true);
            approx2f.convertTo(approx2f, CvType.CV_32S);

            if(approx.size().height >= 4 || approx.size().height <= 7) {
                Rect rect = Imgproc.boundingRect(contour);
                rectGroup.add(rect);
            }
        }

        return rectGroup;
    }

    private List<Point> getFourCornerPts(List<Rect> rectGroup) {
        List<Point> corners = new ArrayList<Point>();

        double curr_distance_tl = Integer.MAX_VALUE;
        double curr_distance_tr = Integer.MAX_VALUE;
        double curr_distance_br = Integer.MAX_VALUE;
        double curr_distance_bl = Integer.MAX_VALUE;

        int curr_index_tl = 0;
        int curr_index_tr = 0;
        int curr_index_br = 0;
        int curr_index_bl = 0;

        int new_index = 0;

        for(Rect r : rectGroup) {
            double new_distance_tl = this.computeDistance(this.ref_tl, r.tl());
            double new_distance_tr = this.computeDistance(this.ref_tr, new Point(r.x+r.width, r.y));
            double new_distance_br = this.computeDistance(this.ref_br, r.br());
            double new_distance_bl = this.computeDistance(this.ref_bl, new Point(r.x, r.y+r.height));

            if(new_distance_tl < curr_distance_tl) {
                curr_distance_tl = new_distance_tl;
                curr_index_tl = new_index;
            }

            if(new_distance_tr < curr_distance_tr) {
                curr_distance_tr = new_distance_tr;
                curr_index_tr = new_index;
            }

            if(new_distance_br < curr_distance_br) {
                curr_distance_br = new_distance_br;
                curr_index_br = new_index;
            }

            if(new_distance_bl < curr_distance_bl) {
                curr_distance_bl = new_distance_bl;
                curr_index_bl = new_index;
            }

            new_index++;
        }

        Point top_left = new Point(rectGroup.get(curr_index_tl).x, (rectGroup.get(curr_index_tl).y+rectGroup.get(curr_index_tl).height));
        Point top_right = new Point((rectGroup.get(curr_index_tr).x+rectGroup.get(curr_index_tr).width), (rectGroup.get(curr_index_tr).y+rectGroup.get(curr_index_tr).height));
        Point bottom_right = new Point((rectGroup.get(curr_index_br).x+rectGroup.get(curr_index_br).width), rectGroup.get(curr_index_br).y);
        Point bottom_left = new Point(rectGroup.get(curr_index_bl).x, rectGroup.get(curr_index_bl).y);

//		Imgproc.drawMarker(this.output_mat, top_left, new Scalar(0, 0, 255), Imgproc.MARKER_CROSS, 10, 1, Imgproc.LINE_4);
//		Imgproc.drawMarker(this.output_mat, top_right, new Scalar(0, 255, 0), Imgproc.MARKER_CROSS, 10, 1, Imgproc.LINE_4);
//		Imgproc.drawMarker(this.output_mat, bottom_right, new Scalar(255, 0, 0), Imgproc.MARKER_CROSS, 10, 1, Imgproc.LINE_4);
//		Imgproc.drawMarker(this.output_mat, bottom_left, new Scalar(0, 255, 255), Imgproc.MARKER_CROSS, 10, 1, Imgproc.LINE_4);

        corners.add(top_left);
        corners.add(top_right);
        corners.add(bottom_right);
        corners.add(bottom_left);

        return corners;
    }

    private Mat getFourPointTransform(List<Point> points, Mat srcImage) {
        Mat src = new MatOfPoint2f(points.get(0), points.get(1), points.get(2), points.get(3));

        double aWidth = computeDistance(points.get(0), points.get(1));
        double bWidth = computeDistance(points.get(2), points.get(3));
        double maxWidth = (aWidth > bWidth) ? aWidth : bWidth;

        double aHeight = computeDistance(points.get(0), points.get(3));
        double bHeight = computeDistance(points.get(1), points.get(2));
        double maxHeight = (aHeight > bHeight) ? aHeight : bHeight;

        Mat dst = new MatOfPoint2f(new Point(0, 0), new Point(maxWidth-1, 0), new Point(maxWidth-1, maxHeight-1), new Point(0, maxHeight-1));
        Mat destImage = new Mat((int)maxHeight, (int)maxWidth, srcImage.type());
        Mat transform = Imgproc.getPerspectiveTransform(src, dst);
        Imgproc.warpPerspective(srcImage, destImage, transform, destImage.size());

        return destImage;
    }

    private double computeDistance(Point p1, Point p2) {
        double x = Math.pow(p1.x-p2.x, 2);
        double y = Math.pow(p1.y - p2.y, 2);

        return Math.sqrt(x+y);
    }

    private static int calcScaleFactor(int rows, int cols) {
        int idealRow, idealCol;

        if(rows < cols) {
            idealRow = 480;
            idealCol = 640;
        }
        else {
            idealCol = 480;
            idealRow = 640;
        }

        int val = Math.min(rows / idealRow, cols / idealCol);

        if(val <= 0)
            return 1;
        else
            return val;
    }

}
