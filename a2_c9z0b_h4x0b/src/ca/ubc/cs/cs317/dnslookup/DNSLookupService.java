package ca.ubc.cs.cs317.dnslookup;

import java.io.Console;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.*;
import java.net.*;
import java.util.*;

public class DNSLookupService {

    private static final int DEFAULT_DNS_PORT = 53;
    private static final int MAX_INDIRECTION_LEVEL = 10;

    private static InetAddress rootServer;
    private static boolean verboseTracing = false;
    private static DatagramSocket socket;

    private static DNSCache cache = DNSCache.getInstance();

    private static Random random = new Random();

    /**
     * Main function, called when program is first invoked.
     *
     * @param args list of arguments specified in the command line.
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Invalid call. Usage:");
            System.err.println("\tjava -jar DNSLookupService.jar rootServer");
            System.err.println("where rootServer is the IP address (in dotted form) of the root DNS server to start the search at.");
            System.exit(1);
        }

        try {
            rootServer = InetAddress.getByName(args[0]);
            System.out.println("Root DNS server is: " + rootServer.getHostAddress());
        } catch (UnknownHostException e) {
            System.err.println("Invalid root server (" + e.getMessage() + ").");
            System.exit(1);
        }

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
        } catch (SocketException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        Scanner in = new Scanner(System.in);
        Console console = System.console();
        do {
            // Use console if one is available, or standard input if not.
            String commandLine;
            if (console != null) {
                System.out.print("DNSLOOKUP> ");
                commandLine = console.readLine();
            } else
                try {
                    commandLine = in.nextLine();
                } catch (NoSuchElementException ex) {
                    break;
                }
            // If reached end-of-file, leave
            if (commandLine == null) break;

            // Ignore leading/trailing spaces and anything beyond a comment character
            commandLine = commandLine.trim().split("#", 2)[0];

            // If no command shown, skip to next command
            if (commandLine.trim().isEmpty()) continue;

            String[] commandArgs = commandLine.split(" ");

            if (commandArgs[0].equalsIgnoreCase("quit") ||
                    commandArgs[0].equalsIgnoreCase("exit"))
                break;
            else if (commandArgs[0].equalsIgnoreCase("server")) {
                // SERVER: Change root nameserver
                if (commandArgs.length == 2) {
                    try {
                        rootServer = InetAddress.getByName(commandArgs[1]);
                        System.out.println("Root DNS server is now: " + rootServer.getHostAddress());
                    } catch (UnknownHostException e) {
                        System.out.println("Invalid root server (" + e.getMessage() + ").");
                        continue;
                    }
                } else {
                    System.out.println("Invalid call. Format:\n\tserver IP");
                    continue;
                }
            } else if (commandArgs[0].equalsIgnoreCase("trace")) {
                // TRACE: Turn trace setting on or off
                if (commandArgs.length == 2) {
                    if (commandArgs[1].equalsIgnoreCase("on"))
                        verboseTracing = true;
                    else if (commandArgs[1].equalsIgnoreCase("off"))
                        verboseTracing = false;
                    else {
                        System.err.println("Invalid call. Format:\n\ttrace on|off");
                        continue;
                    }
                    System.out.println("Verbose tracing is now: " + (verboseTracing ? "ON" : "OFF"));
                } else {
                    System.err.println("Invalid call. Format:\n\ttrace on|off");
                    continue;
                }
            } else if (commandArgs[0].equalsIgnoreCase("lookup") ||
                    commandArgs[0].equalsIgnoreCase("l")) {
                // LOOKUP: Find and print all results associated to a name.
                RecordType type;
                if (commandArgs.length == 2)
                    type = RecordType.A;
                else if (commandArgs.length == 3)
                    try {
                        type = RecordType.valueOf(commandArgs[2].toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Invalid query type. Must be one of:\n\tA, AAAA, NS, MX, CNAME");
                        continue;
                    }
                else {
                    System.err.println("Invalid call. Format:\n\tlookup hostName [type]");
                    continue;
                }
                findAndPrintResults(commandArgs[1], type);
            } else if (commandArgs[0].equalsIgnoreCase("dump")) {
                // DUMP: Print all results still cached
                cache.forEachNode(DNSLookupService::printResults);
            } else {
                System.err.println("Invalid command. Valid commands are:");
                System.err.println("\tlookup fqdn [type]");
                System.err.println("\ttrace on|off");
                System.err.println("\tserver IP");
                System.err.println("\tdump");
                System.err.println("\tquit");
                continue;
            }

        } while (true);

        socket.close();
        System.out.println("Goodbye!");
    }

    /**
     * Finds all results for a host name and type and prints them on the standard output.
     *
     * @param hostName Fully qualified domain name of the host being searched.
     * @param type     Record type for search.
     */
    private static void findAndPrintResults(String hostName, RecordType type) {

        DNSNode node = new DNSNode(hostName, type);
        printResults(node, getResults(node, 0));
    }

