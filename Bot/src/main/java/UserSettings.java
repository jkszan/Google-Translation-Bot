

public class UserSettings {

    String defaultLanguage;
    boolean autoTranslate;

    public UserSettings(){

        autoTranslate = false;

    }

    public void setDefaultLanguage(String lang){
        defaultLanguage = lang;
    }

    public String getDefaultLanguage(){
        return defaultLanguage;
    }




}
