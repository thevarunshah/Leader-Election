package MattsSuperDuperElectionThatMayOrMayNotWork;

import java.util.ArrayList;

public class Election {
	
	public static final int NUM_ACTORS = 19;
	public static final int START_PORT = 9001;
	
	public static void main(String[] args) {
		
		ArrayList<Voter> voters = new ArrayList<Voter>();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		for (int p = START_PORT; p < (START_PORT + NUM_ACTORS); p++) {
			Voter v = new Voter(NUM_ACTORS, p);
			Thread t = new Thread(v);
			t.start();
			voters.add(v);
			threads.add(t);
		}
		
	}
	
}
