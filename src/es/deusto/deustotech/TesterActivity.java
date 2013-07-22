package es.deusto.deustotech;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TesterActivity extends Activity implements android.view.View.OnClickListener{

	private Button testButton;
	private SharedPreferences minimunViewPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(this);
		
		this.minimunViewPreferences = getSharedPreferences(getResources().getString(R.string.preferences_name_minui), 0);
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getRawX();
		float y = event.getRawY();

		Log.d(TesterActivity.class.getSimpleName(),"event.action" + event.getAction());
		Log.d(TesterActivity.class.getSimpleName(), "Coordinates: X: " + x + "; Y: " + y);

		testButton.setHeight((int) x);
		testButton.setWidth((int) y);
		testButton.invalidate();
		
		if (event.getAction() == MotionEvent.ACTION_UP){
			//store
			SharedPreferences.Editor uiEditor = minimunViewPreferences.edit();
			Set<String> values = new HashSet<String>();
			values.add(String.valueOf(x));
			values.add(String.valueOf(y));
			uiEditor.putStringSet(getResources().getString(R.string.adapted_configuration_ui), values);
			uiEditor.commit();
		}

		return super.onTouchEvent(event);
	}
	 	
	
	
}
