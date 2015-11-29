import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.Timer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;


public final class SentimentGet {
	private static OperateDB odb;
	private static SentimentGet sg;
	private static AmazonSQS sqs;
	private static String myQueueUrl;
	private static CreateQueueRequest createQueueRequest;
	private static Timer timer;
	private static DateFormat df;
	private static AmazonSNS sns;
	private static final String TopicArn = "arn:aws:sns:us-east-1:632600293075:TwittMapData";
	
	public static void main(String[] args) {
		timer = new Timer();
		SentimentGet.start();
		df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
	}
	public void setMonitor() {
    	long start = 1000;
    	long interval = 1000 * 2;
    	TimerTask tsk = new TimerTask() {
    		@Override
    		public void run() {
    			try {
					sg.getQueueData();
				} catch (NumberFormatException e) {
					Common.writeLog("Number Format Error", e);
				} catch (Exception e) {
					Common.writeLog("Error", e);
				}
    		}
    	};
    	timer.scheduleAtFixedRate(tsk, start, interval);
    }
	private static void start() {
		odb = new OperateDB();
		try {
			odb.connect();
		} catch (Exception e) {
			Common.writeLog("Database Error", e);
		}
		AWSCredentials credentials = null;
        try {
            credentials = new PropertiesCredentials(
					SentimentGet.class.getResourceAsStream("AwsCredentials.properties"));;
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location, and is in valid format.", e);
        }
        
        // SQS
        sqs = new AmazonSQSClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);
        createQueueRequest = new CreateQueueRequest("TweetQueue");
        myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
        
        // SNS
        sns = new AmazonSNSClient(credentials);
        sns.setRegion(usEast1);
        
		sg = new SentimentGet();
		sg.setMonitor();
	}
	public void getQueueData() throws NumberFormatException, Exception {
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
        	String[] data = message.getBody().split("\n");
        	if (data.length != 5) continue;
        	String messageRecieptHandle = messages.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageRecieptHandle));
            long sId = Long.valueOf(data[0]);
            String sentiment = getSentimentType(data[1]);
            double lo = Double.valueOf(data[2]);
            double la = Double.valueOf(data[3]);
            java.util.Date cd = df.parse(data[4]);
            odb.addRowToTweetTable(new SentimentData(sId, sentiment, lo, la, cd));
            sg.sendNotification(sId, sentiment);
        }
	}
	public String getSentimentType(String input) throws UnsupportedEncodingException {
		String apikey = Credentials.sentimentApiKey;
		String encoded = URLEncoder.encode(input, "UTF-8");
		String sentiment = HttpRequest.get("http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey="+ apikey +"&text=" + encoded + "&outputMode=json").body();
		if (sentiment.indexOf("ERROR") == -1) {
			int type_pos = sentiment.indexOf("type");
			String stype = sentiment.substring(type_pos + 8, type_pos + 16);
			if(stype.charAt(7) == '"')
				stype = stype.substring(0, 7);
			return stype;
		} else {
			return "error";
		}
	}
	public void sendNotification(long sId, String sentiment) {
		String toSend = String.valueOf(sId) + "\n" + sentiment;
		sns.publish(TopicArn, toSend);
	}
	public static void stop() {
		if (odb != null) {
			odb.close();
		}
    }
}