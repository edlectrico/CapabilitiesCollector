package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import es.deusto.deustotech.R;

/**
 * @author edlectrico
 * 
 *         This Activity gathers the UI configuration from the previous bundles
 *         and shows a builds a user interaction profile through a email
 *         application capturing the total time to do this task, clicks, device
 *         orientation variations, etc.
 * 
 *         Part of the code has been extracted from:
 *         http://www.rogcg.com/blog/2013
 *         /11/01/gridview-with-auto-resized-images-on-android
 */

public class PhoneCallActivity extends AbstractActivity {

	private static final String TAG = PhoneCallActivity.class.getSimpleName();
	private static final int ALPHA = 255;
	private static final int DEFAULT_BUTTON_COLOR = -16777216;

	private GridLayout grid;
	private GridView gridView;
	private EditText edit;
	private FrameLayout frame;

	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_call_activity);

		grid = (GridLayout) findViewById(R.id.phone_grid);

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new MyAdapter(this));
		System.out.println(gridView.getCount());

		edit = (EditText) findViewById(R.id.text);

		bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable(getResources().getString(
				R.string.view_params));

		redrawViews();
		initializeServices(TAG);
		addListeners();
		initOntology();
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				frame = (FrameLayout) gridView.getChildAt(position);
				TextView child = (TextView) frame.getChildAt(1); 
				child.setAlpha(0);
				
				if (child.getText().equals("delete")){
					String str = edit.getText().toString().trim();
					   if(str.length()!=0){
					    str  = str.substring( 0, str.length() - 1 ); 
					    edit.setText ( str );
					}
				} else if (child.getText() != ""){
					edit.append(child.getText());
				}
				
