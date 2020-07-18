import com.gtranslate.Language;

public class UserSettings {

    Language defaultLanguage;
    boolean autoTranslate;

    public UserSettings(){

        autoTranslate = false;

    }

    public void setDefaultLanguage(Language lang){
        defaultLanguage = lang;
    }

    public Language getDefaultLanguage(){
        return defaultLanguage;
    }

    public void enableAutoTranslate(Language lang){

    }

    public void enableAutoTranslate(){

    }

    public void disableAutoTranslate(){

    }


}
