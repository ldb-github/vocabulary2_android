package com.ldb.vocabulary2.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.activity.CategoryEditActivity;
import com.ldb.vocabulary2.android.activity.VocabularyActivity;
import com.ldb.vocabulary2.android.adapter.CollectionAdapter;
import com.ldb.vocabulary2.android.data.Constants;

/**
 * Created by lsp on 2016/10/7.
 */
public class CollectionFragment extends Fragment {

    private RecyclerView mCategoryRecycler;
    private CollectionAdapter mAdapter;

    public static CollectionFragment newInstance(){
        return new CollectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        if(savedInstanceState == null) {
            mAdapter = new CollectionAdapter(getActivity());
            refresh();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        mCategoryRecycler = (RecyclerView) rootView.findViewById(R.id.category_recycler_view);
        setUpCategoryRecycler();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    private void refresh(){
        if(mAdapter != null) {
            mAdapter.refresh();
        }
    }

    private void setUpCategoryRecycler(){
        mCategoryRecycler.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.collection_card_number)));

        mAdapter.setOnItemClickListener(new CollectionAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                int id = view.getId();
                switch (id){
                    case R.id.card_upload:
                        Toast.makeText(getActivity(), "upload", Toast.LENGTH_SHORT).show();
                        //mAdapter.uploadCategory(mAdapter.getItem(position));
                        return;
                    case R.id.card_favorite:
                        Toast.makeText(getActivity(), "favorite:", Toast.LENGTH_SHORT).show();
                        return;
                    default:
                        Toast.makeText(getActivity(), "default", Toast.LENGTH_SHORT).show();
                        Intent intent = VocabularyActivity.newIntent(
                                getActivity(), mAdapter.getItem(position));
                        startActivity(intent);
                }
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
