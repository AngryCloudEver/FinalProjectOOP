package application;

import java.io.BufferedReader;
import java.io.File;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import org.apache.commons.lang3.RandomStringUtils;

public class Main extends Application {
	// Main Stage
	private VBox mainContainer = new VBox();
	private VBox starter = new VBox();
	
	// Sub-Stage
	private HBox frontPageTitleController = new HBox();
	private HBox titleController = new HBox();
	private HBox yearController = new HBox();
	private HBox actorController = new HBox();
	private HBox typeController = new HBox();
	private HBox ratingController = new HBox();
	private HBox synopsisController = new HBox();
	private HBox posterController = new HBox();
	private HBox randomerController = new HBox();
	private HBox backerController = new HBox();
	private HBox againController = new HBox();
	
	// Elements
	private Text frontPageTitle = new Text();
	private Text title = new Text();
	private Text releasedYear = new Text();
	private Text notableActors = new Text();
	private Text showType = new Text();
	private Text rating = new Text();
	private Text showSynopsis = new Text();
	private ImageView poster = new ImageView();
	private Button randomer = new Button("Click Me");
	private Button backer = new Button("Back To Main Menu");
	private Button again = new Button("Again?");
	
	// JSON Objects
	private JSONObject movie;
	private JSONObject movieDetail;
	
	// Movie Object
	private Movie theMovie;
	
	// Value dari Key Movie
	private String id;
	private String movieTitle;
	private String releaseYear;
	private String mainActors;
	private String posterUrl;
	private String type;
	private String synopsis;
	private String contentRating;
	
	// Scene
	private Scene mainScene;
	private Scene resScene;
	private Integer mainCount=0;
	private Integer resCount=0;
	
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
	
