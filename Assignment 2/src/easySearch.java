import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.math.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class easySearch {
	public static void main(String[] args) throws ParseException, IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("src\\index")));
		int N = reader.maxDoc();
		IndexSearcher searcher = new IndexSearcher(reader);
		float F = 0;
		double f = 0;
		/**
		 * Get query terms from the query string
		 */
		String queryString = "New York";

		// Get the preprocessed query terms
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("TEXT", analyzer);
		Query query = parser.parse(queryString);
		Set<Term> queryTerms = new LinkedHashSet<Term>();
		searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
		System.out.println("Terms in the query: ");
		HashMap m1 = new HashMap();
		HashMap m2 = new HashMap();
		for (Term t : queryTerms) {	
			/**
			 * Get document frequency
			 */
			int df=reader.docFreq(new Term("TEXT", t.text()));
			//System.out.println("Number of documents containing the term \"police\" for field \"TEXT\": "+df);
			//System.out.println();
	
			/**
			 * Get document length and term frequency
			 */
			// Use DefaultSimilarity.decodeNormValue(…) to decode normalized
			// document length
			ClassicSimilarity dSimi = new ClassicSimilarity();
			// Get the segments of the index
			List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
			// Processing each segment
			for (int i = 0; i < leafContexts.size(); i++) {
				// Get document length
				LeafReaderContext leafContext = leafContexts.get(i);
				int startDocNo = leafContext.docBase;
				int numberOfDoc = leafContext.reader().maxDoc();
				//System.out.println(numberOfDoc);
				PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),"TEXT", new BytesRef(t.text()));
				int doc;
				if(de != null){
					while((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS){
						m1.put((de.docID() + startDocNo), de.freq());
					}
				}
				
				for (int docId = 0; docId < numberOfDoc; docId++) {
					int frequency = 0;
					// Get normalized length (1/sqrt(numOfTokens)) of the document
					float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
					// Get length of the document
					float docLeng = 1 / (normDocLeng * normDocLeng);
					if(m1.containsKey(docId + startDocNo)){
						frequency =  (int)m1.get((docId + startDocNo));
					}
					f = (frequency/docLeng) * Math.log(1 + (N/df));
					if (f != 0){
						System.out.println("F("+t.text()+","+(docId+startDocNo)+") = "+f);
					}
				}
			}	
		}
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
					f2 += (frequency/docLeng) * Math.log(1 + (N/df));					
				}
				if(f2 != 0){
					System.out.println("F("+queryString+","+(docId+startDocNo)+") = "+f2);
				}
			}
		}
	}
}