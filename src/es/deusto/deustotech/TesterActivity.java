package es.deusto.deustotech;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

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
		
	}

	 @Override
	   public boolean onTouchEvent(MotionEvent event) {
		 float x = event.getRawX();
		 float y = event.getRawY();
		 
		 System.out.println("Coordinates: X: " + x + "; Y: " + y);
		 
		 testButton.setHeight((int) x);
		 testButton.setWidth((int) y);
		 testButton.invalidate();
		 
		 return super.onTouchEvent(event);
	   }
	
}
