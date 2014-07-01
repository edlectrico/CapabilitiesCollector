package es.deusto.deustotech.pellet4android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import es.deusto.deustotech.R;
import es.deusto.deustotech.pellet4android.exceptions.OntologyLoadException;

@SuppressLint("SdCardPath")
public class MainActivity extends Activity {

	private final static String TAG = MainActivity.class.getName();
	private static final String ONTOLOGY_NAMESPACE = "http://www.morelab.deusto.es/ontologies/adaptui#";
	private static final String ONTOLOGY_PATH = "/sdcard/data/";
	private static OntologyManager ontManager = new OntologyManager();
	private static String ADAPTUI = "adaptui.owl";
	
	private ProgressDialog dialog;
	
	private static List<String> displays, users, audios;
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		intent = new Intent(this, es.deusto.deustotech.capabilities.views.CapabilitiesActivity.class);
		
		dialog = ProgressDialog.show(MainActivity.this, "", 
				"Loading. Please wait...", true);        
		dialog.show();
		
		new OntologyImports().execute("ontology"); // The name is not important
	}
	
	private String getExternalDirectory(final String file){
		return "file:" + Environment.getExternalStorageDirectory().getPath() + "/ontologies/" + file;
	}
	
	public static OntologyManager getOntologyManager(){
		return ontManager;
	}

	class OntologyImports extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {
			try {
				try {
					//Internal mapping to not depend on the Internet
					ontManager.setMapping("http://xmlns.com/foaf/0.1/", 						getExternalDirectory("foaf.rdf"));
					ontManager.setMapping("http://daml.umbc.edu/ontologies/cobra/0.4/device", 	getExternalDirectory("soupa.rdf"));
					ontManager.setMapping("http://u2m.org/2003/02/UserModelOntology.rdf", 		getExternalDirectory("UserModelOntology.rdf"));
					ontManager.setMapping("http://swrl.stanford.edu/ontologies/3.3/swrla.owl", 	getExternalDirectory("swrla.rdf"));
					ontManager.setMapping("http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl", getExternalDirectory("sqwrl.rdf"));

//					ontManager.loadOntologyFromFile(getAssets().open(ADAPTUI));
					
					File file = new File(ONTOLOGY_PATH + ADAPTUI);
					FileInputStream fileInputStream = new FileInputStream(file);
					
					ontManager.loadOntologyFromFile(fileInputStream);				

				} catch (IOException e) {
					e.printStackTrace();
				}

				insertIndividuals();
//				checkProtegeRules();
			} catch (OntologyLoadException e) {
				e.printStackTrace();
			} 

			return null;
		}

		protected void onPostExecute(String string) {
			dialog.dismiss();
			
			intent.putExtra("caller", 0); //0 - MainActivity; 1 - MainActivity
			startActivity(intent);
		}
	}

	private static void insertIndividuals() {
		Log.d(TAG,
				"Check insertIndividuals() to learn how to insert individuals");

		System.out.println("Inserting individuals");
		
		users 	 = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "User");
		
		if (users.size() == 0){ //No previous insertion
			ontManager.addIndividualMembership(ONTOLOGY_NAMESPACE + "user", 	ONTOLOGY_NAMESPACE + "User");
			ontManager.addIndividualMembership(ONTOLOGY_NAMESPACE + "display", 	ONTOLOGY_NAMESPACE + "Display");
			ontManager.addIndividualMembership(ONTOLOGY_NAMESPACE + "audio", 	ONTOLOGY_NAMESPACE + "Audio");
			ontManager.addIndividualMembership(ONTOLOGY_NAMESPACE + "button", 	ONTOLOGY_NAMESPACE + "Button");
			
			users 	 = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "User");
			displays = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Display");
			audios 	 = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Audio");
			
			ontManager.addObjectPropertyValue(users.get(0), ONTOLOGY_NAMESPACE + "userIsDefinedBy", 	displays.get(0));
			ontManager.addObjectPropertyValue(users.get(0), ONTOLOGY_NAMESPACE + "userIsDefinedBy", 	audios.get(0));
		}
		
		
		//TODO: Aún no lo sabemos... Si el usuario responde YES (o mantiene pulsado) puede que sea ciego 
//			ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayApplicableIsStatic", false);
//			ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayHasApplicable", 		true);
//			ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayHasBrightness", 		50);
		 
		checkInsertions();
	}
	
	private static void checkInsertions() {
		List<String> userInd 	= ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "User");
		List<String> displayInd = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Display");
		List<String> audioInd 	= ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Audio");
		List<String> buttonInd 	= ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Button");
		List<String> editInd 	= ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "EditText");
		List<String> textViewInd 	= ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "TextView");
		List<String> backgroundInd 	= ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Background");
		
		for (String user : userInd) {
			System.out.println(user);
		}
		
		for (String display : displayInd) {
			System.out.println(display);
		}
		
		for (String audio : audioInd) {
			System.out.println(audio);
		}
		
		for (String button : buttonInd) {
			System.out.println(button);
		}
		
		for (String edit : editInd) {
			System.out.println(edit);
		}
		
		for (String textView : textViewInd) {
			System.out.println(textView);
		}
		
		for (String background : backgroundInd) {
			System.out.println(background);
		}
		
		Collection<OWLLiteral> volumes = ontManager.getDataTypePropertyValue(ONTOLOGY_NAMESPACE + "audio", "userAudioHasVolume");
		System.out.println("volumes.size() = " + volumes.size());
		
		System.out.println("userIsDefinedBy: " 	+ ontManager.getPropertyValue(users.get(0), ONTOLOGY_NAMESPACE + "userIsDefinedBy"));
	}
}
