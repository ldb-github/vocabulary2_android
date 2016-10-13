package com.ldb.vocabulary2.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.fragment.VocabularyEditFragment;

public class VocabularyEditActivity extends AppCompatActivity {

    private static final String EXTRA_CATEGORY_ID = "category_id";
    private static final String EXTRA_CATEGORY_UPLOAD = "category_upload";

    private String mCategoryId;
    private boolean mUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_edit);

        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear);
        }

        Intent intent = getIntent();
        if(intent != null){
            mCategoryId = intent.getStringExtra(EXTRA_CATEGORY_ID);
            mUpload = intent.getBooleanExtra(EXTRA_CATEGORY_UPLOAD, false);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        VocabularyEditFragment fragment =
                (VocabularyEditFragment) fragmentManager.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = VocabularyEditFragment.newInstance(mCategoryId, mUpload);
            fragmentManager
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent newIntent(Context context, String categoryId, boolean upload){
        Intent intent = new Intent(context, VocabularyEditActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(EXTRA_CATEGORY_UPLOAD, upload);
        return intent;
    }
}
