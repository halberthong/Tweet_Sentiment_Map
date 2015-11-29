import java.util.*;


public class TweetFilter  {
	HashMap<String, ArrayList<String>> map;
	public TweetFilter() {
		map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> sports = new ArrayList<String>();
		sports.add("ball");
		sports.add("soccer");
		sports.add("sports");
		sports.add("hockey");
		sports.add("athlete");
		sports.add("playground");
		sports.add("nba");
		sports.add("olympic");
		sports.add("tennis");
		sports.add("gym");
		sports.add("race");

		ArrayList<String> tech = new ArrayList<String>();
		tech.add("tech");
		tech.add("computer");
		tech.add("hardware");
		tech.add("software");
		tech.add("Google");
		tech.add("facebook");
		tech.add("apple");
		tech.add("tesla");
		tech.add("hi-tech");
		tech.add("technology");
		tech.add("silicon");
		tech.add("iwatch");
		tech.add("iphone");
		tech.add("electronic");
		ArrayList<String> music = new ArrayList<String>();
		music.add("music");
		music.add("concert");
		music.add("jazz");
		music.add("guitar");
		music.add("rhythm");
		music.add("rock");
		music.add("dance");
		music.add("opera");
		music.add("blues");
		music.add("hip-hop");
		music.add("itunes");
		music.add("pandora");
		music.add("singer");
		music.add("grammy");
		ArrayList<String> movie = new ArrayList<String>();
		movie.add("cinema");
		movie.add("movie");
		movie.add("film");
		movie.add("cinema");
		movie.add("theatre");
		movie.add("show");
		movie.add("screen");
		movie.add("action");
		movie.add("trailer");
		movie.add("scene");
		movie.add("oscar");
		movie.add("amc");
		ArrayList<String> food = new ArrayList<String>();
		food.add("eat");
		food.add("yummy");
		food.add("food");
		food.add("pizza");
		food.add("pasta");
		food.add("noodle");
		food.add("rice");
		food.add("tea");
		food.add("steak");
		food.add("lunch");
		food.add("dinner");
		food.add("breakfast");
		food.add("brunch");
		
		map.put("sports", sports);
		map.put("tech", tech);
		map.put("music", music);
		map.put("movie", movie);
		map.put("food", food);
	}
	
	public String keywordMatch(String str)
	{
		if(str == null || str.length() == 0)
			return "general";
		str = str.toLowerCase();
		for(String keyword : map.keySet())
			for(String matching : map.get(keyword))
				if(str.contains(matching))
					return keyword;
		return "general";
	}
}
