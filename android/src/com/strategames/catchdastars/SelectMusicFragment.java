package com.strategames.catchdastars;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectMusicFragment extends Fragment implements OnItemClickListener {

	public final static String BUNDLE_KEY_LIST = "bundle_key_list";
	
	public interface OnItemSelectedListener {
		public void onCheckBoxChanged(int position, boolean isChecked);
		public void onItemClicked(int position);
	}
	
	private OnItemSelectedListener listener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        try {
        	this.listener = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
        }
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		String[] items = getArguments().getStringArray(BUNDLE_KEY_LIST);
		
		View view = inflater.inflate(R.layout.selectmusicfragment, container, false);
		ListView lv = (ListView) view.findViewById(R.id.listview);
		CheckBoxTextViewAdapter ad = new CheckBoxTextViewAdapter(getActivity(), items);
		lv.setAdapter(ad);
		lv.setOnItemClickListener(this);
		
		return view;
	}
	
	private class CheckBoxTextViewAdapter extends BaseAdapter {
		private Context context;
		private String[] items;
		
		public CheckBoxTextViewAdapter(Context c, String[] items) {
			this.context = c;
			this.items = items;
		}

		public int getCount() {
			return this.items.length;
		}

		public Object getItem(int position) {
			return this.items[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.selectmusiclistviewitem, null);
			}

			TextView tv = (TextView) convertView.findViewById(R.id.textview);
			tv.setText(this.items[position]);

			CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkbox);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					listener.onCheckBoxChanged(position, isChecked);
				}
			});
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.listener.onItemClicked(position);
	}

	
}
