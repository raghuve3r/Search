import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;


public class indexComparison{
	public static void main(String[] args) throws IOException {
		Analyzers a = new Analyzers();
		System.out.println("Results for 4 different analyzers:");
		System.out.println(new String(new char[100]).replace("\0", "-"));
		System.out.println("Standard Analyzer results:");
		System.out.println(new String(new char[100]).replace("\0", "-"));
		a.standardAnalyzer();
		System.out.println(new String(new char[100]).replace("\0", "-"));
		System.out.println("Simple Analyzer results:");
		System.out.println(new String(new char[100]).replace("\0", "-"));
		a.simpleAnalyzer();
		System.out.println(new String(new char[100]).replace("\0", "-"));
		System.out.println("Stop Analyzer results:");
		System.out.println(new String(new char[100]).replace("\0", "-"));
		a.stopAnalyzer();
		System.out.println(new String(new char[100]).replace("\0", "-"));
		System.out.println("Keyword Analyzer results:");
		System.out.println(new String(new char[100]).replace("\0", "-"));
		a.keywordAnalyzer();
	}	
}

class Analyzers{
	
	public void standardAnalyzer() throws IOException{
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
	    System.out.println("Number of documents containing the term \"good\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "good")));
	    if(reader.docFreq(new Term("TEXT", "good")) > 0){
	    	System.out.println("Able to find \"good\", tokenization applied! Because the text is broken down to words.");
	    }
	    else{
	    	System.out.println("Tokenization not applied");
	    }
	    Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
	    System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
	    System.out.println("Searching for one of the most common stop word: \"the\" ");
	    if(reader.docFreq(new Term("TEXT", "the")) > 0){
	    	System.out.println("Stop words not removed.");
	    }
	    else{
	    	System.out.println("No occurence of stop word \"the\". Stop words are removed!");
	    }
	    System.out.println("Number of terms in the dictionary: " + vocabulary.size());
	    reader.close();
	} 
	catch (IOException x) {
	    System.err.println(x);
	}
}
	
	public void simpleAnalyzer() throws IOException{
		Path dir = Paths.get("src\\corpus\\");
		Directory dirc = FSDirectory.open(Paths.get("src\\LuceneDoc"));
		Analyzer analyzer = new SimpleAnalyzer();
		
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
		    System.out.println("Number of documents containing the term \"good\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "good")));
		    if(reader.docFreq(new Term("TEXT", "good")) > 0){
		    	System.out.println("Able to find \"good\", tokenization applied! Because the text is broken down to words.");
		    }
		    else{
		    	System.out.println("Tokenization not applied");
		    }
		    Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		    System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
		    System.out.println("Searching for one of the most common stop word: \"the\" ");
		    if(reader.docFreq(new Term("TEXT", "the")) > 0){
		    	System.out.println("Stop words not removed. "+reader.docFreq(new Term("TEXT", "the"))+" occurences of \"the\" found");
		    }
		    else{
		    	System.out.println("No occurence of stop word \"the\". Stop words are removed!");
		    }
		    System.out.println("Number of terms in the dictionary: " + vocabulary.size());
		    reader.close();
		} 
		catch (IOException x) {
		    System.err.println(x);
		}
	}


	public void stopAnalyzer() throws IOException{
		Path dir = Paths.get("src\\corpus\\");
		Directory dirc = FSDirectory.open(Paths.get("src\\LuceneDoc"));
		Analyzer analyzer = new StopAnalyzer();
		
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
		    System.out.println("Number of documents containing the term \"good\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "good")));
		    if(reader.docFreq(new Term("TEXT", "good")) > 0){
		    	System.out.println("Able to find \"good\", tokenization applied! Because the text is broken down to words.");
		    }
		    else{
		    	System.out.println("Tokenization not applied");
		    }
		    Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		    System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
		    System.out.println("Searching for one of the most common stop word: \"the\" ");
		    if(reader.docFreq(new Term("TEXT", "the")) > 0){
		    	System.out.println("Stop words not removed.");
		    }
		    else{
		    	System.out.println("No occurence of stop word \"the\". Stop words are removed!");
		    }
		    System.out.println("Number of terms in the dictionary: " + vocabulary.size());
		    reader.close();
		} 
		catch (IOException x) {
		    System.err.println(x);
		}
	}
	public void keywordAnalyzer() throws IOException{
		Path dir = Paths.get("src\\corpus\\");
		Directory dirc = FSDirectory.open(Paths.get("src\\LuceneDoc"));
		Analyzer analyzer = new KeywordAnalyzer();
		
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
		    System.out.println("Number of documents containing the term \"good\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "good")));
		    if(reader.docFreq(new Term("TEXT", "good")) > 0){
		    	System.out.println("Able to find \"good\", tokenization applied! Because the text is broken down to words.");
		    }
		    else{
		    	System.out.println("Tokenization not applied");
		    }
		    Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		    System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
		    System.out.println("Searching for one of the most common stop word: \"the\" ");
		    if(reader.docFreq(new Term("TEXT", "the")) > 0){
		    	System.out.println("Stop words not removed.");
		    }
		    else{
		    	System.out.println("No occurence of stop word \"the\". Keyword analyzer treats the whoe text as one token. Hence stop words won't be removed.");
		    }
		    System.out.println("Number of terms in the dictionary: " + vocabulary.size());
		    reader.close();
		} 
		catch (IOException x) {
		    System.err.println(x);
		}
	}

	
private static final Pattern DOC = Pattern.compile("<DOC>(.*?)</DOC>",Pattern.DOTALL);
private static final Pattern DOC_TAG = Pattern.compile("<DOCNO>(.*?)</DOCNO>");
private static final Pattern HEAD_TAG = Pattern.compile("<HEAD>(.*?)</HEAD>");
private static final Pattern BYLINE_TAG = Pattern.compile("<BYLINE>(.*?)</BYLINE>");
private static final Pattern DATELINE_TAG = Pattern.compile("<DATELINE>(.*?)</DATELINE>");
private static final Pattern TEXT_TAG = Pattern.compile("<TEXT>(.*?)</TEXT>",Pattern.DOTALL);

private static ArrayList<ArrayList<String>> getTagValues(final String str) {
    
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
    	String doc = new String();
        String head = new String();
        String bl = new String();
        String dl = new String();
        String text = new String();
        
	    final Matcher docMatcher = DOC_TAG.matcher(script);

	    final Matcher headMatcher = HEAD_TAG.matcher(script);
	    final Matcher blMatcher = BYLINE_TAG.matcher(script);
	    final Matcher dlMatcher = DATELINE_TAG.matcher(script);
	    final Matcher textMatcher = TEXT_TAG.matcher(script);

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