import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class compareAlgorithms {
	public static void main(String [] args) throws IOException, org.apache.lucene.queryparser.classic.ParseException{
		compareAlgorithms c = new compareAlgorithms();
		c.BM25();
		c.VectorSpace();
		c.LMDirichlet();
		c.LMJelinekMercer();
	}
	
	public void BM25() throws IOException, org.apache.lucene.queryparser.classic.ParseException{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("src\\index")));
		int N = reader.maxDoc();
		IndexSearcher searcher = new IndexSearcher(reader);
		float F = 0;
		Path dir = Paths.get("src\\topics.51-100");
		String content = new String(Files.readAllBytes(dir));
		
		Analyzer analyzer = new StandardAnalyzer();
		searcher.setSimilarity(new BM25Similarity());
		QueryParser parser = new QueryParser("TEXT", analyzer);
		
    	ArrayList<ArrayList<String>> values = getTagValues(content);
    	ArrayList<String> tit = values.get(0);
    	ArrayList<String> des = values.get(1);
    	ArrayList<String> num = values.get(2);
    	
		Map<String, Double> h1 = new HashMap<String, Double>();
		Map<String, Double> h2 = new HashMap<String, Double>();
    	
		

    	PrintWriter pw1 = new PrintWriter(new FileWriter("BM25ShortQuery.txt"));
    	PrintWriter pw2 = new PrintWriter(new FileWriter("BM25Longquery.txt"));
    	
    	for(String val: tit){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		TopDocs results = searcher.search(query, 1000);
    		int numTotalHits = results.totalHits;
    		
    		ScoreDoc[] hits = results.scoreDocs;
    		for(int i=0;i<hits.length;i++){	
    			//System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
    			Integer qID = titleMap.get(val);
    			Document doc=searcher.doc(hits[i].doc);
    			String dId = doc.get("DOCNO");
    			pw1.write(qID+"\t"+0+"\t"+dId+"        "+"\t"+(i+1)+"\t"+hits[i].score+"             "+"\t"+"run-1");
    			pw1.println();
    		}
    		
    		
    	} 
    	pw1.close();

    	for(String val: des){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		TopDocs results = searcher.search(query, 1000);
    		int numTotalHits = results.totalHits;
    		
    		ScoreDoc[] hits = results.scoreDocs;
    		for(int i=0;i<hits.length;i++){	
    			//System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
    			Integer qID = descMap.get(val);
    			Document doc=searcher.doc(hits[i].doc);
    			String dId = doc.get("DOCNO");
    			pw2.write(qID+"\t"+0+"\t"+dId+"        "+"\t"+(i+1)+"\t"+hits[i].score+"             "+"\t"+"run-1");
    			pw2.println();
    		}  		
    	} 
    	pw2.close();
    	//System.out.println("Done");
	}
	
	public void VectorSpace() throws IOException, org.apache.lucene.queryparser.classic.ParseException{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("src\\index")));
		int N = reader.maxDoc();
		IndexSearcher searcher = new IndexSearcher(reader);
		float F = 0;
		Path dir = Paths.get("src\\topics.51-100");
		String content = new String(Files.readAllBytes(dir));
		
		Analyzer analyzer = new StandardAnalyzer();
		searcher.setSimilarity(new ClassicSimilarity());
		QueryParser parser = new QueryParser("TEXT", analyzer);
		
    	ArrayList<ArrayList<String>> values = getTagValues(content);
    	ArrayList<String> tit = values.get(0);
    	ArrayList<String> des = values.get(1);
    	ArrayList<String> num = values.get(2);
    	
		Map<String, Double> h1 = new HashMap<String, Double>();
		Map<String, Double> h2 = new HashMap<String, Double>();
    	
		

    	PrintWriter pw1 = new PrintWriter(new FileWriter("VectorSpaceShortQuery.txt"));
    	PrintWriter pw2 = new PrintWriter(new FileWriter("VectorSpaceLongquery.txt"));
    	
    	for(String val: tit){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		TopDocs results = searcher.search(query, 1000);
    		int numTotalHits = results.totalHits;
    		
    		ScoreDoc[] hits = results.scoreDocs;
    		for(int i=0;i<hits.length;i++){	
    			//System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
    			Integer qID = titleMap.get(val);
    			Document doc=searcher.doc(hits[i].doc);
    			String dId = doc.get("DOCNO");
    			pw1.write(qID+"\t"+0+"\t"+dId+"        "+"\t"+(i+1)+"\t"+hits[i].score+"             "+"\t"+"run-1");
    			pw1.println();
    		}
    		
    		
    	} 
    	pw1.close();

    	for(String val: des){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		TopDocs results = searcher.search(query, 1000);
    		int numTotalHits = results.totalHits;
    		
    		ScoreDoc[] hits = results.scoreDocs;
    		for(int i=0;i<hits.length;i++){	
    			//System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
    			Integer qID = descMap.get(val);
    			Document doc=searcher.doc(hits[i].doc);
    			String dId = doc.get("DOCNO");
    			pw2.write(qID+"\t"+0+"\t"+dId+"        "+"\t"+(i+1)+"\t"+hits[i].score+"             "+"\t"+"run-1");
    			pw2.println();
    		}  		
    	} 
    	pw2.close();
    	//System.out.println("Done");
	}

	public void LMDirichlet() throws IOException, org.apache.lucene.queryparser.classic.ParseException{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("src\\index")));
		int N = reader.maxDoc();
		IndexSearcher searcher = new IndexSearcher(reader);
		float F = 0;
		Path dir = Paths.get("src\\topics.51-100");
		String content = new String(Files.readAllBytes(dir));
		
		Analyzer analyzer = new StandardAnalyzer();
		searcher.setSimilarity(new LMDirichletSimilarity());
		QueryParser parser = new QueryParser("TEXT", analyzer);
		
    	ArrayList<ArrayList<String>> values = getTagValues(content);
    	ArrayList<String> tit = values.get(0);
    	ArrayList<String> des = values.get(1);
    	ArrayList<String> num = values.get(2);
    	
		Map<String, Double> h1 = new HashMap<String, Double>();
		Map<String, Double> h2 = new HashMap<String, Double>();
    	
		

    	PrintWriter pw1 = new PrintWriter(new FileWriter("LMDirichletShortQuery.txt"));
    	PrintWriter pw2 = new PrintWriter(new FileWriter("LMDirichletLongquery.txt"));
    	
    	for(String val: tit){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		TopDocs results = searcher.search(query, 1000);
    		int numTotalHits = results.totalHits;
    		
    		ScoreDoc[] hits = results.scoreDocs;
    		for(int i=0;i<hits.length;i++){	
    			//System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
    			Integer qID = titleMap.get(val);
    			Document doc=searcher.doc(hits[i].doc);
    			String dId = doc.get("DOCNO");
    			pw1.write(qID+"\t"+0+"\t"+dId+"        "+"\t"+(i+1)+"\t"+hits[i].score+"             "+"\t"+"run-1");
    			pw1.println();
    		}
    		
    		
    	} 
    	pw1.close();

    	for(String val: des){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		TopDocs results = searcher.search(query, 1000);
    		int numTotalHits = results.totalHits;
    		
    		ScoreDoc[] hits = results.scoreDocs;
    		for(int i=0;i<hits.length;i++){	
    			//System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
    			Integer qID = descMap.get(val);
    			Document doc=searcher.doc(hits[i].doc);
    			String dId = doc.get("DOCNO");
    			pw2.write(qID+"\t"+0+"\t"+dId+"        "+"\t"+(i+1)+"\t"+hits[i].score+"             "+"\t"+"run-1");
    			pw2.println();
    		}  		
    	} 
    	pw2.close();
    	//System.out.println("Done");
	}

	public void LMJelinekMercer() throws IOException, org.apache.lucene.queryparser.classic.ParseException{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("src\\index")));
		int N = reader.maxDoc();
		IndexSearcher searcher = new IndexSearcher(reader);
		float F = 0;
		Path dir = Paths.get("src\\topics.51-100");
		String content = new String(Files.readAllBytes(dir));
		
		Analyzer analyzer = new StandardAnalyzer();
		searcher.setSimilarity(new LMJelinekMercerSimilarity((float)0.7));
		QueryParser parser = new QueryParser("TEXT", analyzer);
		
    	ArrayList<ArrayList<String>> values = getTagValues(content);
    	ArrayList<String> tit = values.get(0);
    	ArrayList<String> des = values.get(1);
    	ArrayList<String> num = values.get(2);
    	
		Map<String, Double> h1 = new HashMap<String, Double>();
		Map<String, Double> h2 = new HashMap<String, Double>();
    	
		

    	PrintWriter pw1 = new PrintWriter(new FileWriter("LMJelinekMercerShortQuery.txt"));
    	PrintWriter pw2 = new PrintWriter(new FileWriter("LMJelinekMercerLongquery.txt"));
    	
    	for(String val: tit){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		TopDocs results = searcher.search(query, 1000);
    		int numTotalHits = results.totalHits;
    		
    		ScoreDoc[] hits = results.scoreDocs;
    		for(int i=0;i<hits.length;i++){	
    			//System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
    			Integer qID = titleMap.get(val);
    			Document doc=searcher.doc(hits[i].doc);
    			String dId = doc.get("DOCNO");
    			pw1.write(qID+"\t"+0+"\t"+dId+"        "+"\t"+(i+1)+"\t"+hits[i].score+"             "+"\t"+"run-1");
    			pw1.println();
    		}
    		
    		
    	} 
    	pw1.close();

    	for(String val: des){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		TopDocs results = searcher.search(query, 1000);
    		int numTotalHits = results.totalHits;
    		
    		ScoreDoc[] hits = results.scoreDocs;
    		for(int i=0;i<hits.length;i++){	
    			//System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
    			Integer qID = descMap.get(val);
    			Document doc=searcher.doc(hits[i].doc);
    			String dId = doc.get("DOCNO");
    			pw2.write(qID+"\t"+0+"\t"+dId+"        "+"\t"+(i+1)+"\t"+hits[i].score+"             "+"\t"+"run-1");
    			pw2.println();
    		}  		
    	} 
    	pw2.close();
    	//System.out.println("Done");
	}

	private static final Pattern TOP = Pattern.compile("<top>(.+?)</top>",Pattern.DOTALL);
	private static final Pattern NUMBER = Pattern.compile("<num>(.+?)<dom>", Pattern.DOTALL);
	private static final Pattern TITLE = Pattern.compile("<title>(.+?)<desc>",Pattern.DOTALL);
	private static final Pattern DESC = Pattern.compile("<desc>(.+?)<smry>",Pattern.DOTALL);
	
	static HashMap<String,Integer> titleMap = new HashMap<String,Integer>();
	static HashMap<String,Integer> descMap = new HashMap<String,Integer>();

	
	private static ArrayList<ArrayList<String>> getTagValues(final String str) {
		//System.out.println(str);
	    String title = new String();
	    String desc = new String();
	    String numb = new String();
	    int intNumb = 0;
	    

	    ArrayList<String> d = new ArrayList<String>();
	    
	    //Pair<String,Integer> p = new Pair<String,Integer>();
	    
	    ArrayList<String> titleList = new ArrayList<String>();
	    ArrayList<String> descList = new ArrayList<String>();
	    ArrayList<String> numberList = new ArrayList<String>();

	    ArrayList<ArrayList<String>> total = new ArrayList<ArrayList<String>>();

	    final Matcher top = TOP.matcher(str);
	    
	    while(top.find()){
	    	d.add(top.group(1));
	    }
	    

	    for(String script: d){
		    final Matcher descMatcher = DESC.matcher(script);
		    final Matcher titleMatcher = TITLE.matcher(script);
		    final Matcher numberMatcher = NUMBER.matcher(script);
	    	while (descMatcher.find()) {	
		    	desc = descMatcher.group(1);
		    	desc = desc.replaceAll("\\n", "");
		    	desc = desc.replaceAll("\\z", "");
		    	desc = desc.replaceAll("Description:", "");


		    	//System.out.println(desc);
		    }
		    while (titleMatcher.find()) {
		    	title = titleMatcher.group(1);
		    	title = title.replaceAll("\\n", "");
		    	title = title.replaceAll("\\z", "");
		    	title = title.trim();
		    	//System.out.println(title);
		    }
		    while(numberMatcher.find()){
		    	numb = numberMatcher.group(1);
		    	numb = numb.replaceAll("\\s","");
		    	numb = numb.replaceAll("^[\\d]","");
		    	numb = numb.replaceAll("Number:", "");
		    	//String numb1 = numb.replaceAll("^[0-9]","");
		    	//System.out.println(numb);
		    	intNumb = Integer.parseInt(numb);
		    	
		    }
		    titleMap.put(title, intNumb);
		    descMap.put(desc, intNumb);
		    //Integer numb1 = new Integer(numb);
		    //Pair<desc,numb1> = new Pair<desc,numb1>();
		    descList.add(desc);
		    titleList.add(title);
		    numberList.add(numb);
	    }
	    
	    
	    total.add(titleList);
	    total.add(descList);
	    total.add(numberList);
	    return total;
	}
}
