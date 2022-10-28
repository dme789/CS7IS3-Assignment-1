package ie.tcd.meudecd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.*;

public class Main
{

    private static String CRAN_QREL = "cran-data/cranqrel";
    private static String CRAN_QREL_FORMATTED = "cran-data/cranqrel-formatted.txt";

    public static void main(String[] args) throws Exception
    {
        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();

        // create index
        int scoringType = CreateIndex.createIndex(analyzer);

        // query index
        QueryIndex.search(scoringType, analyzer);
        formatQRel();
        System.out.println("PROGRAM COMPLETED");

    }

    public static void formatQRel()
    {
        try {
            BufferedReader qrelReader = new BufferedReader(new FileReader(CRAN_QREL));
            BufferedWriter qrelWriter = new BufferedWriter(new FileWriter(CRAN_QREL_FORMATTED));
            String currLine = qrelReader.readLine();
            while(currLine != null) {
                currLine.trim();
                String[] rel = currLine.split(" ");
                qrelWriter.write(rel[0] + " 0 " + rel[1] + " " + rel[2]);
                qrelWriter.newLine();
                currLine = qrelReader.readLine();
            }
            qrelReader.close();
            qrelWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
