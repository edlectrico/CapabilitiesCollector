package es.deusto.deustotech.pellet4android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import es.deusto.deustotech.R;
import es.deusto.deustotech.pellet4android.exceptions.OntologyLoadException;

public class MainActivity extends Activity {

	private static OntologyManager ontManager = new OntologyManager();
	private ProgressDialog dialog;
	private Intent intent;
	private static String ONT_FILENAME, ONT_PATH;
	
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
//			intent.putExtra(getResources().getString(R.string.activity_caller), 0); //0 - MainActivity; 1 - MainActivity
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
		ONT_PATH		= getResources().getString(R.string.ontology_path);
	}

	private void showDialog() {
		dialog = ProgressDialog.show(MainActivity.this, "", getResources().getString(R.string.loading_message_dialog), true);        
		dialog.show();
	}
}
