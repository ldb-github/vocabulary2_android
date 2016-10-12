package com.ldb.vocabulary2.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.fragment.VocabularyFragment;
import com.ldb.vocabulary2.android.model.Category;

public class VocabularyActivity extends AppCompatActivity {

    private static final String EXTRA_CATEGORY = "category";

    private Category mCategory;
    private TextView mCategoryNameTextView;
    private ImageView mCategoryImageView;
    private RecyclerView mVocabularyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        Intent intent = getIntent();
        if(intent != null){
            mCategory = intent.getParcelableExtra(EXTRA_CATEGORY);
        }

        VocabularyFragment fragment = (VocabularyFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);
        if(fragment == null){
            fragment = VocabularyFragment.newInstance(mCategory);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }

    }

    public static Intent newIntent(Context context, Category category){
        Intent intent = new Intent(context, VocabularyActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        return intent;
    }

}
