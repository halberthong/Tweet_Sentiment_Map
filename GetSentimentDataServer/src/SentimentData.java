import java.util.*;
public class SentimentData {
	long statusId;
	String sentiment;
	double longitude;
	double latitude;
	Date createDate;
	public SentimentData(long sId, String se, double lo, double la, Date cd) {
		statusId = sId;
		sentiment = se;
		longitude = lo;
		latitude = la;
		createDate = cd;
	}
}
