package assgn3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Set;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;


import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;


import java.io.FileNotFoundException;
import java.io.IOException;



public class AuthorRank 
{
	 double [][] weigth = new double[13940][13940];
	 int [] prior = new int[10];
	 double [][] Text_wd_weight = new double[1000][1000];
	 int [] prior_text_index = new int [25];
	 public void calculate_PR()
	 { 
		 String fileName = "src\\assgn3\\author.net";
		 String line = null;
		 HashMap<Integer,String> author = new HashMap<Integer, String>();
		 Graph<Integer, String> g = new DirectedSparseGraph<Integer, String> ();
		 
		    try {
	    	FileReader fileReader = 
	                new FileReader(fileName);
	
	            BufferedReader bufferedReader = 
	                new BufferedReader(fileReader);
	            line = bufferedReader.readLine();
	            line = line.trim();
	            if(line.equals("*Vertices     2000")) {
	                for(int i=0;i<2000;i++){
	                	line = bufferedReader.readLine();
	                	String s[] = line.split(" ");
	                	author.put(i, s[0]);
	                	g.addVertex(Integer.parseInt(s[0]));
	                }
	            }
	            line = bufferedReader.readLine();
	            line = line.trim();
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
	    }
	    catch(FileNotFoundException ex) {
	        System.out.println(
	            "Unable to open file '" + 
	            fileName + "'");                
	    }
	    catch(IOException ex) {
	        System.out.println(
	            "Error reading file '" 
	            + fileName + "'");                  
	    }
	    
	       
	    PageRank<Integer, String> PR = new PageRank<Integer, String>(g, 0.85);
	    PR.setMaxIterations(30);
	    PR.evaluate();
	    System.out.println("Page rank score for "+PR.getIterations()+" iterations:");
	    System.out.println("Rank \t Author \t Page Rank Score");
		Map<String, Double> h = new HashMap<String, Double>();
		for(int i=0;i<2000;i++){
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
	    	System.out.print(entry.getValue());
	    	System.out.println();
	        if (counter == 10){
	        	break;
	        }
	    }
	 }
 	public static void main(String[] args) 
 	{ 
		 AuthorRank authorRank = new AuthorRank();
		 authorRank.calculate_PR();
	 }

}