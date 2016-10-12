package com.ldb.vocabulary2.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.fragment.CategoryEditFragment;

public class CategoryEditActivity extends AppCompatActivity {

    private static final String TAG = CategoryEditActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        CategoryEditFragment fragment =
                (CategoryEditFragment) fragmentManager.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = CategoryEditFragment.newInstance();
            fragmentManager
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }
}
