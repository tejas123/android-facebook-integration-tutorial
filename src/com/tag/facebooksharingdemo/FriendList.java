package com.tag.facebooksharingdemo;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

public class FriendList extends Activity {

	private ListView lvFriendList;

	private ArrayList<HashMap<String, String>> friendList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);

		lvFriendList = (ListView) findViewById(R.id.lvFriendList);

		displayFriendList();
	}

	@SuppressWarnings("unchecked")
	private void displayFriendList() {
		friendList = (ArrayList<HashMap<String, String>>) getIntent()
				.getSerializableExtra(MainActivity.FRIEND_LIST);

		FriendListAdapter adapter = new FriendListAdapter(this, friendList);
		lvFriendList.setAdapter(adapter);

		bindEventHandler();
	}

	private void bindEventHandler() {
		lvFriendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				getFriendDetail(friendList.get(position).get(
						MainActivity.USER_ID));
			}
		});
	}

	private void getFriendDetail(final String userId) {
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {
					Request request = new Request(session, userId);

					request.setCallback(new Request.Callback() {

						@Override
						public void onCompleted(Response response) {

							GraphObject graphObj = response.getGraphObject();

							HashMap<String, String> userHashmap = new HashMap<String, String>();
							userHashmap.put(MainActivity.USER_ID, userId);
							userHashmap.put(MainActivity.USER_NAME, graphObj
									.getProperty("name").toString());
							userHashmap.put(MainActivity.FIRST_NAME, graphObj
									.getProperty("first_name").toString());
							userHashmap.put(MainActivity.LAST_NAME, graphObj
									.getProperty("last_name").toString());
							userHashmap.put(MainActivity.GENDER,
									(String) graphObj.getProperty("gender"));

							Intent intent = new Intent(FriendList.this,
									UserInfo.class);
							intent.putExtra(MainActivity.USER_MAP, userHashmap);
							startActivity(intent);
						}
					});

					request.executeAsync();
				}
			}
		});
	}

}
