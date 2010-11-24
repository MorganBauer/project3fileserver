package team3.src.util;
import static java.lang.System.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

/**
 * Responsible for handling data chunking, converting binary data to
 * base64 string format through the {@link Base64 Base64} class
 * @author Joir-dan Gumbs
 *
 */
public final class Data2MsgUtil {
	/** Set this flag if we want verbose output */
	private  boolean verbose;
	/** Constant kilobyte*/
	private  final int KILOBYTE = 1024;
	/** Name of file currently being serviced */
	private  String filename="";
	/** Name of current base64 tempFile */
	private  String base64DataFile = "";
	
	/**
	 * Responsible for grabbing chunkSize worth of converted base64 data from tempFile
	 * @param inFile name of the file we are grabbing data from
	 * @param startKByte where in file we grab data from
	 * @param chunkSize size (in kilobytes) we are trying to grab
	 * @return base64 string representation of our data
	 * @throws FileNotFoundException if the file we are trying to grab data from is unavailable
	 */
	public String data2Base64(String inFile, int startKByte, int chunkSize, boolean verbose) throws FileNotFoundException, IOException{
		if(verbose) out.printf("Converting %d KB of %s to Base64String from KB %s.\n", chunkSize, inFile, startKByte*chunkSize);
		filename = inFile;
		convertDataToString(filename);
		return getDataFromBase64Temp(startKByte, chunkSize);
	}
	
	/**
	 * Converts data from file represented by filename to base64 in a temp file for retrieval
	 * @param fName name of file
	 * @throws IOException if we are unable to create tempFile, or we can notread from file
	 * @throws FileNotFoundException if we can not find file represented by fName
	 */
	private void convertDataToString(String fName) throws FileNotFoundException, IOException{
	    File newFile;
		if(base64DataFile == null){
		    newFile = File.createTempFile("tmp", ".b64data", null);
		    base64DataFile = newFile.getAbsolutePath();
		}else newFile = new File(base64DataFile);
		filename = fName;
		File file = new File(filename);
		if(verbose) out.println("Filesize: "+file.length());
		byte[] buffer = new byte[(int) file.length()];
		RandomAccessFile fileStream = new RandomAccessFile(file, "r");
		PrintWriter newFileStream = new PrintWriter(newFile);
		fileStream.read(buffer);
		String data = Base64.encode(buffer);
		newFileStream.write(data);
		newFileStream.flush();
		if(verbose) out.println("TempFile Length: "+newFile.length());
	}
	
	/**
	 * Grabs chunkSize KB of data from the base64 datafile from startKByte position
	 * @param startKByte where in file we start grabbing data
	 * @param chunkSize how much data (in KB) we try to grab 
	 * @return base64Data string representing data from original file
	 * @throws IOException if we are unable to scan through file, or unable to read data in
	 * @throws FileNotFoundException if we are unable to find tempDataFile we are reading from
	 */
	private  String getDataFromBase64Temp(int startKByte, int chunkSize) throws FileNotFoundException, IOException{
		if(verbose) out.println("Grabbing data from base64 temp file...");
		File file = new File(base64DataFile);
		RandomAccessFile fileStream = new RandomAccessFile(file, "r");
		if(verbose) out.println("TempFile Length: "+ fileStream.length());
		long pointer = Math.min(startKByte*chunkSize*KILOBYTE, fileStream.length());
		if(verbose) out.println("Pointer: "+pointer);
		long end = fileStream.length();
		if(verbose) out.println("End: "+end);
		if(verbose) out.printf("Finding Buffer size: %d vs %d.\n", (end - pointer), chunkSize*KILOBYTE);
		int bufferSize = Math.min(Math.abs((int) (end - pointer)), chunkSize*KILOBYTE);
		fileStream.seek(pointer);
		byte[] buffer = new byte[bufferSize];
		if(verbose) out.printf("Message contains %d bytes.\n", bufferSize);
		fileStream.read(buffer);
		return (bufferSize > 0)? new String(buffer): null;
	}
	
	/**
	 * Converts base64 data to original data, and writes to file denoted by filename
	 * @param filename the name of the file we are writing incoming data to
	 * @param base64Data data string in base64 format
	 * @throws IOException if we are unable to seek/write data to filename
	 * @throws FileNotFoundException if we are unable to find file to write to
	 */
	public int base64toData(String filename, String base64Data) throws FileNotFoundException, IOException{
		if(verbose) out.printf("Writing base64 data to file\n");
		File outFile = new File(filename);
		byte[] bytesWritten;
		RandomAccessFile fileStream = new RandomAccessFile(outFile, "rw");
		try{
			if(verbose) out.printf("OutFile.length: %d\n", outFile.length()/KILOBYTE);
			fileStream.seek(outFile.length());
			fileStream.write(bytesWritten = Base64.decode(base64Data));
			if(verbose) out.printf("Written data to %s\n", filename);
		}finally{
			fileStream.close();
		}
		return (bytesWritten != null)?bytesWritten.length: 0;
	}
	
	/**
	 * Removes created tempFile, and cleans up Base64 data filename and original filename.
	 */
	public void cleanup(){
		File newFile= new File(base64DataFile);
		newFile.delete();
		base64DataFile = null;
		filename = null;
	}
	
	/** Creates new object without setting verbose flag (default is false) */
	private Data2MsgUtil(){ this.verbose = false; }
	/** Creates new object. This constructor should only be called for testing purposes. */
	public Data2MsgUtil(boolean verbose){ this.verbose = verbose; }
	
	/** Test method for Data2MsgUtil...
	 * for args, give it a startFile, endFileName, and maxChunkSize
	 * @param args filename newFilename chunkSize
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, NumberFormatException {
		Data2MsgUtil util = new Data2MsgUtil(true);
		int startByte = 0;
		String base64String;
		File file = new File(args[1]);
		file.delete();
		try{
			while((base64String = util.data2Base64(args[0], startByte++, Integer.parseInt(args[2]), true)) != null){
				out.println();
				util.base64toData(args[1], base64String);
				out.println();
			}
	  }
		finally{
			util.cleanup();
		}
		
	}
	/**
	 * Return a new Data2MsgUtil object
	 */
	public static Data2MsgUtil getUtil(){
		return new Data2MsgUtil();
	}

}
