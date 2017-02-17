********************************************
*****     SolrTSVImporter readme       *****
********************************************

======================================================================
What is SolrTSVImporter and what is it intended to be used for
======================================================================

In a nutshell SolrTSVImporter is a Java application that loads in Solr the TSV file
produced by the RootJuice software.

I developed and used this program in order to make the load process of the data into Solr easier.

======================================================================
How is the project folder made
======================================================================

The SolrTSVImporter folder is the Eclipse project ready to be run or modified in the IDE (you just have to import
the project "as an existing project" and optionally change some configuration parameters in the code).

Ignoring the hidden directories and the hidden files, under the main directory you can find 5 subdirectories :
1) src => contains the source code of the program
2) bin => contains the compiled version of the source files
3) lib => contains the jar files (libraries) that the program needs
4) sandbox => contains the executable jar file that you have to use in order to launch the program and some test input files that you can modify on the basis of your needs
5) solrConfiguration => contains all the stuff that you need to properly install and configure Solr on your machine in order to be used as a storage and search platform

As you probably already know it is definitely not a good practice to put all this stuff into a downloadable project folder, but i decided to break the rules in order to facilitate your job. Having all the stuff that will be necessary in just one location and by following the instructions you should be able to test the whole environment in 5-10 minutes.

======================================================================
How to execute the program on your PC by using the executable jar file
======================================================================

If you have Java already installed on your PC you just have to apply the following instruction points:

1) create a folder on your filesystem (let's say "myDir")

2) copy the file SolrTsvImporter.jar from sandbox directory to "myDir"

3) copy the file solrTsvImporterConf.properties from sandbox directory to "myDir"

4) copy the example file solrInput.csv (or the actual solrInput.csv that you obtained from the execution of the RootJuice program) from sandbox directory to "myDir"

5) customize the parameters inside the solrTsvImporterConf.properties file :
    	
    	If you are behind a proxy simply uncomment and customize the 2 parameters under the "proxy section" by removing the initial # character
    
    	Change the value of the parameters under the "paths section" according with the position of the files and folders on your filesystem.

    	Change the value of the parameters under the "Solr server configuration" according with your local Solr configuration (you can find a Solr environment configuration guide in the solrConfiguration directory)

6) make sure that your Solr server is up and running

7) open a terminal, go into the myDir directory, type and execute the following command:
        java -jar SolrTsvImporter.jar solrTsvImporterConf.properties solrInput.csv

8) at the end of execution you should find the records contained in the csv file loaded into Solr platform


======================================================================
LINUX			
======================================================================

If you are using a Linux based operating system open a terminal and type on a single line :

java -jar 
-Xmx_AmountOfRamMemoryInMB_m
/path_of_the_directory_containing_the_jar/SolrTSVImporter.jar 
/path_of_the_directory_containing_the_conf_file/solrTsvImporterConf.properties
/path_of_the_directory_containing_the_seed_file/tsvFileToLoad.csv


eg:

java -jar -Xmx2048m SolrTSVImporter.jar solrTsvImporterConf.properties solrInput.csv

java -jar -Xmx1024m /home/summa/workspace/SolrTSVImporter/sandbox/SolrTSVImporter.jar /home/summa/workspace/SolrTSVImporter/sandbox/solrTsvImporterConf.properties /home/summa/workspace/SolrTSVImporter/sandbox/solrInput.csv


======================================================================
WINDOWS			
======================================================================

If you are using a Windows based operating system you just have to do exactly the same, the only difference is that you have to substitute the slashes "/" with the backslashes "\" in the filepaths.

eg:

java -jar -Xmx1536m C:\workspace\SolrTSVImporter\sandbox\SolrTSVImporter.jar C:\workspace\SolrTSVImporter\sandbox\solrTsvImporterConf.properties C:\workspace\SolrTSVImporter\sandbox\solrInput.csv


======================================================================
Considerations
======================================================================

This program is still a work in progress so be patient if it is not completely fault tolerant; in any case feel free to contact me (donato.summa@istat.it) if you have any questions or comments.
