package es.deusto.deustotech.pellet4android;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import es.deusto.deustotech.R;
import es.deusto.deustotech.pellet4android.exceptions.OntologyLoadException;

public class MainActivity extends Activity {

	private final static String TAG = MainActivity.class.getName();
	private static final String ONTOLOGY_NAMESPACE = "http://www.morelab.deusto.es/ontologies/adaptui#";
	private static OntologyManager ontManager = new OntologyManager();
	private static String ADAPTUI = "adaptui.owl";
	
	private ProgressDialog dialog;
	
	private static List<String> displays, users, audios, buttons;
	
	private Intent intent;
	
//	private TextView text;
//	private long start;
//	private String ontologies[];
//	private List<Double> times;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		intent = new Intent(this, es.deusto.deustotech.capabilities.views.MainActivity.class);
		
//		text = (TextView) findViewById(R.id.text);
		
		dialog = ProgressDialog.show(MainActivity.this, "", 
				"Loading. Please wait...", true);        
		dialog.show();
		
//		AssetManager mgr = getAssets();
//		try {
//			ontologies = mgr.list("");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		times = new ArrayList<Double>();
		
//		start = System.nanoTime();
		
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

//					for (String ontology : ontologies){
//							if (ontology.contains(".owl")){
//					System.out.println("Using: " + ADAPTUI);
//					System.out.println("-------------------");
//					
//					start = System.nanoTime();
					
					ontManager.loadOntologyFromFile(getAssets().open(ADAPTUI));
					
//					final List<String> classes = (List<String>) ontManager.getClassList();
//					int instances = 0;
//					Set<OWLNamedIndividual> nodeSet = new HashSet<OWLNamedIndividual>();
					
//					double end = System.nanoTime();
//					double elapsed = end - start;
//					double seconds = (elapsed / Math.pow(10, 9));
					
//					times.add(seconds);
//					writeToFile(ADAPTUI, seconds);
					
//					ontManager.removeOntology();
//							}
//					}
					// or from the web: ontManager.loadOntology(ontUri);
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
//			for (int i=0; i < times.size(); i++){
//				System.out.println("Time: " + times.get(i));
//			}
			
//			text.setText("finished!");
			dialog.dismiss();
			
			intent.putExtra("caller", 0); //0 - MainActivity; 1 - MainActivity
			startActivity(intent);
		}
	}

	private static void insertIndividuals() {
		Log.d(TAG,
				"Check insertIndividuals() to learn how to insert individuals");

		// Inserting individual of a class
//		ontManager.addIndividualMembership(
//				ONTOLOGY_NAMESPACE + "user", ONTOLOGY_NAMESPACE
//						+ "User");
//
//		// Checking the insertion
//		 List<String> classAIndividuals;
//		 classAIndividuals = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "User");
//		 
//		 for (String individual : classAIndividuals){
//			 System.out.println(individual);
//		 }
		 
		System.out.println("Inserting individuals");
		
		ontManager.addIndividualMembership(ONTOLOGY_NAMESPACE + "user", 	ONTOLOGY_NAMESPACE + "User");
		ontManager.addIndividualMembership(ONTOLOGY_NAMESPACE + "display", 	ONTOLOGY_NAMESPACE + "Display");
		ontManager.addIndividualMembership(ONTOLOGY_NAMESPACE + "audio", 	ONTOLOGY_NAMESPACE + "Audio");
		ontManager.addIndividualMembership(ONTOLOGY_NAMESPACE + "button", 	ONTOLOGY_NAMESPACE + "Button");
		
		users 	 = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "User");
		displays = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Display");
		audios 	 = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Audio");
		buttons	 = ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "Button");
		
		ontManager.addObjectPropertyValue(users.get(0), ONTOLOGY_NAMESPACE + "userIsDefinedBy", 	displays.get(0));
		ontManager.addObjectPropertyValue(users.get(0), ONTOLOGY_NAMESPACE + "userIsDefinedBy", 	audios.get(0));
		
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
		List<String> buttonInd 	= ontManager.getIndividualOfClass(ONTOLOGY_NAMESPACE + "ViewButton");
		
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
		
		Collection<OWLLiteral> volumes = ontManager.getDataTypePropertyValue(ONTOLOGY_NAMESPACE + "audio", "userAudioHasVolume");
		System.out.println("volumes.size() = " + volumes.size());
		
		System.out.println("userIsDefinedBy: " 	+ ontManager.getPropertyValue(users.get(0), ONTOLOGY_NAMESPACE + "userIsDefinedBy"));
	}


	private static void checkProtegeRules() {
		Log.d(TAG, "RULES:");

		final Collection<OWLLiteral> dataTypes = ontManager.getDataTypePropertyValue(ONTOLOGY_NAMESPACE + "Individual",
				ONTOLOGY_NAMESPACE + "DataTypeProperty");
		
		System.out.println("rules finished");
	}
	
	private void writeToFile(final String ontology, final double seconds) {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		if ((mExternalStorageAvailable) && (mExternalStorageWriteable)){
			// get external storage file reference			
			FileWriter writer;
			try {
				writer = new FileWriter(Environment.getExternalStorageDirectory() + "/" + ontology + ".txt", true);
				// Writes the content to the file
				writer.write(ontology + "\n" + seconds + "\n\n"); 
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}

}
