package communicator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Actively looks for other clients trying to connect to this server.
 * @author mkaiser
 *
 */

public class AcceptorThread implements Runnable {
	
	public Messenger messenger;
	public int port;
	
	public AcceptorThread(Messenger messenger, int port) {
		this.messenger = messenger;
		this.port = port;
	}

	@Override
	public void run() {
		
		// First create ServerSocket
		ServerSocket server = null;
		try {
			server = new ServerSocket(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Now continue listening for clients trying to connect to our ServerSocket
		// Stop when messenger.running is false
		Socket socket;
		while(this.messenger.running) {
			try {
				// Wait for connect
				socket = server.accept();
				// Add connection to messenger
				messenger.addConnection(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		// Close the server socket when we're done
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
