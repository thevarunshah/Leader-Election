import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Client {
	 
	 int  total_message = 0;
	ArrayList<list_c> voteslist = new ArrayList<list_c>();
	class list_c{
		int port; 
		int vote; 
		public list_c(int port, int vote){
			this.port = port;
			this.vote = vote;
		}
		
	}
	class topleader extends Thread {
		
		double total; 
		long milli;
		int total_responded = 0;
		int messages_ex =0;
		int l; 
		ArrayList <Socket> list = new ArrayList<Socket>();
		public topleader(int total,long milli,int l){
			this.total = total;
			this.milli = milli;
			this.l = l;
		}
		@Override
		public void run() {
			//System.out.println("inside topleader");
			ServerSocket socket_connect = null;
			Socket socket_created = null;
			String response = ""; 
			int lead = 0;
			try {
				socket_connect = new ServerSocket(3000);
				//loop for accepting all the connections
				
			try{
			long last = 2000 + System.currentTimeMillis(); 
			lead = (int) (Math.log(total)/Math.log(2));
			lead = l;
			
			while(true && total_responded<total &&(last-System.currentTimeMillis()>0)){
				
				socket_created = socket_connect.accept();
				list.add(socket_created);
				BufferedReader socket_input = new BufferedReader(new InputStreamReader(socket_created.getInputStream()));
				response = socket_input.readLine();
				total_responded++;
				
				try{
					int votes_g = Integer.parseInt(response);
						messages_ex = messages_ex+votes_g;
						//System.out.println("***** Total messages: " + messages_ex + " port " + socket_created.getPort() );
				}catch(NumberFormatException e){
					//System.out.println("Number format ex");
					int index = response.indexOf("lead");
					if(index!=-1){
						//that means that its a leader
						//System.out.println("*******************INDEX ********* " + index + response.charAt(index+5)); 
						response = response.substring(index+5);
						
						try{
							int maybe = Integer.parseInt(response);
								lead = maybe;
						}catch(NumberFormatException io){
							//System.out.println("Number format ex");
							}
						break;
					}
				}
				//socket_created.close();
				
				}
			if((last-System.currentTimeMillis()<=0)){
				//leader elected, tell everyone
				response = "lead" +l;
				connect_all(response);
				
			}
			}finally{
				socket_connect.close();
			}

			} catch (IOException e) {
				System.out.println("Creating server error");
			}
			messages_ex+=total;
			System.out.println("*********I AM A LEADER******** " + lead);
			connect_all(response);
			System.out.println("Total Messages: " + messages_ex + " Avg: " + (messages_ex/total));
			
			
		}

			public void connect_all(String response) {

				for (int i = 0; list != null && list.isEmpty() != true && i < list.size(); i++) {
					PrintWriter out;
					try {
						out = new PrintWriter(list.get(i).getOutputStream(), true);
						out.println(response);
						messages_ex++;

					} catch (IOException e) {
						System.out.println("Informating Votes Error");
					}

				}


			}
		
	}
	
	
	/*
	 * Constructor
	 */
	public Client(int port, int clientnum, int total, long milli, int leader) {
		//System.out.println("port: " + port);
		list_c ob = new list_c(port,1);
		addingvotes(ob);
		main_method(port, clientnum, total, milli, leader);
	}
	int vote =1; 
	boolean leader_connection = true;
	
	/*
	 * This is our socket, we are accepting all the messages from everyone this
	 * would send out message if we are not part of the lead it would accept
	 * messages till nothing is needed //my port number(between 1 -
	 * client_Total) //my client number //total number of clients
	 */
	public void main_method(int port, int clientnum, int total, long milli, int leader) {
		//ArrayList<Socket> socketslist = new ArrayList<Socket>();
		Socket cl = null;
		// SOCKET MADE
		int con = 3000 + port; // port number
		int lead = (int) (Math.log(total) / Math.log(2)); // possible leaders' ports
		
		//Checks for the the port
		
		if(port==0){
			//System.out.println("port equals 0");
			Thread t = new topleader(total,milli-System.currentTimeMillis(),leader);
			t.start();
			return;
		}
		//if there is no leader
		else if (lead <= 0) {
			try {
				long wait = milli-System.currentTimeMillis();
				if(wait>0){
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e1) {
					//System.out.println("Error with Thread sleeping with no lead " + con);
				}
				}
				cl = new Socket("0.0.0.0", 3000);
				PrintWriter output = new PrintWriter(cl.getOutputStream(), true);
				total_message++;
				output.println(total_message);
				cl.close();
			} catch (UnknownHostException e) {
				//System.out.println("No leader UnknownHostException");
			} catch( IOException e){
				//System.out.println("No leader IOException");
			}
			return;
		}
		//if you are a leader
		else if(port<=lead){
			create_server(port,clientnum,total,milli-System.currentTimeMillis(),lead);
		}
		else{
			//not a leader
			long wait = milli-System.currentTimeMillis();
			if(wait>0){
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e1) {
				//System.out.println("ERROR with Thread sleeping with no lead " + con);
			}
			}
			int vote_to = (int) (3000 + ((Math.random() * total) % lead) + 1);
			create_voter(port,clientnum,total,vote_to, lead);
		}
		
		
		
	}
	
	public void create_server(int port,int clientnum,int total,long milli,int lead){
		int con = 3000 + port; // port number
			
				//System.out.println("INSIDE CREATE SERVER " + con);
				ServerSocket socket_connect;
				try{
					
						socket_connect = new ServerSocket(con);
				try {	
					long StartTime = System.currentTimeMillis();
					long lastTime = 100 + (100 * port*3) + System.currentTimeMillis();
				socket_connect.setSoTimeout(1500 +(150*port));
				//System.out.println("here");
				while(vote<(total/2) && (lastTime - StartTime)>0){
					
					Socket socket_created = socket_connect.accept();
					vote++;
					System.out.println("Vote: " + vote + " port: " + con + "by: " + socket_created.getPort());

					socket_created.close();
					if(vote>=(total/2)){
						//you could be a leader
						lead_connect(clientnum,port,true);
					}
					
				} 
			}finally{
				socket_connect.close();
				if(leader_connection==true && vote<(total/2)){
					//you can go ahead and vote
					total_message++;
					int vote_to = (int) con - port + lead;
					System.out.println(" in finally , going into create_voter " + port);
					create_voter(port, clientnum, total, vote_to,  lead);
				}
				else if(leader_connection==true && vote>=(total/2)){
					//you could be a leader
					total_message++;
					lead_connect(clientnum,port,true);
				}
			}
			}catch(SocketTimeoutException e){
				/*
				 * TEST THISS!
				 * */
				if(leader_connection==true && vote<(total/2)){
					//you can go ahead and vote
					total_message++;
					int vote_to = (int) con - port + lead;

					//System.out.println(" in SocketTimeoutExcep , going into create_voter " + port);
					create_voter(port, clientnum, total, vote_to,  lead);
				}
				else if(leader_connection==true && vote>=(total/2)){
					//you could be a leader
					total_message++;
					lead_connect(clientnum,port,true);
				}
				return;
				
			}catch (IOException e) {
				//System.out.println("ERROR Creating socket in server error, IO EXCEPTION + port: " + port + " " + e);
			
			}
			
		
			
				
	}
	
	/*
	 * STOP HERE FOR COMMENTING IT OUT
	 * */
	public void create_voter(int port,int clientnum,int total,int vote_to,int lead){
		boolean connection = true;
		
		while(connection){
		try {
			//System.out.println("Entering Client Side Exception " + port + " vote_to" + vote_to);
			Socket cl = new Socket("0.0.0.0", vote_to);
			
			PrintWriter output = new PrintWriter(cl.getOutputStream(), true);
			output.println(vote);

			connection = false;
			System.out.println("I am done voting " + cl.getPort());
			cl.close();
		} catch (IOException e) {
			//System.out.println(e + " ERROR Client Side Exception port in create_voter: " + port);
			try{ 
				Thread.sleep(2000);
			}
			catch(InterruptedException ie){
				//System.out.println("Went to sleeep for this port: " + port);
			}
			connection = false;
		}
		}
		System.out.println("Exiting create_voter port: " + port);
		if(port>lead){ 
			//System.out.println("Going for the totalmessages " + port);
			totalMessage(port);
			}
		return;
	}
	
	public void totalMessage(int port){
		//try creating a new socket that repondes to the server 3000
		boolean connection = true;
		while(connection){		
		try {
					System.out.println("Entering Sending info " + port + " vote_to " + 3000);
					Socket cl = new Socket("0.0.0.0", 3000);
					PrintWriter output = new PrintWriter(cl.getOutputStream(), true);
					total_message++;
					output.println(total_message);
					String response ="";
					System.out.println("entered my total_votes " + port);
					BufferedReader socket_input = new BufferedReader(new InputStreamReader(cl.getInputStream()));
					response = socket_input.readLine();
					connection = false;
					//***************** I CANNOT HAVE THIS CLOSING **************
					cl.close(); 
					return;
		
				} catch (IOException e) {
					//System.out.println("ERROR IN TOTALMESSAGE Client Side Exception port: " + port);
					try{
						Thread.sleep(2000);
					}
					catch(InterruptedException ie){
						//System.out.println("Went to sleeep for this port: " + port);
					}
					connection = false;
				}
		}
	}
	public void addingvotes(list_c ob){
		
	}

	public void lead_connect(int clientnum, int port, boolean lead){
		System.out.println("Inside lead_connect " + port);
		boolean connection = true;
	while(connection){
		try {
			System.out.println("Conection to 3000 " + port + " vote_to " + 3000);
			Socket cl = new Socket("0.0.0.0", 3000);
			String response ="";
		while(lead!=true && response!=null && response.indexOf("lead")==-1){
			System.out.println("***** IN LEAD CONNECT ENTERED IN THE WHILE LOOP " + port);
			BufferedReader socket_input = new BufferedReader(new InputStreamReader(cl.getInputStream()));
			response = socket_input.readLine();
		}
		//as soon as you get that then you can set the connection to false and end it and 
		//and also send out the number of messages send in total
		PrintWriter output = new PrintWriter(cl.getOutputStream(), true);
		if(lead){
			//its a leader
			output.println("lead "+clientnum);
		}
		total_message+=2;
		output.println(total_message);
		connection = false;
		leader_connection = false;
		//***************** I CANNOT HAVE THIS CLOSING **************
		System.out.println(" done with lead_connect *************** " + port);
			cl.close();
		} catch (IOException e) {
			//System.out.println("ERROR IN TOTALMESSAGE Client Side Exception port: " + port);
			connection = false;
		}
		}
	}
	
	
	
}
