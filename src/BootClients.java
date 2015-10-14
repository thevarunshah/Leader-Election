import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;


public class BootClients {
	/**
	 * number of clients in the election
	 */
	static int numClients = 19;
	/**
	 * when to start the election
	 */
	static long startTime = System.currentTimeMillis() + (100*60);
	
	
	
	public static void main(String[] args) {

		/**
		 * 
		 * Thread which spins off an individual client
		 * 
		 * @author Varun Shah
		 *
		 */
		class BootThread extends Thread{

			/**
			 * port number of the client (added to 9000)
			 */
			int port;
			/**
			 * client number (unique id)
			 */
			int clientNum;
			int lead;
			/**
			 * @param port port number of the client
			 * @param clientNum client number
			 */
			public BootThread(int port, int clientNum, int lead){
				this.lead = lead;
				this.port = port;
				this.clientNum = clientNum;
			}

			/**
			 * spins off this specific client
			 */
			@Override
			public void run(){

				//System.out.println("Booting client " + this.clientNum);
				new Client(this.port, this.clientNum, numClients, startTime-System.currentTimeMillis(), lead);
			}
		}

		/*
		 * generate log base 2 leader ports
		 */
		Random random = new Random();
		//list of clients who will be leaders
		ArrayList<Integer> leaderNums = new ArrayList<Integer>();
		//number of leaders in the initial pool
		int numLeaders = (int) (Math.log(numClients)/Math.log(2));
		int lead = (int)((Math.random()*numLeaders) % numLeaders)+1;
		
		
		
		int number_leader = 1; int regular = 1;
		for(int i = 0; i<=numClients; i++){
			int port =0;
			int clientNum = i;
			if(i==0){}
			else if((((int)(Math.random()*2))==0 && number_leader<=numLeaders) || (regular==(numClients-numLeaders))){
				port = number_leader;//given a port number
				number_leader++; //leader has been added
			}
			else{
				//need to add in for a regular port
				port = regular + numLeaders;
				regular++;
			}
			Thread t = new BootThread(port, clientNum,lead);
			t.start();
		}
	
		System.out.println("Finished client boot");
	}
}
