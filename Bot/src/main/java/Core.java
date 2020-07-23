
//Importing JDA Related Libraries
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

//Importing Java Related Libraries
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;



public class Core extends ListenerAdapter {

    //Add Functionality, defaultResponseLanguage
    //Add Functionality, autoTranslate
    //Add Functionality, manual translate
    //Add Functionality, help command
    //Add Functionality, Detect Language

    //Instance Variables

    char commandKey = '%';
    HashMap<User, UserSettings> userPreferences = new HashMap<>();
    Translator translator = new Translator();
    String translated;
    User currentUser;

    //An action listener that triggers whenever a message visible to the bot is sent
    //Sorts out different responses based on user preferences and contents of the message

    public void onMessageReceived(@NotNull MessageReceivedEvent mess){

        //Sets current user for this command, cuts down on the usage of mess.getAuthor() significantly
        currentUser = mess.getAuthor();

        //Logs viewed messages in bot-side console
        System.out.println("Message received from " + currentUser.getName() + " : " + mess.getMessage().getContentDisplay());

        //Creates a userPreferences instance linked to a user if one doesnt already exist
        if(userPreferences.get(currentUser) == null) {
            userPreferences.put(currentUser, new UserSettings());
        }


        //Checks to see if a user has autoTranslate enabled and did not send a command
        if(getAutoTranslate(currentUser) && !(mess.getMessage().getContentRaw().charAt(0) == commandKey)){

            try {
                //Translates a detected language to the default language selected
                translated = translator.translate("", getDefaultLanguage(currentUser), mess.getMessage().getContentRaw());
                respond(translated, mess);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //Checks if a user has send a command, provided autoTranslate is not enable
        else if(mess.getMessage().getContentRaw().charAt(0) == commandKey) {
            handleCommands(mess);
        }

    }

    //Simple helper method that sends replies to commands in the same channel that the message was sent
    private void respond(CharSequence message, MessageReceivedEvent mess){
        mess.getChannel().sendMessage(message).queue();
    }

    //Returns defaultLanguage of a user
    public String getDefaultLanguage(User user){
        return userPreferences.get(user).getDefaultLanguage();
    }

    public boolean defaultLanguageSet(User user){
        return userPreferences.get(user).defaultLanguageSet();
    }

    //Returns autoTranslate setting of a user
    public boolean getAutoTranslate(User user){
        return userPreferences.get(user).getAutoTranslate();
    }

    //Private method that sets a Default Language for a user
    private void setDefaultLanguage(User user, String lang){
        userPreferences.get(user).setDefaultLanguage(lang);
    }

    //Private method that sets Auto-Translate for a user
    private void setAutoTranslate(User user, boolean set){
        userPreferences.get(user).setAutoTranslate(set);
    }

    //Main method that handles commands made by users
    public void handleCommands(@NotNull MessageReceivedEvent command){

        //Declaring variables that will be used in handleCommands

        //List separating the commands and the text
        String[] textList = command.getMessage().getContentRaw().split(Character.toString('"'));
        //List separating individual command keywords
        String[] commandList = textList[0].split(" ");
        //Initial command, with the commandKey cut out
        String commandTrigger = commandList[0].substring(1);

        //Switch statement with a case for every command
        switch(commandTrigger){


            //Sets default language
            case "setDefaultLanguage":

                try{
                    if(testLanguage(commandList[1])){
                        setDefaultLanguage(currentUser, commandList[1]);
                        respond("Default Language set!", command);
                    }
                    else{
                        respond("Language not recognized. Use " + commandKey +"languages to get a list of languages", command);
                    }
                }
                catch(IndexOutOfBoundsException e) {
                    respond(commandKey + "setDefaultLanguage [Language]. Use " + commandKey + "languages to get a list of languages", command);
                }
                break;

            case "cancelDefaultLanguage":
                if(defaultLanguageSet(currentUser)){
                    setDefaultLanguage(currentUser, null);
                    respond("Default Language cancelled", command);
                }
                else{
                    respond("Default Language is not set", command);
                }
                break;

            //Main Translation command
            case ("translate"):

                if(defaultLanguageSet(currentUser)){
                    try {
                        translated = translator.translate(commandList[1], getDefaultLanguage(currentUser), textList[1]);
                        respond(translated, command);
                    }
                    catch(IOException e){
                        respond(e.getMessage(), command);
                    }
                    catch(IllegalArgumentException l){
                        respond("Language not recognized. Use " + commandKey +"languages to get a list of languages", command);
                    }
                    catch(IndexOutOfBoundsException i){
                        respond("You have Default Language activated. Use " + commandKey +"translate [Origin Language] [\"Text\"]", command);
                    }
                }
                else{
                    try {
                        translated = translator.translate(commandList[1], commandList[2], textList[1]);
                        respond(translated, command);
                    }
                    catch(IOException e){
                        respond(e.getMessage(), command);
                    }
                    catch(IllegalArgumentException l){
                        respond("Language not recognized. Use " + commandKey +"languages to get a list of languages", command);
                    }
                    catch(IndexOutOfBoundsException i){
                        respond("Use " + commandKey +"translate [Origin Language] [Desired Language] [\"Text\"]", command);
                    }

                }
                break;

            //Enables auto-translate provided that a default language is selected
            case "enableAutoTranslate":

                if(defaultLanguageSet(currentUser)) {
                    setAutoTranslate(currentUser, true);
                    respond("Auto Translate Enabled", command);
                }
                else{
                    respond("To use Auto-Translate, you must select a Default Language. Use " + commandKey +"setDefaultLanguage", command);
                }
                break;

            //Disables auto-translate provided that it is enabled
            case "disableAutoTranslate":
                if(getAutoTranslate(currentUser)){
                    setAutoTranslate(currentUser, true);
                    respond("Auto Translate Disabled", command);
                }
                else{
                    respond("Auto Translate is not enabled", command);
                }
                break;

            case "help":
                String helpString;
                helpString = """
                        Commands:
                        translate - Translates Text
                        setDefaultLanguage - Sets Default Language
                        cancelDefaultLanguage - Cancels Default Language
                        enableAutoTranslate - Enables Auto Translate
                        disableAutoTranslate - Disables Auto Translate
                        setCommandKey - Sets commandKey to a different value (Default: %)
                        languages - Displays a list of compatible languages
                        detectLanguageTranslate - translates text, automatically determining origin text""";
                respond(helpString, command);
                break;

            case "setCommandKey":

                try{
                    setCommandKey(commandList, command);
                }
                catch(IndexOutOfBoundsException f){
                    respond(commandKey + "setCommandKey-[New CommandKey]", command);
                }
                break;

            case "languages":
                respond("Languages supported: Afrikaans, Irish, Albanian, Italian, Arabic, Japanese, Azerbaijani, Korean, Latin, Chinese-simplified, Chinese-traditional, Polish, Portuguese, English, German, Spanish, French, Slovenian", command);
                break;

            case "detectLanguageTranslate":

                if(defaultLanguageSet(currentUser)) {
                    try {
                        translated = translator.translate("", getDefaultLanguage(currentUser), textList[1]);
                        respond(translated, command);
                    }
                    catch(IOException e) {
                        respond(e.toString(), command);
                    }
                    catch(IndexOutOfBoundsException i) {
                        respond("Default Language enabled. Use " + commandKey +"detectLanguageTranslate [\"Text\"]", command);
                    }
                    catch(IllegalArgumentException a) {
                        respond("Language not recognized. Use " + commandKey +"languages to get a list of languages", command);
                    }
                }
                else {
                    try {
                        translated = translator.translate("", commandList[1], textList[1]);
                        respond(translated, command);
                    }
                    catch(IOException e) {
                        respond(e.toString(), command);
                    }
                    catch(IndexOutOfBoundsException i) {
                        respond("Use " + commandKey +"detectLanguageTranslate [Desired Language] [\"Text\"]", command);
                    }
                    catch(IllegalArgumentException a) {
                        respond("Language not recognized. Use " + commandKey +"languages to get a list of languages", command);
                    }
                }
                break;

            default:
                respond("Command not recognized", command);

        }


    }

    private boolean testLanguage(String lang){
        return(translator.testLanguage(lang));
    }

    private void setCommandKey(String[] commandList, MessageReceivedEvent command){
        if(commandList[1].length() == 1) {
            commandKey =  commandList[1].charAt(0);
            respond("CommandKey set to " + commandKey + "!", command);
        }
        else{
            respond("Command keys may only consist of one character", command);
        }
    }


    public static void main(String args[]) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault("Bot");
        String token = "NzMzMjEyMTQ5MjAzMzM3MjQ2.XxAjMg.vK2etJw71IYvK63ppKl6cuuoM8U";
        builder.setToken(token);
        builder.addEventListeners(new Core());
        builder.build();

    }
}
