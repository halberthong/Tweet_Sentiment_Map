
public class SentimentDataServer {
	/*
	 * This Server initiate four worker threads
	 * */
	private static SentimentDataServer sds;
	private static final int ThreadNumber = 4;
	public static void main(String[]args) {
		sds = new SentimentDataServer();
		sds.start();
	}
	private void start() {
		for (int i = 0; i < ThreadNumber; i++) {
			Thread tmpThread = new Thread(new SentimentGet());
			System.out.println("Sentiment Worker " + i + " is working");
			tmpThread.start();
		}
	}
}
