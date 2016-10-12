package com.ldb.vocabulary2.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.data.Callback;
import com.ldb.vocabulary2.android.data.CommunicationContract;
import com.ldb.vocabulary2.android.data.Repository;
import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.network.PostParam;
import com.ldb.vocabulary2.android.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsp on 2016/9/26.
 */
public class CategoryEditFragment extends Fragment {

    private static final String TAG = CategoryEditFragment.class.getSimpleName();

    private static final int REQUEST_IMAGE = 0;

    private ImageView mImageView;
    private String mImagePath;

    public static CategoryEditFragment newInstance(){
        return new CategoryEditFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_category_edit, container, false);

        mImageView = (ImageView) view.findViewById(R.id.image_for_upload);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

//        ((Button) view.findViewById(R.id.image_upload))
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case REQUEST_IMAGE:
                if(data.getData() != null){
                    Uri selectedImage = data.getData();
                    //content://com.android.providers.media.documents/document/image%3A45194
                    Log.d(TAG, selectedImage.toString());
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    // Get the cursor
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mImagePath = cursor.getString(columnIndex);
                    cursor.close();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getActivity().getContentResolver(), selectedImage);
                        mImageView.setImageBitmap(bitmap);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                return;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_done:
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save(){
        String categoryName =
                ((EditText) getView().findViewById(R.id.category_name)).getText().toString();
        String translation =
                ((EditText) getView().findViewById(R.id.category_translation)).getText().toString();
        Category category = new Category();
        category.setName(categoryName);
        category.setTranslation(translation);
        category.setImageLocal(mImagePath);
        category.setUploaded(false);
        category.setFavorite(true);
        category.setHasNew(false);
        category.setLastRead(DateUtil.getCurrentDate());
        category.setFavoriteCount(0);
        category.setWordCount(0);
        category.setCreateTime(DateUtil.getCurrentDate());
        category.setLastRead(DateUtil.getCurrentDate());
        boolean isPublic = ((CheckBox) getView().findViewById(R.id.category_public)).isChecked();
        if(isPublic){
            uploadCategory(category);
        }else{
            saveLocal(category);
        }

    }

    private void saveLocal(Category category){
        Repository repository = Repository.getInstance();
        repository.saveCategoryLocal(getActivity(), category);
    }


    private void uploadCategory(final Category category) {
//        List<PostParam> postParams = new ArrayList<>();
//        PostParam param = new PostParam();
//
//        String categoryName = category.getName();
//
//        param.setFile(false);
//        param.setValue(categoryName);
//        param.setFieldName(CommunicationContract.KEY_CATEGORY_NAME);
//        postParams.add(param);
//
//        String imagePath = category.getImageLocal();
//        if(imagePath != null && !imagePath.trim().isEmpty()) {
//            param = new PostParam();
//            param.setFile(true);
//            File image = new File(imagePath);
//            param.setData(image);
//            param.setFileName(image.getName());
//            // TODO 这里需要更好的方法来自动判断文件的mimeType.
//            param.setMimeType("image/jpeg");
//            param.setFieldName(CommunicationContract.KEY_CATEGORY_IMAGE);
//            postParams.add(param);
//        }
        Repository repository = Repository.getInstance();
//        repository.postCategory(getActivity(), postParams, new Callback() {
//            @Override
//            public void onResult(boolean isOk, String response) {
//                onUploadCategory(isOk, response);
//            }
//        });
        repository.postCategory(getActivity(), category, new Callback.PostCategoryCallback() {
            @Override
            public void onSuccess(String message, Category categoryReturn) {
                category.setUploaded(true);
                category.setId(categoryReturn.getId());
                category.setImage(categoryReturn.getImage());
                category.setImageRemote(categoryReturn.getImageRemote());
                category.setCreateTime(categoryReturn.getCreateTime());
                // 更新本地数据
                saveLocal(category);

                onUploadCategory(true, getString(R.string.upload_category_success_text));
            }

            @Override
            public void onError(String error) {
                saveLocal(category);
                onUploadCategory(false, getString(R.string.upload_category_error_text));
            }
        });
    }

    private void showFileChooser(){
        Intent intent = new Intent(Intent.ACTION_PICK ,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_PICK); // ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public void onUploadCategory(boolean isOk, String message) {
        if(isOk){
            // TODO 暂时先显示成功，之后应该弹框询问是增加词汇还是返回主界面
            showMessage(message);
        }else {
            showError(message);
        }
    }

    public void showError(String error) {
        Snackbar.make(getView().findViewById(R.id.category_edit_snackbar_decor),
                error, Snackbar.LENGTH_SHORT).show();
    }

    public void showMessage(String message) {
        Snackbar.make(getView().findViewById(R.id.category_edit_snackbar_decor),
                message, Snackbar.LENGTH_SHORT).show();
    }
}
