package es.deusto.deustotech.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import es.deusto.deustotech.R;

/**
 * This activity shows a Button and a TextEdit as configured in previous activities
 * and changes dynamically the screen brightness for the user to easily check if 
 * he/she is able to interact with the presented views
 * 
 * @author edlectrico
 *
 */
public class BrightnessConfigActivity extends Activity {

	private Button button;
	private EditText editText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brightness_config);
		
		Bundle bundle = getIntent().getExtras();
		
		//Button config
		findViewById(R.id.test_button).setMinimumWidth(Integer.parseInt(bundle.getString(getResources().getString(R.string.button_width))));
		findViewById(R.id.test_button).setMinimumHeight(Integer.parseInt(bundle.getString(getResources().getString(R.string.button_height))));
		findViewById(R.id.test_button).setBackgroundColor(Integer.parseInt(bundle.getString(getResources().getString(R.string.button_background_color))));
		
		//EditText config
		((EditText)findViewById(R.id.test_text_edit)).setTextSize(Integer.parseInt(bundle.getString(getResources().getString(R.string.edit_text_size))));
		((EditText) findViewById(R.id.test_text_edit)).setTextColor(Integer.parseInt(bundle.getString(getResources().getString(R.string.edit_text_text_color))));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.brightness_config, menu);
		return true;
	}

}