//				ImageView child2 = (ImageView) frame.getChildAt(0);//Position 0 is the ImageView
//				child2.setBackgroundColor(userPrefs.getButtonBackgroundColor());
				frame.setMinimumWidth((int)userPrefs.getButtonWidth());
				frame.setMinimumHeight((int)userPrefs.getButtonHeight());
				frame.setBackgroundColor(userPrefs.getButtonBackgroundColor());
				frame.invalidate();
			}
		});
	}
	
	@Override
	public void initializeServices(String TAG) {
		System.out.println("initializeServices()");
		
		if (userPrefs.getDisplayHasApplicable() == 0){
			super.initializeServices(TAG);

			speakOut(getResources().getString(R.string.edit_text_info_message_es));
		}
		
		if (userPrefs.getBrightness() != 0){
			System.out.println("Brightness: " + userPrefs.getBrightness());
			WindowManager.LayoutParams layoutParams = getWindow()
					.getAttributes();
			layoutParams.screenBrightness = userPrefs.getBrightness();
			getWindow().setAttributes(layoutParams);
		}
	}

	@Override
	public void redrawViews() {
		initializeServices(TAG);
		
		if ((ButtonConfigActivity.getLayoutBackgroundColorChanged())
				|| (userPrefs.getLayoutBackgroundColor() != 0)) {
			final int layoutBackgroundColor = userPrefs
					.getLayoutBackgroundColor();
			final int redBackgroundColor = Color.red(layoutBackgroundColor);
			final int greenBackgroundColor = Color.green(layoutBackgroundColor);
			final int blueBackgroundColor = Color.blue(layoutBackgroundColor);
			grid.setBackgroundColor(Color.argb(ALPHA, redBackgroundColor,
					greenBackgroundColor, blueBackgroundColor));
		}

//		for (int i = 0; i < gridView.getCount(); i++) {
//			frame = (FrameLayout) gridView.getChildAt(i);
//			ImageView child2 = (ImageView) frame.getChildAt(0);//Position 0 is the ImageView
//			child2.setBackgroundColor(Color.BLUE);
//			gridView.getChildAt(i).setMinimumWidth(
//					(int) userPrefs.getButtonWidth());
//			gridView.getChildAt(i).setMinimumHeight(
//					(int) userPrefs.getButtonHeight());
//			gridView.getChildAt(i).setBackgroundColor(userPrefs.getButtonBackgroundColor());
//		}

		if ((ButtonConfigActivity.getButtonBackgroundColorChanged())
				|| (userPrefs.getButtonBackgroundColor() != DEFAULT_BUTTON_COLOR)) {
			final int buttonBackgroundColor = userPrefs
					.getButtonBackgroundColor();
			final int redButtonBackgroundColor = Color
					.red(buttonBackgroundColor);
			final int greenButtonBackgroundColor = Color
					.green(buttonBackgroundColor);
			final int blueButtonBackgroundColor = Color
					.blue(buttonBackgroundColor);
			for (int i = 0; i < gridView.getChildCount(); i++) {
				gridView.setBackgroundColor(Color.argb(ALPHA,
						redButtonBackgroundColor, greenButtonBackgroundColor,
						blueButtonBackgroundColor));
			}
		}		

		/* EditText */
		edit.setTextSize(userPrefs.getEditTextTextSize() / 2);
		edit.setTextSize(userPrefs.getEditTextTextSize() / 2);
		edit.setTextSize(userPrefs.getEditTextTextSize() / 2);

		if (userPrefs.getEditTextTextColor() != 0) {
			final int textEditTextColor = userPrefs.getEditTextTextColor();
			final int redTextEditTextColor = Color.red(textEditTextColor);
			final int greenTextEditTextColor = Color.green(textEditTextColor);
			final int blueTextEditTextColor = Color.blue(textEditTextColor);
			edit.setTextColor(Color.argb(ALPHA, redTextEditTextColor,
					greenTextEditTextColor, blueTextEditTextColor));
		}

		if ((EditTextConfigActivity.edit_backgroundcolor_changed)
				|| (userPrefs.getEditTextBackgroundColor() != 0)) {
			final int textEditBackgroundColor = userPrefs
					.getEditTextBackgroundColor();
			final int redTextEditBackgroundColor = Color
					.red(textEditBackgroundColor);
			final int greenTextEditBackgroundColor = Color
					.green(textEditBackgroundColor);
			final int blueTextEditBackgroundColor = Color
					.blue(textEditBackgroundColor);
			edit.setBackgroundColor(Color.argb(ALPHA,
					redTextEditBackgroundColor, greenTextEditBackgroundColor,
					blueTextEditBackgroundColor));
		}
	}

	private class MyAdapter extends BaseAdapter {
		private List<Item> items = new ArrayList<Item>();
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);

			items.add(new Item("1", R.drawable.one));
			items.add(new Item("2", R.drawable.two));
			items.add(new Item("3", R.drawable.three));
			items.add(new Item("4", R.drawable.four));
			items.add(new Item("5", R.drawable.five));
			items.add(new Item("6", R.drawable.six));
			items.add(new Item("7", R.drawable.seven));
			items.add(new Item("8", R.drawable.eight));
			items.add(new Item("9", R.drawable.nine));
			items.add(new Item("*", R.drawable.ast));
			items.add(new Item("0", R.drawable.zero));
			items.add(new Item("#", R.drawable.alm));

			items.add(new Item("", R.drawable.contact));
			items.add(new Item("", R.drawable.call));
			items.add(new Item("delete", R.drawable.delete));
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int i) {
			return items.get(i);
		}

		@Override
		public long getItemId(int i) {
			return items.get(i).drawableId;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			View v = view;
			ImageView picture;
			TextView name;

			if (v == null) {
				v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
				v.setTag(R.id.picture, v.findViewById(R.id.picture));
				v.setTag(R.id.text, v.findViewById(R.id.text));
			}

			picture = (ImageView) v.getTag(R.id.picture);
			name = (TextView) v.getTag(R.id.text);

			Item item = (Item) getItem(i);

			picture.setImageResource(item.drawableId);
			name.setText(item.name);

			return v;
		}

		private class Item {
			final String name;
			final int drawableId;

			Item(String name, int drawableId) {
				this.name = name;
				this.drawableId = drawableId;
			}
		}
	}

}