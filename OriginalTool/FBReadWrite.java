import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FBReadWrite {
	public String fbFeedURL = "";
	public long nPosts = 0;
	public long nCommentsCumulative = 0;
	public String lastPostDate = "";
	
	private String accessToken = "";
	private String fbID="";
	private String dirName = "";
	private boolean dirCreated = false;
	private long feedPageNum = 0;
	private JSONArray jArray;
	private String sinceString = "";
	private String createdTime = "";
	private File saveDirectory = new File(System.getProperty("user.dir"));

	public FBReadWrite (String fid, File saveDir, String aToken) {
		accessToken = aToken;
		fbID = fid;
		fbFeedURL = FBTools.makeFBFeedURL(fbID,accessToken);
		dirName = saveDir.getPath() + "/" + fbID + (Calendar.getInstance()).getTimeInMillis();
	}
	public FBReadWrite (String fid, File saveDir, String aToken, String until, String since) {
		accessToken = aToken;
		fbID = fid;
		saveDirectory = saveDir;
		sinceString = "&since=" + since; // since gets lost from NEXT URL and has to be added on
		fbFeedURL = FBTools.makeFBFeedURL(fbID,accessToken,until,since);
		dirName = saveDir.getPath() + "/" + fbID + (Calendar.getInstance()).getTimeInMillis();
	}
	
	public boolean readWrite () throws Exception {
	
		boolean endOfFeedData = false;
		boolean endOfCommentData = false;

		JSONObject fbFeed = null;
		String fbCommentsURL = "";

		long commentPageNum = 0;
		long nCommentsSaved = 0;
		
		JSONObject jsonPageObj;
		JSONObject fbo;

		String feedPostID = "";	
		long nComments = 0;
		nCommentsCumulative = 0;
		
		//System.out.print("Reading a page...");
		try {		
			fbFeed = FBTools.getFeedAsJSON(fbFeedURL);
			if (fbFeed.equals(null)) {
				endOfFeedData = true;
			}
			else {
				// Create a directory to hold the files
				if (!dirCreated) {
					dirCreated = (new File(dirName)).mkdirs();
				}
				feedPageNum++;
				// Save one page of posts from the News Feed
				saveFeedPage(fbFeed, dirName, feedPageNum);
				//fbPageArray.add(fbFeed);
				fbFeedURL = FBTools.getNext(fbFeed) + sinceString; // since gets lost from NEXT URL and has to be added 
			}
		}
		catch (NullPointerException ex) {
			endOfFeedData = true;
		}
		//System.out.println("Reading comments...");
		
		jArray = (JSONArray)fbFeed.get("data"); // jArray holds onto page from the FB news feed
		nPosts = jArray.size();
			
		//Loop to retrieve comments for individual posts
		for (int post=0 ; post<jArray.size() ; post++) {
			commentPageNum = 0;
			fbo = (JSONObject) jArray.get(post);
			nComments = FBTools.getNComments(fbo);
			lastPostDate = FBTools.getCreatedTime(fbo);

			if (nComments > 0) {
				feedPostID = (String)fbo.get("id");
				fbCommentsURL = FBTools.makeFBCommentsURL(feedPostID,accessToken);
				endOfCommentData = false;
					
				while (!endOfCommentData) { //Loops through successive pages of the comments for a post
					commentPageNum++;
					try {		
						fbFeed = FBTools.getFeedAsJSON(fbCommentsURL);
						if (fbFeed.equals(null)) {
							endOfCommentData = true;
						}
						else {
							// Save one page of posts from the News Feed
							nCommentsSaved = saveComments(fbFeed, dirName, feedPageNum, commentPageNum, feedPostID);
							nCommentsCumulative = nCommentsCumulative + nCommentsSaved;
							fbCommentsURL = FBTools.getNext(fbFeed);
							}
					}
					catch (NullPointerException ex) {
						endOfCommentData = true;
					}
				}
			}
		}
	//System.out.println(jArray.size() + " posts processed...(wait)");
	jArray.clear(); // Finished processing one news feed page
	//System.out.println ("Done.");
	
	return(endOfFeedData);
	}
	public static void saveFeedPage(JSONObject jPage, String dir, long page) throws Exception {
		String dirOut = dir + "/page" + page;
		String fileOut = dirOut + "/feed" + ".json";		
		FileWriter fstream;
		BufferedWriter out = null;
					
		try {
			(new File(dirOut)).mkdirs();		
			fstream = new FileWriter(fileOut);
			out = new BufferedWriter(fstream);
			//System.out.println("Writing " + jPage);
			out.write(jPage.toString());
			out.newLine();
		}
		catch (Exception e){//Catch exception if any
  			System.err.println("Error opening: " + fileOut);
		}
		finally {
			out.close();
		}
	}
public static long saveComments(JSONObject jPage, String dir, long FeedPage, long commentPage, String postID) throws Exception {
		String dirOut = dir + "/page" + FeedPage + "/comments" + "/post" + postID;
		String fileOut = dirOut + "/page" + commentPage + ".json";
		FileWriter fstream;
		BufferedWriter out = null;
		long nComments;
					
		try {
			(new File(dirOut)).mkdirs();		
			fstream = new FileWriter(fileOut);
			out = new BufferedWriter(fstream);
			//System.out.println("Writing " + jPage);
			out.write(jPage.toString());
			out.newLine();
		}
		catch (Exception e){//Catch exception if any
  			System.err.println("Error opening: " + fileOut);
		}
		finally {
			out.close();
		}
		nComments = ((JSONArray)jPage.get("data")).size();
		return (nComments);
	}
}