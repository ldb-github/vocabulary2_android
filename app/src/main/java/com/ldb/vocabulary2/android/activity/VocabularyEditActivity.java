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
import com.ldb.vocabulary2.android.model.Category;

public class VocabularyEditActivity extends AppCompatActivity {

    private static final String EXTRA_CATEGORY = "category";

    private Category mCategory;

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
            mCategory = intent.getParcelableExtra(EXTRA_CATEGORY);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        VocabularyEditFragment fragment =
                (VocabularyEditFragment) fragmentManager.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = VocabularyEditFragment.newInstance(mCategory);
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

    public static Intent newIntent(Context context, Category category){
        Intent intent = new Intent(context, VocabularyEditActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        return intent;
    }
}
