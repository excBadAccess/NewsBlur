package com.newsblur.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.newsblur.R;
import com.newsblur.activity.AddSites;
import com.newsblur.network.APIManager;
import com.newsblur.network.domain.LoginResponse;

public class RegisterProgressFragment extends Fragment {

	private APIManager apiManager;
	private String TAG = "LoginProgress";
	
	private String username;
	private String password;
	private String email;
	private RegisterTask registerTask;
	private ViewSwitcher switcher;
	private Button next;

	public static RegisterProgressFragment getInstance(String username, String password, String email) {
		RegisterProgressFragment fragment = new RegisterProgressFragment();
		Bundle bundle = new Bundle();
		bundle.putString("username", username);
		bundle.putString("password", password);
		bundle.putString("email", email);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		apiManager = new APIManager(getActivity());
		Log.d(TAG , "Creating new fragment instance");
		
		username = getArguments().getString("username");
		password = getArguments().getString("password");
		email = getArguments().getString("email");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_registerprogress, null);
		switcher = (ViewSwitcher) v.findViewById(R.id.register_viewswitcher);
		
		next = (Button) v.findViewById(R.id.registering_next_1);
		
		if (registerTask != null) {
			switcher.showNext();
		} else {
			registerTask = new RegisterTask();
			registerTask.execute();
		}
		
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), AddSites.class);
				startActivity(i);
			}
		});
		
		return v;
	}
	
	private class RegisterTask extends AsyncTask<Void, Void, LoginResponse> {
		
		@Override
		protected LoginResponse doInBackground(Void... params) {
			try {
				// We include this wait simply as a small UX convenience. Otherwise the user could be met with a disconcerting flicker when attempting to register and failing.
				Thread.sleep(700);
			} catch (InterruptedException e) {
				Log.d(TAG, "Error sleeping during login.");
			}
			return apiManager.signup(username, password, email);
		}
		
		@Override
		protected void onPostExecute(LoginResponse response) {
			if (response.authenticated) {
				switcher.showNext();
			} else {
				if (response.errors != null && response.errors.message != null) {
					Toast.makeText(getActivity(), response.errors.message[0], Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), getResources().getString(R.string.login_message_error), Toast.LENGTH_LONG).show();
				}
				getActivity().finish();
			}
		}
		
	}

	
}