package ie.tcd.meudecd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
// import org.apache.lucene.store.RAMDirectory;
 
public class Index
{
    
    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "index";
    private static String CRAN_DATA = "cran-data/cran.all.1400";

    public static void main(String[] args) throws IOException
    {
        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();

        // ArrayList of documents in the corpus
        ArrayList<Document> documents = new ArrayList<Document>();

        // Open the directory that contains the search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Set up an index writer to add process and save documents to the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(directory, config);

        try {
            BufferedReader cranReader = new BufferedReader(new FileReader(CRAN_DATA));
            String currLine = cranReader.readLine();
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
                    doc.add(new TextField("words", words, Field.Store.YES));
                    documents.add(doc);
                }
            }
            System.out.println("FINISHED");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Save the document to the index
        iwriter.addDocuments(documents);

        // Commit changes and close everything
        iwriter.close();
        directory.close();
    }
}
