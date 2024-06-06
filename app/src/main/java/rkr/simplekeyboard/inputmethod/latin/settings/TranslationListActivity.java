package rkr.simplekeyboard.inputmethod.latin.settings;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rkr.simplekeyboard.inputmethod.R;
import rkr.simplekeyboard.inputmethod.compat.PreferenceManagerCompat;

public class TranslationListActivity extends PreferenceActivity {
    private List<TranslationItem> translationItems;
    private TranslationAdapter adapter;

    private RecyclerView recyclerView;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setContentView(R.layout.prompt_list_setting);



        mPrefs = PreferenceManagerCompat.getDeviceSharedPreferences(this);

        Settings.readOpenAITranslationList(mPrefs);

        translationItems = Settings.readOpenAITranslationList(mPrefs);


        adapter = new TranslationAdapter(translationItems);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            translationItems.add(new TranslationItem("", ""));
            adapter.notifyItemInserted(translationItems.size() - 1);
        });

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> saveTranslations());
    }

    public TranslationListActivity getInstance(){
        return this;
    }

    public void setPrefs(SharedPreferences prefs){
        mPrefs = prefs;
    }

    private void saveTranslations() {
        // Gather all the items from the adapter
        for (int i = 0; i < adapter.getItemCount(); i++) {
            TranslationAdapter.ViewHolder holder = (TranslationAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                translationItems.get(i).setOriginalText(holder.originalText.getText().toString());
                translationItems.get(i).setTranslatedText(holder.translatedText.getText().toString());
            }
        }

        // Convert the list to JSON and save it
        Gson gson = new Gson();
        String json = gson.toJson(translationItems);

        mPrefs.edit().putString(Settings.PREF_PROMPT_TRANSLATION_LIST,json).apply();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
