package rkr.simplekeyboard.inputmethod.latin;

import com.google.gson.Gson;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;

import rkr.simplekeyboard.inputmethod.latin.settings.TranslationItem;

public class GPTTranslator {
    private final String mErrUnableToTrans = "500";

    private final String mPromptUnableToTranslate = "If the model is unable to translate, return the code 500.\n";

    private LatinIME mLatinIME;

    private final String mDefaultSysPrompt = "Translate the following sentence to Indonesian with a casual tone.\n";

    public GPTTranslator(final LatinIME latinIME) {
        mLatinIME = latinIME;
    }

    private List<ChatMessage> generateChatMessages(String originalString) {
        String translationList = generateTranslationList();

        String systemPrompt = mPromptUnableToTranslate +mDefaultSysPrompt;

        systemPrompt += "\nbelow is the example of the translation:\n"+translationList+"\n";

        systemPrompt += "Text to translate:\n"+originalString;


        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage("system", "You are the bot fot translator"));
        chatMessages.add(new ChatMessage("user", systemPrompt));
        return chatMessages;
    }

    // return the translation list in format
    // "original"->"translation"
    private String generateTranslationList(){

        StringBuilder stringBuilder = new StringBuilder();
        List<TranslationItem> list =  mLatinIME.mSettings.getCurrent().mOpenAITranslationList;
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i).getOriginalText());
            stringBuilder.append(" -> ");
            stringBuilder.append(list.get(i).getTranslatedText());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    private List<ChatCompletionChoice> getCompletionChoices(ChatCompletionRequest chatCompletionRequest) {
        OpenAiService openAiService = new OpenAiService(mLatinIME.mSettings.getCurrent().mOpenAIAPIKey);
        return openAiService.createChatCompletion(chatCompletionRequest).getChoices();
    }

    private ChatCompletionRequest buildChatCompletionRequest(String text){
        return ChatCompletionRequest.builder()
                .messages(generateChatMessages(text))
                .model(mLatinIME.mSettings.getCurrent().mOpenAIModel)
                .temperature((double) mLatinIME.mSettings.getCurrent().mOpenAITemperature)
                .build();
    }

    public String translate(String text) {
        // reject an empty string
        if (text.isEmpty()){
            return text;
        }
        try{
            for (ChatCompletionChoice choice : getCompletionChoices(buildChatCompletionRequest(text))) {
                String content = choice.getMessage().getContent();
                // 500 means the model is unable to translate
                if (content.equals(mErrUnableToTrans)){
                    return text;
                }
                return choice.getMessage().getContent();
            }
        }catch (Exception e){
            return "Exception:"+e.getMessage();
        }
        return "";
    }
}
