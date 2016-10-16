package com.ldb.vocabulary2.android.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.data.Callback;
import com.ldb.vocabulary2.android.data.Repository;
import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.util.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsp on 2016/10/8.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionHolder>{

    private static final int MESSAGE_UPLOAD = 1;

    private static final String EXTRA_LOCALID = "localid";
    private static final String EXTRA_CATEGORY = "category";

    private Activity mActivity;
    private List<Category> mCategories;
    private int mPage;
    private Handler mHandler;

    private OnItemClickListener mOnItemClickListener;

    public CollectionAdapter(Activity activity){
        mActivity = activity;
        mCategories = new ArrayList<>();
        mPage = 0;
        mHandler = new Handler(new CollectionHandlerCallback());
//        refresh();
    }

    public void setCategories(List<Category> categories){
        mCategories = categories;
    }

    @Override
    public CollectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CollectionHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(final CollectionHolder holder, int position) {
        final Category category = mCategories.get(position);
        holder.mCategoryName.setText(category.getName());
        holder.mLastRead.setText(DateUtil.format(category.getLastRead()));
        Bitmap bitmap = BitmapFactory.decodeFile(category.getImageLocal());
        if(bitmap != null) {
            holder.mCategoryImage.setImageBitmap(bitmap);
        }else {
            Picasso.with(mActivity).load(category.getImage())
                    .placeholder(R.drawable.ic_no_image).into(holder.mCategoryImage);
        }
        if(category.isUploaded()){
            holder.mUpload.setVisibility(View.GONE);
        }else{
            holder.mUpload.setVisibility(View.VISIBLE);
        }
        holder.mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(v, holder.getAdapterPosition());
                uploadCategory(getItem(holder.getAdapterPosition()));

            }
        });
        holder.mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(v, holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public Category getItem(int position){
        return mCategories.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    /**
     * 刷新
     */
    public void refresh(){
        mPage = 0;
        loadMore();
    }

    /**
     * 获取数据
     */
    public void loadMore(){
        // TODO 添加分页
        mPage += 1;
        Repository repository = Repository.getInstance();
        mCategories = repository.getCollectionList(mActivity);
        notifyDataSetChanged();
    }

    private void saveLocal(Category category){
        Repository repository = Repository.getInstance();
        repository.saveCategoryLocal(mActivity, category);
    }

    private void uploadCategory(final Category category) {
        Repository repository = Repository.getInstance();
        repository.postCategory(mActivity, category, new Callback.PostCategoryCallback() {
            @Override
            public void onSuccess(String message, Category categoryReturn) {
                Message data = mHandler.obtainMessage(MESSAGE_UPLOAD);
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_LOCALID, category.getLocalId());
                bundle.putParcelable(EXTRA_CATEGORY, categoryReturn);
                data.setData(bundle);
                mHandler.sendMessage(data);

                // TODO 上传成功处理
                // 上传词汇分类的词汇
                uploadVocabulariesFor(category.getLocalId(), categoryReturn);
            }

            @Override
            public void onError(String error) {
                // TODO 上传失败处理
            }
        });
    }


    private void notifyItemChanged(String localId, Category newCategory){
        int position = getItemPositionById(localId);
        if(position >= 0) {
            Category category = getItem(position);
            category.setUploaded(true);
            category.setId(newCategory.getId());
            category.setImage(newCategory.getImage());
            category.setImageRemote(newCategory.getImageRemote());
            category.setCreateTime(newCategory.getCreateTime());
            mCategories.set(position, category);
            // 更新本地数据
            saveLocal(category);
//            notifyItemChanged(position);
            notifyDataSetChanged();
        }

    }

    private void updateCategory(Category oldCategory, Category newCategory){

    }

    private void uploadVocabulariesFor(String localId, Category category){
        Repository repository = Repository.getInstance();
        repository.uploadVocabulariesFor(mActivity, localId, category);
    }

    private int getItemPositionById(String id) {
        for (int i = 0; i < mCategories.size(); i++) {
            if (mCategories.get(i).getLocalId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    class CollectionHandlerCallback implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_UPLOAD:
                    Bundle bundle = msg.getData();
                    String localId = bundle.getString(EXTRA_LOCALID);
                    Category category = bundle.getParcelable(EXTRA_CATEGORY);
                    notifyItemChanged(localId, category);
                    return true;
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
    public interface OnLoadCategoryCallback{
        void onBeforeLoad();
        void onAfterLoad();
        void onLoadSuccess(String message);
        void onLoadError(String error);
    }

    static class CollectionHolder extends RecyclerView.ViewHolder{

        private ImageView mCategoryImage;
        private TextView mCategoryName;
        private TextView mLastRead;
        private ImageButton mUpload;
        private ImageButton mFavorite;

        public CollectionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_collection, parent, false));
            mCategoryImage = (ImageView) itemView.findViewById(R.id.card_image);
            mCategoryName = (TextView) itemView.findViewById(R.id.card_name);
            mLastRead = (TextView) itemView.findViewById(R.id.card_last_read);
            mUpload = (ImageButton) itemView.findViewById(R.id.card_upload);
            mFavorite = (ImageButton) itemView.findViewById(R.id.card_favorite);
        }

    }
}
