package com.chess.ui.activities;

import actionbarcompat.ActionBarActivity;
import actionbarcompat.ActionBarHelper;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import com.chess.R;
import com.chess.backend.entity.SoundPlayer;
import com.chess.ui.interfaces.PopupDialogFace;
import com.inneractive.api.ads.InneractiveAd;

public abstract class CoreActivityActionBar extends ActionBarActivity implements View.OnClickListener
		, PopupDialogFace {

	protected Bundle extras;
	protected Handler handler;
	protected boolean showActionSearch;
	protected boolean showActionSettings;
	protected boolean showActionNewGame;
	protected boolean showActionRefresh;

	// we may have this add on every screen, so control it on the lowest level
	//protected MoPubView moPubView;
	protected InneractiveAd inneractiveBannerAd;
	protected InneractiveAd inneractiveRectangleAd;

	public void setFullScreen() {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);  // TODO solve problem for QVGA screens
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && getActionBar() != null) {
			getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
					| ActionBar.DISPLAY_HOME_AS_UP
					| ActionBar.DISPLAY_SHOW_CUSTOM);
		}

		handler = new Handler();
		extras = getIntent().getExtras();
	}

	protected void initUpgradeAndAdWidgets() {
//		if (!AppUtils.isNeedToUpgrade(this)) {
//			findViewById(R.id.bannerUpgradeView).setVisibility(View.GONE);
//		} else {
//			findViewById(R.id.bannerUpgradeView).setVisibility(View.VISIBLE);
//		}
//
//		Button upgradeBtn = (Button) findViewById(R.id.upgradeBtn);
//		upgradeBtn.setOnClickListener(this);
//
//		inneractiveBannerAd = (InneractiveAd) findViewById(R.id.inneractiveBannerAd);
//		InneractiveAdHelper.showBannerAd(upgradeBtn, inneractiveBannerAd, this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
//		View mainView = findViewById(R.id.mainView);
//		if (mainView != null) {
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
//				mainView.setBackground(backgroundChessDrawable);
//			} else {
//				mainView.setBackgroundDrawable(backgroundChessDrawable);
//			}
//		}
	}

	@Override
	protected void onStart() {
		if (HONEYCOMB_PLUS_API) {
			adjustActionBar();
		}
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!HONEYCOMB_PLUS_API) {
			adjustActionBar();
		}
	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
//		backgroundChessDrawable.updateConfig();
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		super.onPause();

	    /* // try to destroy ad here as MoPub team suggested
		if (moPubView != null) {
		moPubView.destroy();
    	}*/
		/*preferencesEditor.putLong(AppConstants.LAST_ACTIVITY_PAUSED_TIME, System.currentTimeMillis());
		preferencesEditor.commit();*/
	}

	@Override
	protected void onDestroy() {
//		if (inneractiveBannerAd != null) {
//			inneractiveBannerAd.cleanUp();
//		}
//		if (inneractiveRectangleAd != null) {
//			inneractiveRectangleAd.cleanUp();
//		}
		super.onDestroy();
	}

	protected void adjustActionBar() {
//		getActionBarHelper().showMenuItemById(R.id.menu_settings, showActionSettings);
//		getActionBarHelper().showMenuItemById(R.id.menu_new_game, showActionNewGame);
//		getActionBarHelper().showMenuItemById(R.id.menu_refresh, showActionRefresh);
//		getActionBarHelper().showMenuItemById(R.id.menu_search, showActionSearch);
//		getActionBarHelper().showMenuItemById(R.id.menu_singOut, LccHelper.getInstance(this).isConnected());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				getSlidingMenu().toggle();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
//		menuInflater.inflate(R.menu.sign_out, menu);
		menuInflater.inflate(R.menu.new_action_menu, menu);
//		getActionBarHelper().showMenuItemById(R.id.menu_singOut, LccHelper.getInstance(this).isConnected(), menu);
//		getActionBarHelper().showMenuItemById(R.id.menu_search, showActionSearch, menu);
//		getActionBarHelper().showMenuItemById(R.id.menu_settings, showActionSettings, menu);
//		getActionBarHelper().showMenuItemById(R.id.menu_new_game, showActionNewGame, menu);
//		getActionBarHelper().showMenuItemById(R.id.menu_refresh, showActionRefresh, menu);

//		if(HONEYCOMB_PLUS_API){
//			// Get the SearchView and set the searchable configuration
//			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//			SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//		}
		return super.onCreateOptionsMenu(menu);
	}



//	protected abstract class ChessUpdateListener extends ActionBarUpdateListener<String> {
//		public ChessUpdateListener() {
//			super(CoreActivityActionBar.this);
//		}
//	}




	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.re_signin){
			signInUser();
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if(resultCode == RESULT_OK && requestCode == Facebook.DEFAULT_AUTH_ACTIVITY_CODE && facebook != null){
//			facebook.authorizeCallback(requestCode, resultCode, data);
//		}
//	}

	public ActionBarHelper provideActionBarHelper() {
		return getActionBarHelper();
	}

	protected CoreActivityActionBar getInstance() {
		return this;
	}

	public SoundPlayer getSoundPlayer() {
		return SoundPlayer.getInstance(this);
	}

	@Override
	protected void afterLogin() {

	}
}
