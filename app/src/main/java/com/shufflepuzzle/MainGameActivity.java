package com.shufflepuzzle;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.shufflepuzzle.Constants.FONT_GAME_MAIN;
import static com.shufflepuzzle.Constants.isConnected;


public class MainGameActivity extends Activity {
	final Handler h = new Handler();
	List<ImageView> items;
	List<Integer> items_rotations;
	MediaPlayer mp = new MediaPlayer();
	SoundPool sndpool;
	int snd_move;
	int snd_result;
	int snd_info;
	float start_x;
	float start_y;
	int item_size;
	boolean items_enabled;
	AnimatorSet anim;
	int screen_width;
	int screen_height;
	int t;
	int current_section;
	int num_cols;
	int num_rows;
	final int spacing = 2; // spacing between blocks


	RecyclerView recyclerViewStages;
	ArrayList<LevelModel> levelModelArrayList;
	PuzzleDatabaseHelper puzzleDatabaseHelper;
	CategoryModel categoryModel;
	int currentSelectedPosition=0;
	MyLevelsAdapter myLevelsAdapter;

	AdView mAdViewAdmob;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_game_activity);

		// fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// preferences

		show_section(R.id.main);

		categoryModel = (CategoryModel) getIntent().getSerializableExtra("categoryModel");

		puzzleDatabaseHelper = new PuzzleDatabaseHelper(this);
		levelModelArrayList = puzzleDatabaseHelper.getAllLevels(categoryModel.getId());
		recyclerViewStages = findViewById(R.id.recyclerViewStages);
		recyclerViewStages.setLayoutManager(new GridLayoutManager(this,4));



		myLevelsAdapter = new MyLevelsAdapter();
		recyclerViewStages.setAdapter(myLevelsAdapter);


		// bg sound
		try {
			mp = new MediaPlayer();
			AssetFileDescriptor descriptor = getAssets().openFd("snd_bg.mp3");
			mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setLooping(true);
			mp.setVolume(0, 0);
			mp.prepare();
			mp.start();
		} catch (Exception e) {
		}


		// mute
		if (puzzleDatabaseHelper.isMuteGameSound()) {
			((SwitchButton) findViewById(R.id.config_mute)).setChecked(true);
		} else {
			mp.setVolume(0.2f, 0.2f);
		}


		// volume switch listener
		((SwitchButton) findViewById(R.id.config_mute)).setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(SwitchButton view, boolean isChecked) {
				puzzleDatabaseHelper.updateSettings(1,isChecked?1:0);

				if (isChecked) {
					mp.setVolume(0, 0);
				} else {
					mp.setVolume(0.2f, 0.2f);
				}
			}
		});


		// mode
		if (puzzleDatabaseHelper.getGameMode()==2) {
			String hard = getResources().getString(R.string.hard);
			((TextView)findViewById(R.id.txtGameMode)).setText(getResources().getString(R.string.mode)+" :"+hard);
			((RadioGroup) findViewById(R.id.radioGroupMode)).check(R.id.radioHard);
			num_cols = 12;
			num_rows = 15;
		} else if(puzzleDatabaseHelper.getGameMode()==1)
		{
			String moderate = getResources().getString(R.string.moderate);
			((TextView)findViewById(R.id.txtGameMode)).setText(getResources().getString(R.string.mode)+" :"+moderate);
			((RadioGroup) findViewById(R.id.radioGroupMode)).check(R.id.radioMedium);
			num_cols = 8;
			num_rows = 10;
		}
		else {
			String easy = getResources().getString(R.string.easy);
			((TextView)findViewById(R.id.txtGameMode)).setText(getResources().getString(R.string.mode)+" :"+easy);
			((RadioGroup) findViewById(R.id.radioGroupMode)).check(R.id.radioEasy);
			num_cols = 4;
			num_rows = 5;
		}

		// mode switch listener
		((RadioGroup)findViewById(R.id.radioGroupMode)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.radioHard)
				{
					String hard = getResources().getString(R.string.hard);
					((TextView)findViewById(R.id.txtGameMode)).setText(getResources().getString(R.string.mode)+" :"+hard);
					puzzleDatabaseHelper.updateSettings(2,2);
					num_cols = 12;
					num_rows = 15;
					puzzleDatabaseHelper.updateSettings(2,2);
				}else if(checkedId==R.id.radioMedium)
				{
					String moderate = getResources().getString(R.string.moderate);
					((TextView)findViewById(R.id.txtGameMode)).setText(getResources().getString(R.string.mode)+" :"+moderate);
					puzzleDatabaseHelper.updateSettings(2,1);
					num_cols = 8;
					num_rows = 10;
					puzzleDatabaseHelper.updateSettings(2,1);
				}else
				{
					String easy = getResources().getString(R.string.easy);
					((TextView)findViewById(R.id.txtGameMode)).setText(getResources().getString(R.string.mode)+" : "+ easy);
					puzzleDatabaseHelper.updateSettings(2,0);
					num_cols = 4;
					num_rows = 5;
					puzzleDatabaseHelper.updateSettings(2,0);
				}



			}
		});


		// SoundPool
		sndpool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		try {
			snd_move = sndpool.load(getAssets().openFd("snd_move.mp3"), 1);
			snd_result = sndpool.load(getAssets().openFd("snd_result.mp3"), 1);
			snd_info = sndpool.load(getAssets().openFd("snd_info.mp3"), 1);
		} catch (IOException e) {
		}

		// custom font
		Typeface font = Typeface.createFromAsset(getAssets(), "CooperBlack.otf");
		Typeface font2= Typeface.createFromAsset(getAssets(),"bangers.ttf");
		((TextView) findViewById(R.id.txt_result)).setTypeface(font);
		((TextView) findViewById(R.id.txt_high_result)).setTypeface(font);
		((TextView) findViewById(R.id.txt_faq)).setTypeface(font);
		font = Typeface.createFromAsset(getAssets(), "BankGothic.ttf");
		((TextView) findViewById(R.id.txt_tap)).setTypeface(font);
		((TextView) findViewById(R.id.txt_description)).setTypeface(font2);


		if (isConnected(this)) {

			try {
				addBannnerAdsAdmob();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}



	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			// fullscreen mode
			if (Build.VERSION.SDK_INT >= 19) {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
								| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
		}
	}

	// START
	void START(int position) {
		items_enabled = true;
		t = 0;
		items = new ArrayList<ImageView>();
		items_rotations = new ArrayList<Integer>();
		((ViewGroup) findViewById(R.id.game)).removeAllViews();

		// screen size
		screen_width = Math.min(findViewById(R.id.all).getWidth(), findViewById(R.id.all).getHeight());
		screen_height = Math.max(findViewById(R.id.all).getWidth(), findViewById(R.id.all).getHeight());

		// item size
		item_size = (int) Math.floor(Math.min((screen_width - (num_cols - 1) * spacing) / num_cols,
				(screen_height - (num_rows - 1) * spacing) / num_rows));

		// start position
		start_x = (screen_width - item_size * num_cols - (num_cols - 1) * spacing) / 2;
		start_y = (screen_height - item_size * num_rows - (num_rows - 1) * spacing) / 2;

		// bitmap
		final Bitmap bitmap = getBitmapFromAsset(levelModelArrayList.get(position).getImage_name());

		// scale matrix
		final Matrix m = new Matrix();
		m.setScale((float) (num_cols * item_size) / bitmap.getWidth(), (float) (num_rows * item_size) / bitmap.getHeight());

		// bitmap scaled
		final Bitmap bitmap_scaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

		// add items
		float x_pos = 0;
		float y_pos = 0;
		for (int i = 0; i < num_rows * num_cols; i++) {
			ImageView item = new ImageView(this);
			item.setClickable(true);
			item.setLayoutParams(new LayoutParams(item_size, item_size));

			// image piece
			item.setImageBitmap(Bitmap.createBitmap(bitmap_scaled, (int) x_pos * item_size, (int) y_pos * item_size, item_size,
					item_size));

			// position
			item.setX(start_x + x_pos * item_size + x_pos * spacing);
			item.setY(start_y + y_pos * item_size + y_pos * spacing);

			// random rotation
			items_rotations.add((int) (Math.round(Math.random() * 3) * 90));
			item.setRotation(items_rotations.get(i));

			((ViewGroup) findViewById(R.id.game)).addView(item);
			items.add(item);

			x_pos++;
			if (x_pos == num_cols) {
				x_pos = 0;
				y_pos++;
			}

			// click listener
			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (items_enabled) {
						// animation list
						final List<Animator> anim_list = new ArrayList<Animator>();

						// scale down
						anim = new AnimatorSet();
						anim.playTogether(ObjectAnimator.ofFloat(v, "scaleX", 0.5f), ObjectAnimator.ofFloat(v, "scaleY", 0.5f));
						anim_list.add(anim);

						// rotate
						final int current_item = items.indexOf(v);
						items_rotations.set(current_item, items_rotations.get(current_item) + 90);
						anim_list.add(ObjectAnimator.ofFloat(v, "rotation", items_rotations.get(current_item)));

						// scale up
						anim = new AnimatorSet();
						anim.playTogether(ObjectAnimator.ofFloat(v, "scaleX", 1), ObjectAnimator.ofFloat(v, "scaleY", 1));
						anim_list.add(anim);

						// animation
						anim = new AnimatorSet();
						anim.playSequentially(anim_list);
						anim.setDuration(50);
						anim.addListener(new AnimatorListener() {
							@Override
							public void onAnimationEnd(Animator animation) {
								check_items();
							}

							@Override
							public void onAnimationCancel(Animator animation) {
							}

							@Override
							public void onAnimationRepeat(Animator animation) {
							}

							@Override
							public void onAnimationStart(Animator animation) {
								// sound
								if (!puzzleDatabaseHelper.isMuteGameSound()) {
									sndpool.play(snd_move, 0.2f, 0.2f, 0, 0, 1);
								}
							}
						});
						anim.start();
					}
				}
			});
		}

		h.postDelayed(TIMER, 1000);
		show_section(R.id.game);

		// AdMob Interstitial

	}

	// check_items
	void check_items() {
		for (int i = 0; i < items.size(); i++) {
			if ((float) items_rotations.get(i) / 360 != (float) Math.round(items_rotations.get(i) / 360)) {
				return;
			}
		}

		// all done
		items_enabled = false;
		h.removeCallbacks(TIMER);




		LevelModel levelModel  = levelModelArrayList.get(currentSelectedPosition);
		levelModel.setStatus(1);
		levelModel.setCoin(t);

		int gameMode = puzzleDatabaseHelper.getGameMode();
		if(gameMode==0)
			gameMode=1;

		if(t<levelModel.getHigh_coin()||levelModel.getHigh_coin()==0)
		{
			levelModel.setHigh_coin(t);
			if(t<=(60*gameMode))
			{
				levelModel.setRating(3);
			}else if(t<=(120*gameMode))
			{
				levelModel.setRating(2);
			}else
			{
				levelModel.setRating(1);
			}
			puzzleDatabaseHelper.updateLevel(levelModel);
		}
		if(t<=(60*gameMode))
		{
			levelModel.setTemp_rating(3);
		}else if(t<=(120*gameMode))
		{
			levelModel.setTemp_rating(2);
		}else
		{
			levelModel.setTemp_rating(1);
		}

		levelModelArrayList = puzzleDatabaseHelper.getAllLevels(categoryModel.getId());
		myLevelsAdapter.notifyDataSetChanged();


		// show time message
		Toast toast = Toast.makeText(MainGameActivity.this, getString(R.string.completed), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		((TextView) ((LinearLayout) toast.getView()).getChildAt(0)).setTextSize(DpToPx(20));
		toast.show();

		// sound
		if (!puzzleDatabaseHelper.isMuteGameSound()) {
			sndpool.play(snd_info, 1f, 1f, 0, 0, 1);
		}

		h.postDelayed(STOP, 3000);
	}

	// TIMER
	Runnable TIMER = new Runnable() {
		@Override
		public void run() {
			t++;
			h.postDelayed(TIMER, 1000);
		}
	};

	// STOP
	Runnable STOP = new Runnable() {
		@Override
		public void run() {
			// show result
			show_section(R.id.result);

			LevelModel levelModel = levelModelArrayList.get(currentSelectedPosition);
			if(levelModel.getRating()<1)
			{
				((ImageView)findViewById(R.id.startComplete1)).setImageResource(R.drawable.ic_start_blank);
				((ImageView)findViewById(R.id.startComplete2)).setImageResource(R.drawable.ic_start_blank);
				((ImageView)findViewById(R.id.startComplete3)).setImageResource(R.drawable.ic_start_blank);
			}
			else if (levelModel.getRating() < 2) {
				((ImageView)findViewById(R.id.startComplete1)).setImageResource(R.drawable.ic_star_fill);
				((ImageView)findViewById(R.id.startComplete2)).setImageResource(R.drawable.ic_start_blank);
				((ImageView)findViewById(R.id.startComplete3)).setImageResource(R.drawable.ic_start_blank);
			} else if (levelModel.getRating() < 3) {
				((ImageView)findViewById(R.id.startComplete1)).setImageResource(R.drawable.ic_star_fill);
				((ImageView)findViewById(R.id.startComplete2)).setImageResource(R.drawable.ic_star_fill);
				((ImageView)findViewById(R.id.startComplete3)).setImageResource(R.drawable.ic_start_blank);
			} else if (levelModel.getRating() == 3) {
				((ImageView)findViewById(R.id.startComplete1)).setImageResource(R.drawable.ic_star_fill);
				((ImageView)findViewById(R.id.startComplete2)).setImageResource(R.drawable.ic_star_fill);
				((ImageView)findViewById(R.id.startComplete3)).setImageResource(R.drawable.ic_star_fill);
			}


			// show result
			((TextView) findViewById(R.id.txt_result)).setText(getString(R.string.time) + " " + timeConvert(t));
			((TextView) findViewById(R.id.txt_high_result)).setText(getString(R.string.best_time) + " "
					+ timeConvert(levelModel.getHigh_coin()));



			// sound
			if (!puzzleDatabaseHelper.isMuteGameSound()) {
				sndpool.play(snd_result, 0.6f, 0.6f, 0, 0, 1);
			}
		}
	};

	// onClick
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start:
			START(0);
			break;
		case R.id.btn_config:
			show_section(R.id.config);
			break;
		case R.id.result:
			show_section(R.id.main);
			break;
		case R.id.txt_faq:
			findViewById(R.id.txt_faq).setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		switch (current_section) {
		case R.id.main:
			super.onBackPressed();
			break;
		case R.id.config:
		case R.id.result:
			show_section(R.id.main);
			break;
		case R.id.game:
			show_section(R.id.main);
			h.removeCallbacks(TIMER);
			h.removeCallbacks(STOP);
			if (anim != null) {
				anim.cancel();
			}
			break;
		}
	}

	// show_section
	void show_section(int section) {
		current_section = section;
		findViewById(R.id.main).setVisibility(View.GONE);
		findViewById(R.id.game).setVisibility(View.GONE);
		findViewById(R.id.config).setVisibility(View.GONE);
		findViewById(R.id.result).setVisibility(View.GONE);
		findViewById(current_section).setVisibility(View.VISIBLE);

		if (current_section == R.id.game) {
			findViewById(R.id.txt_faq).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.txt_faq).setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		if (mAdViewAdmob != null) {
			mAdViewAdmob.destroy();
		}
		super.onDestroy();
		h.removeCallbacks(TIMER);
		h.removeCallbacks(STOP);
		mp.release();
		sndpool.release();


	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdViewAdmob != null) {
			mAdViewAdmob.resume();
		}
		// sound
		if (!puzzleDatabaseHelper.isMuteGameSound()) {
			mp.setVolume(0.2f, 0.2f);
		}

		// timer
		if (current_section == R.id.game && items_enabled) {
			h.removeCallbacks(TIMER);
			h.postDelayed(TIMER, 1000);
		}
	}

	@Override
	protected void onPause() {
		if (mAdViewAdmob != null) {
			mAdViewAdmob.pause();
		}
		super.onPause();
		mp.setVolume(0, 0);
		h.removeCallbacks(TIMER);
	}

	// DpToPx
	float DpToPx(float dp) {
		return (dp * (getResources().getDisplayMetrics().densityDpi / 160f));
	}

	// PxToDp
	float PxToDp(float px) {
		return px / (getResources().getDisplayMetrics().densityDpi / 160f);
	}

	// timeConvert
	String timeConvert(int t) {
		String str = "";
		int d, h, m, s;

		if (t / 86400 >= 1) {// if day exist
			d = t / 86400;
			str += d + ":";
		} else {
			d = 0;
		}

		t = t - (86400 * d);

		if (t / 3600 >= 1) {// if hour exist
			h = t / 3600;
			if (h < 10 && d > 0) {
				str += "0";
			}
			str += h + ":";
		} else {
			h = 0;
		}

		if ((t - h * 3600) / 60 >= 1) {// if minute exist
			m = (t - h * 3600) / 60;
			s = (t - h * 3600) - m * 60;
			if (m < 10 && h > 0) {
				str += "0";
			}
			str += m + ":";
		} else {
			m = 0;
			s = t - h * 3600;
		}

		if (s < 10 && m > 0) {
			str += "0";
		}
		str += s;

		return str;
	}

	public Bitmap getBitmapFromAsset(String fileName) {
		AssetManager assetManager = getAssets();

		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open("Categories/"+fileName);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			// handle exception
		}

		return bitmap;
	}

	private List<String> getImage() throws IOException
	{
		AssetManager assetManager = getAssets();
		String[] files = assetManager.list("Levels");
		List<String> it= Arrays.asList(files);
		Collections.sort(it);
		return it;
	}

	public class MyLevelsAdapter extends RecyclerView.Adapter<MyLevelsAdapter.LevelsViewHolder>
	{
		@NonNull
		@Override
		public MyLevelsAdapter.LevelsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(MainGameActivity.this).inflate(R.layout.level_list_item,null);

			return new MyLevelsAdapter.LevelsViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull MyLevelsAdapter.LevelsViewHolder holder, final int position) {
			LevelModel levelModel = levelModelArrayList.get(position);

			holder.txtLevelNumber.setTypeface(FONT_GAME_MAIN);
			holder.txtLevelNumber.setText(levelModel.getLevel()+"");
			Picasso.get().load(levelModel.getImage()).into(holder.imgLevelImage);

			if(levelModel.getStatus()==1)
			{
				holder.relBg.setBackgroundColor(getResources().getColor(R.color.semi_transparent));
			}else
			{
				holder.relBg.setBackgroundColor(getResources().getColor(R.color.dark_transparent));
			}

			if(levelModel.getRating()<1)
			{
				holder.start1.setImageResource(R.drawable.ic_start_blank);
				holder.start2.setImageResource(R.drawable.ic_start_blank);
				holder.start3.setImageResource(R.drawable.ic_start_blank);

			}
			else if (levelModel.getRating() < 2) {
				holder.start1.setImageResource(R.drawable.ic_star_fill);
				holder.start2.setImageResource(R.drawable.ic_start_blank);
				holder.start3.setImageResource(R.drawable.ic_start_blank);
			} else if (levelModel.getRating() < 3) {
				holder.start1.setImageResource(R.drawable.ic_star_fill);
				holder.start2.setImageResource(R.drawable.ic_star_fill);
				holder.start3.setImageResource(R.drawable.ic_start_blank);
			} else if (levelModel.getRating() == 3) {
				holder.start1.setImageResource(R.drawable.ic_star_fill);
				holder.start2.setImageResource(R.drawable.ic_star_fill);
				holder.start3.setImageResource(R.drawable.ic_star_fill);
			}

			holder.imgLevelImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currentSelectedPosition = position;
					START(position);
				}
			});



		}

		@Override
		public int getItemCount() {
			return levelModelArrayList.size();
		}

		public class LevelsViewHolder extends RecyclerView.ViewHolder
		{
			protected ImageView imgLevelImage;
			protected TextView txtLevelNumber;
			protected ImageView start1;
			protected ImageView start2;
			protected ImageView start3;
			protected RelativeLayout relBg;

			public LevelsViewHolder(@NonNull View itemView) {
				super(itemView);
				imgLevelImage = itemView.findViewById(R.id.imgLevelImage);
				txtLevelNumber = itemView.findViewById(R.id.txtLevelNumber);
				start1 = itemView.findViewById(R.id.start1);
				start2 = itemView.findViewById(R.id.start2);
				start3 = itemView.findViewById(R.id.start3);
				relBg = itemView.findViewById(R.id.relBg);

			}
		}
	}



	void addBannnerAdsAdmob() {
		LinearLayout linearAdsBanner = (LinearLayout) findViewById(R.id.linearAdsBanner);
		linearAdsBanner.removeAllViews();

		if (isConnected(this)) {

			mAdViewAdmob = new AdView(MainGameActivity.this);
			AdSize adSize = getAdSize();
			mAdViewAdmob.setAdSize(adSize);
			mAdViewAdmob.setAdUnitId(getResources().getString(R.string.admob_banner));
			AdRequest adRequest = new AdRequest.Builder()
					.build();
			linearAdsBanner.addView(mAdViewAdmob);
			mAdViewAdmob.loadAd(adRequest);

		}
	}







	private AdSize getAdSize() {
		// Step 2 - Determine the screen width (less decorations) to use for the ad width.
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float widthPixels = outMetrics.widthPixels;
		float density = outMetrics.density;

		int adWidth = (int) (widthPixels / density);

		// Step 3 - Get adaptive ad size and return for setting on the ad view.
		return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
	}
}