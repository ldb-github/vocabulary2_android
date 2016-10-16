package com.ldb.vocabulary2.android.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.activity.VocabularyEditActivity;
import com.ldb.vocabulary2.android.adapter.VocabularyAdapter;
import com.ldb.vocabulary2.android.data.Repository;
import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.model.Vocabulary;
import com.ldb.vocabulary2.android.util.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsp on 2016/9/24.
 */
public class VocabularyFragment extends Fragment {

    private static final String TAG = VocabularyFragment.class.getSimpleName();

    private static final String ARG_CATEGORY = "category";

    private Category mCategory;
    private RecyclerView mVocabularyView;
    private VocabularyAdapter mAdapter;
    private List<Vocabulary> mVocabularyList = new ArrayList<>();
    private int mPage;
    private ImageView mCategoryImageView;
    private FloatingActionButton mFab;

    public static VocabularyFragment newInstance(Category category){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CATEGORY, category);

        VocabularyFragment fragment = new VocabularyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

//        if(savedInstanceState == null) {
            Bundle arg = getArguments();
            if(arg != null){
                mCategory = arg.getParcelable(ARG_CATEGORY);
            }
            mAdapter = new VocabularyAdapter(getActivity(), mCategory);
            refresh();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);

        mCategoryImageView = (ImageView) view.findViewById(R.id.category_image);

        collapsingToolbarLayout.setTitle(mCategory.getName());
        Bitmap bitmap = BitmapFactory.decodeFile(mCategory.getImageLocal());
        if(bitmap != null) {
            mCategoryImageView.setImageBitmap(bitmap);
        }else {
            Picasso.with(getActivity()).load(mCategory.getImage()).into(mCategoryImageView);
        }

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        setFab(mCategory.isFavorite());

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite(!mCategory.isFavorite());
            }
        });

        mVocabularyView = (RecyclerView) view.findViewById(R.id.vocabulary_list);
//        mVocabularyView.setHasFixedSize(true);

//        // 导致Toolbar的scroll失效
//        mVocabularyView.setNestedScrollingEnabled(false);

        setUpVocabularyRecycler();

        return view;
    }

    private void setUpVocabularyRecycler(){
        mVocabularyView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setOnItemClickListener(new VocabularyAdapter.OnItemClickListener() {
            @Override
            public int onClick(View view, int position) {
                int id = view.getId();
                switch (id){
                    case R.id.vocabulary_delete:
                        // TODO 弹框提示是否删除

                        return 1;
                }
                return 0;
            }
        });
        mVocabularyView.setAdapter(mAdapter);
    }

    private void setFavorite(boolean favorite){
        mCategory.setFavorite(favorite);
        mCategory.setLastRead(DateUtil.getCurrentDate());
        Repository repository = Repository.getInstance();
        if(!favorite){
            List<String> ids = new ArrayList<>();
            ids.add(mCategory.getLocalId());
            repository.deleteCollections(getActivity(), ids);
        }else{
            repository.saveCategoryLocal(getActivity(), mCategory);
            // TODO 保存词汇到本地
        }
        setFab(favorite);
    }

    private void deleteFavorite(){

    }


    private void setFab(boolean favorite){
        if(favorite){
            mFab.setImageDrawable(ContextCompat
                    .getDrawable(getActivity(), R.drawable.ic_favorite_white));
        }else{
            mFab.setImageDrawable(ContextCompat
                    .getDrawable(getActivity(), R.drawable.ic_favorite_white_border));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_vocabulary, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.vocabulary_menu_refresh:
                refresh();
                return true;
            case R.id.vocabulary_menu_add:
                Intent intent = VocabularyEditActivity.newIntent(getActivity(),
                        mCategory);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showError(String error){
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void refresh(){
        if(mAdapter != null){
            mAdapter.refresh(new VocabularyAdapter.OnLoadVocabularyCallback() {
                @Override
                public void onBeforeLoad() {
                    // TODO 添加下拉刷新控制
                    showMessage(getResources().getString(R.string.refresh_start_text));
                }

                @Override
                public void onAfterLoad() {
                    // TODO 添加下拉刷新控制
                    showMessage(getResources().getString(R.string.refresh_end_text));
                }

                @Override
                public void onLoadSuccess(String message) {
                    showMessage(message);
                }

                @Override
                public void onLoadError(String error) {
                    showError(error);
                }
            });
        }
    }
}
