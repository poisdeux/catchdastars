package com.strategames.catchdastars.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.strategames.catchdastars.R;
import com.strategames.catchdastars.adapters.CheckBoxTextViewAdapter;

public class SelectMusicFragment extends Fragment implements OnItemClickListener {
	
	private CheckBoxTextViewAdapter adapter;
	private ListView listview;
	
	public enum STATE {
		ARTISTS, ALBUMS, TRACKS
	}

	private STATE state = STATE.ARTISTS;
	
	public interface OnItemSelectedListener {
		public void onCheckBoxChanged(int position, boolean isChecked);
		public void onItemClicked(String item);
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
		
		View view = inflater.inflate(R.layout.selectmusicfragment, container, false);
		this.listview = (ListView) view.findViewById(R.id.listview);
		this.listview.setOnItemClickListener(this);
		if( this.adapter != null ) {
			this.listview.setAdapter(this.adapter);
		}
		return view;
	}

	public void setAdapter(CheckBoxTextViewAdapter adapter) {
		this.adapter = adapter;
		if( this.listview != null ) {
			this.listview.setAdapter(this.adapter);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.listener.onItemClicked((String) this.adapter.getItem(position));
	}

	public void setState(STATE state) {
		this.state = state;
	}
	
	public STATE getState() {
		return state;
	}
}
