import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OperateDB {
	private Connection connect = null;
	String dbName = "TwittInfo";
	String userName = Credentials.DBname;
	String password = Credentials.DBpassword;
	String hostname = Credentials.DBHostName;
	String port = "3306";
	String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
	
	public boolean isConnected() {
		return connect != null;
	}
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager.getConnection(jdbcUrl);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			Common.writeLog("Error", e);
		}
	}

	public void addRowToTweetTable(TweetData tweetData) throws Exception {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connect.prepareStatement("INSERT INTO FilteredTweetData "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			preparedStatement.setLong(1, tweetData.statusId);
			preparedStatement.setLong(2, tweetData.userId);
			preparedStatement.setString(3, tweetData.screenName);
			preparedStatement.setString(4, tweetData.content);
			preparedStatement.setDouble(5, tweetData.longitude);
			preparedStatement.setDouble(6, tweetData.latitude);
			preparedStatement.setTimestamp(7, new java.sql.Timestamp(tweetData.createDate.getTime()));
			preparedStatement.setString(8, tweetData.keyword);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			Common.writeLog("SQL Error", e);
		}
	}
	
	public void autoDelete() {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connect.prepareStatement("DELETE FROM FilteredTweetData ORDER BY statusId LIMIT 100");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			Common.writeLog("SQL Error, Unable to auto delete", e);
		}
	}
	
	public int getEntriesCount() {
		int res = 0;
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connect.prepareStatement("SELECT COUNT(1) FROM FilteredTweetData");
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			res = resultSet.getInt(1);
		} catch (SQLException e) {
			Common.writeLog("SQL Error", e);
		}
		return res;
	}
	
	public void close() {
		try {
			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			Common.writeLog("Error in closing the database", e);
		}
	}
}
