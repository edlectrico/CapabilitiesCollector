package es.deusto.deustotech.capabilities.views;

import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLLiteral;

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
import android.widget.EditText;
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

	private static List<String> displays, physicalEnvLights;

	private GridLayout grid;
	private TextView textViewCurrentLuxes;
	private NumberPicker brightnessPicker;

	private float brightnessValue = 0.5f; // dummy default value
	private boolean brightnessChanged = false;
	private float currentLuxes;

	private static final int DEFAULT_BUTTON_COLOR = -16777216;

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
		brightnessPicker.setBackgroundColor(userPrefs.getTextEditBackgroundColor());

		textViewCurrentLuxes = (TextView) findViewById(R.id.light_sensor_value);

		redrawViews();
		initializeServices(TAG);
		addListeners();
		
		removePreviousValuesFromOntology();
	}

	@Override
	public void initializeServices(String TAG) {
		if (userPrefs.getSightProblem() == 1){
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
					currentLuxes = event.values[0];

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
		findViewById(R.id.button_next).setOnClickListener(this);

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
		//EditText config
		((EditText)findViewById(R.id.button_text_edit)).setTextSize(userPrefs.getTextEditSize() / 2);

		if (userPrefs.getTextEditTextColor() != 0){
			((EditText) findViewById(R.id.button_text_edit)).setTextColor(userPrefs.getTextEditTextColor());
			((TextView)findViewById(R.id.brightness_message)).setTextColor(userPrefs.getTextEditTextColor());
		}

		if (userPrefs.getTextEditBackgroundColor() != 0){
			((EditText) findViewById(R.id.button_text_edit)).setBackgroundColor(userPrefs.getTextEditBackgroundColor());
			((TextView)findViewById(R.id.brightness_message)).setBackgroundColor(userPrefs.getTextEditBackgroundColor());
		}

		System.out.println("TextEditTextSize: " + userPrefs.getTextEditSize());

		((TextView)findViewById(R.id.brightness_message)).setTextSize(userPrefs.getTextEditSize() / 2);

		findViewById(R.id.button_next).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.button_next).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.button_next)).setTextColor(userPrefs.getButtonTextColor());

		findViewById(R.id.test_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.test_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.test_button)).setTextColor(userPrefs.getButtonTextColor());


		if (userPrefs.getButtonBackgroundColor() != DEFAULT_BUTTON_COLOR){
			((Button)findViewById(R.id.test_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			((Button)findViewById(R.id.button_next)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		}

		if (userPrefs.getLayoutBackgroundColor() != 0){
			grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_next){
			Intent intent = new Intent(this, VolumeConfigActivity.class);

			if (brightnessChanged){
				userPrefs.setBrightness(brightnessValue);
				brightnessChanged = false;
			} else {
				userPrefs.setBrightness(0);
			}

			intent.putExtra(getResources().getString(R.string.view_params), userPrefs);
			intent.putExtra(getResources().getString(R.string.activity_caller), 1); //0 - MainActivity; 1 - BrightnessAtivity

			super.getOntologyManager().addDataTypePropertyValue(physicalEnvLights.get(0), super.getOntologyNamespace() + "contextHasLight", currentLuxes);
			super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "displayHasBrightness", brightnessValue);
			
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}

			checkOntology();
			startActivity(intent);

		}
	}
	
	private void removePreviousValuesFromOntology() {
		displays = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Display");
		physicalEnvLights = super.getOntologyManager().getIndividualOfClass("http://u2m.org/2003/02/UserModelOntology.rdf#Light");
		
		super.getOntologyManager().deleteAllValuesOfProperty(displays.get(0), super.getOntologyNamespace() + "displayHasBrightness");
		super.getOntologyManager().deleteAllValuesOfProperty(physicalEnvLights.get(0), super.getOntologyNamespace() + "contextHasLight");
		
		final List<String> contextAux = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "ContextAux");
		super.getOntologyManager().deleteAllValuesOfProperty(contextAux.get(0), super.getOntologyNamespace() + "contextAuxHasLightLevel");
	}

	private void checkOntology() {
		final Collection<OWLLiteral> brightness	= super.getOntologyManager().getDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "displayHasBrightness");
		final Collection<OWLLiteral> contextLight = super.getOntologyManager().getDataTypePropertyValue(physicalEnvLights.get(0), super.getOntologyNamespace() + "contextHasLight");
		final List<String> contextAux = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "ContextAux");
		final Collection<OWLLiteral> contextCheckedLight = super.getOntologyManager().getDataTypePropertyValue(contextAux.get(0), super.getOntologyNamespace() + "contextAuxHasLightLevel");

		final List<String> devices = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "DeviceAux");
		final Collection<OWLLiteral> battery = super.getOntologyManager().getDataTypePropertyValue(devices.get(0), super.getOntologyNamespace() + "deviceAuxBatteryIsSufficient");
		
		final List<String> adaptations = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Adaptation");
		final Collection<OWLLiteral> adaptationBrightness = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationBrightnessHasValue");
		
		System.out.println("checkOntology(): " 	 + TAG);
		System.out.println("brightness: " 		 + brightness);
		System.out.println("light: " 			 + contextLight);
		System.out.println("contextLightLevel: " + contextCheckedLight);
		System.out.println("battery: " + battery);
		System.out.println("adaptations: " + adaptations);
		System.out.println("adaptationHasBrightness: " + adaptationBrightness);
	}

}
