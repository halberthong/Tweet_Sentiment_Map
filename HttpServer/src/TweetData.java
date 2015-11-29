import java.util.*;

public class TweetData {
	long statusId;
	long userId;
	String screenName;
	String content;
	double longitude;
	double latitude;
	Date createDate;
	String keyword;
		
	public TweetData (long sid, long uid, String sname, String t, double lo, double la, Date d, String key) {
		this.statusId = sid;
		this.userId = uid;
		this.screenName = sname;
		this.content = t;
		this.longitude = lo;
		this.latitude = la;
		this.createDate = d;
		this.keyword = key;
	}
}