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
import com.strategames.catchdastars.music.Media;

public class CheckBoxTextViewAdapter extends BaseAdapter {
	private Context context;
	private Media[] items;
	private OnItemSelectedListener listener;
	
	public CheckBoxTextViewAdapter(Context c, Media[] items, OnItemSelectedListener listener) {
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

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(this.context).inflate(R.layout.selectmusiclistviewitem, null);
		}

		final Media media = this.items[position];
		
		TextView tv = (TextView) convertView.findViewById(R.id.textview);
		tv.setText(media.getName());

		CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkbox);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				listener.onCheckBoxChanged(media, isChecked);
			}
		});
		/**
		 * TODO setting checkbox state is really slow. Should find another method to
		 * speedup UI
		 */
		cb.setChecked(media.isSelected());
		
		return convertView;
	}
}