	public void mainMenu(Stage primaryStage) {
		mainCount++;

		frontPageTitle.setText("The Movie Recommender");
		frontPageTitle.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
		// frontPageTitleController.getChildren().add(frontPageTitle);
		frontPageTitleController.setAlignment(Pos.BASELINE_CENTER);
		VBox.setMargin(frontPageTitleController, new Insets(20, 0, 20, 0));
		
		randomer.setPrefSize(100, 50);
		randomer.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
		randomer.setOnAction(f -> {
			resultScene(primaryStage);
		});
		
		// randomerController.getChildren().add(randomer);
		randomerController.setAlignment(Pos.CENTER);
		VBox.setMargin(randomerController, new Insets(20,0,20,0));
		
//		starter.getChildren().addAll(
//				frontPageTitleController,
//				randomerController
//		);
		
		if(mainCount==1) mainScene = new Scene(starter,400,300);
		primaryStage.setScene(mainScene);
		primaryStage.setTitle("The Movie Recommender");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	public void resultScene(Stage primaryStage) {
		resCount++;
		try {
			// Fetch Random Movie
			do {				
				String randomWord = RandomStringUtils.randomAlphabetic(3);
				movie = getRandomMovie(randomWord, false);
			} while (movie == null);
			
			// Store movie variable from main api url
			id = movie.get("#IMDB_ID").toString();
			movieTitle = movie.get("#TITLE").toString();
			
			try {
				releaseYear = movie.get("#YEAR").toString();
			} catch(Exception e) {
				releaseYear = "Year Not Available";
			}
			
			try {
				mainActors = movie.get("#ACTORS").toString();
			} catch(Exception e) {
				mainActors = "Actors Not Available";
			}
			
			try {
				posterUrl = movie.get("#IMG_POSTER").toString();
				// System.out.println(posterUrl);
			} catch(Exception e) {
				posterUrl = "https://img.freepik.com/premium-vector/white-exclamation-mark-sign-red-circle-isolated-white-background_120819-332.jpg?w=2000";
			}
			
			// releaseYear = movie.has("#YEAR") ? movie.get("#YEAR").toString() : "Year Not Available";
			// mainActors = movie.has("#ACTORS") ? movie.get("#ACTORS").toString() : "Actors Not Available";
			// posterUrl = movie.has("#IMG_POSTER") ? movie.get("#IMG_POSTER").toString() : "Poster Not Available";
			
			// Get Movie Details
			movieDetail = getRandomMovie(id, true);
			
			try {
				type = movieDetail.getString("@type");
			} catch (Exception e) {
				type = "Type Not Available";
			}
			
			try {
				synopsis = movieDetail.getString("description");
			} catch (Exception e) {
				synopsis = "Description Not Available";
			}
			
			try {
				contentRating = movieDetail.getString("contentRating");
			} catch (Exception e) {
				contentRating = "Content Rating Not Available";
			}
			
			// Buat Object
			theMovie = new Movie(id, movieTitle, releaseYear, mainActors, posterUrl, type, synopsis, contentRating);
			
			// Title
			title.setText(theMovie.getTitle());
			title.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 18));
			// titleController.getChildren().add(title);
			titleController.setAlignment(Pos.CENTER);
			VBox.setMargin(titleController, new Insets(20, 0, 0, 0));
			
			// Poster
			poster.setImage(new Image(theMovie.getPosterUrl()));
		    poster.setFitWidth(240);
		    poster.setFitHeight(180);
		    poster.setPreserveRatio(true);
		    // posterController.getChildren().add(poster);
		    posterController.setAlignment(Pos.BASELINE_CENTER);
			VBox.setMargin(posterController, new Insets(20, 0, 20, 0));
			
			// releasedYear
			releasedYear.setText(theMovie.getReleaseYear());
			releasedYear.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
			// yearController.getChildren().add(releasedYear);
			yearController.setAlignment(Pos.CENTER);
			VBox.setMargin(titleController, new Insets(20, 0, 0, 0));
			
			// Actors
			notableActors.setText(theMovie.getMainActors());
			notableActors.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
			// actorController.getChildren().add(notableActors);
			actorController.setAlignment(Pos.CENTER);
			VBox.setMargin(actorController, new Insets(20, 0, 0, 0));
			
			// Type
			showType.setText(theMovie.getType());
			showType.setFont(Font.font("verdana",FontWeight.BOLD, FontPosture.REGULAR, 12));
			//typeController.getChildren().add(showType);
			typeController.setAlignment(Pos.CENTER);
			VBox.setMargin(typeController, new Insets(20, 0, 0, 0));
			
			// Rating
			rating.setText(theMovie.getContentRating());
			rating.setFont(Font.font("verdana",FontWeight.BOLD, FontPosture.REGULAR, 12));
			// ratingController.getChildren().add(rating);
			ratingController.setAlignment(Pos.CENTER);
			VBox.setMargin(ratingController, new Insets(20, 0, 0, 0));
			
			// Synopsis
			showSynopsis.setText(theMovie.getSynopsis());
			showSynopsis.setFont(Font.font("verdana",FontWeight.BOLD, FontPosture.REGULAR, 11));
			showSynopsis.setWrappingWidth(590);
			showSynopsis.setTextAlignment(TextAlignment.CENTER);
			// synopsisController.getChildren().add(showSynopsis);
			synopsisController.setAlignment(Pos.CENTER);
			VBox.setMargin(synopsisController, new Insets(20, 0, 0, 0));
			
			// Backer
			backer.setPrefSize(200, 30);
			backer.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
			backer.setOnAction(h -> {
				mainMenu(primaryStage);
			});
			
			// backerController.getChildren().add(backer);
			backerController.setAlignment(Pos.CENTER);
			VBox.setMargin(backerController, new Insets(10, 0, 0, 0));
			
			// Again
			again.setPrefSize(100, 50);
			again.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
			again.setOnAction(g -> {
				resultScene(primaryStage);
			});
			
			// againController.getChildren().add(again);
			againController.setAlignment(Pos.CENTER);
			VBox.setMargin(againController, new Insets(10, 0, 0, 0));
			
//			mainContainer.getChildren().addAll(
//					posterController,
//					titleController,
//					yearController,
//					actorController,
//					typeController,
//					synopsisController,
//					ratingController,
//					againController,
//					backerController
//			);
			
			if(resCount==1) resScene = new Scene(mainContainer, 800, 600);
			resScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(resScene);
			primaryStage.setTitle("The Movie Recommender");
			primaryStage.setResizable(false);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		frontPageTitleController.getChildren().add(frontPageTitle);
		randomerController.getChildren().add(randomer);
		starter.getChildren().addAll(
				frontPageTitleController,
				randomerController
		);
		titleController.getChildren().add(title);
		posterController.getChildren().add(poster);
		yearController.getChildren().add(releasedYear);
		actorController.getChildren().add(notableActors);
		typeController.getChildren().add(showType);
		ratingController.getChildren().add(rating);
		synopsisController.getChildren().add(showSynopsis);
		backerController.getChildren().add(backer);
		againController.getChildren().add(again);
		mainContainer.getChildren().addAll(
				posterController,
				titleController,
				yearController,
				actorController,
				typeController,
				synopsisController,
				ratingController,
				againController,
				backerController
		);
		mainMenu(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
