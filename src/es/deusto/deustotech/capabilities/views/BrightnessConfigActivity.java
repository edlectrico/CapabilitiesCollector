package es.deusto.deustotech.capabilities.views;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import es.deusto.deustotech.R;

/**
 * This activity shows a Button and a TextEdit as configured in previous activities
 * and changes dynamically the screen brightness for the user to easily check if 
 * he/she is able to interact with the presented views
 * 
 * @author edlectrico
 *
 */
public class BrightnessConfigActivity extends AbstractActivity {

	private static final String TAG = BrightnessConfigActivity.class.getSimpleName();

	private GridLayout grid;
	private Button btnNext;
	private TextView textViewCurrentLuxesLabel;
	private TextView textViewCurrentLuxes;
	private NumberPicker brightnessPicker;

	private float brightnessValue; // dummy default value
	private boolean brightnessChanged = false;
	private int currentLuxes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brightness_config_activity);

		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable(getResources().getString(R.string.view_params));

		grid = (GridLayout) findViewById(R.id.default_layout);

		brightnessPicker = (NumberPicker) findViewById(R.id.brightness_picker);
		brightnessPicker.setMinValue(0);
		brightnessPicker.setMaxValue(10);
		brightnessPicker.setBackgroundColor(userPrefs.getEditTextBackgroundColor());
		
		brightnessValue = brightnessPicker.getValue();

		textViewCurrentLuxesLabel = (TextView) findViewById(R.id.light_sensor_value_label);
		textViewCurrentLuxes = (TextView) findViewById(R.id.light_sensor_value);
		btnNext = (Button) findViewById(R.id.button_next_br);

		redrawViews();
		initializeServices(TAG);
		addListeners();
	}

	@Override
	public void initializeServices(String TAG) {
		if (userPrefs.getDisplayHasApplicable() == 0){
			super.initializeServices(TAG);

			speakOut(getResources().getString(R.string.edit_text_info_message_es));
		}

		SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		SensorEventListener lightSensorEventListener = new SensorEventListener(){
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) { }

			@Override
			public void onSensorChanged(SensorEvent event) {
				if(event.sensor.getType() == Sensor.TYPE_LIGHT){
					currentLuxes = (int)event.values[0];

					textViewCurrentLuxes.setText(currentLuxes + " luxes");
				}
			}
		};
		
		if (lightSensor != null){
			sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void addListeners() {
		btnNext.setOnClickListener(this);

		brightnessPicker.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				brightnessChanged = true;
				brightnessValue = newVal / 10F;
				Log.d("Brightness", String.valueOf(brightnessValue));
				WindowManager.LayoutParams layoutParams = getWindow()
						.getAttributes();
				layoutParams.screenBrightness = brightnessValue;
				getWindow().setAttributes(layoutParams);
			}
		});
	}

	@Override
	public void redrawViews() {
		if (userPrefs.getEditTextTextColor() != 0){
			((TextView)findViewById(R.id.brightness_message)).setTextColor(userPrefs.getEditTextTextColor());
			textViewCurrentLuxes.setTextColor(userPrefs.getEditTextTextColor());
			textViewCurrentLuxesLabel.setTextColor(userPrefs.getEditTextTextColor());
		}

		if (userPrefs.getEditTextBackgroundColor() != 0){
			((TextView)findViewById(R.id.brightness_message)).setBackgroundColor(userPrefs.getEditTextBackgroundColor());
			textViewCurrentLuxes.setBackgroundColor(userPrefs.getTextViewBackgroundColor());
			textViewCurrentLuxesLabel.setBackgroundColor(userPrefs.getTextViewBackgroundColor());
		}
		
		if (userPrefs.getTextViewTextSize() != 0){
			textViewCurrentLuxes.setTextSize(userPrefs.getTextViewTextSize() / 2);
			textViewCurrentLuxesLabel.setTextSize(userPrefs.getTextViewTextSize() / 2);
		}


		((TextView)findViewById(R.id.brightness_message)).setTextSize(userPrefs.getEditTextTextSize() / 2);

//		findViewById(R.id.button_next).setMinimumWidth((int)userPrefs.getButtonWidth());
//		findViewById(R.id.button_next).setMinimumHeight((int) userPrefs.getButtonHeight());
		btnNext.setTextSize(userPrefs.getButtonTextSize());
		btnNext.setTextColor(userPrefs.getButtonTextColor());
		btnNext.setBackgroundColor(userPrefs.getButtonBackgroundColor());

		if (ButtonConfigActivity.getLayoutBackgroundColorChanged()){
			grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_next_br){
			Intent intent = new Intent(this, VolumeConfigActivity.class);

			if (brightnessChanged){
				userPrefs.setBrightness(brightnessValue);
				brightnessChanged = false;
			} else {
				userPrefs.setBrightness(0);
			}
			
			userPrefs.setLuxes(currentLuxes);
			System.out.println("Luxes: " + currentLuxes);
			
			intent.putExtra(getResources().getString(R.string.view_params), userPrefs);
			intent.putExtra(getResources().getString(R.string.activity_caller), 1); //0 - MainActivity; 1 - BrightnessAtivity
			
			startActivity(intent);
			
			if (CapabilitiesActivity.getDisplayIsApplicable() == 0){
				speakOut("Well done!");
			}
		}
	}
}
