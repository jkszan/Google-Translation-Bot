//Author: Jacob Kszan, 2020


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


//Core is the main class of the translation bot, it handles determining the response to a MessageRecievedEvent and any commands that the user inputs
public class Core extends ListenerAdapter {

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
                respondTranslated(translated, mess);
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

    private void respondTranslated(String text, MessageReceivedEvent mess){
        mess.getChannel().sendMessage(mess.getAuthor().getName() + ": " + text).queue();
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

            //De-selects a default language
            case "cancelDefaultLanguage":
                //Checks if auto-Translate is on and disallows it if so
                if(!getAutoTranslate(currentUser)) {
                    if (defaultLanguageSet(currentUser)) {
                        setDefaultLanguage(currentUser, null);
                        respond("Default Language cancelled", command);
                    } else {
                        respond("Default Language is not set", command);
                    }
                }
                else{
                    respond("Disable Auto Translate before cancelling Default Language", command);
                }
                break;

            //Main Translation command
            case ("translate"):

                //Checks for default language, default language takes precedence over input
                if(defaultLanguageSet(currentUser)){
                    try {
                        translated = translator.translate(commandList[1], getDefaultLanguage(currentUser), textList[1]);
                        respondTranslated(translated, command);
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
                    //Non default language translation
                    try {
                        translated = translator.translate(commandList[1], commandList[2], textList[1]);
                        respondTranslated(translated, command);
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
                    setAutoTranslate(currentUser, false);
                    respond("Auto Translate Disabled", command);
                }
                else{
                    respond("Auto Translate is not enabled", command);
                }
                break;

                //This command just replies with a list of commands and a short summary of what they do
            case "help":
                String helpString ="Error in HelpString production";
                try {
                    //This switch statement produces a helpString for each individual command
                    switch (commandList[1]) {
                        case("translate"):
                            helpString = """
                                    Translates text from one language to another
                                    
                                    translate [Origin Language] [Target Language] [\"Text"\"]
                                    Alternatively, if a default language is set:
                                    translate [Origin Language] [\"Text\"]
                                    Text must be surrounded by quotations""";
                            break;

                        case ("setDefaultLanguage"):
                            helpString = """
                                    Sets a default target language for translations and auto-translate
                                    
                                    setDefaultLanguage [Language]""";
                            break;
                        case("cancelDefaultLanguage"):
                            helpString = """
                                    Resets your default language to none
                                    
                                    cancelDefaultLanguage""";
                            break;
                        case("enableAutoTranslate"):
                            helpString = """
                                    Enables auto translate using your set default language
                                    
                                    enableAutoTranslate""";
                            break;
                        case("disableAutoTranslate"):
                            helpString = """
                                    Disables auto translate
                                    
                                    disableAutoTranslate""";
                            break;
                        case("setCommandKey"):
                            helpString = """
                                    Sets your command key (Default: %) to a different one character value
                                    
                                    setCommandKey [New Command Key]
                                   """;
                            break;
                        case("languages"):
                            helpString = """
                                    Displays a list of languages in the correct spelling that you can use for translation
                                    
                                    languages""";
                            break;
                        case("detectLanguageTranslate"):
                            helpString = """
                                    Translates text without the need to specify the original language
                                    
                                    detectLanguageTranslate [Target Language] [\"Text\"]
                                    Text must be contained in quotations""";
                                break;
                        default:
                            helpString = "Command not recognized, please use " + commandKey +"help to get a list of commands";

                    }
                }
                //This catches if no individual command was specified and instead gives a general helpString
                catch(IndexOutOfBoundsException e) {
                    helpString = """
                            Commands:
                            translate - Translates Text
                            setDefaultLanguage - Sets Default Language
                            cancelDefaultLanguage - Cancels Default Language
                            enableAutoTranslate - Enables Auto Translate
                            disableAutoTranslate - Disables Auto Translate
                            setCommandKey - Sets commandKey to a different value (Default: %)
                            languages - Displays a list of compatible languages
                            detectLanguageTranslate - translates text, automatically determining origin text
                            
                            Type""" + " " + commandKey + "help [Command] to get more information on a specific command";


                }

                respond(helpString, command);
                break;

            //Changes the starting character that signifies a command
            case "setCommandKey":

                try{
                    setCommandKey(commandList, command);
                }
                catch(IndexOutOfBoundsException f){
                    respond(commandKey + "setCommandKey-[New CommandKey]", command);
                }
                break;

            //Sends a list of accepted languages
            case "languages":
                respond("Languages supported: Afrikaans, Irish, Albanian, Italian, Arabic, Japanese, Azerbaijani, Korean, Latin, Chinese-simplified, Chinese-traditional, Polish, Portuguese, English, German, Spanish, French, Slovenian", command);
                break;

            //Like the prior translate, except submitting an empty string for the source lang parameter. This indicates that the language should be detected
            case "detectLanguageTranslate":

                if(defaultLanguageSet(currentUser)) {
                    try {
                        translated = translator.translate("", getDefaultLanguage(currentUser), textList[1]);
                        respondTranslated(translated, command);
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
                        respondTranslated(translated, command);
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

    //Checks if a language is supported
    private boolean testLanguage(String lang){
        return(translator.testLanguage(lang));
    }

    //Helper method that implements the setCommandKey case statement from handleCommands()
    private void setCommandKey(String[] commandList, MessageReceivedEvent command){
        if(commandList[1].length() == 1) {
            commandKey =  commandList[1].charAt(0);
            respond("CommandKey set to " + commandKey + "!", command);
        }
        else{
            respond("Command keys may only consist of one character", command);
        }
    }

                                                                               
    //Main method, attributes a token to the discord bot and runs it. This main method and only this main method is based on a tutorial I found on StackExchange
    public static void main(String args[]) throws LoginException {
        JDABuilder botMaker = JDABuilder.createDefault("Bot");
        String token = "NzMzMjEyMTQ5MjAzMzM3MjQ2.XxAjMg.vK2etJw71IYvK63ppKl6cuuoM8U";
        botMaker.setToken(token);
        botMaker.addEventListeners(new Core());
        botMaker.build();

    }
}
