import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * Spins off the different clients for the Leader-Election protocol
 * 
 * @author Varun Shah
 *
 */
public class BootClients {

	/**
	 * number of clients in the election
	 */
	static int numClients = 19;
	/**
	 * when to start the election
	 */
	static long startTime = System.currentTimeMillis() + (1000*60);

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

			/**
			 * @param port port number of the client
			 * @param clientNum client number
			 */
			public BootThread(int port, int clientNum){

				this.port = port;
				this.clientNum = clientNum;
			}

			/**
			 * spins off this specific client
			 */
			@Override
			public void run(){

				System.out.println("Booting client " + this.clientNum);
				new Client(this.port, this.clientNum, numClients, startTime-System.currentTimeMillis());
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

		//randomly select log base 2 leaders for the initial pool
		for(int i = 0; i < numLeaders; i++){

			int leaderNum = random.nextInt(numClients + 1);
			while(leaderNums.contains(leaderNum)){
				leaderNum = random.nextInt(numClients + 1);
			}
			leaderNums.add(leaderNum);
		}

		/*
		 * start up the clients
		 */
		System.out.println("Starting client boot");
		int leadersBooted = 0;
		for(int i = 0; i < numClients; i++){

			int port;
			int clientNum = i;

			//assign port numbers to leaders differently than voters
			if(leaderNums.contains(i)){
				port = leadersBooted;
				leadersBooted++;
			}
			else{
				port = numLeaders+i;
			}

			//create and start the client
			Thread t = new BootThread(port, clientNum);
			t.start();
		}
		System.out.println("Finished client boot");
	}
}
