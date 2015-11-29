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

	public void addRowToTweetTable(SentimentData sentimentData) throws Exception {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connect.prepareStatement("INSERT INTO SentimentData "
					+ "VALUES (?, ?, ?, ?, ?)");
			preparedStatement.setLong(1, sentimentData.statusId);
			preparedStatement.setString(2, sentimentData.sentiment);
			preparedStatement.setDouble(3, sentimentData.longitude);
			preparedStatement.setDouble(4, sentimentData.latitude);
			preparedStatement.setTimestamp(5, new java.sql.Timestamp(sentimentData.createDate.getTime()));			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			Common.writeLog("SQL Error", e);
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