    /**
     * Finds all the result for a specific node.
     *
     * @param node             Host and record type to be used for search.
     * @param indirectionLevel Control to limit the number of recursive calls due to CNAME redirection.
     *                         The initial call should be made with 0 (zero), while recursive calls for
     *                         regarding CNAME results should increment this value by 1. Once this value
     *                         reaches MAX_INDIRECTION_LEVEL, the function prints an error message and
     *                         returns an empty set.
     * @return A set of resource records corresponding to the specific query requested.
     */
    private static Set<ResourceRecord> getResults(DNSNode node, int indirectionLevel) {

        if (indirectionLevel > MAX_INDIRECTION_LEVEL) {
            System.err.println("Maximum number of indirection levels reached.");
            return Collections.emptySet();
        }

        // TODO To be completed by the student

        InetAddress server = rootServer;
        if(cache.getCachedResults(node).isEmpty()) {//node is not in the cache
            retrieveResultsFromServer(node, server);
        }

        return cache.getCachedResults(node);
    }

    /**
     * Retrieves DNS results from a specified DNS server. Queries are sent in iterative mode,
     * and the query is repeated with a new server if the provided one is non-authoritative.
     * Results are stored in the cache.
     *
     * @param node   Host name and record type to be used for the query.
     * @param server Address of the server to be used for the query.
     */
    private static void retrieveResultsFromServer(DNSNode node, InetAddress server) {

        // TODO To be completed by the student
            //connect socket
            socket.connect(server, DEFAULT_DNS_PORT);

            //send packet
            sendPacket(node, server);

            //receive packet
            receivePacket(node, server);
    }

    private static void verbosePrintResourceRecord(ResourceRecord record, int rtype) {
        if (verboseTracing)
            System.out.format("       %-30s %-10d %-4s %s\n", record.getHostName(),
                    record.getTTL(),
                    record.getType() == RecordType.OTHER ? rtype : record.getType(),
                    record.getTextResult());
    }

    /**
     * Prints the result of a DNS query.
     *
     * @param node    Host name and record type used for the query.
     * @param results Set of results to be printed for the node.
     */
    private static void printResults(DNSNode node, Set<ResourceRecord> results) {
        if (results.isEmpty())
            System.out.printf("%-30s %-5s %-8d %s\n", node.getHostName(),
                    node.getType(), -1, "0.0.0.0");
        for (ResourceRecord record : results) {
            System.out.printf("%-30s %-5s %-8d %s\n", node.getHostName(),
                    node.getType(), record.getTTL(), record.getTextResult());
        }
    }

    /**
     * @param node   Host name and record type to be used for the query.
     * @param server Address of the server to be used for the query.
     */
    private static void sendPacket(DNSNode node, InetAddress server) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            //int queryID = random.nextInt();
            dos.writeShort(random.nextInt()); // Query ID
            //System.out.println("QueryID " + queryID + node.getHostName() + " " + node.getType() + " --> " + server);
            dos.writeShort(0x0100); // Write Query Flags
            dos.writeShort(0x0001); // Question Count
            dos.writeShort(0x0000); // Answer count
            dos.writeShort(0x0000); // Authority Record count
            dos.writeShort(0x0000); // Additional Record count
            String[] domainParts = node.getHostName().split("\\.");
            for (int i = 0; i < domainParts.length; i++) {
                byte[] domainBytes = domainParts[i].getBytes("UTF-8");
                dos.writeByte(domainBytes.length);
                dos.write(domainBytes);
            }

