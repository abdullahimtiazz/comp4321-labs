/* --
COMP336 Lab1 Exercise
Student Name: Abdullah, Imtiaz
Student ID: 20996401
Section: LX
Email: iabdullah@connect.ust.hk
*/

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;

class Posting implements Serializable
{
	public String doc;
	public int freq;
	Posting(String doc, int freq)
	{
		this.doc = doc;
		this.freq = freq;
	}
}

public class InvertedIndex
{
// 	public static class Posting implements Serializable
// {
// 	public String doc;
// 	public int freq;
// 	Posting(String doc, int freq)
// 	{
// 		this.doc = doc;
// 		this.freq = freq;
// 	}
// }
	private RecordManager recman;
	private HTree hashtable;

	InvertedIndex(String recordmanager, String objectname) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		long recid = recman.getNamedObject(objectname);
			
		if (recid != 0)
			hashtable = HTree.load(recman, recid);
		else
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject( "ht1", hashtable.getRecid() );
		}
	}


	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();				
	} 

	public void addEntry(String word, int x, int y) throws IOException
	{
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
    
		Object value = hashtable.get(word);
		Vector<Posting> postings;

		if (value != null) {
			postings = (Vector<Posting>) value;
		} else {
			postings = new Vector<Posting>();
		}

		// Create a new Posting; adjust the parameters as needed.
		Posting newPosting = new Posting("doc" + x, y);
		postings.add(newPosting);

		// Finally, put the updated postings back into the hashtable.
		hashtable.put(word, postings);
	}

	public void delEntry(String word) throws IOException
	{
		// Delete the word and its list from the hashtable
		// ADD YOUR CODES HERE
		hashtable.remove(word);
	} 
	public void printAll() throws IOException
	{
		// Print all the data in the hashtable
		// ADD YOUR CODES HERE
		FastIterator iter = hashtable.keys();
		String key;
		while ((key = (String) iter.next()) != null) {
			Vector<Posting> postings = (Vector<Posting>) hashtable.get(key);
			StringBuilder sb = new StringBuilder();
			if (postings != null) {     
				for (Posting p : postings) {
					sb.append(p.doc).append(":").append(p.freq).append(" ");
				}
			}
			System.out.println(key + " = " + sb.toString());
		}
	}	
	
	public static void main(String[] args)
	{
		try
		{
			InvertedIndex index = new InvertedIndex("lab1","ht1");
	
			index.addEntry("cat", 2, 6);
			index.addEntry("dog", 1, 33);
			System.out.println("First print");
			index.printAll();
			
			index.addEntry("cat", 8, 3);
			index.addEntry("dog", 6, 73);
			index.addEntry("dog", 8, 83);
			index.addEntry("dog", 10, 5);
			index.addEntry("cat", 11, 106);
			System.out.println("Second print");
			index.printAll();
			
			index.delEntry("dog");
			System.out.println("Third print");
			index.printAll();
			index.finalize();
		}
		catch(IOException ex)
		{
			System.err.println(ex.toString());
		}

	}
}
