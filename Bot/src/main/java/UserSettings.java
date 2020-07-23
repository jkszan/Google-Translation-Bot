//Author: Jacob Kszan, 2020.

//UserSettings is a class tied to each Discord User that stores settings relating to the translation app
//It allows us to implement defaultLanguage and autoTranslate based on user and not on instance of the bot
public class UserSettings {

    //Instance variables
    String defaultLanguage;
    boolean autoTranslate;

    //Constructor that sets Auto-Translate to false by default
    public UserSettings(){
        autoTranslate = false;
    }

    //Sets a default language
    public void setDefaultLanguage(String lang){
        defaultLanguage = lang;
    }

    //Accessor method that returns the Default Language
    public String getDefaultLanguage(){
        return defaultLanguage;
    }

    //Checks if the Default Language is set
    public boolean defaultLanguageSet(){
        return defaultLanguage != null;
    }

    //Accessor method that returns the value of Auto-Translate
    public boolean getAutoTranslate(){
        return autoTranslate;
    }
    
    //Sets autoTranslate variable to the passed boolean
    public void setAutoTranslate(boolean auto){
        autoTranslate = auto;
    }

}
