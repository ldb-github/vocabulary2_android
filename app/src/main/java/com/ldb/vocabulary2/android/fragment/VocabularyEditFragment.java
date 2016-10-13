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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.data.Callback;
import com.ldb.vocabulary2.android.data.Repository;
import com.ldb.vocabulary2.android.model.Vocabulary;
import com.ldb.vocabulary2.android.util.DateUtil;

import java.io.IOException;

/**
 * Created by lsp on 2016/10/1.
 */
public class VocabularyEditFragment extends Fragment {
    private static final String TAG = VocabularyEditFragment.class.getSimpleName();

    private static final int REQUEST_IMAGE = 0;
    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_CATEGORY_UPLOAD = "category_upload";

    private String mCategoryId;
    private boolean mUpload;
    private ImageView mImageView;
    private String mImagePath;

    public static VocabularyEditFragment newInstance(String categoryId, boolean upload){
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CATEGORY_ID, categoryId);
        bundle.putBoolean(ARG_CATEGORY_UPLOAD, upload);

        VocabularyEditFragment fragment = new VocabularyEditFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle arg = getArguments();
        if(arg != null){
            mCategoryId = arg.getString(ARG_CATEGORY_ID);
            mUpload = arg.getBoolean(ARG_CATEGORY_UPLOAD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_vocabulary_edit, container, false);

//        ((Button) view.findViewById(R.id.image_select))
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showFileChooser();
//                    }
//                });

        mImageView = (ImageView) view.findViewById(R.id.vocabulary_image_for_upload);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

//        ((Button) view.findViewById(R.id.upload_vocabulary_button))
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String categoryName =
//                                ((EditText) view.findViewById(R.id.vocabulary_name)).getText().toString();
////                        mPresenter.uploadVocabulary(categoryName, mImagePath, mCategoryId, mUpload);
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
//            case android.R.id.home:
//                getActivity().finish();
//                return true;
            case R.id.action_done:
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save(){
        String vocabularyName =
                ((EditText) getView().findViewById(R.id.vocabulary_name)).getText().toString();
        String translation =
                ((EditText) getView().findViewById(R.id.vocabulary_translation)).getText().toString();
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setName(vocabularyName);
        vocabulary.setCId(mCategoryId);
        vocabulary.setImageLocal(mImagePath);
        vocabulary.setTranslation(translation);
        vocabulary.setCreateTime(DateUtil.getCurrentDate());
        vocabulary.setUpload(false);
        if(mUpload) {
            uploadVocabulary(vocabulary);
        }else{
            saveLocal(vocabulary);
        }

    }

    private void saveLocal(Vocabulary vocabulary){
        Repository repository = Repository.getInstance();
        repository.saveVocabularyLocal(getActivity(), vocabulary);
    }

    private void uploadVocabulary(final Vocabulary vocabulary){
        Repository repository = Repository.getInstance();
        repository.postVocabulary(getActivity(), vocabulary, new Callback.PostVocabularyCallback() {
            @Override
            public void onSuccess(String message, Vocabulary vocabularyReturn) {
                vocabulary.setId(vocabularyReturn.getId());
                vocabulary.setImage(vocabularyReturn.getImage());
                vocabulary.setImageRemote(vocabularyReturn.getImageRemote());
                vocabulary.setCreateTime(vocabularyReturn.getCreateTime());
                vocabulary.setUpload(true);
                // 更新本地数据
                saveLocal(vocabulary);

                onUploadVocabulary(true, getString(R.string.upload_category_success_text));
            }

            @Override
            public void onError(String error) {
                saveLocal(vocabulary);
                onUploadVocabulary(false, getString(R.string.upload_category_error_text));
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

    public void onUploadVocabulary(boolean isOk, String message) {
        if(isOk){
            // TODO 暂时先显示成功，之后应该弹框询问是增加词汇还是返回主界面
            showMessage(message);
        }else {
            showError(message);
        }
    }

    public void showError(String error) {
        Snackbar.make(getView().findViewById(R.id.snack_bar_decor),
                error, Snackbar.LENGTH_SHORT).show();
    }

    public void showMessage(String message) {
        Snackbar.make(getView().findViewById(R.id.snack_bar_decor),
                message, Snackbar.LENGTH_SHORT).show();
    }
}
