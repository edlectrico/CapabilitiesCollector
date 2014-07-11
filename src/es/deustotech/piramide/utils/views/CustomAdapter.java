package es.deustotech.piramide.utils.views;

import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.views.AbstractActivity;

public class CustomAdapter extends ArrayAdapter<String>{

	private List<String> textViews;
	
	public CustomAdapter(Context context, int resource) {
		super(context, resource);
        textViews = AbstractActivity.getOntologyManager().getIndividualOfClass("http://www.morelab.deusto.es/ontologies/adaptui#TextView");
	}
	
	public CustomAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
        textViews = AbstractActivity.getOntologyManager().getIndividualOfClass("http://www.morelab.deusto.es/ontologies/adaptui#TextView");
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  super.getView(position, convertView, parent);

        TextView tv = (TextView) view.findViewById(R.id.list_item);
        
        final Collection<OWLLiteral> textEditBackColor 	= AbstractActivity.getOntologyManager().getDataTypePropertyValue(textViews.get(0), "http://www.morelab.deusto.es/ontologies/adaptui#viewHasColor");
        final Collection<OWLLiteral> textEditTextColor 	= AbstractActivity.getOntologyManager().getDataTypePropertyValue(textViews.get(0), "http://www.morelab.deusto.es/ontologies/adaptui#viewHasTextColor");
        final Collection<OWLLiteral> textEditTextSize 	= AbstractActivity.getOntologyManager().getDataTypePropertyValue(textViews.get(0), "http://www.morelab.deusto.es/ontologies/adaptui#viewHasTextSize");
	
        final int viewColor 	= Integer.parseInt(((OWLLiteral) textEditBackColor.toArray()[0]).getLiteral());
        final int textColor 	= Integer.parseInt(((OWLLiteral) textEditTextColor.toArray()[0]).getLiteral());
        final float textSize 	= Float.parseFloat(((OWLLiteral) textEditTextSize.toArray()[0]).getLiteral());
        
        tv.setTextColor(textColor);
        tv.setBackgroundColor(viewColor);
        tv.setTextSize(textSize);

        return view;
    }
}