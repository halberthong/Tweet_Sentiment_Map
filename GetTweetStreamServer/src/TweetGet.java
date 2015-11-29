import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.FilterQuery;

public final class TweetGet {
	private static OperateDB db;
	private static TweetFilter filter;
	private static TweetGet tg;
	private static AmazonSQS sqs;
	private static String myQueueUrl;
	private static CreateQueueRequest createQueueRequest;
	
	public static void main(String[] args) {
		TweetGet.start();
	}
	
	private static void start() {
		db = new OperateDB();
		try {
			db.connect();
		} catch (Exception e) {
			Common.writeLog("Database Error", e);
		}
		AWSCredentials credentials = null;
        try {
            credentials = new PropertiesCredentials(
					TweetGet.class.getResourceAsStream("AwsCredentials.properties"));
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location, and is in valid format.", e);
        }
        sqs = new AmazonSQSClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);
        createQueueRequest = new CreateQueueRequest("TweetQueue");
        myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
		tg = new TweetGet();
		tg.getTweetStream();
    }
	public static void stop() {
		if (db != null) {
			db.close();
		}
	}
    private void getTweetStream() {
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true)
          .setOAuthConsumerKey(Credentials.oauthConsumerKey)
          .setOAuthConsumerSecret(Credentials.oauthConsumerSecret)
          .setOAuthAccessToken(Credentials.oauthAccessToken)
          .setOAuthAccessTokenSecret(Credentials.oauthAccessTokenSecret);
    	
    	filter = new TweetFilter();
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
            	if (status.getGeoLocation() != null) {
            		String keyword = filter.keywordMatch(status.getText());
            		TweetData tweetData = new TweetData(status.getId(),
            											status.getUser().getId(),
            											status.getUser().getScreenName(),
            											status.getText(),
            											status.getGeoLocation().getLongitude(),
            											status.getGeoLocation().getLatitude(),
            											status.getCreatedAt(),
            											keyword);
            		try {
						db.addRowToTweetTable(tweetData);
					} catch (Exception e) {
						Common.writeLog("Database Error", e);
					}
            		StringBuilder sb = new StringBuilder();
            		sb.append(tweetData.statusId);
            		sb.append('\n');
            		sb.append(tweetData.content);
            		sb.append('\n');
            		sb.append(tweetData.longitude);
            		sb.append('\n');
            		sb.append(tweetData.latitude);
            		sb.append('\n');
            		sb.append(tweetData.createDate.toString());
                    sqs.sendMessage(new SendMessageRequest(myQueueUrl, sb.toString()));
            	}
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }

            @Override
            public void onException(Exception ex) {
            }
        };
        FilterQuery tweetFilter = new FilterQuery();
        String[] keywords = {"ball", "hockey", "athlete", "soccer", "sports", "playground", "nba", "olympic", "tennis", "gym", "race", 
        		"tech", "computer", "hardware", "software", "google", "facebook", "apple", "tesla", "hi-tech", "technology", "silicon", "iwatch", "iphone", "electronic",
        		"music", "concert", "jazz", "guitar", "rhythm", "rock", "dance","opera", "blues", "hip-hop", "itunes", "pandora", "singer", "grammy",
        		"cinema", "movie", "film", "theatre","show","screen", "action","trailer","scene", "oscar", "amc",
        		"eat", "pizza", "yummy", "food", "pasta", "noodle", "rice", "tea", "steak", "lunch", "dinner", "breakfast", "brunch"
        };
        tweetFilter.track(keywords);
        twitterStream.addListener(listener);
        twitterStream.filter(tweetFilter);
    }
}