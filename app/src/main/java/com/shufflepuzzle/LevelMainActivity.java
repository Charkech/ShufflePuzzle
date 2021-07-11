package com.shufflepuzzle;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.shufflepuzzle.Constants.FONT_GAME_MAIN;
import static com.shufflepuzzle.Constants.isConnected;

public class LevelMainActivity extends AppCompatActivity {
    RecyclerView recyclerViewStages;
    ArrayList<CategoryModel> levelModelArrayList = new ArrayList<>();
    PuzzleDatabaseHelper puzzleDatabaseHelper;

    String[] categoryNameLanguage;
    AdView mAdViewAdmob;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.level_main_activity);
        categoryNameLanguage = getResources().getStringArray(R.array.category_name);
        puzzleDatabaseHelper = new PuzzleDatabaseHelper(this);

        recyclerViewStages = findViewById(R.id.recyclerViewStages);
        recyclerViewStages.setLayoutManager(new GridLayoutManager(this,3));

        levelModelArrayList = puzzleDatabaseHelper.getAllCategories();
        if(levelModelArrayList==null||levelModelArrayList.size()==0)
        {
            puzzleDatabaseHelper.addDefaultSettings();
            new TaskWriteData().execute();
        }else
        {
            recyclerViewStages.setAdapter(new MyCategoryAdapter());
        }
        if (isConnected(this)) {

            try {
                addBannnerAdsAdmob();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    class TaskWriteData extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                puzzleDatabaseHelper.addAllCategory(getAllCategories());
                levelModelArrayList  = puzzleDatabaseHelper.getAllCategories();
                for(int i=0;i<levelModelArrayList.size();i++)
                {
                    puzzleDatabaseHelper.addAllLevels(getAllImagesByCategory(levelModelArrayList.get(i).getCategoryName()),(i+1),levelModelArrayList.get(i).getCategoryName());
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerViewStages.setAdapter(new MyCategoryAdapter());
        }


    }
    private List<String> getAllImagesByCategory(String category) throws IOException
    {
        AssetManager assetManager = getAssets();
        String[] files = assetManager.list("Categories/"+category);
        List<String> it= Arrays.asList(files);
        Collections.sort(it);
        return it;
    }
    private List<String> getAllCategories() throws IOException
    {
        AssetManager assetManager = getAssets();
        String[] files = assetManager.list("Categories");
        List<String> it= Arrays.asList(files);
        Collections.sort(it);

        return it;
    }



    public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.CategoryViewHolder>
    {
        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LevelMainActivity.this).inflate(R.layout.category_list_item,null);

            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            final CategoryModel categoryModel = levelModelArrayList.get(position);

            holder.txtCategoryName.setTypeface(FONT_GAME_MAIN);
            holder.txtCategoryName.setText(categoryNameLanguage[position]);
            Picasso.get().load(categoryModel.getImage()).into(holder.imgCategoryImage);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LevelMainActivity.this,MainGameActivity.class);
                    intent.putExtra("categoryModel",categoryModel);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return levelModelArrayList.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder
        {
            protected ImageView imgCategoryImage;
            protected TextView txtCategoryName;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                imgCategoryImage = itemView.findViewById(R.id.imgCategoryImage);
                txtCategoryName = itemView.findViewById(R.id.txtCategoryName);


            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdViewAdmob != null) {
            mAdViewAdmob.resume();
        }
    }

    void addBannnerAdsAdmob() {
        LinearLayout linearAdsBanner = (LinearLayout) findViewById(R.id.linearAdsBanner);
        linearAdsBanner.removeAllViews();

        if (isConnected(this)) {

            mAdViewAdmob = new AdView(LevelMainActivity.this);
            AdSize adSize = getAdSize();
            mAdViewAdmob.setAdSize(adSize);
            mAdViewAdmob.setAdUnitId(getResources().getString(R.string.admob_banner));
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            linearAdsBanner.addView(mAdViewAdmob);
            mAdViewAdmob.loadAd(adRequest);

        }
    }



    @Override
    public void onDestroy() {
        if (mAdViewAdmob != null) {
            mAdViewAdmob.destroy();
        }

        super.onDestroy();
    }
    @Override
    protected void onPause() {
        if (mAdViewAdmob != null) {
            mAdViewAdmob.pause();
        }
        super.onPause();
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
