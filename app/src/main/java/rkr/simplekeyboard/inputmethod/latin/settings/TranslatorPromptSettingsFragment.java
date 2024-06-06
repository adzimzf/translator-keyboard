package rkr.simplekeyboard.inputmethod.latin.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.util.AttributeSet;

import rkr.simplekeyboard.inputmethod.R;

public class TranslatorPromptSettingsFragment extends Preference {

    private final Context context;

    private TranslationListActivity mTranslationListActivity;

    public TranslatorPromptSettingsFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mTranslationListActivity = new TranslationListActivity();
    }

    @Override
    protected void onClick() {
        Intent intent = new Intent(context, TranslationListActivity.class);
        context.startActivity(intent);
    }
}
