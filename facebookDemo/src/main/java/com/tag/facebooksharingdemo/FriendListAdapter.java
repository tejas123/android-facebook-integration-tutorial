package com.tag.facebooksharingdemo;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<HashMap<String, String>> friendList;

	public FriendListAdapter(Context context,
			ArrayList<HashMap<String, String>> historyList) {

		this.friendList = historyList;
		this.context = context;

		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return friendList.size();
	}

	@Override
	public Object getItem(int index) {
		return friendList.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		final ViewHolder holder;

		if (row == null) {
			row = (LinearLayout) inflater.inflate(R.layout.list_inflater,
					parent, false);

			holder = new ViewHolder();
			holder.tvFriendName = (TextView) row
					.findViewById(R.id.tvFriendName);

			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		holder.tvFriendName.setText(friendList.get(position).get(
				MainActivity.NAME));

		return row;
	}

	private class ViewHolder {
		private TextView tvFriendName;
	}

}
