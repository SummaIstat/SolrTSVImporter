package it.istat.solrtsvimporter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.opencsv.CSVReader;

/**
* @author  Donato Summa
*/
public class SolrTsvImporter {
	
	static Logger logger = Logger.getLogger(SolrTsvImporter.class);
	private static SolrServer solrServer;
	private static String logFilePath;
 	 
	public static void main(String[] args) throws Exception {
		
		SolrTsvImporter solrTsvImporter = new SolrTsvImporter();
        solrTsvImporter.configure(args);
        
		//=====================================================================================================
		// Initial prints
        //=====================================================================================================
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date startDateTime = new Date();
		logger.info("\n\n\n");
        logger.info("**********************************************************");
        logger.info("Starting datetime = " + dateFormat.format(startDateTime)); //15/12/2014 15:59:48
        logger.info("remember to modify maxPostSize e timeout in Tomcat if needed");
        
        solrTsvImporter.loadAndSendTsvFileToSolr(args[1]);
		
        //=====================================================================================================
      	// Final prints
        //=====================================================================================================
        Date endDateTime = new Date();
		logger.info("Started at = " + dateFormat.format(startDateTime)); //15/12/2014 15:59:48
        logger.info("Ending datetime = " + dateFormat.format(endDateTime)); //15/12/2014 15:59:48
        
	}
	
	private void configure(String[] args) throws IOException{
		
		if (args.length == 2){
			
			if (Utils.isAValidFile(args[0])){
				
				FileInputStream fis = new FileInputStream(args[0]); // conf.properties
				InputStream inputStream = fis;
				Properties props = new Properties();
				props.load(inputStream);
				
				configLogFile(props); // logFile configuration
				configSolrServer(props); // Solr server configuration
					
				inputStream.close();
				fis.close();
				
			} else {
				System.out.println("Error opening the file " + args[0] + " or file non-existent");
				System.exit(1);
			}

		} else {
			System.out.println("Usage: java -jar SolrTsvImporter.jar [conf.properties fullpath] [tsvFileToLoad.csv fullpath]");
			System.exit(1);
		}	
		
	}

	private void configSolrServer(Properties props) {
		
		if(props.getProperty("SOLR_SERVER_URL") != null && props.getProperty("SOLR_SERVER_QUEUE_SIZE") != null && props.getProperty("SOLR_SERVER_THREAD_COUNT") != null){
//			solrServer = new ConcurrentUpdateSolrServer(    props.getProperty("SOLR_SERVER_URL"),
//															Integer.parseInt(props.getProperty("SOLR_SERVER_QUEUE_SIZE")),
//															Integer.parseInt(props.getProperty("SOLR_SERVER_THREAD_COUNT")));
			solrServer = new HttpSolrServer(props.getProperty("SOLR_SERVER_URL"));
			
			((HttpSolrServer) solrServer).setMaxRetries(1); // defaults to 0. > 1 not recommended.
			((HttpSolrServer) solrServer).setConnectionTimeout(10000); // 5 seconds to establish TCP
			 // The following settings are provided here for completeness.
			 // They will not normally be required, and should only be used
			 // after consulting javadocs to know whether they are truly required.
			((HttpSolrServer) solrServer).setSoTimeout(10000); // socket read timeout
			((HttpSolrServer) solrServer).setDefaultMaxConnectionsPerHost(100);
			((HttpSolrServer) solrServer).setMaxTotalConnections(100);
			((HttpSolrServer) solrServer).setFollowRedirects(false); // defaults to false
			 // allowCompression defaults to false.
			 // Server side must support gzip or deflate for this to have any effect.
			((HttpSolrServer) solrServer).setAllowCompression(false);
			
			
		}else{
			logger.error("Wrong/missing configuration for the parameters SOLR_SERVER_URL, SOLR_SERVER_QUEUE_SIZE and SOLR_SERVER_THREAD_COUNT !");
			System.exit(1);
		}
		
	}

	private void configLogFile(Properties props) {
		
		if(props.getProperty("LOG_FILE_PATH") != null){
			
			logFilePath = props.getProperty("LOG_FILE_PATH");
			
			RollingFileAppender rfa = new RollingFileAppender();
			rfa.setName("FileLogger");
			rfa.setFile(logFilePath);
			rfa.setAppend(true);
			rfa.activateOptions();
			rfa.setMaxFileSize("20MB");
			rfa.setMaxBackupIndex(30);
			rfa.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));

