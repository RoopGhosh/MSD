package apis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

/*
@Auth: Roop
	this class should be used to obtain event lists from the the given set of criteria.
	the input would be in form of a formed URL which will return JSON from EventBrite REST apis
	which is then parsed to obtain the Event object.
	CURRENTLY, event object is not described hence just parsing for the title text of each event */

public class EventBrite_API_Call {
	public static String getJsontext(String url) throws IOException
	{
		URL neturl = new URL(url);
		BufferedReader br = new BufferedReader(new InputStreamReader(neturl.openStream()));
		String jsontext = "";
		String temp;
		while((temp=br.readLine())!=null)
			jsontext = jsontext.concat(temp);
		return jsontext;
	}
	
	public static void main(String args[]) throws IOException, JSONException
	{
		ArrayList<String> LoEvents = new ArrayList<>();
		System.out.println("Enter the url you want to retrive with your key");
		Scanner s = new Scanner(System.in);
		String url = s.nextLine();
		s.close();
		if(!url.contains("&format=json"))
			url = url.concat("&format=json");
		String jsontext = getJsontext(url);
		//System.out.println(jsontext);
		//creating jsonobject from text
		JSONObject json = new JSONObject(jsontext);
		int page_size = hasPagination(json);
		for(int j=0;j<page_size;j++) // now for no of page of results, we loop...
		{
			url = url.concat("&page="+j);
			jsontext = getJsontext(url);
			json = new JSONObject(jsontext);
			org.json.JSONArray listings = json.getJSONArray("events"); // getting the actual Events array.
			System.out.println(j);
			for(int i=0;i<listings.length();i++) // for every event in the array, retrving the required materials.
			{
				JSONObject iterateObj = listings.getJSONObject(i);
				JSONObject temp = iterateObj.getJSONObject("name"); // just getting the "name" for the time being.
				String t = temp.toString().split("text")[1].replaceAll("}", "").replaceAll("\"", "").replaceAll(":", "");
				LoEvents.add(t);
				//.getJSONObject("text")
			}
		}
		System.out.println(LoEvents.size());
		if(Integer.parseInt(json.getJSONObject("pagination").get("page_count").toString()) == LoEvents.size())
			System.out.println("Write on !!");
		System.out.println("DONE!!");
	}
	public static int hasPagination(JSONObject json) throws NumberFormatException, JSONException {
		int page_size= 1;
		if(json.has("pagination")) // checking if the page has pagination tag to summarize the returns
			page_size =Integer.parseInt(json.getJSONObject("pagination").get("page_count").toString());
		return page_size;
	}
}