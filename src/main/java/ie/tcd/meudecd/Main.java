package ie.tcd.meudecd;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class Main
{

    public static void main(String[] args) throws Exception
    {
        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();

        // create index
        int scoringType = CreateIndex.createIndex(analyzer);

        // query index
        QueryIndex.search(scoringType, analyzer);

    }
}
