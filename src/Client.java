import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/*
 * Client side of the socket
 * Author: Risham Chokshi, Matt Kaiser, Varun Shah, Bhavin Patel
 * has a voting recognition 
 * 3 parameters
 * */

public class Client {
	
	int total_message = 0;
	/*
	 * Constructor
	 * */
	public Client(int port, int clientnum, int total, long milli){
		
			main_method(port,clientnum,total,milli);
		
	}
	/*
	 * This is our socket, we are accepting all the messages from everyone this
	 * would send out message if we are not part of the lead it would accept
	 * messages till nothing is needed
	 * //my port number(between 1 - client_Total)
	 * //my client number
	 * //total number of clients
	 */
	public void main_method(int port, int clientnum, int total, long milli){
		ArrayList<Socket> socketslist = new ArrayList<Socket>();
		Socket cl = null;
		int vote = 1;
		// SOCKET MADE

		int con = 9000 + port; //port number
		int lead = (int) (Math.log(total)/Math.log(2)); // possible leaders' ports
		
		//condition check
		if(lead<=0){
			System.out.println("leader" + clientnum);
			return;
		}
		
		//sleep till everyone is at the same point
	
		ServerSocket socket_connect = null;	
		Socket socket_created = null;
		if(port<=lead){ //creating a socket
				try {
					socket_connect = new ServerSocket(con);
					socket_created = socket_connect.accept();
				} catch (IOException e) {
					System.out.println("Creating server error");
				}
			}
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e1) {} //waiting till everyone is upto speed
			if (port > lead) {
				// now the port is greater so we would need to send
				// out the signal
				int vote_to = (int) (9000 + ((Math.random() * total) % lead) + 1); //cannot have 0 as a port
				try {
					cl = new Socket("0.0.0.0", vote_to);
				} catch (IOException e) {}
					connect(cl, vote); // this will send out a message 
					vote = 0;
				// done with our regular client
				}else{
					//you create a server, you are leader
						
					try {
						long start_time = System.currentTimeMillis();
						long last = 10550 + (1000*(port%(1+lead))); //total time it would last, sec max -> depends on highest number leader
						socket_connect.setSoTimeout((int) last); // how long it should stay
						while ((System.currentTimeMillis()-start_time)<=last) {	
						// accepting all the connections made, now lets hear for one if
							// we are not
							socket_created = socket_connect.accept();
							socketslist.add(socket_created);//it will add it to the list
							
								BufferedReader socket_input = new BufferedReader(new InputStreamReader(socket_created.getInputStream()));
								String response = socket_input.readLine();
								//a leader
								if(response!=null && response.isEmpty()!=true && (response.indexOf("leader"))== -1){
									// so then this will be sending a message
									try{
										int votes_g = Integer.parseInt(response);
										vote = votes_g+vote; // count every time you get a response
										if(vote>(total/2)){
											//this is the leader
											connect_all("leader " + clientnum, socketslist,lead, con);
											socket_connect.close(); socket_input.close();
											return;
										}
									}catch(Exception e){}
								}
								else if(response!=null&&(response.indexOf("leader"))!= -1){
									//got a message of someone being a leader
									connect_all(response, socketslist, lead, con);
									socket_connect.close(); socket_input.close();
									return;
								}
								if(!socket_connect.isClosed())
									socket_connect.close();
						}
						}catch(Exception e){}
					
						} 					
			try {
			if(vote>0){ //round 2, send it to someone
				int vote_to = (int) (9000 + ((Math.random() * total) % (lead-port))+ port + 1);
				if(cl!=null){
					cl.close(); 
					cl=null;
					}
				cl = new Socket("0.0.0.0", vote_to);
				connect(cl,vote);
				vote = 0;
			}
				String response = "";
				
				BufferedReader socket_input;
				
			if(cl!=null && cl.isClosed()!=true && cl.isConnected()){
				cl.setSoTimeout(20550 + (1000*lead));	
				socket_input = new BufferedReader(new InputStreamReader(cl.getInputStream()));
				response = socket_input.readLine(); 
						
			if(response!=null && (response.indexOf("leader")!=-1)){
				//found the leader need to tell all the people
				connect_all(response,socketslist,lead, con);
			}
			
			if(!cl.isClosed())
				cl.close();
				System.out.println(total_message);
			}
			}catch(Exception e){}
	}
	

	

	/*
	 * This method sends out votes to leaders takes in two parameters - vote_to
	 * is where it would be voted to con - is the connection that we have( our
	 * port)
	 */
	public  void connect(Socket client, int vote) {
		try {
			
			// gotta send the votes in
			PrintWriter output = new PrintWriter(client.getOutputStream(), true);
			output.println(vote);
			total_message++;
			//done voting
		} catch (Exception e) {
			System.out.println("client error");
			
		}
	}
	/*
	 * Connect all will notify everyone that has connected to this server about the message
	 * it has received the leader
	 * */
	public  void connect_all(String response, ArrayList<Socket> socketslist, int lead, int con){
		
		for(int i =0; socketslist!=null && socketslist.isEmpty()!=true && i<socketslist.size(); i++){
			PrintWriter out;
			try {
				out = new PrintWriter(socketslist.get(i).getOutputStream(),true);
				out.println(response); 
				total_message++;
				
			} catch (IOException e){System.out.println("Informating Votes Error");}	

		}
		// for all the leads
		for(int i =1; i<=lead; i++){
			Socket cl;
			try {
				cl = new Socket("0.0.0.0", (9000+lead));
				PrintWriter out;
				out = new PrintWriter(cl.getOutputStream(),true);
				out.println(response);
				total_message++;
				
			} catch (Exception e){System.out.println("Information Leads Error");}

		}
		
	}
	


}
