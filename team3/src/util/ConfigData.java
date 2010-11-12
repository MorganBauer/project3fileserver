package team3.src.util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.out;
/**
 * 
 */
public class ConfigData{
	/** The ConfigData singleton */
	private static ConfigData singleton = null;
	/** The map that holds the key-value pairs obtained from config.ini **/
	private HashMap<String, String> configData;
	
	/**
	 * Singleton Method for default ConfigData object
	 * @return Copy of ConfigData singleton.
	 * @throws IOException
	 */
	public static ConfigData getConfigData() throws IOException{
		return (singleton == null)? generateConfigData(): singleton;
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
	
	private ConfigData(){ configData = new HashMap<String, String>(); }
	
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
		data = overrideConfigData(args[0]);
		out.println(data.toString());
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
		private static HashMap<String, String> parseFile(FileReader file) throws IOException{
			String line;
			Scanner scanner;
			HashMap<String, String> map = new HashMap<String, String>();
			BufferedReader configFile = new BufferedReader(file);
			while((line =configFile.readLine()) != null){
				scanner = new Scanner(line).useDelimiter(": ");
				map.put(scanner.next(), scanner.next());
			}
			return map;
		}
		/**
		 * Overloaded method for reaing in a config.ini file. 
		 * @param filename Absolute path to config.ini file
		 * @return HashMap containing key-value pairs for config parameters
		 * @throws IOException
		 */
		public static HashMap<String, String> readFile(String filename) throws IOException{
			return parseFile(new FileReader(filename));
		}
		/**
		 *  Default method for reading in config file. Defaults to config.ini within current directory
		 * @return HashMap containing key-value pairs for config parameters 
		 * @throws IOException if the config.ini file is unable to be found or data is corrupt
		 */
		@SuppressWarnings("unused")
		public static HashMap<String, String> readFile() throws IOException{ return readFile("config.ini"); }
		
	}
}