			Logger.getRootLogger().addAppender(rfa);
			
		}else{
			logger.error("Wrong/missing configuration for the parameter LOG_FILE_PATH !");
			System.exit(1);
		}
		
	}

	private void loadAndSendTsvFileToSolr (String tsvFileToLoad){
		
		if (Utils.isAValidFile(tsvFileToLoad)) {
		
			try {	
				
//				CSVReader csvReader = new CSVReader(new FileReader(tsvFileToLoad), '\t');
//				String [] nextLine;
//				csvReader.readNext(); // skip the first line
//			    while ((nextLine = csvReader.readNext()) != null) {
//			        // nextLine[] is an array of values from the line
//			        System.out.println(nextLine[0] + nextLine[1] + "etc...");
//			        logger.info(nextLine[0] + nextLine[1] + "etc...");
//			    }
//			    System.out.println("csvReader.getLinesRead() " + csvReader.getLinesRead());
//			    System.out.println("csvReader.getRecordsRead() " + csvReader.getRecordsRead());
//			    System.out.println("csvReader.keepCarriageReturns() " + csvReader.keepCarriageReturns());
//				csvReader.close();
				
				String delimiter = "\t";
				FileInputStream fis = new FileInputStream(tsvFileToLoad);
				InputStream is = fis;
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String strLine;
				int i = 0;		
				List<SolrInputDocument> docList = new ArrayList<SolrInputDocument>();
				br.readLine(); // avoid the first line with headers
				String[] tokens;
				while ((strLine = br.readLine()) != null) {
					tokens = strLine.split(delimiter);
					//System.out.println(strLine + "etc...");
					if (tokens.length != 16){
						logger.error(tokens[0] + " having " + tokens.length + " tokens was not added in Solr !");
					}else{
						SolrInputDocument solrInputDocument = new SolrInputDocument();
					    solrInputDocument.setField("id", tokens[0]);
					    solrInputDocument.setField("url", tokens[1]);
					    solrInputDocument.setField("imgsrc", tokens[2]);
					    solrInputDocument.setField("imgalt", tokens[3]);
					    solrInputDocument.setField("links", tokens[4]);
					    solrInputDocument.setField("ahref", tokens[5]);
					    solrInputDocument.setField("aalt", tokens[6]);
					    solrInputDocument.setField("inputvalue", tokens[7]);
					    solrInputDocument.setField("inputname", tokens[8]);
					    solrInputDocument.setField("metatagDescription", tokens[9]);
					    solrInputDocument.setField("metatagKeywords", tokens[10]);
					    solrInputDocument.setField("codiceAzienda", tokens[11]);
					    solrInputDocument.setField("sitoAzienda", tokens[12]);
					    solrInputDocument.setField("codiceLink", tokens[13]);
					    solrInputDocument.setField("title", tokens[14]);
					    solrInputDocument.setField("corpoPagina", tokens[15]);
					    logger.debug(tokens[0] + " ==> " + strLine.length());
					    docList.add(solrInputDocument);
					    //UpdateResponse response = solrServer.add(solrInputDocument, 25000);
					    //solrServer.add(solrInputDocument, 25000);
					    //logger.info(tokens[0] + " ==> " + response.toString());
					    
					    i++;
						
					    if( i % 500 == 0){ // commit every 100 docs could be too aggressive for Solr server
					    	commitToSolr(docList);
					    	//aspetta(10000);					    	
					    	docList.clear();
					    	logger.info("===> " + i + " docs sent to Solr server");
					    	System.out.println("===> " + i + " docs sent to Solr server");
					    }
				    } 
				}
				
				commitToSolr(docList);
				//aspetta(10000);
				logger.info("===> " + i + " docs sent to Solr server");
				System.out.println("===> " + i + " docs sent to Solr server");
				
				br.close();
				is.close();
			}catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
				System.err.println("Error: " + fnfe.getMessage());
				logger.error("Error: " + fnfe.getMessage());
				System.exit(1);
			} catch (SolrServerException e) {
				e.printStackTrace();
				logger.error("Error sending document to Solr ! " + e.getMessage() + " " + e.getCause());
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Error sending document to Solr ! " + e.getMessage() + " " + e.getCause());
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error: " + e.getMessage());
				logger.error("Error: " + e.getMessage());
			}
			
		} else {
			
			System.out.println("Error opening the file " + tsvFileToLoad + " or file non-existent");
			System.exit(1);
		}
			
	}

	private void aspetta(int milliSeconds) {
		try{
    		Thread.sleep(milliSeconds); // wait for 10 sec
    		logger.info("===> I waited for " + milliSeconds + " milliseconds");
    	}catch(InterruptedException e){
    	
    	}		
	}

	private void commitToSolr(List<SolrInputDocument> docList) throws SolrServerException, IOException {
		
//    	UpdateResponse response = solrServer.add(docList, 15000);					    	
//    	solrServer.commit(true,false);  // periodically flush
		
		long startTime = System.currentTimeMillis();
    	UpdateRequest req = new UpdateRequest();
    	req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
    	req.add (docList);
    	UpdateResponse rsp = req.process( solrServer );
    	System.out.print ("Added " + docList.size() + " documents to solr. Time taken = " + rsp.getElapsedTime() + " millisecs. " + rsp.toString());
    	logger.info("Added " + docList.size() + " documents to solr. Time taken = " + rsp.getElapsedTime() + " millisecs. " + rsp.toString());
    	long endTime = System.currentTimeMillis();
    	System.out.println (" , time-taken=" + ((double)(endTime-startTime))/1000.00 + " seconds");
    	//logger.info(" , time-taken=" + ((double)(endTime-startTime))/1000.00 + " seconds");
		
	}
	
	
}
