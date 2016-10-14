package com.ldb.vocabulary2.android.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.data.Repository;
import com.ldb.vocabulary2.android.data.Callback;
import com.ldb.vocabulary2.android.model.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsp on 2016/10/7.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private Activity mActivity;
    private List<Category> mCategories;
    private int mPage;

    private OnItemClickListener mOnItemClickListener;

    public CategoryAdapter(Activity activity){
        mActivity = activity;
        mCategories = new ArrayList<>();
        mPage = 0;
    }

    public void setCategories(List<Category> categories){
        mCategories = categories;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(final CategoryHolder holder, int position) {
        Category category = mCategories.get(position);
        holder.mCategoryName.setText(category.getName());
        Picasso.with(mActivity).load(category.getImage())
                .placeholder(R.drawable.ic_no_image).into(holder.mCategoryImage);
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

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    /**
     * 刷新
     * @param callback
     */
    public void refresh(OnLoadCategoryCallback callback){
        mPage = 0;
        mCategories.clear();
        if(callback == null){
            callback = new OnLoadCategoryCallback() {
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

    /**
     * 获取数据
     * @param callback
     */
    public void loadMore(final OnLoadCategoryCallback callback){
        mPage += 1;
        callback.onBeforeLoad();
        // TODO 获取category
        Repository repository = Repository.getInstance();
        repository.getCategoryList(mActivity, mPage, null, null, new Callback.RequestCategoryListCallback() {
            @Override
            public void onSuccess(String message, List<Category> categoryList) {
                if(categoryList != null) {
                    mCategories.addAll(categoryList);
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

    public Category getItem(int position){
        return mCategories.get(position);
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

    static class CategoryHolder extends RecyclerView.ViewHolder{

        private ImageView mCategoryImage;
        private TextView mCategoryName;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_category, parent, false));
            mCategoryImage = (ImageView) itemView.findViewById(R.id.category_image);
            mCategoryName = (TextView) itemView.findViewById(R.id.category_name);
        }

    }
}
