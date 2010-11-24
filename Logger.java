import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.bind.JAXBException;

import team3.src.message.AbstractMessage;
import team3.src.message.LogMessage;

import static java.lang.System.out;


public class Logger extends Thread{
    DatagramSocket serverSocket;
    BufferedWriter log;
    BufferedWriter html;
    Boolean endLogging;
    HashMap<String, String> ServerLoad;
    LinkedList < String > list;
        
    public Logger(int port) throws IOException {
    	this.serverSocket = new DatagramSocket(port);
    	this.log = new BufferedWriter(new FileWriter("Logger.log"));
    	log.write("Read every line as <ServerName> <Port> <Event> <ServerLoad> <OriginatingHostID> <TimeStamp>\n");
    	this.ServerLoad = new HashMap <String, String> ();
    	this.list = new LinkedList< String >();
        log.close();
    	endLogging = false;
    }
    public void run(){
		// TODO Auto-generated method stub
    	@SuppressWarnings("unused")
		LogMessage message;
    	byte []logMessage = new byte[64*1024];  
    	DatagramPacket logPacket;
    	      	while(!endLogging) {
    		try {
    			logPacket = new DatagramPacket(logMessage,logMessage.length);
    			serverSocket.setReceiveBufferSize(logMessage.length);
    		   	serverSocket.receive(logPacket);
    			String toLog = (new String(logPacket.getData(),0,logPacket.getLength())).trim();
    			
    			message = (LogMessage) AbstractMessage.unmarshal(toLog);	 
    			out.println(toLog);
			    writeText(message);
			    writeHtml(message);
    		} 
    		catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
      	try {
			log.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	    
    private void writeText(LogMessage message) throws IOException {
		// TODO Auto-generated method stub
		String finalFormat="";
    	this.log = new BufferedWriter(new FileWriter("Logger.log",true));
		//<ServerName> <Port> <Event> <ServerLoad> <Originating hostID> <time stamp>
		finalFormat += message.getHostName()+" "+message.getPort()+" "+message.read()
		               +" "+ message.getLoad()+" "+message.getID()+" "+message.getDateTime();
		log.write(finalFormat + "\n");
		list.addFirst(finalFormat);
		 if (list.size()>5)	{
			list.removeLast();
		}
		
		log.close();
	}
	private void writeHtml(LogMessage message) throws IOException{
		// TODO Auto-generated method stub
		//String finalFormat;
		this.html = new BufferedWriter(new FileWriter("Logger.html"));
		html.write("<html>");
		html.write("<B>Server Load Table <br /> </B>");
		html.write("<table border=\"1\">");
		ServerLoad.put(message.getHostName() + "-" +message.getPort(),message.getLoad());
		for (Map.Entry<String, String> e : ServerLoad.entrySet()) {
			html.write("<tr>");
			html.write("<td>" + e.getKey() +"</td>");
			html.write("<td>" + e.getValue() +"</td>");
			html.write("</tr>");
		}
		html.write("</table>");
		html.write("<B>Recent updates in the system <br /> </B> \n");
		for (int i=0;i<list.size();i++)
		{
			html.write(list.get(i));
			html.write("<br />");
		}
		html.write("<A href=\"Logger.log\"> Click here for the whole text log </A>");
		html.write("<A href=\"Logger.log\"> Click here for the whole text log </A>");
		html.write("</html>");
		html.close();
	}
	public static void main(String[]args) {
		if (args.length != 1)
		{
			out.println("Port number should be the command line arguement, exiting now..");
			System.exit(1);
		}
		try{
			Logger logServer = new Logger(Integer.parseInt(args[0]));
			logServer.start();
			out.println("Log server started. press return to finish logging with the next incoming message");
			InputStreamReader i = new  InputStreamReader(System.in);
			i.read();
			logServer.endLogging=true;
		}
	    catch( IOException e){
	    	out.println("Port Not available exiting now..");
			System.exit(1);
	    }
    }
}
