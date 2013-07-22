package es.deusto.deustotech;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TesterActivity extends Activity implements android.view.View.OnClickListener{

	Button testButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(this);
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
		
		if (event.getAction() == event.ACTION_UP){
			//store
		}

		return super.onTouchEvent(event);
	}
	 	
	
	
}
