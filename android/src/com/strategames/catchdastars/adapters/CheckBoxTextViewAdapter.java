package com.strategames.catchdastars.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.strategames.catchdastars.R;
import com.strategames.engine.musiclibrary.LibraryItem;

public class CheckBoxTextViewAdapter extends BaseAdapter {
	private Context context;
	private LibraryItem[] items;
	private OnCheckboxChangedListener listener;
	
	public interface OnCheckboxChangedListener {
		public void onCheckBoxChanged(CheckBoxTextViewAdapter adapter, LibraryItem item, boolean isChecked);
	}
	
	public CheckBoxTextViewAdapter(Context c, LibraryItem[] items, OnCheckboxChangedListener listener) {
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

		final LibraryItem media = this.items[position];
		
		TextView tv = (TextView) convertView.findViewById(R.id.textview);
		tv.setText(media.getName());

		CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkbox);
		cb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onCheckBoxChanged(CheckBoxTextViewAdapter.this, media, ((CheckBox) v).isChecked());
			}
		});
		
		/**
		 * TODO setting checkbox state is really slow. Should find another method to
		 * speedup UI
		 */
		cb.setChecked(media.isSelected());
		
		return convertView;
	}
	
	public LibraryItem[] getItems() {
		return items;
	}
}
