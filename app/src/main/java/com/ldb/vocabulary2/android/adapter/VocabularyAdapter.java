package com.ldb.vocabulary2.android.adapter;

/**
 * Created by lsp on 2016/10/8.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.data.Callback;
import com.ldb.vocabulary2.android.data.Repository;
import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.model.Vocabulary;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyHolder>{

    private Activity mActivity;
    private List<Vocabulary> mVocabularies;
    private int mPage;
    private Category mCategory;

    private OnItemClickListener mOnItemClickListener;

    public VocabularyAdapter(Activity activity, Category category){
        mActivity = activity;
        mVocabularies = new ArrayList<>();
        mPage = 0;
        mCategory = category;
    }

    @Override
    public VocabularyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VocabularyHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(VocabularyHolder holder, int position) {
        Vocabulary vocabulary = mVocabularies.get(position);
        holder.mVocabularyName.setText(vocabulary.getName());
//        if(holder.mVocabulary.getImage() != null && !holder.mVocabulary.getImage().trim().isEmpty()) {
                Picasso.with(mActivity).load(vocabulary.getImage()).into(holder.mVocabularyImage);
//        }
    }

    @Override
    public int getItemCount() {
        return mVocabularies.size();
    }

    public void refresh(OnLoadVocabularyCallback callback){
        mPage = 0;
        mVocabularies.clear();
        if(callback == null){
            callback = new OnLoadVocabularyCallback() {
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

                }
            };
        }
        loadMore(callback);
    }

    public void loadMore(final OnLoadVocabularyCallback callback){
        mPage += 1;
        callback.onBeforeLoad();
        Repository repository = Repository.getInstance();
        repository.getVocabularyList(mActivity, mCategory.getId(), mPage, null,
                new Callback.RequestVocabularyListCallback() {
                    @Override
                    public void onSuccess(String message, List<Vocabulary> vocabularyList) {
                        mVocabularies.addAll(vocabularyList);
                        notifyDataSetChanged();
                        callback.onLoadSuccess(message);
                        callback.onAfterLoad();
                    }

                    @Override
                    public void onError(String error) {
                        callback.onLoadError(error);
                        callback.onAfterLoad();
                    }
                });
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public interface OnLoadVocabularyCallback {
        void onBeforeLoad();
        void onAfterLoad();
        void onLoadSuccess(String message);
        void onLoadError(String error);
    }

    static class VocabularyHolder extends RecyclerView.ViewHolder{

        private Vocabulary mVocabulary;

        private ImageView mVocabularyImage;
        private TextView mVocabularyName;

        public VocabularyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_vocabulary, parent, false));
            mVocabularyImage = (ImageView) itemView.findViewById(R.id.vocabulary_image);
            mVocabularyName = (TextView) itemView.findViewById(R.id.vocabulary_name);
        }
    }
}