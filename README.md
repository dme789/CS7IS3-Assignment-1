# CS7IS3-Assignment-1
#### Dominique Meudec - 18327666

This assignment uses Apache Lucence to index and search the Cranfield Collection.
The project directory already includes the Cranfield collection, which can 
be found [online here](http://ir.dcs.gla.ac.uk/resources/test_collections/cran/).
The project also uses [trec_eval](https://github.com/usnistgov/trec_eval/blob/master/README) to evaluate the result of the program with the
provided cranqrel file from the Cranfield Collection. 


## Usage
To build the project, you can use the following maven command
with the project directory:
```bash
mvn package
```

Upon success of the build, a jar will be built. To run the resulting
program, run the following command:
```bash
java -jar target/Assignment1-1.0.jar
```

You will be prompted twice, once to select an Analyzer and once to select a Scoring method
of your choice. In both instances, you will be prompted to reply with a number
selecting your choice.

## Result
To return the Mean Average Precision (MAP) score, run the following command:
```bash
trec_eval-9.0.7/trec_eval -m map cran-data/QRelsCorrectedforTRECeval results/query-results.txt
```

To return the Recall score, run the following command:
```bash
trec_eval-9.0.7/trec_eval -m recall cran-data/QRelsCorrectedforTRECeval results/query-results.txt
```
