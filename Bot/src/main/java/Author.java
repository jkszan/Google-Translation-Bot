import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

public abstract class Author implements User {

    Language defaultLanguage;

    public void setDefaultLanguage(Language lang){
        defaultLanguage = lang;
    }

    public Language getDefaultLanguage(){
        return defaultLanguage;
    }


}
