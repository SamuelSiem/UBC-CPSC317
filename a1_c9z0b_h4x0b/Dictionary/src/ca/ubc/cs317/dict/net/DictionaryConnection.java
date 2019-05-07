package ca.ubc.cs317.dict.net;

import ca.ubc.cs317.dict.exception.DictConnectionException;
import ca.ubc.cs317.dict.model.Database;
import ca.ubc.cs317.dict.model.Definition;
import ca.ubc.cs317.dict.model.MatchingStrategy;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Jonatan on 2017-09-09.
 */
public class DictionaryConnection {

    private static final int DEFAULT_PORT = 2628;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    private Map<String, Database> databaseMap = new LinkedHashMap<String, Database>();

    /** Establishes a new connection with a DICT server using an explicit host and port number, and handles initial
     * welcome messages.
     *
     * @param host Name of the host where the DICT server is running
     * @param port Port number used by the DICT server
     * @throws DictConnectionException If the host does not exist, the connection can't be established, or the messages
     * don't match their expected value.
     */
    public DictionaryConnection(String host, int port) throws DictConnectionException {
       
        try {
            this.socket = new Socket(host, port);
            this.output = new PrintWriter(this.socket.getOutputStream());
            this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String welcomeMsg = this.input.readLine();
            String[] welcomeMsgArr = welcomeMsg.split(" ");
            if (!welcomeMsgArr[0].equals("220")) {
                this.close();
            }
        } catch (IOException e) {
            System.out.println("error");
            throw new DictConnectionException("Dict Connection Error");
        } 

    }

    /** Establishes a new connection with a DICT server using an explicit host, with the default DICT port number, and
     * handles initial welcome messages.
     *
     * @param host Name of the host where the DICT server is running
     * @throws DictConnectionException If the host does not exist, the connection can't be established, or the messages
     * don't match their expected value.
     */
    public DictionaryConnection(String host) throws DictConnectionException {
        this(host, DEFAULT_PORT);
    }

    /** Sends the final QUIT message and closes the connection with the server. This function ignores any exception that
     * may happen while sending the message, receiving its reply, or closing the connection.
     *
     */
    public synchronized void close() {
        try {
            this.output.println("QUIT");
            this.output.flush();
            String firstLine;
            while(!(firstLine = this.input.readLine()).split(" ")[0].equals("221")){
                //keep skipping until found correct status code
            }
            System.out.println(firstLine);
            this.input.close();
            this.output.close();
            this.socket.close();
        } catch(IOException e){
                e.printStackTrace();
        }
    }

