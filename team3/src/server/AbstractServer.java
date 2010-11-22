package team3.src.server;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import team3.src.message.Message;
import team3.src.message.client.AbstractClientMessage;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * Abstraction of our server process
 * 
 * @author Joir-dan Gumbs
 * 
 */
public abstract class AbstractServer {

	/**
	 * Responsible for queuing client connections by their priorities
	 */
	protected static PriorityBlockingQueue<PrioritySocket> priorityPool = new PriorityBlockingQueue<PrioritySocket>();

	/** Flag to check whether the server process is in a runnable state */
	private static volatile Boolean isRunning = true;
	/** Server Listening object. */
	//protected static ServerSocket socket;
	protected static SSLServerSocket sslserversocket;// = (SSLServerSocket) sslserversocketfactory.createServerSocket(0);

	/** Responsible for cataloging all events that happen on the server */

	/**
	 * Checks to see if server is still in runnable state
	 * 
	 * @return True if it is, false otherwise
	 */
	protected static boolean isRunning() {
		synchronized (isRunning) {
			return isRunning;
		}
	}

	/** Sets our running flag to false, done to end server. */
	protected static void finished() {
		synchronized (isRunning) {
			isRunning = false;
		}
	}

	/** Closes Server's connection listener. */
	protected static void closeConnection() {
		try {
			sslserversocket.close();
		} catch (IOException e) {
			// logger.log(errorHandler.handleSocketIOException(SocketError.INACCESSIBLE_STREAM));
		}
	}

	/**
	 * Abstraction of Server's thread of communication/execution.
	 * 
	 * @author Joir-dan Gumbs
	 * 
	 */
	protected static abstract class AbstractServerThread extends Thread {
		/** Server's instream buffer. */
		private BufferedReader serverIn;
		/** Server's outstream buffer. */
		private PrintWriter serverOut;
		// /** Server protocol responsible for processing messages*/
		// protected AbstractServerProtocol protocol;
		// /** Responsible for logging this thread's activities */
		// protected Logger serverLogger;
		// /** Communication channel related to client. */
		protected Socket client;

		private boolean isDone;

		// protected AbstractErrorHandler errorHandler;

		/**
		 * Check to see if this thread is done with it's current work
		 * 
		 * @return False if we are able to finish thread execution and true if
		 *         not
		 */
		protected boolean weCantStop() {
			return !isDone;
		}

		/**
		 * Prepares this thread for termination
		 */
		protected void prepareToFinish() {
			isDone = true;
		}

		/**
		 * @param string
		 */
		protected AbstractServerThread(String string) {
			super(string);
			isDone = false;
			// this.errorHandler = new ErrorHandler();
		}

		protected void setStreams(Socket client) throws IOException {
			serverOut = new PrintWriter(client.getOutputStream());
			serverIn = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
		}

		/**
		 * Responsible for reading server instream data.
		 * 
		 * @return message from client.
		 * @throws IOException
		 *             if unable to read from instream
		 * @throws JAXBException
		 */
		protected AbstractClientMessage readInstream() throws IOException,
				JAXBException {
			String msg = serverIn.readLine();
			AbstractClientMessage ret = AbstractClientMessage.unmarshal(msg);
			return ret;
		}

		/**
		 * Sends message to client
		 * 
		 * @param message
		 *            that we want to send
		 */
		protected void writeOutstream(String message) {
			serverOut.println(message);
			serverOut.flush();
		}

		/**
		 * Responsible for closing down client connection and streams.
		 */
		protected void closeConnection() {
			try {
				client.close();
				serverIn.close();
				serverOut.close();
			} catch (IOException e) {
			}
		}

	}

	/**
	 * Wrapper for our client, useful in establishing priority
	 * 
	 * @author Joir-dan Gumbs
	 * 
	 */
	protected static final class PrioritySocket implements
			Comparable<PrioritySocket> {
		private Socket client;
		private int priority;
		private XMLGregorianCalendar timestamp;
		private AbstractClientMessage currentMsg;

		/**
		 * Creates Priority Wrapper used in the priorityPool to aid in choosing
		 * clients to run on server
		 * 
		 * @param client
		 *            Socket of client
		 * @param currentMsg
		 *            message sent by client
		 * @return new PrioritySocket
		 */
		public static PrioritySocket wrapSocket(Socket client,
				AbstractClientMessage currentMsg) {
			return new PrioritySocket(client, currentMsg);
		}

		/**
		 * Main Priority Socket Constructor
		 * 
		 * @param client
		 *            Socket representing clients connection to server
		 * @param msg
		 *            Message that was sent by client
		 */
		private PrioritySocket(Socket client, AbstractClientMessage msg) {
			this.priority = msg.getPriority();
			this.timestamp = msg.getDateTime();
			this.client = client;
			this.currentMsg = msg;
		}

		/**
		 * Gets the id of the sender
		 * 
		 * @return senders id
		 */
		public String getID() {
			return currentMsg.getID();
		}

		/**
		 * Gets the priority of this client request
		 * 
		 * @return integer representation of priority
		 */
		public int getPriority() {
			return priority;
		}

		/**
		 * Grabs the timestamp
		 * 
		 * @return XMLGregorianCalendar timestamp
		 */
		public XMLGregorianCalendar getTimestamp() {
			return timestamp;
		}

		/**
		 * Grabs the socket associated with the client
		 * 
		 * @return socket
		 */
		public Socket getClient() {
			return client;
		}

		/**
		 * Gets the current message for this client
		 * 
		 * @return AbstractClientMessage
		 */
		public AbstractClientMessage getCurrentMsg() {
			return currentMsg;
		}

		/**
		 * Grabs the type of message this is
		 * 
		 * @return the type of message
		 */
		public Message.Type getMode() {
			return currentMsg.getMsgType();
		}

		public int compareTo(PrioritySocket socket) {
			/*
			 * Priority is established in the following order If priority is of
			 * type OTHER, then it has high priority
			 */
			return (socket.getPriority() > priority) ? 1 : (socket
					.getPriority() < priority) ? -1 : (socket.getTimestamp()
					.compare(timestamp) > 0) ? -1 : 1;
		}

		public String toString() {
			return String.format("Mode: %s\nPriority: %d\nTimestamp: %s.\n",
					getMode(), priority, (timestamp == null) ? "Not Available"
							: timestamp);
		}
	}

}