            dos.writeByte(0x00); // Ending the sequence
            dos.writeShort(node.getType().getCode()); // Type 0x01 = A (Host Request)
            dos.writeShort(0x0001); // Class 0x01 = IN
            byte[] sendData = baos.toByteArray(); //put it together
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, server, socket.getPort()); //put it in a packet
            socket.send(sendPacket); // send the data
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void receivePacket(DNSNode node, InetAddress server) {
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            //header
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(receiveData));
            short transactionID = din.readShort();
            short flags = din.readShort();
            short questionCount = din.readShort();
            short answerCount = din.readShort();
            short authRRCount = din.readShort();
            short addRRCount = din.readShort();
            while (questionCount != 0) {
                int queryLen;
                while ((queryLen = din.readByte()) > 0) {
                    byte[] record = new byte[queryLen];
                    for (int i = 0; i < queryLen; i++) {
                        record[i] = din.readByte();
                    }
                    //System.out.println("Query record = " + new String(record, "UTF-8"));
                }
                short queryType = din.readShort();
                RecordType qType = RecordType.getByCode(queryType);
                short queryClass = din.readShort();
                questionCount--;
            }
            if(verboseTracing){
                System.out.println("Respond ID " + transactionID);
                System.out.println("Answers (" +answerCount+ ")");
            }

            //answer
            if(verboseTracing) {
                System.out.println("Nameservers (" + authRRCount + ")");
            }
            //decodeRR(din,authRRCount,receiveData,node, server); //decoding authorativeRR
            boolean isNS = false;
            boolean gotAns = false;
            try {
                while(answerCount > 0){
                    String name ="";
                    byte pointer = din.readByte();
                    if((int) pointer >> 4 == -4) {
                        byte location = din.readByte();
                        name = findDomainName(receiveData, pointer, location);
                    }else{
                        while(pointer > 0) {
                            if((int) pointer >> 4 == -4) {
                                byte location = din.readByte();
                                name = name + "." + findDomainName(receiveData, pointer, location);
                            }
                            byte[] record = new byte[pointer];
                            for (int i = 0; i < pointer; i++) {
                                record[i] = din.readByte();
                            }
                            if (name.equals("")) {
                                name = name + new String(record, "UTF-8");
                            } else {
                                name = name + "." + new String(record, "UTF-8");
                            }
                            pointer = din.readByte();
                        }
                    }
                    short answerType = din.readShort();
                    RecordType aType = RecordType.getByCode(answerType);
                    short answerClass = din.readShort();
                    int TTL = din.readInt();
                    short addrLen = din.readShort();
                    String address = "";
                    if (answerType == (byte) 0x01) {//it is an IPv4 format address
                        for (int i = 0; i < addrLen; i++) {
                            String helper1 = String.format("%d", (din.readByte() & 0xFF));
                            address = address + helper1;
                            if (i != addrLen - 1) {
                                address = address + ".";
                            }
                        }
                    } else if (answerType == (byte) 0x1c) {//it is an IPv6 format address
                        for (int i = 0; i < addrLen; i++) {
                            String helper1 = String.format("%02x", din.readByte());
                            address = address + helper1;
                            if (i % 2 == 1 && i != addrLen-1) {
                                address = address + ":";
                            }
                        }
                    }
                    answerCount--;
                    ResourceRecord newRR = new ResourceRecord(name, aType, TTL, address);
                    cache.addResult(newRR);
                    verbosePrintResourceRecord(newRR,(int) answerType);
                    gotAns = true;
                }
                while (authRRCount > 0) {
                    String name = "";
                    byte pointer = din.readByte();
                    if((int) pointer >>4 == -4) {
                        byte location = din.readByte();
                        name = findDomainName(receiveData, pointer, location);
                    }else{
                        while(pointer > 0) {
                            byte[] record = new byte[pointer];
                            for (int i = 0; i < pointer ; i++) {
                                record[i] = din.readByte();
                            }
                            if (name.equals("")) {
                                name = name + new String(record, "UTF-8");
                            } else {
                                name = name + "." + new String(record, "UTF-8");
                            }
                            pointer = din.readByte();
                        }
                    }
                    short answerType = din.readShort();
                    RecordType aType = RecordType.getByCode(answerType);
                    short answerClass = din.readShort();
                    int TTL = din.readInt();
                    short addrLen = din.readShort();
                    String address = "";
                    if (answerType == (byte) 0x01) {//it is an IPv4 format address
                        for (int i = 0; i < addrLen; i++) {
                            String helper1 = String.format("%d", (din.readByte() & 0xFF));
                            address = address + helper1;
                            if (i != addrLen - 1) {
                                address = address + ".";
                            }
                        }
                    } else if (answerType == (byte) 0x1c) {//it is an IPv6 format address
                        for (int i = 0; i < addrLen; i++) {
                            String helper1 = String.format("%02x", din.readByte());
                            address = address + helper1;
                            if (i % 2 == 1 && i != addrLen-1) {
                                address = address + ":";
                            }
                        }
                    } else { //it is not an IP address
                        if(answerType == (byte) 0x02) { // it is a NS
                            byte recLen = din.readByte();
                            addrLen--;
                            while (addrLen > 0) {
                                if ((int) recLen >> 4 == -4) { //pointer
                                    byte location2 = din.readByte();
                                    address = address + "." + findDomainName(receiveData, recLen, location2);
                                    addrLen--;
                                } else {
                                    while ((recLen > 0)) {
                                        byte[] record = new byte[recLen];
                                        for (int i = 0; i < recLen; i++) {
                                            record[i] = din.readByte();
                                            addrLen--;
                                        }
                                        if (address.equals("")) {
                                            address = address + new String(record, "UTF-8");
                                        } else {
                                            address = address + "." + new String(record, "UTF-8");
                                        }
                                        recLen = din.readByte();
                                        addrLen--;
                                    }
                                }
                            }
                            isNS = true;
                        }
                    }
                    authRRCount--;
                    ResourceRecord newRR = new ResourceRecord(name, aType, TTL, address);
                    cache.addResult(newRR);
                    verbosePrintResourceRecord(newRR,(int) answerType);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            if(verboseTracing) {
                System.out.println("Additional Information (" + addRRCount + ")");
            }
            //decodeRR(din,addRRCount,receiveData,node, server); //decoding addRR
            String newServer = server.getHostName();
            boolean nsisEmpty = isNS;
            try {
                while (addRRCount > 0) {
                    byte pointer = din.readByte();
                    byte location = din.readByte();
                    String name = findDomainName(receiveData, pointer, location);
                    short answerType = din.readShort();
                    RecordType aType = RecordType.getByCode(answerType);
                    short answerClass = din.readShort();
                    int TTL = din.readInt();
                    short addrLen = din.readShort();
                    String address = "";
                    if (answerType == (byte) 0x01) {//it is an IPv4 format address
                        for (int i = 0; i < addrLen; i++) {
                            String helper1 = String.format("%d", (din.readByte() & 0xFF));
                            address = address + helper1;
                            if (i != addrLen - 1) {
                                address = address + ".";
                            }
                        }
                        if(nsisEmpty){
                            newServer = address;
                            nsisEmpty = false;
                            //System.out.println("New Server = " + newServer);
                        }
                    } else if (answerType == (byte) 0x1c) {//it is an IPv6 format address
                        for (int i = 0; i < addrLen; i++) {
                            String helper1 = String.format("%02x", din.readByte());
                            address = address + helper1;
                            if (i % 2 == 1 && i != addrLen-1) {
                                address = address + ":";
                            }
                        }
                    } else { //it is not an IP address
                        //if(answerType == (byte) 0x02) { // it is a NS
                        byte recLen = din.readByte();
                        addrLen--;
                        while (addrLen > 0) {
                            if ((int) recLen >> 4 == -4) { //pointer
                                byte location2 = din.readByte();
                                address = address + "." + findDomainName(receiveData, recLen, location2);
                                addrLen--;
                            }else {
                                while ((recLen > 0)) {
                                    byte[] record = new byte[recLen];
                                    for (int i = 0; i < recLen; i++) {
                                        record[i] = din.readByte();
                                        addrLen--;
                                    }
                                    if (address.equals("")) {
                                        address = address + new String(record, "UTF-8");
                                    } else {
                                        address = address + "." + new String(record, "UTF-8");
                                    }
                                    recLen = din.readByte();
                                    addrLen--;
                                }
                            }
                        }
                    }
                    addRRCount--;
                    ResourceRecord newRR = new ResourceRecord(name, aType, TTL, address);
                    cache.addResult(newRR);
                    verbosePrintResourceRecord(newRR,(int) answerType);

                }
                if(isNS && !gotAns){
                    retrieveResultsFromServer(node, InetAddress.getByName(newServer));
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static void decodeRR(DataInputStream din, int RRCount, byte[] receiveData, DNSNode node, InetAddress server){
//        try {
//            while (RRCount > 0) {
//                byte pointer = din.readByte();
//                byte location = din.readByte();
//                String name = findDomainName(receiveData,pointer,location);
//                short answerType = din.readShort();
//                RecordType aType = RecordType.getByCode(answerType);
//                short answerClass = din.readShort();
//                int TTL = din.readInt();
//                short addrLen = din.readShort();
//                String address = "";
//                if (answerType == (byte) 0x01) {//it is an IPv4 format address
//                    for (int i = 0; i < addrLen; i++) {
//                        String helper1 = String.format("%d", (din.readByte() & 0xFF));
//                        address = address + helper1;
//                        if (i != addrLen - 1) {
//                            address = address + ".";
//                        }
//                    }
//                } else if (answerType == (byte) 0x1c) {//it is an IPv6 format address
//                    for (int i = 0; i < addrLen; i++) {
//                        String helper1 = String.format("%02x", din.readByte());
//                        address = address + helper1;
//                        if (i % 2 == 1 && i != addrLen-1) {
//                            address = address + ":";
//                        }
//                    }
//                } else { //it is not an IP address
//                    //if(answerType == (byte) 0x02) { // it is a NS
//                        byte recLen = din.readByte();
//                        addrLen--;
//                        while (addrLen > 0) {
//                            if ((int) recLen >> 4 == -4) { //pointer
//                                byte location2 = din.readByte();
//                                address = address + "." + findDomainName(receiveData, recLen, location2);
//                                addrLen--;
//                            }else {
//                                while ((recLen > 0)) {
//                                    byte[] record = new byte[recLen];
//                                    for (int i = 0; i < recLen; i++) {
//                                        record[i] = din.readByte();
//                                        addrLen--;
//                                    }
//                                    if (address.equals("")) {
//                                        address = address + new String(record, "UTF-8");
//                                    } else {
//                                        address = address + "." + new String(record, "UTF-8");
//                                    }
//                                    recLen = din.readByte();
//                                    addrLen--;
//                                }
//                            }
//                    }
//                }
//                RRCount--;
//                ResourceRecord newRR = new ResourceRecord(name, aType, TTL, address);
//                cache.addResult(newRR);
//                verbosePrintResourceRecord(newRR,(int) answerType);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * @param packet       The DNS response
     * @param pointerPart1 1st part of the pointer
     * @param pointerPart2 2nd part of the pointer
     * @return string  The domain name
     */
    private static String findDomainName(byte[] packet, byte pointerPart1, byte pointerPart2) {
        int offset1 = ((int) pointerPart1 & 0x0f) << 8 ;
        int offset2 = Integer.valueOf(String.format("%x", pointerPart2), 16);
        int offset = offset1 + offset2;
        if (offset < 0) {
            offset *= -1;
        }
        String res = "";
        int curr = offset;
        int firstHex = packet[curr] >> 4;
        if (firstHex == -4) {
            return findDomainName(packet, packet[offset], packet[offset + 1]);
        } else {
            while (packet[curr] != 0) {
                if ((int) packet[curr] >> 4 == -4){
                    res += "." + findDomainName(packet, packet[curr], packet[curr+1]);
                    break;
                }
                else {
                    if (packet[curr] < 0x0f) {
                        res = res + ".";
                    } else {
                        res = res + ((char) (int) packet[curr]);
                    }
                }
                curr++;

            }
            return res.substring(1);
        }
    }


}