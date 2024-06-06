package rkr.simplekeyboard.inputmethod.latin.settings;

public class TranslationItem {
    private String originalText;
    private String translatedText;

    public TranslationItem(String originalText, String translatedText) {
        this.originalText = originalText;
        this.translatedText = translatedText;
    }

    // Getters and setters
    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
