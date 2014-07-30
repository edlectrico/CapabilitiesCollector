package es.deusto.deustotech.pellet4android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import es.deusto.deustotech.R;
import es.deusto.deustotech.pellet4android.exceptions.OntologyLoadException;

public class MainActivity extends Activity implements OnInitListener {

	private static OntologyManager ontManager = new OntologyManager();
	private ProgressDialog dialog;
	private Intent intent;
	private static String ONT_FILENAME, ONT_PATH;

	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		loadStringResources();
		intent = getDefaultIntent();
		showDialog();

		new OntologyImports().execute("adaptui"); // The name is not important
	}

	class OntologyImports extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			try {
				try {
					//Internal mapping to not depend on the Internet
					ontManager.setMapping(getResources().getString(R.string.foaf_uri), getExternalDirectory("foaf.rdf"));
					ontManager.setMapping(getResources().getString(R.string.cobra_device_uri), getExternalDirectory("soupa.rdf"));
					ontManager.setMapping(getResources().getString(R.string.user_model_ont_uri), getExternalDirectory("UserModelOntology.rdf"));
					ontManager.setMapping(getResources().getString(R.string.swrla_uri), getExternalDirectory("swrla.rdf"));
					ontManager.setMapping(getResources().getString(R.string.sqwrl_uri), getExternalDirectory("sqwrl.rdf"));

					//Direct load from '/assets' folder
					//ontManager.loadOntologyFromFile(getAssets().open("adaptui.owl"));

					File file = new File(ONT_PATH + ONT_FILENAME);
					FileInputStream fileInputStream = new FileInputStream(file);

					ontManager.loadOntologyFromFile(fileInputStream);				
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (OntologyLoadException e) {
				e.printStackTrace();
			} 
			return null;
		}

		protected void onPostExecute(String string) {
			dialog.dismiss();
			startActivity(intent);
		}
	}

	private String getExternalDirectory(final String file){
		return "file:" + Environment.getExternalStorageDirectory().getPath() + "/ontologies/" + file;
	}

	public static OntologyManager getOntologyManager(){
		return ontManager;
	}

	private Intent getDefaultIntent() {
		return new Intent(this, es.deusto.deustotech.capabilities.views.CapabilitiesActivity.class);
	}

	private void loadStringResources() {
		ONT_FILENAME 	= getResources().getString(R.string.ontology_filename);
//		ONT_FILENAME = "test.owl";
		ONT_PATH		= getResources().getString(R.string.ontology_path);
	}

	private void showDialog() {
		dialog = ProgressDialog.show(MainActivity.this, "", getResources().getString(R.string.loading_message_dialog), true);        
		dialog.show();
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(new Locale("spa", "ES"));

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} 
		} else {
			Log.e("TTS", "Initilization Failed!");
		}
//		speakOut(getResources().getString(R.string.basic_input_message_es));
	}

}
