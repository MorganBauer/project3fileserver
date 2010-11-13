package team3.src.message;

import static team3.src.message.server.ServerVotingMessage.*;

import javax.xml.datatype.XMLGregorianCalendar;

import team3.src.message.server.ServerVotingMessage;

public final class ServerMessageFactory extends AbstractMessageFactory {
    public static ServerMessageFactory singleton;
    
    /**
     * Grab the factory for building server messages
     * @return
     */
    public static final ServerMessageFactory getFactory(){
        return (singleton != null)?singleton:(singleton = new ServerMessageFactory());
    }
    /**
     * Build a message that casts a vote for leadership within distributed system
     * @param host the hostname of this server
     * @param port the portnum of this server
     * @param timestamp When this server was "born." The older the machine, the higher chance it will be accepted as leader
     * @return a vote message
     */
    public ServerVotingMessage createVotingMessage(String host, int port, XMLGregorianCalendar timestamp){
        return buildVotingMessage(host, port, timestamp);
    }
}
