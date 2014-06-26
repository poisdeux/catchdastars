package com.strategames.catchdastars.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.strategames.catchdastars.R;
import com.strategames.catchdastars.fragments.SelectMusicFragment.OnItemSelectedListener;

public class CheckBoxTextViewAdapter extends BaseAdapter {
	private Context context;
	private String[] items;
	private OnItemSelectedListener listener;
	
	public CheckBoxTextViewAdapter(Context c, String[] items, OnItemSelectedListener listener) {
		this.context = c;
		this.items = items;
		this.listener = listener;
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
