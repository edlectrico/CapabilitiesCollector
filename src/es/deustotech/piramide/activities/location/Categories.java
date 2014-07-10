/*
 * Copyright (C) 2010 PIRAmIDE-SP3 authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This software consists of contributions made by many individuals, 
 * listed below:
 *
 * Author: Eduardo Castillejo <eduardo.castillejo@deusto.es>
 *
 */

package es.deustotech.piramide.activities.location;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.views.AbstractActivity;
import es.deustotech.piramide.activities.options.Help;
import es.deustotech.piramide.services.LocationService;
import es.deustotech.piramide.utils.constants.Constants;
import es.deustotech.piramide.utils.net.GoogleLocalClient;
import es.deustotech.piramide.utils.net.GoogleLocalClient.GoogleLocalException;
import es.deustotech.piramide.utils.parcelable.Point;

@SuppressLint("NewApi")
public class Categories extends Activity implements TextToSpeech.OnInitListener{
	//TODO: show a list of some points of interest near our location
	private static Class<?> onLoadActivity;
	private final Class<Directions> defaultActivity = Directions.class;
	private static Context currentContext;
	private Vibrator vibrator;
	private TextToSpeech tts;
	private static Intent locationService;
	private static String selection = "";
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			Toast.makeText(getApplicationContext(), "Imposible conectar con la red...", Toast.LENGTH_SHORT);
		}
	};
	
	private static List<String> backgrounds, textViews;
	
	public static String getSelection(){
		return selection;
	}
	
	private static class ImageConfig{
		private final int unpressedDrawable;
		private final int pressedDrawable;
		private final String uri;
		
		public ImageConfig(int unpressedDrawable, int pressedDrawable, String uri) {
			this.unpressedDrawable = unpressedDrawable;
			this.pressedDrawable = pressedDrawable;
			this.uri = uri;
		}
		
		public int getUnpressedDrawable() {
			return unpressedDrawable;
		}

		public int getPressedDrawable() {
			return pressedDrawable;
		}
		

		public String getUri() {
			return uri;
		}
	}
	
	final Map<Integer, ImageConfig> images = new HashMap<Integer, Categories.ImageConfig>();
	{
		images.put(R.id.img_bar_button, new ImageConfig(R.drawable.bar_normal,R.drawable.bar_pressed, "bar"));
		images.put(R.id.img_restaurant_button, new ImageConfig(R.drawable.restaurant_normal,R.drawable.restaurant_pressed, "restaurant"));
		images.put(R.id.img_cafe_button, new ImageConfig(R.drawable.cafe_normal,R.drawable.cafe_pressed, "cafe"));
		images.put(R.id.img_hotel_button, new ImageConfig(R.drawable.hotel_normal,R.drawable.hotel_pressed, "lodging"));
		images.put(R.id.img_museums_button, new ImageConfig(R.drawable.museum_normal,R.drawable.museum_pressed, "embassy"));
		images.put(R.id.img_gas_button, new ImageConfig(R.drawable.gas_normal,R.drawable.gas_pressed, "gas_station"));
		images.put(R.id.img_transport_button, new ImageConfig(R.drawable.taxi_normal,R.drawable.taxi_pressed, "taxi_stand"));
		images.put(R.id.img_airport_button, new ImageConfig(R.drawable.airport_normal,R.drawable.airport_pressed, "airport"));
	}
	
	private void startLocationService() {
		//Referencing the LocationService
        LocationService.setMainActivity(this);
        locationService = new Intent(this, LocationService.class);
        startService(locationService);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.categories_menu_normal);

        startLocationService();
        currentContext = this.getApplicationContext();
        Log.d(Constants.TAG, "Launching Categories...");
        
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        createMenu();
        setResult(Constants.SUCCESS_RETURN_CODE, new Intent());
        
        textViews = AbstractActivity.getOntologyManager().getIndividualOfClass(getResources().getString(R.string.ontology_namespace) + "TextView");
        backgrounds = AbstractActivity.getOntologyManager().getIndividualOfClass(getResources().getString(R.string.ontology_namespace) + "Background");
        
        final Collection<OWLLiteral> textViewBackColor = AbstractActivity.getOntologyManager().getDataTypePropertyValue(textViews.get(0), getResources().getString(R.string.ontology_namespace) + "viewHasColor");
        final Collection<OWLLiteral> backgroundColor = AbstractActivity.getOntologyManager().getDataTypePropertyValue(backgrounds.get(0), getResources().getString(R.string.ontology_namespace) + "viewHasColor");
	
        final int viewColor = Integer.parseInt(((OWLLiteral) textViewBackColor.toArray()[0]).getLiteral());
        final int back = Integer.parseInt(((OWLLiteral) backgroundColor.toArray()[0]).getLiteral());
        
        TableLayout table = (TableLayout) findViewById(R.id.table_row);
		table.setBackgroundColor(Color.argb(255, Color.red(back), Color.green(back), Color.blue(back)));
		
		TableRow row1 = (TableRow) findViewById(R.id.row_1);
		TableRow row2 = (TableRow) findViewById(R.id.row_2);
		
		row1.setBackgroundColor(Color.argb(255, Color.red(viewColor), Color.green(viewColor), Color.blue(viewColor)));
		row2.setBackgroundColor(Color.argb(255, Color.red(viewColor), Color.green(viewColor), Color.blue(viewColor)));
	}
	
	public static Context getContext(){
		return currentContext;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume() {
		if (tts != null){
			speak("Listando categorías");
			for(String category : Constants.CATEGORIES)
				speak(category);
			
		}
		super.onResume();
	}
	
	private void createMenu() {
		onLoadActivity = defaultActivity; //default activity to be launched
		createButtons();
	}
	
	private void requestInterestedPoints(String selection)
							throws GoogleLocalException {
		final GoogleLocalClient client 		= new GoogleLocalClient(selection, Double.valueOf("43.2620151"), Double.valueOf("-2.9350757"));
		final Vector<Point> pointList 		= client.getPoints();
		Intent intent 						= new Intent(Categories.this, Points.class);
		String[] streetsArray 				= new String[pointList.size()];
		String[] titlesArray 				= new String[pointList.size()];
		double[] latitudesArray				= new double[pointList.size()];
		double[] longitudesArray			= new double[pointList.size()];
		//TODO: Use EncapsulatePoints
		//EncapsulatePoints encapsulatePoints = new EncapsulatePoints();

		for (int i=0; i<pointList.size(); i++){
			streetsArray[i] 	= pointList.get(i).getStreetAddress();
			titlesArray[i] 		= pointList.get(i).getTitle();
			latitudesArray[i] 	= Double.parseDouble(pointList.get(i).getLatitude());
			longitudesArray[i]	= Double.parseDouble(pointList.get(i).getLongitude());
		}

		final Bundle extras = putExtras(pointList, streetsArray, titlesArray,
				latitudesArray, longitudesArray);

		//		encapsulatePoints.setPoints(streetsArray, titlesArray);
		//		extras.putParcelable("points", encapsulatePoints);
		intent.setAction("points");
		intent.putExtras(extras);
		startActivityForResult(intent, 0);
	}
	
	private void createButtons(){
		for(Integer imageButtonId : this.images.keySet()){
			final ImageView currentImageView = (ImageView)findViewById(imageButtonId.intValue());
			final ImageConfig config = this.images.get(imageButtonId);
			currentImageView.setOnTouchListener(new View.OnTouchListener() {
				
				ProgressDialog dialog = null;
				final Handler dialogHandler = new Handler(){
					
					@Override
					public void handleMessage(Message msg){
						dialog.cancel();
					}
				};
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN)
					{
						currentImageView.setImageResource(config.getPressedDrawable());
						vibrator.vibrate(500);
						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									requestInterestedPoints(config.getUri());
									dialogHandler.sendMessage(dialogHandler.obtainMessage());
								} catch (GoogleLocalException e) {
									handler.sendMessage(handler.obtainMessage());
								}
							}
						});
						t.start();
						return true;
					}
					else if (event.getAction() == MotionEvent.ACTION_UP)
					{
						currentImageView.setImageResource(config.getUnpressedDrawable());
						dialog = ProgressDialog.show(Categories.this, "", 
		                        "Consultando lugares...", true);
						return true;
					}
					else return false;
				}
			});
		}
	}
	
	private Bundle putExtras(final Vector<Point> pointList, String[] streetsArray, 
			String[] titlesArray, double[] latitudesArray, 
			double[] longitudesArray) {
		Bundle extras = new Bundle();

		extras.putInt("POINTS_SIZE", titlesArray.length);
		
		for (int i=0; i<pointList.size(); i++){
			extras.putString(Constants.POINT + i + Constants.TITLE, titlesArray[i]);
			extras.putString(Constants.POINT + i + Constants.STREET, streetsArray[i]);
			extras.putDouble(Constants.POINT + i + Constants.LATITUDE, latitudesArray[i]);
			extras.putDouble(Constants.POINT + i + Constants.LONGITUDE, longitudesArray[i]);
		}

		return extras;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.points_options_menu, menu);
	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    /*case R.id.add_point:
		        addPoint();
		        return true;
		    case R.id.remove_point:
		        removePoint();
		        return true;*/
		    case R.id.help:
		    	if (tts != null){
					tts.stop();
		    	}
		    	showHelpActivity();
		    	return true;
		    case R.id.exit:
		    	exit();
		    	return true;
		    default:
		    	return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showHelpActivity() {
		if (tts != null){
			tts.stop();
		}
		startActivityForResult(new Intent(Categories.this, 
				Help.class), 0);
	}

	private void exit() {
		if (tts != null){
			tts.stop();
		}
		this.finish();
	}
	
	/*
	//TODO
	private void removePoint() { }
	//TODO
	private void addPoint() { }
	*/
	public static Class<?> getOnLoadActivity() {
		return onLoadActivity;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME){
			if (tts != null) {
				tts.stop();	
				tts.shutdown();
			}
			stopService(locationService);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onInit(int status) {
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = tts.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result tts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
               // Lanuage data is missing or the language is not supported.
                Log.e(Constants.TAG, "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.

                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
//                button.setEnabled(true);
                // Greet the user.
            	speak("Listando categorías");
            	
            	for(String category : Constants.CATEGORIES)
            		speak(category);
            }
        } else {
            // Initialization failed.
            Log.e(Constants.TAG, "Could not initialize TextToSpeech.");
        }
	}
	
	private void speak(String message){
        tts.speak(message,
            TextToSpeech.QUEUE_ADD,  // Drop all pending entries in the playback queue.
            null);
	}
	
	@Override 
	protected void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		stopService(locationService);
		this.finish();
		super.onDestroy();
	}
	
	
	@Override
	public void onBackPressed() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		stopService(locationService);
		this.finish();
		super.onBackPressed();
	}
}