    /** Requests and retrieves all definitions for a specific word.
     *
     * @param word The word whose definition is to be retrieved.
     * @param database The database to be used to retrieve the definition. A special database may be specified,
     *                 indicating either that all regular databases should be used (database name '*'), or that only
     *                 definitions in the first database that has a definition for the word should be used
     *                 (database '!').
     * @return A collection of Definition objects containing all definitions returned by the server.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Collection<Definition> getDefinitions(String word, Database database) throws DictConnectionException {
        Collection<Definition> set = new ArrayList<>();
        getDatabaseList(); // Ensure the list of databases has been populated
        try {
            this.output.println("DEFINE " + database.getName() + " " + word);
            this.output.flush();
            String firstLine;
            while(!(firstLine = this.input.readLine()).split(" ")[0].equals("150")) {
                String status = firstLine.split(" ")[0];
                if (status.equals("550") || status.equals("551") || status.equals("552") || status.equals("501")) {
                    return set;
                }
            }
            String reply = this.input.readLine();
            String[] replyArr = reply.split(" ",4);
            Definition defn = new Definition(word, databaseMap.get(replyArr[2]));
            reply = this.input.readLine();
            replyArr = reply.split(" ", 4);
            while(reply != null){
                if(replyArr[0].equals("250")){
                    break;
                }
                if(replyArr[0].equals("151")){
                    set.add(defn);
                    defn = new Definition(word, databaseMap.get(replyArr[2]));
                    reply = this.input.readLine();
                    replyArr = reply.split(" ",4);
                }else {
                    if(!reply.equals(".")) {
                        defn.appendDefinition(reply);
                    }
                    reply = this.input.readLine();
                    replyArr = reply.split(" ", 4);
                }
            }
            set.add(defn);

        } catch (Exception e) {
            throw new DictConnectionException();
        }
        return set;
    }

    /** Requests and retrieves a list of matches for a specific word pattern.
     *
     * @param word     The word whose definition is to be retrieved.
     * @param strategy The strategy to be used to retrieve the list of matches (e.g., prefix, exact).
     * @param database The database to be used to retrieve the definition. A special database may be specified,
     *                 indicating either that all regular databases should be used (database name '*'), or that only
     *                 matches in the first database that has a match for the word should be used (database '!').
     * @return A set of word matches returned by the server.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Set<String> getMatchList(String word, MatchingStrategy strategy, Database database) throws DictConnectionException {
        Set<String> set = new LinkedHashSet<>();

        try {

            this.output.println("MATCH " + database.getName() + " " + strategy.getName() + " " + word);
            this.output.flush();
            String firstLine;
            while (!(firstLine = this.input.readLine()).split(" ")[0].equals("152")) {
                String status = firstLine.split(" ")[0];
                if (status.equals("550") || status.equals("551") || status.equals("552") || status.equals("501")) {
                    set.add(firstLine);
                    return set;
                }
            }
            String[] str = firstLine.split(" ");
            Stream<String> lines = this.input.lines().limit(Integer.parseInt(str[1]));
            Object[] arr = lines.toArray();
            for (int i = 0; i < arr.length; i++) {
                set.add(arr[i].toString().split(" ", 2)[1]);
            }
        } catch (IOException e) {
            throw new DictConnectionException();
        }

        return set;
    }

    /** Requests and retrieves a list of all valid databases used in the server. In addition to returning the list, this
     * method also updates the local databaseMap field, which contains a mapping from database name to Database object,
     * to be used by other methods (e.g., getDefinitionMap) to return a Database object based on the name.
     *
     * @return A collection of Database objects supported by the server.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Collection<Database> getDatabaseList() throws DictConnectionException {

        if (!databaseMap.isEmpty()) return databaseMap.values();

        try {
            this.output.println("SHOW DB");
            this.output.flush();
            String firstLine = this.input.readLine();
            String[] str = firstLine.split(" ");
            Stream<String> lines = this.input.lines().limit(Integer.parseInt(str[1]));
            Object[] arr = lines.toArray();
            for (int i = 0; i < arr.length; i++) {
                String[] dbData = arr[i].toString().split(" ", 2);
                databaseMap.put(dbData[0], new Database(dbData[0], dbData[1]));
            }
        } catch (IOException e) {
            throw new DictConnectionException();
        }

        return databaseMap.values();
    }

    /** Requests and retrieves a list of all valid matching strategies supported by the server.
     *
     * @return A set of MatchingStrategy objects supported by the server.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Set<MatchingStrategy> getStrategyList() throws DictConnectionException {
        Set<MatchingStrategy> set = new LinkedHashSet<>();

        try{
            this.output.println("SHOW STRAT");
            this.output.flush();
            String firstLine;
            while(!(firstLine = this.input.readLine()).split(" ")[0].equals("111")) {
                String status = firstLine.split(" ")[0];
                if (status.equals("550") || status.equals("551") || status.equals("552") || status.equals("501")) {
                    return set;
                }
            }
            String[] str = firstLine.split(" ");
            Stream<String> lines = this.input.lines().limit(Integer.parseInt(str[1]));
            Object[] arr = lines.toArray();
            for (int i = 0; i < arr.length; i++) {
                String[] strat = arr[i].toString().split(" ", 2);
                set.add(new MatchingStrategy(strat[0], strat[1]));
            }

        }catch(IOException e){
            throw new DictConnectionException();
        }

        return set;
    }

}
