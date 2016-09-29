import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;


public class generateIndex{
	public static void main(String[] args) throws IOException {
		Path dir = Paths.get("src\\corpus\\");
		Directory dirc = FSDirectory.open(Paths.get("src\\LuceneDoc"));
		Analyzer analyzer = new StandardAnalyzer();
		
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dirc,iwc);
		try(DirectoryStream<Path> stream =
			     Files.newDirectoryStream(dir, "*.trectext")) {
		    for (Path entry: stream) {
		    	String content = new String(Files.readAllBytes(entry));
		    	ArrayList<ArrayList<String>> values = getTagValues(content);
		    	
		    	ArrayList<String> doc = new ArrayList<String>();
		    	ArrayList<String> file = new ArrayList<String>();
		    	doc = values.get(0);
		    	file = values.get(4);
		    	int y = file.size();
		    	for(int j = 0; j < y;j++){
		    		Document luceneDoc = new Document();
		    		luceneDoc.add(new StringField("DOCNO",doc.get(j),Field.Store.YES));
		    		luceneDoc.add(new TextField("TEXT",file.get(j),Field.Store.YES));
		    		writer.addDocument(luceneDoc);
		    	}
		    		
		    }
		    writer.forceMerge(1);
		    writer.close();
		    IndexReader reader = DirectoryReader.open(dirc);
		    
		    System.out.println("No of documents in the corpus: "+reader.maxDoc());
		    System.out.println("Number of documents containing the term \"new\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "new")));
		    System.out.println("Number of occurrences of \"new\" in the field \"TEXT\": "+reader.totalTermFreq(new Term("TEXT","new")));
		    Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		    System.out.println("Size of the vocabulary for this field: "+vocabulary.size());
		    System.out.println("Number of documents that have at least one term for this field: "+vocabulary.getDocCount());
		    System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
		    System.out.println("Number of postings for this field: "+vocabulary.getSumDocFreq());
		    TermsEnum iterator = vocabulary.iterator();
		    BytesRef byteRef = null;
		    System.out.println("\n*******Vocabulary-Start**********");
		    while((byteRef = iterator.next()) != null) {
		    String term = byteRef.utf8ToString();
		    System.out.print(term+"\t");
		    }
		    System.out.println("\n*******Vocabulary-End**********");
		    reader.close();
		} 
		catch (IOException x) {
		    System.err.println(x);
		}
	}
	private static final Pattern DOC = Pattern.compile("<DOC>(.+?)</DOC>",Pattern.DOTALL);
	private static final Pattern DOC_TAG = Pattern.compile("<DOCNO>(.+?)</DOCNO>");
	private static final Pattern HEAD_TAG = Pattern.compile("<HEAD>(.+?)</HEAD>");
	private static final Pattern BYLINE_TAG = Pattern.compile("<BYLINE>(.+?)</BYLINE>");
	private static final Pattern DATELINE_TAG = Pattern.compile("<DATELINE>(.+?)</DATELINE>");
	private static final Pattern TEXT_TAG = Pattern.compile("<TEXT>(.+?)</TEXT>",Pattern.DOTALL);
	
	private static ArrayList<ArrayList<String>> getTagValues(final String str) {
	    String doc = new String();
	    String head = new String();
	    String bl = new String();
	    String dl = new String();
	    String text = new String();
	    ArrayList<String> d = new ArrayList<String>();
	    
	    ArrayList<String> docList = new ArrayList<String>();
	    ArrayList<String> headList = new ArrayList<String>();
	    ArrayList<String> blList = new ArrayList<String>();
	    ArrayList<String> dlList = new ArrayList<String>();
	    ArrayList<String> textList = new ArrayList<String>();
	    
	    final Matcher dMatcher = DOC.matcher(str);

	    ArrayList<ArrayList<String>> total = new ArrayList<ArrayList<String>>();

	    
	    while(dMatcher.find()){
	    	d.add(dMatcher.group(1));
	    }

	    for(String script: d){
		    final Matcher docMatcher = DOC_TAG.matcher(script);

		    final Matcher headMatcher = HEAD_TAG.matcher(d.get(0));
		    final Matcher blMatcher = BYLINE_TAG.matcher(d.get(0));
		    final Matcher dlMatcher = DATELINE_TAG.matcher(d.get(0));
		    final Matcher textMatcher = TEXT_TAG.matcher(d.get(0));

	    	while (docMatcher.find()) {	
		    	doc = docMatcher.group(1);
		    }
		    while (headMatcher.find()) {
		    	head = head + headMatcher.group(1);
		    }
		    while (blMatcher.find()) {
		    	bl = bl + blMatcher.group(1);
		    }
		    while (dlMatcher.find()) {
		    	dl = dl + dlMatcher.group(1);
		    }
		    while (textMatcher.find()) {
		    	text = textMatcher.group(1);
		    }
		    docList.add(doc);
	    	headList.add(head);
	    	blList.add(bl);
	    	dlList.add(dl);
	    	textList.add(text);
	    }
	    
	    
	    
	    total.add(docList);
	    total.add(headList);
	    total.add(blList);
	    total.add(dlList);
	    total.add(textList);
	    
	    return total;
	}	
}