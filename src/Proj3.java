/**********************************************************************
 *
 * Project 3: Grepping URLs
 *
 * Driver program
 *
 * This program takes a list of one or more URLs from its command
 * line.  For each such URL:
 *
 * (1) A URLGrepper object is created.  
 * 
 * (2) Said URLGrepper automatically determines all the URLs found on
 *     the web page corresponding to the command-line parameter URL.
 *
 * (3) These URLs are printed on standard output.
 *
 * If the command line has a "-a" flag, only absolute URLs are
 * printed; otherwise, all URLs are printed.
 *
 * For 5 points' worth of extra credit: handle URLs with embedded
 * blanks.  This change doesn't appear in the current file, since it's
 * an implementation detail in the URLGrepper class.
 *
 * Author: Yoonjae Jung
 * Date:   29 February 2016
 *
 **********************************************************************/

public class Proj3 {

   public static void main(String args[]) {
      if (args.length == 0) 
         usageError(1);
      int startArg = 0;

      boolean includeRelative = true;	// include relative URLs?
      if (args[0].charAt(0) == '-') 
         if (!args[0].equals("-a"))
            usageError(2);
         else if (args.length == 1)
            usageError(3);
         else {
            includeRelative = false;
            startArg = 1;
         }

      for (int i = startArg; i < args.length; i++) {
         try {
            URLGrepper allURLs = new URLGrepper(args[i], includeRelative);
            System.out.println(allURLs);
         }
         catch (Exception ex) {
            System.err.println(ex);
            System.exit(1);
         }
      }
   }

   public static void usageError(int returnCode) {
      System.err.println("Usage: java Proj3 [-a] url url ... ");
      System.err.println("  -a: only print absolute URLs");
      System.exit(returnCode);
   }

}
