package team3.src.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import static java.lang.System.out;
/**
 * 
 */
public class ConfigData{
	/** The ConfigData singleton */
	private static ConfigData singleton = null;
	/** The map that holds the key-value pairs obtained from config.ini **/
	private LinkedHashMap<String, String> configData;
	
	/**
	 * Singleton Method for default ConfigData object
	 * @return Copy of ConfigData singleton.
	 * @throws IOException
	 */
	public static ConfigData getConfigData() throws IOException{
		return (singleton == null)? generateConfigData(): singleton;
	}
	
	public int getSize(){
	    return this.configData.size();
	}
	
	/**
	 * Creates a special ConfigData object based on an external config.ini file
	 * @param filename The absolute path to the external config.ini file
	 * @return new ConfigData Object
	 * @throws IOException
	 */
	public static ConfigData overrideConfigData(String filename) throws IOException{ return generateConfigData(filename); }
	/**
	 * Grabs the value associated with a key from ConfigData
	 * @param key The identifier of the value we are requesting
	 * @return the value
	 */
	public String get(String key){ return (configData.containsKey(key))?configData.get(key):null; }
	
	/**
	 * Adds entry to ConfigData as a key, value pair
	 * @param key Identifier
	 * @param value Value associated with identifier
	 */
	public void set(String key, String value){ configData.put(key, value); }
	
	public void removeServer(String host, int port){
	    Iterator<Entry<String, String>> iter = configData.entrySet().iterator();
	    int num = 0;
	    boolean match = false;
	    while(iter.hasNext()){
	        Entry<String, String> pair = iter.next();
	        
	        out.format("Key:%s .Host: %s.\n", pair.getKey(), pair.getValue());
	        if(pair.getKey().startsWith("server-hostname")){
	            num = Integer.parseInt(Character.toString((pair.getKey().toCharArray()[pair.getKey().length()-1])));
	            out.println(host.length());
	            
	            out.println(pair.getValue().length());
	            out.println(String.format("Config.ini hostname: %s. getHostname's hostname: %s", host, pair.getValue()));
	            if(pair.getValue().compareTo(host) == 0){
	                out.println(configData.get("server-port"+num));
	                out.println(port);
	                if(Integer.parseInt(configData.get("server-port"+num)) == port){
	                    out.println(match);
	                    match = true;
	                    break;
	                }
	            }else out.println("Not equal...");
	        }
	    }
	    if(match){
	        configData.remove("server-hostname"+num);
	        configData.remove("server-port"+num);
	    } 
	}
	
	
	public HashMap<String, Integer> getServerPorts(){
	    HashMap<String, Integer> servers=new HashMap<String, Integer>();
	    Iterator<Entry<String, String>> iter =configData.entrySet().iterator();
	    int num = 0;
	    while(iter.hasNext()){
	        Entry<String, String> pair = iter.next();
	        if(pair.getKey().startsWith("server-hostname")){
	            num = Integer.parseInt(Character.toString((pair.getKey().toCharArray()[pair.getKey().length()-1])));
	            
	            servers.put(pair.getValue(), Integer.parseInt(configData.get("server-port"+num)));
	        }
	    }
	    return servers;
	}
	
	
	private ConfigData(){ configData = new LinkedHashMap<String, String>(); }
	
	/**
	 * Method that generates a ConfigData object based on a given ini filename
	 * @param filename Absolute path of file we will generate this object from
	 * @return new ConfigData object
	 * @throws IOException
	 */
	private static ConfigData generateConfigData(String filename) throws IOException{
		ConfigData data = new ConfigData();
		for(Map.Entry<String, String> entry : INIReader.readFile(filename).entrySet()){
			data.set(entry.getKey(), entry.getValue());
		}
		return data;
	}
	/**
	 *  Default static method for generating ConfigData object.
	 * @return new ConfigData object initializes from config.ini file in current directory
	 * @throws IOException
	 */
	private static ConfigData generateConfigData() throws IOException{ return generateConfigData("config.ini"); }
	
	/**
	 * Main method that tests this class
	 * @param args filename - for external config.ini
	 */
	public static void main(String args[]) throws IOException{
		ConfigData data = getConfigData();
		out.println(data.toString());
		data.removeServer("localhost", 41152);
		out.println(data.toString());
	}
	/**
	 * 
	 * @return
	 */
	public String generateConfigDataAsWritableString()
	{
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> e : configData.entrySet()) {
			String k = e.getKey();
			String v = e.getValue();
			sb.append(k).append(": ").append(v).append("\n");
		}
		// final newline so we can always add more by appending.
		return sb.toString();
	}
	/**
	 * This writes the data for servers and clients.
	 * They are not in any order as a result of using a Map.
	 * @param args filename - for external config.ini
	 * @throws IOException 
	 */
	public void writeConfigData (String filename) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
		String s = generateConfigDataAsWritableString();
		bw.write(s); // write to file
		bw.flush(); // ensure data is written
		bw.close(); // release file handle
	}
	/**
	 * Returns string representing contents of ConfigData
	 */
	public String toString(){
		return configData.toString();
	}
	
	/**
	 * The config.ini reader. A static class with methods for initializing our client/server 
	 * socket nodes. 
	 *
	 */
	private static final class INIReader{
		/**
		 * Parses the config.ini file for system information
		 * @param file a FileReader referencing a config.ini file we want to read
		 * @return HashMap corresponding to key-value pairs within config.ini file
		 * @throws IOException if our config.ini file can not be found or is corrupted
		 */
		private static LinkedHashMap<String, String> parseFile(FileReader file) throws IOException{
			String line;
			Scanner scanner;
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			BufferedReader configFile = new BufferedReader(file);
			while((line =configFile.readLine()) != null){
				scanner = new Scanner(line).useDelimiter(": ");
				String key = scanner.next();
				String val = stripInvalids(scanner.next()).toLowerCase();
				if(val.equals("localhost")) val = InetAddress.getLocalHost().getHostName();
				map.put(key, val);
			}
			return map;
		}
		
		private static String stripInvalids(String s){
		    char[] v = s.toCharArray();
            StringBuffer b = new StringBuffer();
            for(char c : v){
                if(c == '\n'|| c =='\0') break;
                b.append(Character.toString(c));
            }
            return b.toString();
		}
		
		/**
		 * Overloaded method for reaing in a config.ini file. 
		 * @param filename Absolute path to config.ini file
		 * @return HashMap containing key-value pairs for config parameters
		 * @throws IOException
		 */
		public static LinkedHashMap<String, String> readFile(String filename) throws IOException{
			return parseFile(new FileReader(filename));
		}
		/**
		 *  Default method for reading in config file. Defaults to config.ini within current directory
		 * @return HashMap containing key-value pairs for config parameters 
		 * @throws IOException if the config.ini file is unable to be found or data is corrupt
		 */
		@SuppressWarnings("unused")
		public static LinkedHashMap<String, String> readFile() throws IOException{ return readFile("config.ini"); }
		
	}
}
