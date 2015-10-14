package communicator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {
	
	public Socket socket = null;
	
	//public inThread inThread
	
	public Connection(Socket socket) {
		this.socket = socket;
	
		//this.voterIn = new VoterIn(this.socket);
		//this.voterOut = new VoterOut(this.socket);
	}

}

class VoterIn implements Runnable {
	
	public DataInputStream in = null;
	
	public VoterIn(Socket socket) {
		try {
			this.in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Continue to listen for messages from socket
		while(true) {
			
		}
	}
	
}

class VoterOut implements Runnable {
	
	public DataOutputStream out = null;
	
	public VoterOut(Socket socket) {
		try {
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}