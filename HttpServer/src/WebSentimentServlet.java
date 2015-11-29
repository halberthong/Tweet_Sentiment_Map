import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.List;

public class WebSentimentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private OperateDB odb;

	public WebSentimentServlet() {
		super();
		odb = new OperateDB();
		odb.connect();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int minutes = 60;
		try {
			if (request.getParameter("minutes") != null) {
				minutes = Integer.parseInt(request.getParameter("minutes"));
			}
		} catch (Exception e) {
			Common.writeLog("Incorrect data from front end", e);
		}
		List<SentimentData> data = odb.getSentimentDataByMinutes(minutes);
		PrintWriter pr = response.getWriter();
		if (data.size() == 0) {
			pr.write("no_matching");
		} else {
			pr.write("start\n");
			for (SentimentData ele : data) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("statusId", String.valueOf(ele.statusId));
					obj.put("sentiment", ele.sentiment);
					obj.put("longitude", ele.longitude);
					obj.put("latitude", ele.latitude);
				} catch (JSONException e) {
					Common.writeLog("JSON Error", e);
				}
				pr.write(obj.toString() + "\n");
			}
			pr.write("end");
		}
		pr.close();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
	}

}
