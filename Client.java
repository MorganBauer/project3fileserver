import static java.lang.System.out;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBException;

import team3.src.client.AbstractClient;
import team3.src.exception.IllegalCommandException;
import team3.src.message.AbstractMessage;
import team3.src.message.response.AbstractResponse;
import team3.src.protocol.ClientProtocol;
import team3.src.util.ConfigData;


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
        out.println("Start!");
        initData();
        //TODO: create Logger initLogger();
        out.println(configData.toString());
        while(checkIsNotDone()){
            try{
                String[] commandArgs = testIsNotFirstRequest()? parseCommand(): parseCommand(args);
                if(commandArgs[0].equals("my directory")){ 
                    getDirectory(commandArgs);
                }else{
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
                //printFriendly = protocol.stripData(toServer);
                //out.println(printFriendly);
                //logger.log(printFriendly);
                writeToServer(toServer);
                while(weCantStop()){
                    try{
                        fromServer = grabFromServer();
                        switch(fromServer.getType()){
                            case ERROR:
                            case END:
                                toServer = null;
                                out.println(fromServer.toString());
                                prepareToFinish();
                                break;
                            case DATA_IN:
                            case DATA_OUT:
                                toServer = protocol.handleNext(fromServer);
                                break;
                            case WAIT:
                                toServer = null;
                                out.println("Waiting...");
                                break;
                            default: throw new AssertionError("SOMEONE MESSED UP!! UNKNOWN RESPONSE");
                        }
                        if(toServer!= null)
                            //TODO: Log events
                            writeToServer(toServer);
                    }catch(IOException e){ 
                        //TODO: LOGGER!
                        prepareToFinish();
                    }catch(JAXBException e){
                        //TODO: Logger!
                        prepareToFinish();
                    }
                }
            }catch(IllegalCommandException e){ 
                //TODO: Logger
                out.println("Bad Command");
            }catch(JAXBException e){
                //TODO: Logger
                out.println("JAXBException... do somethign about this...");
            }
            closeConnection();
            //TODO: Logger
        }
        
    }
}
