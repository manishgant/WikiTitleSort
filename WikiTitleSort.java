import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.Map.Entry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WikiTitleSort {
	Random generator;
	String userName;
	String inputFileName;
	String delimiters = " \t,;.?!-:@[](){}_*/";
	String[] stopWordsArray = { "i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
			"yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
			"itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
			"these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
			"do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
			"of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
			"after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
			"further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
			"few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
			"too", "very", "s", "t", "can", "will", "just", "don", "should", "now" };

	void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA");
		messageDigest.update(seed.toLowerCase().trim().getBytes());
		byte[] seedMD5 = messageDigest.digest();

		long longSeed = 0;
		for (int i = 0; i < seedMD5.length; i++) {
			longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
		}

		this.generator = new Random(longSeed);
	}

	Integer[] getIndexes() throws NoSuchAlgorithmException {
		Integer n = 10000;
		Integer number_of_lines = 50000;
		Integer[] ret = new Integer[n];
		this.initialRandomGenerator(this.userName);
		for (int i = 0; i < n; i++) {
			ret[i] = generator.nextInt(number_of_lines);
		}
		return ret;
	}

	public WikiTitleSort(String userName, String inputFileName) {
		this.userName = userName;
		this.inputFileName = inputFileName;
	}

	public String[] process() throws Exception {
		String[] ret = new String[20];
		/**
		 * Create the Data Structures necessary for the algorithm
		 */
		HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
		ArrayList<String> finalList = new ArrayList<String>();
		ArrayList<String> lineByLineList = new ArrayList<String>();
		ArrayList<String> indexWordList = new ArrayList<String>();

		/**
		 * Read input Wikipedia Titles
		 */

		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		try {
			String line = br.readLine();
			while (line != null) {
				lineByLineList.add(line);
				line = br.readLine();
			}

		} finally {
			br.close();
		}

		String[] lineByLineArray = lineByLineList.toArray(new String[lineByLineList.size()]);
		/*
		 * Get the index based on argument from the coursera ID
		 */
		Integer[] indexArray = getIndexes();

		/*
		 * Based on the indices returned, read only those lines from the
		 * Wikipedia titles
		 */

		for (Integer i : indexArray) {
			StringTokenizer st = new StringTokenizer(lineByLineArray[i], delimiters);

			while (st.hasMoreTokens()) {
				indexWordList.add(st.nextToken().toLowerCase().trim());
			}

		}

		/*
		 * Remove commonly used words based on pre-prepared list
		 */
		indexWordList.removeAll(Arrays.asList(stopWordsArray));

		/*
		 * Insert filtered words into the HashMap along with the frequency of
		 * occurrence
		 */
		for (String word : indexWordList) {
			Integer f = wordMap.get(word);
			if (f == null) {
				wordMap.put(word, 1);
			} else {
				wordMap.put(word, f + 1);
			}
		}


		List<Map.Entry<String, Integer>> entries = new LinkedList<Map.Entry<String, Integer>>(
				sortByValues(wordMap).entrySet());

		/*
		 * Create a final list of entries with sorted keys based on frequency of
		 * occurrence
		 */
		for (Map.Entry<String, Integer> entry : entries) {
			finalList.add(entry.getKey());
		}

		String[] wordArray = finalList.toArray(new String[finalList.size()]);

		/*
		 * Take top 20 results and return the String[]
		 */
		for (int i = 0; i < 20; i++) {
			ret[i] = wordArray[i];
		}

		return ret;

	}

	/***
	 * This method compares and sorts the HashMap based on Values
	 * 
	 * @param map
	 * @return sortedMap
	 */
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				/*
				 * Sort by value and if value is equal, Sort the keys that have
				 * equal value
				 */
				int sComp = o2.getValue().compareTo(o1.getValue());

				if (sComp != 0) {
					return sComp;
				} else {
					String x1 = (String) o1.getKey();
					String x2 = (String) o2.getKey();
					return x1.compareTo(x2);
				}

			}
		});

		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (int i = 1; i < entries.size() - 1; i++) {

		}

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("MP1 <User ID>");
		} else {
			String userName = args[0];
			String inputFileName = "./input.txt";
			MP1 mp = new MP1(userName, inputFileName);
			String[] topItems = mp.process();
			for (String item : topItems) {
				System.out.println(item);
			}
		}
	}
}
