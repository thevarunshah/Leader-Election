package communicator;

public class InThread implements Runnable {
	
	public InThread() {
		
	}

	@Override
	public void run() {
		
		while(true) {
			
			Message msg = Message.read(in);
			
		}
		
	}

}
