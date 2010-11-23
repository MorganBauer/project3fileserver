import static java.lang.System.out;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBException;

import sun.jkernel.DownloadManager;
import team3.src.client.AbstractClient;
import team3.src.exception.IllegalCommandException;
import team3.src.message.AbstractMessage;
import team3.src.message.response.AbstractResponse;
import team3.src.protocol.ClientProtocol;
import team3.src.util.ConfigData;
import team3.src.util.SSLEncryptor;


/**
 * The client 
 * @author Joir-dan Gumbs
 *
 */
public class Client extends AbstractClient {

    /**
     * Main method... starts everything
     * @param args
     */
    public static void main(String[] args) {
        setSSLProperties();
        out.println("Start!");
        initData();
        //TODO: create Logger initLogger();
        out.println(configData.toString());
        while(checkIsNotDone()){
            try{
                String[] commandArgs = testIsNotFirstRequest()? parseCommand(): parseCommand(args);
                if(commandArgs[0].equals("my directory")){ 
                    getDirectory(commandArgs);
                }
                else if(commandArgs[0].equals("bye")){
                    thenClientIsDone();   
                }
                else if (commandArgs[0].equals("encryptify"))
                {
                	out.println("changing encryption to " + commandArgs[1]);
                	changeEncryptionAlgorithm(commandArgs[1]);
                }
                else{
                    if(commandArgs[0].equals("terminate")) thenClientIsDone();
                    ClientThread clientThread = ClientThread.createThread(generateRequestID(), configData, commandArgs);
                    clientThread.start(); 
                } 
            }catch(IllegalCommandException e){ out.println("Invalid command! Try again..."); }
            catch(UnknownHostException e){ /* TODO: Handle this error... */ }
            catch(IOException e){ /* TODO: Handle this error... */ }
            //TODO: logger.log("Completed!");
        }
    }

    /**
     * Client thread that has knowledge of priority
     * @author Joir-dan Gumbs
     *
     */
    private static class ClientThread extends AbstractClientThread{

        /**
         * Creates a new ClientThread
         * @param requestID request identifier
         * @param data ConfigData information
         * @param command client command 
         * @return new ClientThread object
         * @throws IOException If unable to set up
         * @throws UnknownHostException if unable to find host
         */
        protected static final ClientThread createThread(String requestID, ConfigData data, String[] command) throws IOException, UnknownHostException{
            return new ClientThread(requestID, data, command);
        }
        
        private ClientThread(String requestID, ConfigData data, String[] message) throws UnknownHostException, IOException {
            super(requestID, data, message);
            this.protocol = ClientProtocol.getProtocol(data.get("clientID"), Integer.parseInt(data.get("chunk-size")));
        }
        public void run(){
            //TODO: logger.log("Client Thread "+getName()+" has started!");
            String init = "";
            AbstractMessage toServer;
            AbstractResponse fromServer;
            for(String arg : commandArgs) init+=arg+" ";
            try{
                toServer = protocol.handleInput(commandArgs, getName());
                initConnection();
                //printFriendly = protocol.stripData(toServer);
                //logger.log(printFriendly);
                out.println(toServer);
                writeToServer(toServer);
                while(weCantStop()){
                    try{
                        fromServer = grabFromServer();
                        switch(fromServer.getType()){
                            case ERROR:
                            case END:
                                out.println("ATOMIC!!!");
                                out.println(fromServer);
                                toServer = null;
                                protocol.handleSimpleResponse(fromServer);
                                prepareToFinish();
                                break;
                            case DATA_IN:
                            case DATA_OUT:
                                out.println("Data Message!!");
                                toServer = protocol.handleNext(fromServer);
                                if(toServer == null){
                                    out.println("No response...");
                                    prepareToFinish();
                                }
                                break;
                            case WAIT:
                                toServer = null;
                                out.println("Waiting...");
                                break;
                            default: throw new AssertionError("SOMEONE MESSED UP!! UNKNOWN RESPONSE");
                        }
                        if(toServer!= null){
                            out.println(toServer);
                            writeToServer(toServer);
                            out.println("sent msg!");
                        }
                    }catch (SocketException se)
                	{ // connection to server died
                    	se.printStackTrace();
                    	System.out.println("we cant stop is " + weCantStop());
                		// set server status to dead
                    	blacklistCurrentServer();
                    	//reinit connection
                		initConnection();
                		writeToServer(toServer);
                	}catch(IOException e){ 
                        //TODO: LOGGER!
                    	e.printStackTrace();
                        prepareToFinish();
                    }catch(JAXBException e){
                        //TODO: Logger!
                        e.printStackTrace();
                        out.println("HEY!!!");
                        prepareToFinish();
                    }
                } // end keep doing stuff loop (multi-part messages?)
            }catch(IOException e){ 
            	e.printStackTrace();
            	out.println("Unable to connect..."); }
            catch(IllegalCommandException e){ 
                //TODO: Logger
                out.println("Bad Command");
            }catch(JAXBException e){
                //TODO: Logger
                e.printStackTrace();
                out.println("JAXBException... do somethign about this...");
            }
            closeConnection();
            //TODO: Logger
        }
        
    }
}
