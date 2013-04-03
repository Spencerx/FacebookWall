import java.io.*;
import java.net.*;

public class FBTools {

	public static String makeFBFeedURL(String pageID, String accessToken) {
		String urlIn="https://graph.facebook.com/" +
		pageID +
		"/feed?access_token="
		+ accessToken;
		return(urlIn);		
	}
	public static String makeFBFeedURL (String pageID, String accessToken, String untilStr, String sinceStr) {
		String urlIn = "https://graph.facebook.com/" +
		pageID +
		"/feed?access_token=" +
		accessToken +
		"&until=" +
		untilStr +
		"&since=" +
		sinceStr;
		return(urlIn);
	}
	public static String makeFBCommentsURL(String pageID, String accessToken) {
		String urlIn="https://graph.facebook.com/" +
		pageID +
		"/comments?access_token="
		+ accessToken;
		return(urlIn);		
	}
	/**
	Returns a String representing the content of a page from the FB feed
	*/
	public static String getFeedAsString(String urlIn) throws Exception {
		boolean endOfData=false;
		URL FBURL;
		URLConnection FByc;
		BufferedReader in = null;
		
		String jsonPageString;
		JSONObject jsonPageObj;
		
		try {	
			FBURL = new URL(urlIn);
			//System.out.println("urlIn at getPosts: " + urlIn);
			FByc = FBURL.openConnection();
			in = new BufferedReader(new InputStreamReader(FByc.getInputStream()));
		
			if ((jsonPageString = in.readLine()) != null) { //reads one page which is a JSON string
			}
			else {
				endOfData = true;
				jsonPageString = null;
			}
		}
		catch (NullPointerException e) {
			endOfData = true;
			jsonPageString = null;
		}
		finally {
			in.close();
		}
		return(jsonPageString);
	}	
	/**
	Returns a JSON Object representing the content of a page from the FB feed
	*/
	public static JSONObject getFeedAsJSON(String urlIn) throws Exception {
		
		String jsonPageString = "";
		JSONObject jsonPageObj = null;
		
		jsonPageString = getFeedAsString(urlIn);
		
		if (jsonPageString != null) {
				jsonPageObj = (JSONObject) JSONValue.parse(jsonPageString);
			}
		else {
				jsonPageObj = null;
			}
		return(jsonPageObj);
	}
	/**
	Returns an array of JSON Objects representing the content of a page from the FB feed
	*/
	public static JSONArray getFeedAsArray(String urlIn) throws Exception {
		
		String jsonPageString = "";
		JSONObject jsonPageObj = null;
		JSONArray jArray = null;
		
		jsonPageString = getFeedAsString(urlIn);
		
		if (jsonPageString != null) {
				jsonPageObj = (JSONObject) JSONValue.parse(jsonPageString);
				jArray = (JSONArray)jsonPageObj.get("data");
			}
		else {
				jsonPageObj = null;
			}
		return(jArray);
	}
	
	public static String getNext(JSONObject jobj) {
		JSONObject pagingJSONobj = (JSONObject) jobj.get("paging");
		return((String) pagingJSONobj.get("next"));
	}
	/**
	Takes a single post as a JSON object and returns the number of comments as an integer.
	*/
	public static long getNComments (JSONObject jobj) {
		JSONObject comments = (JSONObject)jobj.get("comments");
		Long nCommString = (Long) comments.get("count");
		return (nCommString.longValue());
	}
	/**
	Takes a single post as a JSON object and returns the created time.
	*/
	public static String getCreatedTime (JSONObject jobj) {
		return ((String)jobj.get("created_time"));
	}

}		