package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.commons.lang3.RandomStringUtils;

public class Main extends Application {
	private VBox mainContainer = new VBox();
	private HBox titleController = new HBox();
	private Text title = new Text();
	
	private JSONObject movie;
	private JSONObject movieDetail;
	
	// Value dari Key Movie
	private String id;
	private String movieTitle;
	private String releaseYear;
	private String mainActors;
	private String posterUrl;
	private String type;
	private String synopsis;
	private String contentRating;
	
	private String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	}

	public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	 }
	
	private JSONObject getRandomMovie(String keyword, Boolean isDetail) {
		JSONObject json = null;
		
		// Fetching Dari URL
		try {
			if(!isDetail) {				
				json = readJsonFromUrl(("https://search.imdbot.workers.dev/?q=" + keyword));
			}
			else {
				json = readJsonFromUrl(("https://search.imdbot.workers.dev/?tt=" + keyword));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		// Pecah isi description jadi array of json objects
		ArrayList<JSONObject> listdata = new ArrayList<JSONObject>();
		
		JSONArray jArray = null;
		
		try {
			if(!isDetail) {				
				jArray = (JSONArray)json.get("description");
			}
			else {
				json = (JSONObject) json.get("short");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if (jArray != null && !isDetail) { 
			int randomChoosing = (int) Math.floor(Math.random() * (jArray.length() - 1));
			
			try {
				listdata.add(jArray.getJSONObject(randomChoosing));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return listdata.size() > 0 ? listdata.get(0) : null;
		}
		else {
			return json;
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {	
		try {
			// Fetch Random Movie
			do {				
				String randomWord = RandomStringUtils.randomAlphabetic(3);
				movie = getRandomMovie(randomWord, false);
			} while (movie == null);
			
			// Store movie variable from main api url
			id = movie.get("#IMDB_ID").toString();
			movieTitle = movie.get("#TITLE").toString();
			releaseYear = movie.get("#YEAR").toString();
			mainActors = movie.get("#ACTORS").toString();
			posterUrl = movie.get("#IMG_POSTER") != null ? movie.get("#IMG_POSTER").toString() : "Poster Not Available";
			
			// Get Movie Details
			movieDetail = getRandomMovie(id, true);
			
			type = movieDetail.getString("@type");
			synopsis = movieDetail.getString("description") != null ? movieDetail.get("description").toString() : "Description Not Available";
			contentRating = movieDetail.getString("contentRating") != null ? movieDetail.get("contentRating").toString() : "Content Rating Not Available";
			
			// Buat Object
			Movie theMovie = new Movie(id, movieTitle, releaseYear, mainActors, posterUrl, type, synopsis, contentRating);
			
			title.setText(theMovie.getTitle());
			title.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
			
			titleController.getChildren().add(title);
			titleController.setAlignment(Pos.CENTER);
			VBox.setMargin(titleController, new Insets(30, 0, 0, 0));
			
			mainContainer.getChildren().addAll(
				titleController
			);
			
			Scene scene = new Scene(mainContainer, 600, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
