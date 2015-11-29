public class SentimentData {
	long statusId;
	String sentiment;
	double longitude;
	double latitude;
	public SentimentData(long sId, String se, double lo, double la) {
		statusId = sId;
		sentiment = se;
		longitude = lo;
		latitude = la;
	}
}
