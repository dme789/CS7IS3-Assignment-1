package ie.tcd.meudecd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
 
public class CreateIndex
{
    
    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "index";
    private static String CRAN_DATA = "cran-data/cran.all.1400";

    public static int createIndex(Analyzer analyzer) throws IOException
    {

        // Open the directory that contains the search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Set up an index writer to add process and save documents to the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        int scoringType = setScoring(config);
        IndexWriter iwriter = new IndexWriter(directory, config);

        try {
            System.out.println("Starting index processing...");
            BufferedReader cranReader = new BufferedReader(new FileReader(CRAN_DATA));
            String currLine = cranReader.readLine();
            int docNumbers = 0;
            while(currLine != null) {
                String title = "";
                String author = "";
                String bib = "";
                String words = "";
                Document doc = new Document();

                // checking for new document
                if (currLine.startsWith(".I")) {
                    // adding doc id to document
                    String id_val = currLine.substring(3);
                    doc.add(new StringField("id", id_val, Field.Store.YES));
                    currLine = cranReader.readLine();
                    String currAtr = "";
                    while (currLine != null) {
                        if (currLine.startsWith(".T") || currLine.startsWith(".A") || currLine.startsWith(".B") || currLine.startsWith(".W")) {
                            currAtr = currLine.substring(0,2);
                            currLine = cranReader.readLine();
                        } else if (currLine.startsWith(".I")) {
                            break;
                        }
                        if (!currLine.substring(0,1).equals(" ")) {
                            currLine = " " + currLine;
                        }
                        if (currAtr.equals(".T")) {
                            title = title + currLine;
                        } else if (currAtr.equals(".A")) {
                            author = author + currLine;
                        } else if (currAtr.equals(".B")) {
                            bib = bib + currLine;
                        } else if (currAtr.equals(".W")) {
                            words = words + currLine;
                        }
                        currLine = cranReader.readLine();
                    }
                    doc.add(new TextField("title", title, Field.Store.YES));
                    doc.add(new TextField("author", author, Field.Store.YES));
                    doc.add(new TextField("bib", bib, Field.Store.YES));
                    doc.add(new TextField("content", words, Field.Store.YES));
                    iwriter.addDocument(doc);
                }
                docNumbers++;
            }
            cranReader.close();
            System.out.println("FINISHED: Indexing, total docs added is " + docNumbers);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Commit changes and close everything
        iwriter.close();
        directory.close();
        return scoringType;
    }

    public static int setScoring(IndexWriterConfig config)
    {
        Scanner scoringIn = new Scanner(System.in);
        System.out.println("Please select the type of Scoring:\n1. 1 for BM25\n2. 2 for Classic (VSM)\n3. 3 for Boolean");
        int scoringType = scoringIn.nextInt();

        switch(scoringType) {
            case 1:
                config.setSimilarity(new BM25Similarity());
                System.out.println("Selected BM25 for scoring.");
                break;
            case 2:
                config.setSimilarity(new ClassicSimilarity());
                System.out.println("Selected Classic (VSM) for scoring.");
                break;
            case 3:
                config.setSimilarity(new BooleanSimilarity());
                System.out.println("Selected Boolean for scoring.");
                break;
        }
        return scoringType;
    }

}
