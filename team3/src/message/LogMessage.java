package team3.src.message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import team3.src.message.response.AbstractResponse;

public class LogMessage extends AbstractMessage{
	
	@XmlAttribute(required=true)
	private String hostName;
	
	@XmlAttribute(required=true)
	private int port;
	
	@XmlAttribute(required=true)
	private String event;
	
	@XmlAttribute(required=true)
	private String load;
	
	@XmlAttribute(required=true)
	private String id;
	
	private LogMessage(){}
	
	private LogMessage(String host,int port,String event,String load,String id){
		this.hostName = host;
		this.port = port;
		this.event = event;
		this.load = load;
		this.id = id;
	}
	private LogMessage(String host,int port,String event,String load){
		this.hostName = host;
		this.port = port;
		this.event = event;
		this.load = load;
		this.id = "none";
	}
	public static LogMessage buildLogEventMessage(String host,int port,String event,String load,String id){
		return new LogMessage(host, port, event, load, id );
	}
	public static LogMessage buildLogStatusMessage(String host,int port,String event,String load,String id){
		return new LogMessage(host, port, event, load);
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String read() {
		// TODO Auto-generated method stub
		return event;
	}
	
}
