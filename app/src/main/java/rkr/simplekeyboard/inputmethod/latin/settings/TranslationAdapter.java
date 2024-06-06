package rkr.simplekeyboard.inputmethod.latin.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import rkr.simplekeyboard.inputmethod.R;

public class TranslationAdapter extends RecyclerView.Adapter<TranslationAdapter.ViewHolder> {
    private List<TranslationItem> translationItems;

    public TranslationAdapter(List<TranslationItem> translationItems) {
        this.translationItems = translationItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prompt_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TranslationItem item = translationItems.get(position);
        holder.originalText.setText(item.getOriginalText());
        holder.translatedText.setText(item.getTranslatedText());

        holder.removeButton.setOnClickListener(v -> {
            translationItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, translationItems.size());
        });
    }

    @Override
    public int getItemCount() {
        return translationItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText originalText, translatedText;
        ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            originalText = itemView.findViewById(R.id.original_text);
            translatedText = itemView.findViewById(R.id.translated_text);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
