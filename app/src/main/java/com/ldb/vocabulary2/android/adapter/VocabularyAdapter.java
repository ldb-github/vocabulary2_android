package com.ldb.vocabulary2.android.adapter;

/**
 * Created by lsp on 2016/10/8.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

    private static final int MESSAGE_UPLOAD = 1;
    private static final int MESSAGE_DELETE = 2;

    private static final String EXTRA_LOCALID = "localid";
    private static final String EXTRA_VOCABULARY = "vocabulary";

    private Activity mActivity;
    private List<Vocabulary> mVocabularies;
    private int mPage;
    private Category mCategory;
    private Handler mHandler;

    private OnItemClickListener mOnItemClickListener;

    public VocabularyAdapter(Activity activity, Category category){
        mActivity = activity;
        mVocabularies = new ArrayList<>();
        mPage = 0;
        mCategory = category;
        mHandler = new Handler(new VocabularyHandlerCallback());
    }

    @Override
    public VocabularyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VocabularyHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(final VocabularyHolder holder, int position) {
        Vocabulary vocabulary = mVocabularies.get(position);
        holder.mVocabularyName.setText(vocabulary.getName());
        holder.mVocabularyTranslation.setText(vocabulary.getTranslation());
        Bitmap bitmap = BitmapFactory.decodeFile(vocabulary.getImageLocal());
        if(bitmap != null) {
            holder.mVocabularyImage.setImageBitmap(bitmap);
        }else{
            Picasso.with(mActivity).load(vocabulary.getImage()).into(holder.mVocabularyImage);
        }
        if(vocabulary.isUploaded()){
            holder.mVocabularyDelete.setVisibility(View.GONE);
        }else{
            holder.mVocabularyDelete.setVisibility(View.VISIBLE);
        }
        if(mCategory.isUploaded() && !vocabulary.isUploaded()){
            holder.mVocabularyUpload.setVisibility(View.VISIBLE);
        }else{
            holder.mVocabularyUpload.setVisibility(View.GONE);
        }
        holder.mVocabularyDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null) {
                    int result = mOnItemClickListener.onClick(v, holder.getAdapterPosition());
                    if (result == 1) {
                        deleteVocabulary(getItem(holder.getAdapterPosition()).getLocalId());
                    }
                }
            }
        });
        holder.mVocabularyUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vocabulary vocabularyForUpload = getItem(holder.getAdapterPosition());
                vocabularyForUpload.setCId(mCategory.getId());
                vocabularyForUpload.setCIdLocal(mCategory.getLocalId());
                uploadVocabulary(vocabularyForUpload);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVocabularies.size();
    }

    public Vocabulary getItem(int position){
        return mVocabularies.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
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
        repository.getVocabularyList(mActivity, mCategory, mPage, null,
                new Callback.RequestVocabularyListCallback() {
                    @Override
                    public void onSuccess(String message, List<Vocabulary> vocabularyList) {
                        if(vocabularyList != null) {
                            mVocabularies.addAll(vocabularyList);
                            notifyDataSetChanged();
                        }
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

    private void deleteVocabulary(String localId){
        List<String> localIds = new ArrayList<>();
        localIds.add(localId);
        deleteVocabularies(localIds);
    }

    private void deleteVocabularies(List<String> localIds){
        Repository repository = Repository.getInstance();
        repository.deleteVocabularyLocal(mActivity, localIds);
        Message message = mHandler.obtainMessage(MESSAGE_DELETE);
        Bundle bundle = new Bundle();
        ArrayList<String> localIdArray = new ArrayList<>();
        if(localIds != null) {
            localIdArray.addAll(localIds);
        }
        bundle.putStringArrayList(EXTRA_LOCALID, localIdArray);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void removeItems(List<String> localIds){
        for(String localId : localIds){
            notifyItemMove(localId);
        }
    }

    private void notifyItemMove(String localId){
        int position = getItemPositionById(localId);
        if(position > 0){
            mVocabularies.remove(position);
            notifyDataSetChanged();
        }
    }

    private void saveLocal(Vocabulary vocabulary){
        Repository repository = Repository.getInstance();
        repository.saveVocabularyLocal(mActivity, vocabulary);
    }

    private void uploadVocabulary(final Vocabulary vocabulary) {
        Repository repository = Repository.getInstance();
        repository.postVocabulary(mActivity, vocabulary, new Callback.PostVocabularyCallback() {
            @Override
            public void onSuccess(String message, Vocabulary vocabularyReturn) {
                vocabularyReturn.setCIdLocal(vocabulary.getCIdLocal());
                vocabularyReturn.setLocalId(vocabulary.getLocalId());
                Message data = mHandler.obtainMessage(MESSAGE_UPLOAD);
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_LOCALID, vocabulary.getLocalId());
                bundle.putParcelable(EXTRA_VOCABULARY, vocabularyReturn);
                data.setData(bundle);
                mHandler.sendMessage(data);

                // TODO 上传成功处理

            }

            @Override
            public void onError(String error) {
                // TODO 上传失败处理
            }
        });
    }

    private void notifyItemChanged(String localId, Vocabulary newVocabulary){
        int position = getItemPositionById(localId);
        if(position >= 0) {
            Vocabulary vocabulary = getItem(position);
            vocabulary.setUploaded(true);
            vocabulary.setId(newVocabulary.getId());
            vocabulary.setImage(newVocabulary.getImage());
            vocabulary.setImageRemote(newVocabulary.getImageRemote());
            vocabulary.setCreateTime(newVocabulary.getCreateTime());
            vocabulary.setCId(newVocabulary.getCId());
            vocabulary.setCIdLocal(newVocabulary.getCIdLocal());
            mVocabularies.set(position, vocabulary);
            // 更新本地数据
            saveLocal(vocabulary);
//            notifyItemChanged(position);
            notifyDataSetChanged();
        }

    }

    private int getItemPositionById(String id) {
        for (int i = 0; i < mVocabularies.size(); i++) {
            if (mVocabularies.get(i).getLocalId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    class VocabularyHandlerCallback implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_UPLOAD:
                    Bundle bundle = msg.getData();
                    String localId = bundle.getString(EXTRA_LOCALID);
                    Vocabulary vocabulary = bundle.getParcelable(EXTRA_VOCABULARY);
                    notifyItemChanged(localId, vocabulary);
                    return true;
                case MESSAGE_DELETE:
                    Bundle deleteData = msg.getData();
                    ArrayList<String> localIds = deleteData.getStringArrayList(EXTRA_LOCALID);
                    removeItems(localIds);
                    return true;
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        int onClick(View view, int position);
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
        private TextView mVocabularyTranslation;
        private ImageView mVocabularyDelete;
        private ImageView mVocabularyUpload;

        public VocabularyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_vocabulary, parent, false));
            mVocabularyImage = (ImageView) itemView.findViewById(R.id.vocabulary_image);
            mVocabularyName = (TextView) itemView.findViewById(R.id.vocabulary_name);
            mVocabularyTranslation = (TextView) itemView.findViewById(R.id.vocabulary_translation);
            mVocabularyDelete = (ImageView) itemView.findViewById(R.id.vocabulary_delete);
            mVocabularyUpload = (ImageView) itemView.findViewById(R.id.vocabulary_upload);
        }
    }
}