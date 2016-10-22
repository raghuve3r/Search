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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import myClass.Pair;
import myClass.Pair.*;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;




public class searchTRECtopics {
	public static void main(String [] args) throws IOException, org.apache.lucene.queryparser.classic.ParseException{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("src\\index")));
		int N = reader.maxDoc();
		IndexSearcher searcher = new IndexSearcher(reader);
		float F = 0;
		Path dir = Paths.get("src\\topics.51-100");
		String content = new String(Files.readAllBytes(dir));
		
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("TEXT", analyzer);
		
    	ArrayList<ArrayList<String>> values = getTagValues(content);
    	ArrayList<String> tit = values.get(0);
    	ArrayList<String> des = values.get(1);
    	ArrayList<String> num = values.get(2);
    	
		Map<String, Double> h1 = new HashMap<String, Double>();
		Map<String, Double> h2 = new HashMap<String, Double>();
    	
		

    	PrintWriter pw1 = new PrintWriter(new FileWriter("tfidfShortQuery.txt"));
    	PrintWriter pw2 = new PrintWriter(new FileWriter("tfidfLongquery.txt"));
    	
    	//ArrayList<String> tit = new ArrayList<String>();
    	//for(String s: tit){
    		//System.out.println(s);
    	//} 
    	//tit.add("New York");
    	for(String val: tit){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		Set<Term> queryTerms = new LinkedHashSet<Term>();
    		searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
    		HashMap m2 = new HashMap();
    		ClassicSimilarity dSimi = new ClassicSimilarity();
    		// Get the segments of the index
    		List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
    		for (int i = 0; i < leafContexts.size(); i++) {
    			LeafReaderContext leafContext = leafContexts.get(i);
    			int startDocNo = leafContext.docBase;
    			int numberOfDoc = leafContext.reader().maxDoc();
    			//System.out.println(numberOfDoc);
    			
    			for (int docId = 0; docId < numberOfDoc; docId++) {
    				int frequency = 0;
    				// Get normalized length (1/sqrt(numOfTokens)) of the document
    				float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
    				// Get length of the document
    				float docLeng = 1 / (normDocLeng * normDocLeng);
    				int df = 0;
    				double f2 = 0;
    				for (Term t : queryTerms) {
    					
    					df=reader.docFreq(new Term("TEXT", t.text()));
    					PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),"TEXT", new BytesRef(t.text()));
    					int doc;
    					if(de != null){
    						while((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS){
    							m2.put((de.docID() + startDocNo), de.freq());
    						}
    					}
    					if(m2.containsKey(docId + startDocNo)){
    						frequency =  (int)m2.get((docId + startDocNo));
    					}
    					if(df != 0){
    						f2 += (frequency/docLeng) * Math.log(1 + (N/df));
    					}
    				}
    				if(f2 != 0){
    					Integer qID = titleMap.get(val);
    					int zero = 0;
    					Set s = new TreeSet<String>();
    					s.add("DOCNO");
    					Document doc = leafContext.reader().document(docId, s);
    					String documentId = doc.get("DOCNO");
    					int docNo = (docId+startDocNo);
    					String docString = "DOC-NO"+docNo;
    					String combined = qID+"\t"+ zero +"\t"+documentId+"      ";
    					h1.put(combined, f2);
    					//System.out.println(qID +"\t"+ docString);
    					//System.out.println("F("+queryString+","+(docId+startDocNo)+") = "+f2);
    				}
    			}
    		}
    		Map<String, Double> sortedMap1 = sortByValue(h1);
        	int count1 = 1;
        	Iterator it1 = sortedMap1.entrySet().iterator();
        	while(it1.hasNext() && count1 <= 1000){
        		Map.Entry pair = (Map.Entry)it1.next();
        		it1.remove();
        		//String v = pair.getKey();
        		pw1.println();
        		pw1.write(pair.getKey() + "\t" + count1 + "\t" + pair.getValue() + "          \t" + "run-1");
        		
        		//System.out.println(count + "    "+pair.getKey()+" "+pair.getValue());
        		count1++;
        	}
    	} 
    	
    	for(String val: des){
    		String queryString = val;
    		Query query = parser.parse(QueryParser.escape(queryString));
    		Set<Term> queryTerms = new LinkedHashSet<Term>();
    		searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
    		HashMap m2 = new HashMap();
    		ClassicSimilarity dSimi = new ClassicSimilarity();
    		// Get the segments of the index
    		List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
    		for (int i = 0; i < leafContexts.size(); i++) {
    			LeafReaderContext leafContext = leafContexts.get(i);
    			int startDocNo = leafContext.docBase;
    			int numberOfDoc = leafContext.reader().maxDoc();
    			//System.out.println(numberOfDoc);
    			
    			for (int docId = 0; docId < numberOfDoc; docId++) {
    				int frequency = 0;
    				// Get normalized length (1/sqrt(numOfTokens)) of the document
    				float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
    				// Get length of the document
    				float docLeng = 1 / (normDocLeng * normDocLeng);
    				int df = 0;
    				double f2 = 0;
    				for (Term t : queryTerms) {
    					
    					df=reader.docFreq(new Term("TEXT", t.text()));
    					PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),"TEXT", new BytesRef(t.text()));
    					int doc;
    					if(de != null){
    						while((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS){
    							m2.put((de.docID() + startDocNo), de.freq());
    						}
    					}
    					if(m2.containsKey(docId + startDocNo)){
    						frequency =  (int)m2.get((docId + startDocNo));
    					}
    					if(df != 0){
    						f2 += (frequency/docLeng) * Math.log(1 + (N/df));
    					}
    				}
    				if(f2 != 0){
    					Integer qID = descMap.get(val);
    					int zero = 0;
    					Set s = new TreeSet<String>();
    					s.add("DOCNO");
    					org.apache.lucene.document.Document doc = leafContext.reader().document(docId, s);
    					String documentId = doc.get("DOCNO");
    					int docNo = (docId+startDocNo);
    					String docString = "DOC-NO"+docNo;
    					String combined = qID+"\t"+zero+"\t"+documentId+"      ";
    					h2.put(combined, f2);
    					//System.out.println(qID +"\t"+ docString);
    					//System.out.println("F("+queryString+","+(docId+startDocNo)+") = "+f2);
    				}
    			}
    		}
    		Map<String, Double> sortedMap2 = sortByValue(h2);
        	int count2 = 1;
        	Iterator it2 = sortedMap2.entrySet().iterator();
        	while(it2.hasNext() && count2 <= 1000){
        		Map.Entry pair = (Map.Entry)it2.next();
        		it2.remove();
        		//String v = pair.getKey();
        		pw2.println();
        		pw2.write(pair.getKey() + "\t" + count2 + "\t" + pair.getValue() + "          \t" + "run-1");
        		
        		//System.out.println(count + "    "+pair.getKey()+" "+pair.getValue());
        		count2++;
        	}
        	
    	}
    	
    	
    	
    	//System.out.println(sortedMap1.size());
    	//System.out.println(sortedMap2.size());
    	//pw.write("QueryID	Q0	DocID	Rank	Score	RunID");
    	
    	System.out.println("Done");
    	pw1.close();
    	pw2.close();
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
	
	   private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

	        // 1. Convert Map to List of Map
	        List<Map.Entry<String, Double>> list =
	                new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

	        // 2. Sort list with Collections.sort(), provide a custom Comparator
	        //    Try switch the o1 o2 position for a different order
	        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
	            public int compare(Map.Entry<String, Double> o1,
	                               Map.Entry<String, Double> o2) {
	                return (o1.getValue()).compareTo(o2.getValue());
	            }
	        });

	        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
	        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
	        for (Map.Entry<String, Double> entry : list) {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }

	        /*
	        //classic iterator example
	        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
	            Map.Entry<String, Integer> entry = it.next();
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }*/


	        return sortedMap;
	    }
}
