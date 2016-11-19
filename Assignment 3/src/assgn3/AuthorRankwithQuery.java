package assgn3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class AuthorRankwithQuery {	
	 double [][] weigth = new double[13940][13940];
	 float [] prior = new float[2001];
	 double [][] Text_wd_weight = new double[1000][1000];
	 int [] prior_text_index = new int [25];
	 HashMap<String,Float> h1 = new HashMap<String,Float>();			
	 public void calculate_PR(String q) throws IOException, ParseException
	 {		 
		 	IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("src\\index")));
			int N = reader.maxDoc();
			IndexSearcher searcher = new IndexSearcher(reader);		
			Analyzer analyzer = new StandardAnalyzer();
			searcher.setSimilarity(new BM25Similarity());
			QueryParser parser = new QueryParser("content", analyzer);
			//String q = new String("Data Mining");
	    	String queryString = q;
	    	Query query = parser.parse(QueryParser.escape(queryString));
	    	TopDocs results = searcher.search(query, 300);
	    	int numTotalHits = results.totalHits;
	    	ScoreDoc[] hits = results.scoreDocs;
	    	for(int i=0;i<hits.length;i++){	
	    		Document doc=searcher.doc(hits[i].doc);
	    		String str = doc.getField("authorid").stringValue();
	    		str = str.replaceAll("\\D+","");    			
	    		if (h1.containsKey(str)){
	    			//System.out.println(str);
	    			float f = h1.get(str) + hits[i].score;
	    			h1.put(str, f);
	    		}
	    		else{
	    			h1.put(str, hits[i].score);
	    		}
	    	}  
	    	//System.out.println(h1.size());
	    	Iterator<Entry<String, Float>> it = h1.entrySet().iterator();
    		float total = 0;
    		while(it.hasNext()){
    			Map.Entry pair = (Map.Entry)it.next();    			
    			total += h1.get(pair.getKey());
    		}
    		//System.out.println(h1.size());
    		String fileName = "src\\assgn3\\author.net";
    		String line = null;
    		HashMap<Integer,String> author = new HashMap<Integer, String>();
    		Graph<Integer, String> g = new DirectedSparseGraph<Integer, String> ();		 
    			FileReader fileReader = new FileReader(fileName);	
    			BufferedReader bufferedReader = new BufferedReader(fileReader);
    			line = bufferedReader.readLine();
    			line = line.trim();
    			if(line.equals("*Vertices     2000")) {
	                for(int i=0;i<2000;i++){
	                	line = bufferedReader.readLine();
	                	String s[] = line.split(" ");
	                	author.put(i+1, s[0]);
	                	g.addVertex(Integer.parseInt(s[0]));
	                }
	        }
	        line = bufferedReader.readLine();
	        line = line.trim();
	        //System.out.println(h1.size());
	        if(line.equals("*Edges     13940")) {
	            for(int i=0;i<13940;i++){
	               	line = bufferedReader.readLine();
	               	String s[] = line.split(" ");
	               	int r = Integer.parseInt(s[0]);
	               	int c = Integer.parseInt(s[1]);
	               	String tmp_edge = r + "->" + c;
	    			g.addEdge(tmp_edge, r, c, EdgeType.DIRECTED);
	            }
	        }
	        bufferedReader.close();      	            
	        //System.out.println(h1.size());
    		
	        //System.out.println("a");
    		for(int j=1;j<=2000;j++){
    			String au = author.get(j);
    			//System.out.println(h1.size());
    			if(h1.containsKey(au)){
    				float fl = h1.get(au)/total;
    				//System.out.println(fl);
    				prior[j] = h1.get(au)/total;
    			}
    			else{
    				prior[j] = 0;
    			}
    		}
	       
		/*for(int i=1;i<=2000;i++){
			System.out.println(prior[i]);
		}*/

	    Transformer<Integer, Double> vertex_prior = 
	            new Transformer<Integer, Double>()
	            {            
	         @Override
	                 public Double transform(Integer v) 
	                 {                        
	                     return (double) prior[v];            
	                 }           
	            };
	    PageRankWithPriors<Integer, String> PR = new PageRankWithPriors<Integer, String>(g, vertex_prior,0.7);
	    PR.setMaxIterations(30);
	    PR.evaluate();
	    System.out.println("Page rank score for "+PR.getIterations()+" iterations:");
	    System.out.println("Rank \t Author \t Page Rank Score");
		Map<String, Double> h = new HashMap<String, Double>();
		for(int i=1;i<=2000;i++){
	    	h.put(author.get(i), PR.getVertexScore(Integer.parseInt(author.get(i))));
	    }
		Set<Entry<String, Double>> set = h.entrySet();
	    List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
	    Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
	    {
	        public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
	        {
	            return (o2.getValue()).compareTo( o1.getValue() );
	        }
	    } );
	    int counter = 0;
	    for(Map.Entry<String, Double> entry:list){
	    	counter++;
	    	System.out.printf(counter + "\t" + " "+entry.getKey()+"\t");
	    	System.out.print("       ");
	    	System.out.printf("%.15f",entry.getValue());
	    	System.out.println();
	        if (counter == 10){
	        	break;
	        }
	    }
	 }
	public static void main(String[] args) throws IOException, ParseException 
	{ 
		 AuthorRankwithQuery authorRank = new AuthorRankwithQuery();
		 authorRank.calculate_PR("Data Mining");
		 authorRank.calculate_PR("Information Retrieval");
	 }
}
