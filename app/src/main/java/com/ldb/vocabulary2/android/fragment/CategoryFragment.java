package com.ldb.vocabulary2.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.activity.CategoryEditActivity;
import com.ldb.vocabulary2.android.activity.VocabularyActivity;
import com.ldb.vocabulary2.android.adapter.CategoryAdapter;
import com.ldb.vocabulary2.android.data.Constants;

/**
 * Created by lsp on 2016/10/7.
 */
public class CategoryFragment extends Fragment {

    private static final String TAG = CategoryFragment.class.getSimpleName();

    private RecyclerView mCategoryRecycler;
    private CategoryAdapter mAdapter;

    public static CategoryFragment newInstance(){
        return new CategoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
//        setRetainInstance(true);
//        if(savedInstanceState == null) {
//            Log.d(TAG, "s null");
            mAdapter = new CategoryAdapter(getActivity());
            refresh();
//        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        mCategoryRecycler = (RecyclerView) rootView.findViewById(R.id.category_recycler_view);
        setUpCategoryRecycler();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.category_menu_refresh:
                refresh();
                return true;
            case R.id.category_menu_add:
                Intent intent = new Intent(getActivity(), CategoryEditActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_CATEGORY_ADD && resultCode == R.id.submit){
            int code = data.getIntExtra(Constants.KEY_CODE, -1);
            String message = data.getStringExtra(Constants.KEY_MESSAGE);
            if(code == Constants.VALUE_CODE_OK){
                // TODO 刷新界面
                showMessage(message);
            }else{
                // TODO
                showError(message);
            }
        }
    }

    private void refresh() {
        if(mAdapter != null) {
            mAdapter.refresh(new CategoryAdapter.OnLoadCategoryCallback() {
                @Override
                public void onBeforeLoad() {

                }

                @Override
                public void onAfterLoad() {

                }

                @Override
                public void onLoadSuccess(String message) {

                }

                @Override
                public void onLoadError(String error) {
                    showError(error);
                }
            });
        }
    }

    private void setUpCategoryRecycler(){
        mCategoryRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = VocabularyActivity.newIntent(
                        getActivity(), mAdapter.getItem(position));
                startActivity(intent);
            }
        });
        mCategoryRecycler.setAdapter(mAdapter);
    }

    private void showMessage(String message){
        Snackbar.make(getView().findViewById(R.id.snack_bar_decor),
                message, Snackbar.LENGTH_SHORT).show();
    }

    private void showError(String error){
        Snackbar.make(getView().findViewById(R.id.snack_bar_decor),
                error, Snackbar.LENGTH_SHORT).show();
    }

}
