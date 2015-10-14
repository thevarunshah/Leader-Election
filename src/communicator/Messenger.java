package communicator;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messenger {
	
	public List<Message> InMsgQueue;
	public List<Message> OutMsgQueue;
	public Map<Integer, Connection> connections;
	public AcceptorThread acceptor;
	public boolean running;
	
	public Messenger(int port) {
		this.running = true;
		this.connections = new HashMap<Integer, Connection>();
		
		// Start connection acceptor thread
		acceptor = new AcceptorThread(this, port);
		new Thread(acceptor).start();
	}
	
	public void addConnection(Socket socket) {
		Connection c = new Connection(socket);
		connections.put(socket.getPort(), c);
	}

	public Connection getConnection(int port) {
		return connections.get(port);
	}

}
