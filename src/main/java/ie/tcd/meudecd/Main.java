package ie.tcd.meudecd;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.Scanner;

public class Main
{

    public static void main(String[] args) throws Exception
    {
        // Analyzer that is used to process TextField
        Analyzer analyzer = setAnalyzer();

        // create index, returns int defining scoring type
        int scoringType = CreateIndex.createIndex(analyzer);

        // query index
        QueryIndex.search(scoringType, analyzer);
        System.out.println("PROGRAM COMPLETED");
    }

    // Asks the user for preferred analyzer to use and sets the analyzer.
    public static Analyzer setAnalyzer() {
        Analyzer analyzer = null;
        Scanner analyzerIn = new Scanner(System.in);
        System.out.println("Please select the type of Analyzer to use:\n1. 1 for English Analyzer\n" +
                "2. 2 for Standard Analyzer");
        int analyzerType = analyzerIn.nextInt();

        switch(analyzerType) {
            case 1:
                analyzer = new EnglishAnalyzer();
                System.out.println("Selected English Analyzer.");
                break;
            case 2:
                analyzer = new StandardAnalyzer();
                System.out.println("Selected Standard Analyzer.");
                break;
            default:
                analyzer = new EnglishAnalyzer();
                System.out.println("Default selected - English Analyzer.");
                break;
        }
        return analyzer;
    }
}
