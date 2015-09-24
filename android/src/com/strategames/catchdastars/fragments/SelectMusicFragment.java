/**
 * 
 * Copyright 2014 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
import com.strategames.engine.musiclibrary.LibraryItem;

public class SelectMusicFragment extends Fragment implements OnItemClickListener {
	
	private CheckBoxTextViewAdapter adapter;
	private ListView listview;
	
	public enum STATE {
		ARTISTS, ALBUMS, TRACKS
	}

	private STATE state = STATE.ARTISTS;
	
	public interface SelectMusicFragmentListener {
		public void onItemClicked(LibraryItem item);
	}
	
	private SelectMusicFragmentListener listener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        try {
        	this.listener = (SelectMusicFragmentListener) activity;
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
		this.listener.onItemClicked((LibraryItem) this.adapter.getItem(position));
	}

	public void setState(STATE state) {
		this.state = state;
	}
	
	public STATE getState() {
		return state;
	}
}
