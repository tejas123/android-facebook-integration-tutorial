package com.tag.facebooksharingdemo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity implements OnClickListener {

	private Button btnMainUserInfo, btnMainFriendList;

	private HashMap<String, String> userHashmap;
	private ArrayList<HashMap<String, String>> friendList;

	private ProgressDialog pd;

	public static final String USER_MAP = "userHashmap";
	public static final String FRIEND_LIST = "friendList";

	public static final String USER_ID = "userId";
	public static final String NAME = "name";
	public static final String USER_NAME = "userName";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String BIRTHDAY = "birthday";
	public static final String GENDER = "gender";
	public static final String EMAIL_ID = "emailId";
	public static final String IMAGE_URL = "imageUrl";

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (pd != null && pd.isShowing())
				pd.dismiss();

			if (msg.what == 1) {
				Intent intent = new Intent(MainActivity.this, UserInfo.class);
				intent.putExtra(USER_MAP, userHashmap);
				startActivity(intent);
			} else if (msg.what == 2) {
				if (friendList.size() > 0) {
					Intent intent = new Intent(MainActivity.this,
							FriendList.class);
					intent.putExtra(FRIEND_LIST, friendList);
					startActivity(intent);
				} else {
					Toast.makeText(MainActivity.this, "No friends found.",
							Toast.LENGTH_SHORT).show();
				}
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getKeyHash();

		btnMainUserInfo = (Button) findViewById(R.id.btnMainUserInfo);
		btnMainFriendList = (Button) findViewById(R.id.btnMainFriendList);

		btnMainUserInfo.setOnClickListener(this);
		btnMainFriendList.setOnClickListener(this);
	}

	private void getKeyHash() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("Keyhash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnMainUserInfo) {
			getFacebookUserInfo();
		} else if (v == btnMainFriendList) {
			getFacebookFriendList();
		}
	}

	private void getFacebookUserInfo() {
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {
					boolean isPermissionAvailable = false;
					for (int i = 0; i < session.getPermissions().size(); i++) {
						if (session.getPermissions().get(i).contains("email")) {
							pd = ProgressDialog.show(MainActivity.this, "", "");
							isPermissionAvailable = true;

							Request.newMeRequest(session,
									new GraphUserCallback() {

										@Override
										public void onCompleted(
												final GraphUser user,
												Response response) {

											if (user != null) {
												getUserInfoFromFacebook(user);
											}
										}
									}).executeAsync();
						}
					}
					if (!isPermissionAvailable)
						getPermissionFromFacebook();
				}
			}
		});
	}

	private void getFacebookFriendList() {
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {
					boolean isPermissionAvailable = false;
					for (int i = 0; i < session.getPermissions().size(); i++) {
						if (session.getPermissions().get(i)
								.contains("user_friends")) {
							pd = ProgressDialog.show(MainActivity.this, "", "");
							isPermissionAvailable = true;

							Request.newMyFriendsRequest(session,
									new GraphUserListCallback() {
										@Override
										public void onCompleted(
												List<GraphUser> users,
												Response response) {
											makeFacebookFriendList(users);
										}
									}).executeAsync();
						}
					}
					if (!isPermissionAvailable)
						getPermissionFromFacebook();
				}
			}
		});
	}

	private void makeFacebookFriendList(List<GraphUser> users) {
		friendList = new ArrayList<HashMap<String, String>>();
		friendList.clear();

		for (int i = 0; i < users.size(); i++) {
			GraphUser user = users.get(i);
			HashMap<String, String> friendHashmap = new HashMap<String, String>();
			friendHashmap.put(USER_ID, user.getId());
			friendHashmap.put(NAME, user.getName());

			friendList.add(friendHashmap);
		}

		handler.sendEmptyMessage(2);
	}

	private void getUserInfoFromFacebook(final GraphUser user) {
		userHashmap = new HashMap<String, String>();

		userHashmap.put(USER_ID, user.getId());
		userHashmap.put(USER_NAME, user.getUsername());
		userHashmap.put(FIRST_NAME, user.getFirstName());
		userHashmap.put(LAST_NAME, user.getLastName());
		userHashmap.put(BIRTHDAY, user.getBirthday());
		userHashmap.put(GENDER, (String) user.getProperty("gender"));
		userHashmap.put(EMAIL_ID, user.asMap().get("email").toString());

		handler.sendEmptyMessage(1);
	}

	private void getPermissionFromFacebook() {
		String[] permissions = { "basic_info", "user_friends", "email" };
		Session.getActiveSession().requestNewReadPermissions(
				new NewPermissionsRequest(MainActivity.this, Arrays
						.asList(permissions)));
	}

	// private void getPermissionForFriendList() {
	// String[] permissions = { "user_friends" };
	// Session.getActiveSession().requestNewReadPermissions(
	// new NewPermissionsRequest(MainActivity.this, Arrays
	// .asList(permissions)));
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

}