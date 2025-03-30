//package com.example.market_researcher_and_car_maintenance_copilots.ui.dashboard;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//
//import java.io.InputStream;
//
//public class PDFUtils {
//    public static String extractTextFromPDF(InputStream inputStream) {
//        try {
//            PDDocument document = PDDocument.load(inputStream);
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(document);
//            document.close();
//            return text;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
//}
//

package com.example.market_researcher_and_car_maintenance_copilots.ui.dashboard;

import android.content.Context;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;

public class PDFUtils {
    public static String extractTextFromPDF(Context context, InputStream inputStream) {
        try {
            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
