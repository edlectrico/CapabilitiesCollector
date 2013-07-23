package es.deusto.deustotech;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

public class TesterActivity extends Activity implements android.view.View.OnClickListener{

	private static final String TAG = TesterActivity.class.getSimpleName();
	private Button testButton1;
	private Button testButton2;
	private SharedPreferences minimunViewPreferences;
	private GridLayout grid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		testButton1 = (Button) findViewById(R.id.test_button_1);
		testButton1.setOnClickListener(this);
		
		testButton2 = (Button) findViewById(R.id.test_button_2);
		testButton2.setOnClickListener(this);
		
		this.minimunViewPreferences = getSharedPreferences(getResources().getString(R.string.preferences_name_minui), 0);
		
		this.grid = (GridLayout) findViewById(R.id.default_layout);
		
		
		this.grid.getChildAt(0).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "Layout 0");
				
				return false;
			}
		});
		
		this.grid.getChildAt(1).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "Layout 1");
				
				float x = event.getRawX();
				float y = event.getRawY();
		
//				Log.d(TAG,"event.action" + event.getAction());
//				Log.d(TAG, "Coordinates: X: " + x + "; Y: " + y);
		
				testButton1.setHeight((int) x);
				testButton1.setWidth((int) y);
				testButton1.invalidate();
				
				if (event.getAction() == MotionEvent.ACTION_UP){
					//store
					SharedPreferences.Editor uiEditor = minimunViewPreferences.edit();
					Set<String> values = new HashSet<String>();
					values.add(String.valueOf(x));
					values.add(String.valueOf(y));
					uiEditor.putStringSet(getResources().getString(R.string.adapted_configuration_ui), values);
					uiEditor.commit();
					
					Log.d(TAG, "Stored!");
				}
				
				return true;
			}
		});
		
		this.grid.getChildAt(2).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "Layout 2");
				
				float x = event.getRawX();
				float y = event.getRawY();
		
//				Log.d(TAG,"event.action" + event.getAction());
//				Log.d(TAG, "Coordinates: X: " + x + "; Y: " + y);
		
				testButton2.setHeight((int) x);
				testButton2.setWidth((int) y);
				
				float textsize 		= testButton2.getTextSize();
				float viewHeight 	= testButton2.getHeight();
				
				float proportion = viewHeight / textsize;

				testButton2.invalidate();
				testButton2.setTextSize(proportion);
				
				
				if (event.getAction() == MotionEvent.ACTION_UP){
					//store
					SharedPreferences.Editor uiEditor = minimunViewPreferences.edit();
					Set<String> values = new HashSet<String>();
					values.add(String.valueOf(x));
					values.add(String.valueOf(y));
					uiEditor.putStringSet(getResources().getString(R.string.adapted_configuration_ui), values);
					uiEditor.commit();
					
					Log.d(TAG, "Stored!");
				}
				
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		Toast.makeText(getApplicationContext(), "This is a test!", Toast.LENGTH_SHORT).show();
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		float x = event.getRawX();
//		float y = event.getRawY();
//
//		Log.d(TAG,"event.action" + event.getAction());
//		Log.d(TAG, "Coordinates: X: " + x + "; Y: " + y);
//
//		testButton.setHeight((int) x);
//		testButton.setWidth((int) y);
//		testButton.invalidate();
//		
//		if (event.getAction() == MotionEvent.ACTION_UP){
//			//store
//			SharedPreferences.Editor uiEditor = minimunViewPreferences.edit();
//			Set<String> values = new HashSet<String>();
//			values.add(String.valueOf(x));
//			values.add(String.valueOf(y));
//			uiEditor.putStringSet(getResources().getString(R.string.adapted_configuration_ui), values);
//			uiEditor.commit();
//			
//			Log.d(TAG, "Stored!");
//		}
//
//		return super.onTouchEvent(event);
//	}
}
