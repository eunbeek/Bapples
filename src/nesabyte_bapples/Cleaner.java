package nesabyte_bapples;


/***
 * @author nesa bertanico
 * @version 4
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.*;
import java.util.HashSet;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Cleaner {

    // for colored terminal text
    public static final String RESET = "\033[0m";

    final static String RED = "\033[0;31m";     // RED
    final static String GREEN = "\033[0;32m";   // GREEN
    final static String YELLOW = "\033[0;33m";  // YELLOW

    //set URL status code apart from 404 400 200 into 999
    final static int unknown = 999;

    /**
     * Demonstrate processing a single provided argument.
     *
     * @param arguments Command-line arguments; expecting a
     *                  String-based name.
     */
    public static void main(String[] args) throws Exception {

        String mUrl, html, txt = "";
        HashSet<String> m_url;
        HashSet<String> commands;

        if (args.length == 0) {
            bappleHelp();
        } else {

            StringJoiner sb = new StringJoiner("");

            //loop through the args[] and save it into 1 string to be processed
            for (int i = 0; i < args.length; i++) {
                sb.add(args[i] + " ");
            }

            //turn the StringJoiner into a plain String
            String str = sb.toString();

            //get the commands from the command
            commands = pullCommands(str);

            //get the urls from the command
            m_url = pullLinks(str);

            //get the html from the command
            html = pullHTML(str);

            try {
                //if user gave 1 or more commands
                if (commands.size() > 0) {

                    //loop through all the commands from the user
                    for (String cmds : commands) {

                        //if user wants the check the version
                        if (cmds.matches("--v") || cmds.matches("--version")) {
                            System.out.println("Bapples version: bap.v.03");

                            //if you wants to check the help
                        } else if (cmds.matches("--h") || cmds.matches("--help")) {
                            bappleHelp();

                            //if you wants to check --all from HTML
                        } else if (cmds.matches("--all") && !html.equals("")) {
                            File directory = new File(html);
                            classifyingHTML(directory.getAbsolutePath(), unknown);

                            //if you wants to check --good from HTML
                        } else if (cmds.matches("--good") && !html.equals("")) {
                            File directory = new File(html);
                            classifyingHTML(directory.getAbsolutePath(), 200);

                            //if you wants to check --bad from HTML
                        } else if (cmds.matches("--bad") && !html.equals("")) {
                            File directory = new File(html);
                            classifyingHTML(directory.getAbsolutePath(), 499);

                            //if you wants to check --all from URL
                        } else if (cmds.matches("--all") && m_url.size() > 0) {
                            classifyingApples(m_url, unknown);

                            //if you wants to check --good from URL
                        } else if (cmds.matches("--good") && m_url.size() > 0) {
                            classifyingApples(m_url, 200);

                            //if you wants to check --bad from URL
                        } else if (cmds.matches("--bad") && m_url.size() > 0) {
                            classifyingApples(m_url, 499);

                            //if user gives status command with a url
                        } else if (cmds.matches("--[0-9][0-9][0-9]") && m_url.size() > 0) {
                            String number = cmds.substring(2);
                            int num = Integer.parseInt(number.trim());
                            System.out.println("            nums: " + number);
                            classifyingApples(m_url, num);

                            //if user give status command with an html file
                        } else if (cmds.matches("--[0-9][0-9][0-9]") && !html.equals("")) {
                            //String finall = "src\\nesabyte_bapples\\" + html;
                            File directory = new File(html);
                            //String fin = directory.getAbsolutePath();

                            String number = cmds.substring(2);
                            int num = Integer.parseInt(number.trim());
                            classifyingHTML(directory.getAbsolutePath(), num);

                            //if user wants to check if http can be https from a url
                        } else if (cmds.matches("--secure") && m_url.size() > 0) {
                            checkSecureURL(m_url);

                            //if user wants to output JSON from html
                        } else if ((cmds.matches("--json") || cmds.matches("--j")) && !html.equals("")) {
                            tobeJSON(html);

                            //if user wants to ignore urls, read ignore-url from txt file
                        } else if((cmds.matches("--ignore") || cmds.matches("--i"))){
                    		
                        	txt = pullTXT(str);                       	
                            File directory = new File(txt);
                            classifyingTXT(directory.getAbsolutePath(),html, unknown);
                        	
                        	//if nothing matches, command is not allowed
                        } else {
                            System.out.println("Forbidden Command [" + cmds + "]");
                        }
                    }//end of for loop

                    //if user inputs 1 URL
                } else if (m_url.size() == 1) {
                    classifyingApples(m_url, unknown); //


                    //if user inputs 1 HTML
                } else if (!html.equals("")) { // if user inputs an HTML
                    //String finall = "src\\nesabyte_bapples\\" + html;
                    File directory = new File(html);
                    // String fin = directory.getAbsolutePath();
                    classifyingHTML(directory.getAbsolutePath(), unknown);


                    //if nothing matches, command is not allowed
                } else {
                    System.out.println("You gave me a bad command");
                    bappleHelp();
                }
                bappleExitMessage(); // displays exit message

                //catch all exception errors
            } catch (Exception e) {
                System.out.println("\n\nYou gave me a Bad Apple Tree: " + e + "\n");
            }
        }
    }

    /**
     * this function turns the list of URLS into json.
     * we feed the url its status code in Urljson class with String url and int status code,
     * then outputs using Gson gson = new GsonBuilder().setPrettyPrinting().create(); to make the display pretty.
     *
     * @param file
     */
    private static void tobeJSON(String file) {
        try {
            //reads from the *.html file then store all its contents in a string
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String inputLine;
            StringBuilder a = new StringBuilder();

            //reading all the content of the HTML
            while ((inputLine = reader.readLine()) != null)
                a.append(inputLine);
            reader.close();
            String content = a.toString();
            HashSet<String> aLink = pullLinks(content);

            for (String link : aLink) {
                String regex = "(http://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(link);


                //Gson gson = new Gson();

                while (m.find()) {
                    int stat = 0;
                    String urlStr = m.group();
                    if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                        urlStr = urlStr.substring(1, urlStr.length() - 1);
                    }

                    stat = AppleCode(urlStr);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Urljson url = new Urljson(urlStr, stat);

                    String json = gson.toJson(url);
                    System.out.println(json);

                }/**/
            }

        } catch (Exception e) {
            System.out.println("\n\nYou gave me a Bad Apple Tree: " + e + "[" + file + "]");
        }
    }

	/**
	 * this method takes in the html file, reads the file and store it in a string.
	 * then the long string will be validated to take all the links
	 * this method reads from the url then store all its html resource contents in a string.
	 * The string then is processed by the use of the pullLinks() method to take all of the links and be placed inside a HashSet.
	 * Then send the Hashset in FindUnsecured() to find all the links with http and turn them into https and check their status
	 * @param file
	 */
	private static void checkSecureHTML(String file) {
        //if user puts more urls to check
        try {
            //reads from the *.html file then store all its contents in a string
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String inputLine;
            StringBuilder a = new StringBuilder();

            //reading all the content of the HTML
            while ((inputLine = reader.readLine()) != null)
                a.append(inputLine);
            String currentLine = reader.readLine();
            reader.close();
            String content = a.toString();
            HashSet<String> aLink = pullLinks(content);

            findUnsecured(aLink);

        } catch (Exception e) {
            System.out.println("\n\nYou gave me a Bad Apple Tree: " + e + "[" + file + "]");
        }


    }

	/**
	 *  This method takes in the a url, connects to the url, and store all its' html content in a string.
	 * 	then the long string will be validated to take all the links
	 * 	This method reads from the url then store all its html resource contents in a string.
	 * 	The string then is processed by the use of the pullLinks() method to take all of the links and be placed inside a HashSet.
	 * 	Then send the Hashset in FindUnsecured() to find all the links with http and turn them into https and check their status
	 * @param url
	 */
	private static void checkSecureURL(HashSet<String> url) {
        for (String myUrl : url) {
            try {
                //connecting to myUrl
                URL URL = new URL(myUrl);
                URLConnection myURLConnection = URL.openConnection();
                myURLConnection.connect();

                //read the html from the url
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        myURLConnection.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();

                String content = a.toString();

                HashSet<String> aLink = pullLinks(content);

                findUnsecured(aLink);

            } catch (Exception e) {
                System.out.println("\n\nYou gave me a Bad Apple Tree: " + e + "\n" + url);
            }
        }
    }

	/**
	 * This method finds all the links with http and turn them into https and check their status
	 * @param links
	 */
	private static void findUnsecured(HashSet<String> links) {
        HashSet<String> unlinks = new HashSet<String>();
        HashSet<String> unlinks_test = new HashSet<String>();
        int Gcounter = 0, Bcounter = 0, Ucounter = 0;

        for (String link : links) {
            String regex = "(http://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(link);
            while (m.find()) {
                String urlStr = m.group();
                if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                    urlStr = urlStr.substring(1, urlStr.length() - 1);
                }
                //if a link is found, save it in the hashset
                unlinks.add(urlStr);

            }
        }
        StringBuilder str;
        for (String un : unlinks) {
            str = new StringBuilder(un);
            str.insert(4, 's');

            try {
                int code = AppleCode(str.toString());
                if (code == 400 || code == 404) {
                    System.out.println(RED + "[ " + code + " ]   BAD APPLE     : " + str.toString() + RESET);
                    Bcounter++;
                } else if (code == 200) {
                    System.out.println(GREEN + "[ " + code + " ]   GOOD APPLE    : " + str.toString() + RESET);
                    Gcounter++;
                } else if (code == 0) {
                    System.out.println(YELLOW + "[ ??? ]   UNKNOWN APPLE : " + str.toString() + RESET);
                    Ucounter++;
                } else {
                    System.out.println(YELLOW + "[ " + code + " ]   UNKNOWN APPLE : " + str.toString() + RESET);
                    Ucounter++;
                }


            } catch (Exception e) {
                System.out.println("\n\nYou gave me a Bad Apple Tree: " + e + "\n" + str.toString());
            }
        }


        System.out.println("--------------------------------------------------------");
        System.out.println("      Done counting apples!");
        System.out.println("          Good apples:    " + Gcounter);
        System.out.println("          Bad apples:     " + Bcounter);
        System.out.println("          Unknown apples: " + Ucounter);
        System.out.println("--------------------------------------------------------");
    }


    /***
     * this method is inspired from https://www.computing.dcu.ie/~humphrys/Notes/Networks/java.html
     *
     * this method accepts a String containing a URL, and it will open a connection.
     * if connection is granted, it will get the response code of the link,
     * the connection max time is 10secs,
     * then it returns an Integer that holds the status code.
     *
     * @param link
     * @return
     * @throws Exception
     */
    private static int AppleCode(String link) throws Exception {

        HttpURLConnection connection = null;
        int myCode = 0;
        try {
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            myCode = connection.getResponseCode();
            connection.setConnectTimeout(1000);
            return myCode;
        } catch (IOException e) {
            return myCode;
        } finally {
            // If the connection is open, disconnect.
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    /***
     * this method is inspired from http://blog.houen.net/java-get-url-from-string/
     *
     * this method accepts a string, it finds all the links from the string.
     * It recognizes the link through the help of the regex.
     * then returns the LINKS
     *
     * @param text
     * @return
     */
    private static HashSet<String> pullLinks(String text) {
        HashSet<String> links = new HashSet<String>();

        //regex to find the link
        String regex = "(?:(?:https?|ftp)://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            //if a link is found, save it in the hashset
            links.add(urlStr);
        }
        return links;
    }

    /***
     * this method accepts a string, it finds all the *.html from the string.
     * It recognizes the link .html the help of the regex.
     * then returns the HTML FILE NAME
     *
     * @param text
     * @return
     */
    private static String pullHTML(String text) {
        String HTMLStr = "";

        //regex to find the html filename
        String regex = "[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|].html";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            HTMLStr = m.group();

            //return the html file name, if there is.
            return HTMLStr;
        }
        //return an emtpy string
        return HTMLStr;
    }

    /***
     * this method accepts a string, it finds all the COMMANDS from the string.
     * It recognizes the link through the help of the regex.
     * then returns the COMMANDS
     *
     * @param text
     * @return
     */
    private static HashSet<String> pullCommands(String text) {

        HashSet<String> commands = new HashSet<String>();
        String regex = "--[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();
            commands.add(urlStr);
        }

        String regex_num = "--[0-9][0-9][0-9]";
        Pattern paa = Pattern.compile(regex_num);
        Matcher maa = paa.matcher(text);
        while (maa.find()) {
            String urlStrrr = maa.group();
            commands.add(urlStrrr);
        }
        return commands;
    }

    /**
     * this method takes in 2 parameters:
     * 1.) a string which is the absolute pathway of the .html file.
     * 2.) an in for the statcode, if the user wants to search for a specific status code.
     * <p>
     * this method reads from the *.html file then store all its contents in a string.
     * The string then is processed by the use of the pullLinks() method to take all of the links and be placed inside a HashSet.
     * Then each of the link will be passed into the AppleCode() method to check all the Status code.
     * then it prints out the link with its status code.
     * <p>
     * Each status code has their own color:
     * = RED      = Bad Apples with status code 404 or 400
     * = GREEN    = Good Apples with status code 200
     * = YELLOW     = Unknown Apples with other status code
     *
     * @param file
     * @param statcode
     */
    private static void classifyingHTML(String file, int statcode) {
        //if user puts more urls to check
        try {
            //reads from the *.html file then store all its contents in a string
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String inputLine;
            StringBuilder a = new StringBuilder();

            //reading all the content of the HTML
            while ((inputLine = reader.readLine()) != null)
                a.append(inputLine);
            String currentLine = reader.readLine();
            reader.close();
            String content = a.toString();

            //string then is processed by the use of the pullLinks() method to take all of the links and be placed inside a HashSet.
            HashSet<String> aLink = pullLinks(content);
            System.out.println("\nTotal Apple count: " + aLink.size());

            //counters for good, bad, unknown links
            int Gcounter = 0, Bcounter = 0, Ucounter = 0;

            //if the user wants the specific status code of 200, it will only print the links with status code 200
            if (statcode == 200) {
                System.out.println("Finding Apples with Status " + statcode);

                for (String s : aLink) {
                    int code = AppleCode(s);
                    if (code == statcode) {
                        System.out.println("[ " + statcode + " ]   GOOD APPLE    : " + s); //green
                        Gcounter++;
                    }
                }
                //if the user wants the specific status code of 400 or 404, it will only print the links with status code 400 or 404
            } else if (statcode == 400 || statcode == 404) {
                System.out.println("Finding Apples with Status " + statcode);

                for (String s : aLink) {
                    int code = AppleCode(s);
                    if (code == statcode) {
                        System.out.println("[ " + statcode + " ]   BAD APPLE     : " + s); //red
                        Bcounter++;
                    }
                }
                //if the user wants the specific status code of 400 or 404, it will only print the links with status code 400 or 404
            } else if (statcode == 499) {
                System.out.println("Finding Apples with Status 404 or 400");

                for (String s : aLink) {
                    int code = AppleCode(s);
                    if (code == 404 || code == 400) {
                        System.out.println("[ " + statcode + " ]   BAD APPLE     : " + s); //red
                        Bcounter++;
                    }
                }
                //if the user does not have any specific status code, it will print all the links
            } else if (statcode == unknown) {
                for (String s : aLink) {
                    int code = AppleCode(s);

                    if (code == 400 || code == 404) {
                        System.out.println(RED + "[ " + code + " ]   BAD APPLE     : " + s + RESET); //red
                        Bcounter++;
                    } else if (code == 200) {
                        System.out.println(GREEN + "[ " + code + " ]   GOOD APPLE    : " + s + RESET); //green
                        Gcounter++;
                    } else if (code == 0) {
                        System.out.println(YELLOW + "[ ??? ]   UNKNOWN APPLE : " + s + RESET); //yellow
                        Ucounter++;
                    } else {
                        System.out.println(YELLOW + "[ " + code + " ]   UNKNOWN APPLE : " + s + RESET);//white
                        Ucounter++;
                    }
                }
                //if user wants any other Status Code
            } else {
                System.out.println("Finding Apples with Status " + statcode);

                for (String s : aLink) {
                    int code = AppleCode(s);
                    if (code == statcode) {
                        System.out.println("[ " + statcode + " ]   UNRIPE APPLE  : " + s); // pink
                        Ucounter++;
                    }
                }
            }
            bappleResults(Gcounter, Bcounter, Ucounter);

        } catch (Exception e) {
            System.out.println("\n\nYou gave me a Bad Apple Tree: " + e + "[" + file + "]");
        }
    }

    /**
     * this method takes in 2 parameters:
     * 1.) a HashSet that contains url to be checked.
     * 2.) an in for the statcode, if the user wants to search for a specific status code.
     * <p>
     * this method reads from the url then store all its html resource contents in a string.
     * The string then is processed by the use of the pullLinks() method to take all of the links and be placed inside a HashSet.
     * Then each of the link will be passed into the AppleCode() method to check all the Status code.
     * then it prints out the link with its status code.
     * <p>
     * Each status code has their own color:
     * = RED      = Bad Apples with status code 404 or 400
     * = GREEN    = Good Apples with status code 200
     * = YELLOW     = Unknown Apples with other status code
     *
     * @param file
     * @param statcode
     */
    private static void classifyingApples(HashSet<String> m_url, int statcode) {
        //if user puts more urls to check
        for (String multi_link : m_url) {
            System.out.println("\n[ Counting Apples at " + multi_link + " ]");

            try {
                //connecting to myUrl
                //Document myDoc = Jsoup.connect(myUrl).get();
                URL URL = new URL(multi_link);
                URLConnection myURLConnection = URL.openConnection();
                myURLConnection.connect();

                //read the html from the url
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        myURLConnection.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();

                //the contents of the html is now in one String
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();

                String content = a.toString();

                //string then is processed by the use of the pullLinks() method to take all of the links and be placed inside a HashSet.
                HashSet<String> aLink = pullLinks(content);
                System.out.println("\nTotal Apple count: " + aLink.size());

                //counters for good, bad, unknown links
                int Gcounter = 0, Bcounter = 0, Ucounter = 0;

                //if the user wants the specific status code of 200, it will only print the links with status code 200
                if (statcode == 200) {
                    System.out.println("Finding Apples with Status " + statcode);

                    for (String s : aLink) {
                        int code = AppleCode(s);
                        if (code == statcode) {
                            System.out.println("[ " + statcode + " ]   GOOD APPLE    : " + s);
                            Gcounter++;
                        }
                    }
                } else if (statcode == 499) {
                    System.out.println("Finding Apples with Status 404 or 400");

                    for (String s : aLink) {
                        int code = AppleCode(s);
                        if (code == 400 || code == 404) {
                            System.out.println("[ " + code + " ]   BAD APPLE     : " + s);
                            Bcounter++;
                        }
                    }
                    //if the user does not have any specific status code, it will print all the links
                } else if (statcode == unknown) {
                    for (String s : aLink) {
                        int code = AppleCode(s);

                        if (code == 400 || code == 404) {
                            System.out.println("[ " + code + " ]   BAD APPLE     : " + s);
                            Bcounter++;
                        } else if (code == 200) {
                            System.out.println("[ " + code + " ]   GOOD APPLE    : " + s);
                            Gcounter++;
                        } else if (code == 0) {
                            System.out.println("[ ??? ]   UNKNOWN APPLE : " + s);
                            Ucounter++;
                        } else {
                            System.out.println("[ " + code + " ]   UNKNOWN APPLE : " + s);
                            Ucounter++;
                        }
                    }
                    //if the user wants the specific status code of 400 or 404, it will only print the links with status code 400 or 404
                } else if (statcode == 400 || statcode == 404) {
                    System.out.println("Finding Apples with Status " + statcode);

                    for (String s : aLink) {
                        int code = AppleCode(s);
                        if (code == statcode) {
                            System.out.println("[ " + code + " ]   BAD APPLE     : " + s);
                            Bcounter++;
                        }
                    }
                    //if the user does not have any specific status code, it will print all the links
                } else if (statcode == unknown) {
                    for (String s : aLink) {
                        int code = AppleCode(s);

                        if (code == 400 || code == 404) {
                            System.out.println("[ " + code + " ]   BAD APPLE     : " + s);
                            Bcounter++;
                        } else if (code == 200) {
                            System.out.println("[ " + code + " ]   GOOD APPLE    : " + s);
                            Gcounter++;
                        } else if (code == 0) {
                            System.out.println("[ ??? ]   UNKNOWN APPLE : " + s);
                            Ucounter++;
                        } else {
                            System.out.println("[ " + code + " ]   UNKNOWN APPLE : " + s);
                            Ucounter++;
                        }
                    }
                    //if user wants any other Status Code
                } else {
                    System.out.println("Finding Apples with Status " + statcode);

                    for (String s : aLink) {
                        int code = AppleCode(s);
                        if (code == statcode) {
                            System.out.println("[ " + statcode + " ]   UNRIPE APPLE  : " + s); // pink
                            Ucounter++;
                        }
                    }
                }
                bappleResults(Gcounter, Bcounter, Ucounter);
            } catch (Exception e) {
                System.out.println("\n\nYou gave me a Bad Apple Tree: " + e + "\n" + multi_link.toString());
            }
        }
    }


    /***
     * This method is called when the user wants help, all commands is listed in here
     */
    private static void bappleHelp() {
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("- WELCOME TO BAPPLES! Finding bad apples from any LINKS or HTML files           -");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("-      --v or --version | to check the Bapple version                           -");
        System.out.println("-      --h or --help    | to check the Bapple help                              -");
        System.out.println("-      <filename>       | to validate links within a file                       -");
        System.out.println("-      --200            | to list urls with status code: SUCCESS                -");
        System.out.println("-      --400 or --404   | to list urls with status code: CLIENT ERRORS          -");
        System.out.println("-      --XXX            | to list urls with status code: UNKNOWNS               -");
        System.out.println("-      --secure         | to check URLS with http:// if they work with https:// -");
        System.out.println("-      --all            | to list urls with all status                          -");
        System.out.println("-      --good           | to list urls with good status code: 200               -");
        System.out.println("-      --bad            | to list urls with bad status code: 404 and 400        -");
        System.out.println("-      --i or --ignore  | to list urls except ignore url list                   -");
        System.out.println("---------------------------------------------------------------------------------");
    }

    /***
     * This method is called when the tool is finished counting the bapples
     *
     * @author Royce Ayroso-Ong
     *
     * @param goodCounter number of valid links
     * @param badCounter number of bad links
     * @param unknownCounter number of unknown links
     */
    private static void bappleResults(int goodCounter, int badCounter, int unknownCounter) {
        System.out.println("--------------------------------------------------------");
        System.out.println("          Done counting apples!");
        System.out.println(GREEN + "          Good apples:    " + goodCounter + RESET);
        System.out.println(RED + "          Bad apples:     " + badCounter + RESET);
        System.out.println(YELLOW + "          Unknown apples: " + unknownCounter + RESET);
        System.out.println("--------------------------------------------------------");
    }

    /***
     * This method displays an exit message
     *
     * @author Royce Ayroso-Ong
     */
    private static void bappleExitMessage() {
        System.out.println("     ****************************");
        System.out.println("     ****** Bapples is out ******");
        System.out.println("     ****************************");
    }
    
    /***
     * this method accepts a string, it finds all the *.txt from the string.
     * It recognizes the link .txt the help of the regex.
     * then returns the TXT FILE NAME
     * 
     * @author Eunbee Kim
     * @param text
     * @return
     */
    private static String pullTXT(String text) {
        String TXTStr = "";

        //regex to find the txt filename
        String regex = "[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|].txt";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
        	TXTStr = m.group();

            //return the html file name, if there is.
            return TXTStr;
        }
        //return an emtpy string
        return TXTStr;
    }
    
    /**
     * this method takes in 2 parameters:
     * It get the list of ignore from txt file, and the request url list are from html file.
     * the request url will get rid of the list of ignore urls. 
     * 
     * @author Eunbee Kim
     * @param txtFile
     * @param htmlFile
     * @param statcode
     */
    private static void classifyingTXT (String txtFile, String htmlFile, int statcode) {
    	HashSet<String> aLink = null;
    	HashSet<String> bLink = null;
        //if user puts more urls to check
        try {
            //reads from the *.txt file then store all its contents in a string
            BufferedReader reader = new BufferedReader(new FileReader(txtFile));
            String inputLine;
            StringBuilder a = new StringBuilder();

            //reading all the content of the TXT
            while ((inputLine = reader.readLine()) != null)
                a.append(inputLine);
            
            reader.close();
            String contentTXT = a.toString();

            //string then is processed by the use of the pullLinksExcept() method to take all of the links and be placed inside a HashSet.
            aLink = pullLinksExcept(contentTXT);
            
            //reads from the *.html file then store all its contents in a string
            BufferedReader readerHtml = new BufferedReader(new FileReader(htmlFile));
            String inputHtml;
            StringBuilder b = new StringBuilder();

            //reading all the content of the HTML
            while ((inputHtml = readerHtml.readLine()) != null)
                b.append(inputHtml);
            
            readerHtml.close();
            
            String contentHTML = b.toString();

            //string then is processed by the use of the pullLinks() method to take all of the links and be placed inside a HashSet.
            bLink = pullLinks(contentHTML);
            
            // string list has valid request urls applied ignored url set
            HashSet<String> cLink = ignoreUrl(bLink, aLink);

            System.out.println("\nTotal Apple count: " + cLink.size());

            //counters for good, bad, unknown links
            int Gcounter = 0, Bcounter = 0, Ucounter = 0;

            //if the user wants the specific status code of 200, it will only print the links with status code 200
            if (statcode == 200) {
                System.out.println("Finding Apples with Status " + statcode);

                for (String s : cLink) {
                    int code = AppleCode(s);
                    if (code == statcode) {
                        System.out.println("[ " + statcode + " ]   GOOD APPLE    : " + s); //green
                        Gcounter++;
                    }
                }
                //if the user wants the specific status code of 400 or 404, it will only print the links with status code 400 or 404
            } else if (statcode == 400 || statcode == 404) {
                System.out.println("Finding Apples with Status " + statcode);

                for (String s : cLink) {
                    int code = AppleCode(s);
                    if (code == statcode) {
                        System.out.println("[ " + statcode + " ]   BAD APPLE     : " + s); //red
                        Bcounter++;
                    }
                }
                //if the user wants the specific status code of 400 or 404, it will only print the links with status code 400 or 404
            } else if (statcode == 499) {
                System.out.println("Finding Apples with Status 404 or 400");

                for (String s : cLink) {
                    int code = AppleCode(s);
                    if (code == 404 || code == 400) {
                        System.out.println("[ " + statcode + " ]   BAD APPLE     : " + s); //red
                        Bcounter++;
                    }
                }
                //if the user does not have any specific status code, it will print all the links
            } else if (statcode == unknown) {
                for (String s : cLink) {
                    int code = AppleCode(s);

                    if (code == 400 || code == 404) {
                        System.out.println(RED + "[ " + code + " ]   BAD APPLE     : " + s + RESET); //red
                        Bcounter++;
                    } else if (code == 200) {
                        System.out.println(GREEN + "[ " + code + " ]   GOOD APPLE    : " + s + RESET); //green
                        Gcounter++;
                    } else if (code == 0) {
                        System.out.println(YELLOW + "[ ??? ]   UNKNOWN APPLE : " + s + RESET); //yellow
                        Ucounter++;
                    } else {
                        System.out.println(YELLOW + "[ " + code + " ]   UNKNOWN APPLE : " + s + RESET);//white
                        Ucounter++;
                    }
                }
                //if user wants any other Status Code
            } else {
                System.out.println("Finding Apples with Status " + statcode);

                for (String s : cLink) {
                    int code = AppleCode(s);
                    if (code == statcode) {
                        System.out.println("[ " + statcode + " ]   UNRIPE APPLE  : " + s); // pink
                        Ucounter++;
                    }
                }
            }
            bappleResults(Gcounter, Bcounter, Ucounter);

        } catch (Exception e) {
            System.out.println("\n\nYou gave me a Bad Apple Tree: " + e + "[" + txtFile + "]");
        }

    }
    
    
    /***
     * this method is saving only valid request urls to string set
     *
     * this method accepts a string, it finds invalid urls from html file.
     * then returns the LINKS
     * 
     * @author Eunbee Kim
     * @param text
     * @return
     */
    private static HashSet<String> ignoreUrl(HashSet<String> text, HashSet<String> ignore) {
        HashSet<String> links = new HashSet<String>();
               
        for(String url : text)
        {
        	 boolean ignorePass = true;
        	
        	  for(String ign : ignore)
        	  {
        		  if(url.contains(ign)|| url.startsWith(ign)) ignorePass = false; 
        	  }
        	  if(ignorePass) links.add(url);
        }
      
        return links;
    }

    /***
     * this method accepts a string, it finds all the links from the string.
     * It recognizes the link through the help of the regex.(# except)
     * then returns the LINKS
     *
     * @author Eunbee Kim
     * @param text
     * @return
     */
    private static HashSet<String> pullLinksExcept(String text) {
        HashSet<String> links = new HashSet<String>();

        //regex to find the link
        String regex = "(?:(?:https?|ftp)://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            //if a link is found, save it in the hashset
            links.add(urlStr);
        }
        return links;
    }
}