package rkr.simplekeyboard.inputmethod.latin.inputlogic;

import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import rkr.simplekeyboard.inputmethod.keyboard.Key;
import rkr.simplekeyboard.inputmethod.keyboard.KeyboardView;
import rkr.simplekeyboard.inputmethod.latin.GPTTranslator;
import rkr.simplekeyboard.inputmethod.latin.RichInputConnection;
import rkr.simplekeyboard.inputmethod.latin.utils.DelayedTaskQueue;

// Translator act as the proxy to RichConnection, it'll decided weather to hold continue
// commit to the connection or stop it here.
public class TranslatorInputConnection extends RichInputConnection{

    private final boolean DEBUG = false;

    private String mOriginalString = "";

    private String mTranslationString = "";

    private TextView mOriginalStringView;

    private RichInputConnection mConnection;

    private DelayedTaskQueue mTaskQueue;

    private GPTTranslator mTranslator;

    private Key mTransOutKey;

    private KeyboardView mKeyboardView;
    public TranslatorInputConnection(InputMethodService parent) {
        super(parent);
    }

    public TranslatorInputConnection(RichInputConnection inputConnection, GPTTranslator translator){
        super(inputConnection.getParent());
        mConnection = inputConnection;
        mTaskQueue = new DelayedTaskQueue();
        mTranslator = translator;
    }

    private void setOriginalStringViewText(String text){
        mOriginalString = text;
        // add the text space in the binning and the end to increase
        // readability
        if (!text.isEmpty()){
            text = " "+text+" ";
        }
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new BackgroundColorSpan(Color.WHITE),
                0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mOriginalStringView.setText(spannableString);
    }


    public RichInputConnection getConnection(){
        return mConnection;
    }

    // start the new input
    public void setInput(){
        setOriginalStringViewText("");
        queueTrans(mOriginalString);
    }

    public void setOriginalStringView(TextView originalStringView){
        mOriginalStringView = originalStringView;
//        mTransStringView = transView;
    }

    private void transOutBtnOnClick(View v){
        mConnection.commitText("this text",1);
    }

    public void setTransOutKey(Key key){
        if (mTransOutKey == null){
            mTransOutKey = key;
        }
    }

    public void setKeyboardView(KeyboardView keyboardView){
        if (mKeyboardView == null){
            mKeyboardView = keyboardView;
        }
    }

    // commitText will write the text into original_text view and wait for the translation
    public void commitText(final CharSequence text, final int newCursorPosition) {
        setOriginalStringViewText(mOriginalString+text);
        queueTrans(mOriginalString);
    }

    private void delEvent(final KeyEvent keyEvent){
        if (!mOriginalString.isEmpty()){
            setOriginalStringViewText(mOriginalString.substring(0,mOriginalString.length()-1));
            queueTrans(mOriginalString);
        }else {
            // delete from the connection
            mConnection.getParent().getCurrentInputConnection().sendKeyEvent(keyEvent);
        }
    }

    public void sendKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getAction() != KeyEvent.ACTION_DOWN){
            // do nothing unless it's an action down
            return;
        }

        switch (keyEvent.getKeyCode()){
            case KeyEvent.KEYCODE_DEL:
                delEvent(keyEvent);
                break;
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
            case KeyEvent.KEYCODE_0:
                commitText(String.valueOf(keyEvent.getDisplayLabel()),1);
                break;
        }
    }

    private void queueTrans(String text){
        long taskAddedTime = System.currentTimeMillis();
        mTaskQueue.addTask(() -> {
            if (DEBUG){
                long taskStartedTime = System.currentTimeMillis();
                Log.d(this.getClass().toString(), "text: "+text+" Time elapsed since task added: " + (taskStartedTime - taskAddedTime) + " ms");
            }
            mTranslationString = mTranslator.translate(text);
            mKeyboardView.onDrawKeyTransOut(mTranslationString);
        });
    }

    public void translateWithoutQueue(){
        mTaskQueue.addTaskWithoutDelay(()->{
            mTranslationString = mTranslator.translate(mOriginalString);
            mKeyboardView.onDrawKeyTransOut(mTranslationString);
        });
    }

    // commit the current translation output to the connection
    // and reset the original & translation string
    public void commitTransOut(){
        mConnection.beginBatchEdit();
        mConnection.commitText(mTranslationString,1);
        mConnection.endBatchEdit();
        setOriginalStringViewText("");
        mTranslationString = "";
        mKeyboardView.onDrawKeyTransOut("");
    }
}
