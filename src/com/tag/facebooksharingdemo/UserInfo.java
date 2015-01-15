package com.tag.facebooksharingdemo;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class UserInfo extends Activity {

	private String userId;

	private LinearLayout llUserName, llFirstName, llLastName, llBirthDate,
			llEmailId;
	private TextView tvUserId, tvUserName, tvFirstNAme, tvLastName, tvBirthday,
			tvEmail;
	private ImageView ivProfileImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);

		getReferences();
		displayUserInfo();
		displayProfileImage();
	}

	private void getReferences() {
		llUserName = (LinearLayout) findViewById(R.id.llUserName);
		llFirstName = (LinearLayout) findViewById(R.id.llFirstName);
		llLastName = (LinearLayout) findViewById(R.id.llLastName);
		llBirthDate = (LinearLayout) findViewById(R.id.llBirthDate);
		llEmailId = (LinearLayout) findViewById(R.id.llEmailId);

		tvUserId = (TextView) findViewById(R.id.tvUserId);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvFirstNAme = (TextView) findViewById(R.id.tvFirstNAme);
		tvLastName = (TextView) findViewById(R.id.tvLastName);
		tvBirthday = (TextView) findViewById(R.id.tvBirthday);
		tvEmail = (TextView) findViewById(R.id.tvEmail);

		ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
	}

	@SuppressWarnings("unchecked")
	private void displayUserInfo() {
		HashMap<String, String> userHashmap = (HashMap<String, String>) getIntent()
				.getSerializableExtra(MainActivity.USER_MAP);

		userId = userHashmap.get(MainActivity.USER_ID);

		String userName = userHashmap.get(MainActivity.USER_NAME);
		String firstName = userHashmap.get(MainActivity.FIRST_NAME);
		String lastName = userHashmap.get(MainActivity.LAST_NAME);
		String birthday = userHashmap.get(MainActivity.BIRTHDAY);
		String email = userHashmap.get(MainActivity.EMAIL_ID);

		tvUserId.setText(userId);

		if (birthday != null) {
			if (userName.length() > 0)
				tvUserName.setText(userName);
			else
				llUserName.setVisibility(View.GONE);
		} else
			llUserName.setVisibility(View.GONE);

		if (firstName != null) {
			if (firstName.length() > 0)
				tvFirstNAme.setText(firstName);
			else
				llFirstName.setVisibility(View.GONE);
		} else
			llFirstName.setVisibility(View.GONE);

		if (lastName != null) {
			if (lastName.length() > 0)
				tvLastName.setText(lastName);
			else
				llLastName.setVisibility(View.GONE);
		} else
			llLastName.setVisibility(View.GONE);

		if (birthday != null) {
			if (birthday.length() > 0)
				tvBirthday.setText(birthday);
			else
				llBirthDate.setVisibility(View.GONE);
		} else
			llBirthDate.setVisibility(View.GONE);

		if (birthday != null) {
			if (email.length() > 0)
				tvEmail.setText(email);
			else
				llEmailId.setVisibility(View.GONE);
		} else
			llEmailId.setVisibility(View.GONE);

	}

	private void displayProfileImage() {
		ImageLoader imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		}

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.defaultimage)
				.showImageForEmptyUri(R.drawable.defaultimage)
				.showImageOnFail(R.drawable.defaultimage).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		imageLoader.displayImage("https://graph.facebook.com/" + userId
				+ "/picture?type=large", ivProfileImage, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
					}
				});
	}

}
