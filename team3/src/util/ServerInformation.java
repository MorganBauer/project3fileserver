/**
 * 
 */
package team3.src.util;

import java.util.Date;

/**
 * @author Morgan
 * 
 */
public class ServerInformation {
	Date StartupTimeStamp;
	String serverHostName;
	int serverPortNumber;
	boolean isMaster;
	boolean isUp;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
public String toString()
{
	StringBuilder sb = new StringBuilder();
	sb.append(serverHostName).append(serverPortNumber);
	return sb.toString();
}
}
