public class WebData {
	Long statusId;
	Double latitude;
	Double longitude;
	String content;
	String keyword;
	public WebData(Long sid, Double la, Double lo, String c, String key) {
		this.statusId = sid;
		this.latitude = la;
		this.longitude = lo;
		this.content = c;
		this.keyword = key;
	}
}