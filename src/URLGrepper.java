import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

/**
 * This class is used to find all the URLs on a given web page. A URLGrepper
 * object has a String representation, which allows it to be printed.<br><br>
 * Internally, it creates a Vector, each of whose elements is a String; these
 * Strings are the URLs that appear on the page.
 * <br><br>
 * This implementation assumes that any anchor tags containing URLs must sati
 * sfy the following restrictions:
 * <br><br>
 * <ol>
 * <li>The opening part of an <STRONG>href</STRONG> tag must be either 
 * <b>{@literal <} a href=" </b> or 
 * <strong>{@literal <}A HREF=".</strong>
 * That is: <br><ol type="a">
 * <li>Mixed-case specification 
 * (such as <strong>{@literal <}A hReF="</strong>) is not supported.</li>
 * <li>Only one space is allowed between the 
 * <strong>a</strong> (or <strong>A</strong>) and 
 * the <strong>href</strong> (or <strong>HREF</strong>).</li>
 * <li>No spaces are allowed on either side of the equals sign.</li>
 * </ol></li>
 * <li>The entire opening <strong>href</strong> 
 * tag (from the opening {@literal <} to the 
 * closing {@literal >}) must be on one line. However, the anchor text and 
 * the closing <strong>/href</strong> tag may be on subsequent lines.</li>
 * </ol><br>
 * The default behavior of a URLGrepper object is to include both relative 
 * and absolute URLs. This can be changed to only including absolute URLs
 * at the time we construct the URLGrepper object.
 * <br><br>
 * For extra credit: Spaces are allowed in URLs. That's because:
 * <ul>
 * <li>Such spaces may be genuine, since people sometimes put spaces into 
 * filenames.</li>
 * <li>Spammers often look for email addresses on webpages. So it's a good 
 * idea to obfuscate same. Part of the obfuscation may include 
 * spaces.</li>
 * </ul><br>
 * @version 2.2 16 Mar 2016
 * @author Yoonjae (Joe) Jung
 */
public class URLGrepper {
	private ArrayList<String> urls;
	private URL url;
	private String urlString;
	private boolean includeRelative;

	/**
	 * Two-parameter constructor sets up the URLGrepper.
	 * @param urlString the URL of the web page, as a String
	 * @param includeRelative true iff we are including relative URLs
	 * @throws java.net.MalformedURLException if theURLString is not 
	 * a valid URL
	 * @throws java.io.IOException if grepURLs throws an IOExcpetion
	 */
	public URLGrepper(String urlString, boolean includeRelative)
		throws MalformedURLException, IOException {
		try {
			this.urlString = urlString;
			this.includeRelative = includeRelative;
			urls = new ArrayList<String>();
			url = new URL(this.urlString);
			InputStream urlInputStream = url.openStream();
			grepURLs(urlInputStream);
		} catch (MalformedURLException urle) {
			urle.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Extracts all the URLs on the InputStream associated with a webpage, 
	 * saving them as in the Vector data member urls.
	 * @param urlInputStream the InputStream associated with the URL 
	 * whose contents we are grepping
	 * @throws java.net.MalformedURLException if urlString is not a valid URL
	 * @throws java.io.IOException if any I/O errors happen	 
	 */
	private void grepURLs(InputStream urlInputStream) throws
		MalformedURLException,IOException {	
		try {	
			String urlRegex = ""; 
			BufferedReader in = new BufferedReader(
				new InputStreamReader(urlInputStream));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if(inputLine.contains("<a href")||
					inputLine.contains("<A HREF")) { 
					if(this.includeRelative == false) 
						urlRegex = "((https?|ftp|gopher|telnet|file):"
							+"((//)|(\\\\))+"
							+"[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)|"
							+"mailto:*.*.(edu|com)";
					else 
						urlRegex = "(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|"
							+"([^'\">\\s]+))";
					Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
					Matcher urlMatcher = pattern.matcher(inputLine);
					while (urlMatcher.find())
	        		{
	        			urls.add(inputLine.substring(urlMatcher.start(0), urlMatcher.end(0)));
	        		}
				}
			}
			in.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	/**
	 * Provide a String version of the list of URLs found by a URLGrepper. 
	 * Each URL is placed on a separate line.
	 * @override toString in class java.lang.Object
	 * @return this String representation
	 */
	public String toString() {
		String asString = "";
		for(int i = 0; i < urls.size() ; i++)
			asString += urls.get(i).toString().replaceAll("[\\[\\],\\\"\\=]","")
				+ "\n";
		asString = asString.replace("href", "");
		asString = asString.replace("HREF", "");
		asString = encodeBlanks(asString);
		return asString;
	}
	
  /**
   * Choose the proper index when doing a quasi-case-independent search.
   * @param indexLC index of where lower-case search succeeded
	* @param indexUC index of where upper-case search succeeded
	* @return whichever index is appropriate (lower-case or upper-case)<ul>
	* <li>if indexLC {@literal <} 0, then return indexUC</li>
	* <li>if indexUC {@literal <} 0, then return indexLC</li>
	* <li>otherwise, return the minimum of indexLC and indexUC</li></ul>
   */
	@SuppressWarnings("unused")
	private int properIndex(int indexLC, int indexUC) {
		if(indexLC < 0) return indexUC;
		else if(indexUC < 0) return indexLC; 
		else return (indexUC > indexLC ? indexUC : indexLC);
	}

	/**
	 * URL-encode the blanks (and nothing else!) appearing in a URL, 
	 * replacing them with plus-signs (+). A URL can contain blanks:
	 * <ul><li>A file name may actually contain a blank.</li>
	 * <li>An obfuscated email address may contain a blank.</li></ul>
	 * Such URLs will confound the isAbsolute() method of the URI class. 
	 * This function will produce a partially-URL-encoded String that 
	 * represents the URL, in the sense described above.
	 * @param urlString the URL to be thus encoded
	 * @return the partially-URL-encoded URL.
	 */
	private String encodeBlanks(String urlString) {
		Scanner scan = new Scanner(urlString);
		while(scan.hasNextLine()) {
			scan.nextLine();
			urlString = urlString.replace(" ","+");
		}
		return urlString;
	}
}
