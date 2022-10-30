package ie.tcd.meudecd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.search.similarities.*;

import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;

public class QueryIndex {

    private static String INDEX_DIRECTORY = "index";
    private static String CRAN_QUERY = "cran-data/cran.qry";
    private static String RESULT_DIRECTORY = "results/query-results.txt";

    public static void search(int scoringType, Analyzer analyzer) throws Exception

    {
        DirectoryReader ireader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_DIRECTORY)));

        // Use IndexSearcher to retrieve some arbitrary document from the index
        IndexSearcher isearcher = new IndexSearcher(ireader);

        // Sets the scoring as the same that was used to index the documents
        switch(scoringType) {
            case 1:
                isearcher.setSimilarity(new BM25Similarity());
                break;
            case 2:
                isearcher.setSimilarity(new ClassicSimilarity());
                break;
            case 3:
                isearcher.setSimilarity(new BooleanSimilarity());
                break;
            case 4:
                isearcher.setSimilarity(new LMDirichletSimilarity());
                break;
        }


        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "author", "bib", "content"}, analyzer);

        try {
            System.out.println("Starting index querying...");
            BufferedReader queryReader = new BufferedReader(new FileReader(CRAN_QUERY));
            BufferedWriter queryWriter = new BufferedWriter(new FileWriter(RESULT_DIRECTORY));
            String currLine = queryReader.readLine();
            int queryNumber = 0;
            while(currLine != null) {
                queryNumber++;
                String query = "";

                // checking for new document
                if (currLine.startsWith(".I")) {
                    currLine = queryReader.readLine();
                    String currAtr = "";
                    while (currLine != null) {
                        if (currLine.startsWith(".W")) {
                            currAtr = currLine.substring(0,2);
                            currLine = queryReader.readLine();
                        } else if (currLine.startsWith(".I")) {
                            break;
                        }
                        if (!currLine.substring(0,1).equals(" ")) {
                            currLine = " " + currLine;
                        }
                        if (currAtr.equals(".W")) {
                            query = query + currLine;
                        }
                        currLine = queryReader.readLine();
                    }
                }
                query = query.trim();
                query = query.replace("?", "");
                Query queryQ = queryParser.parse(QueryParser.escape(query));
                ScoreDoc[] hits = isearcher.search(queryQ, 50).scoreDocs;

                for (int i =0; i < hits.length; i++) {
                    queryWriter.write(queryNumber + " Q0 " + isearcher.doc(hits[i].doc).get("id") + " " + i +
                            " " + hits[i].score + " STANDARD");
                    queryWriter.newLine();
                }

            }
            queryReader.close();
            queryWriter.close();
            System.out.println("FINISHED: Queries, total queries performed is " + queryNumber);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
