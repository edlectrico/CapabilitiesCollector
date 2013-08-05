package es.deusto.deustotech.views;

import es.deusto.deustotech.R;
import es.deusto.deustotech.R.layout;
import es.deusto.deustotech.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BrightnessConfigActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brightness_config);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.brightness_config, menu);
		return true;
	}

}
