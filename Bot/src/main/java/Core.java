

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import com.gtranslate.*;
import javax.security.auth.login.LoginException;
import java.util.HashMap;



public class Core extends ListenerAdapter {

    //Add Functionality, defaultResponseLanguage
    //Add Functionality, autoTranslate
    //Add Functionality, manual translate
    //Add Functionality, help command
    //Add Functionality, Detect Language

    char commandKey = '%';
    HashMap<User, UserSettings> userPreferences = new HashMap<>();
    Translator translate = Translator.getInstance();
    Language lan = Language.getInstance();

    public void onMessageReceived(@NotNull MessageReceivedEvent mess){
        System.out.println("Message received from " + mess.getAuthor().getName() + " : " + mess.getMessage().getContentDisplay());

        if(userPreferences.get(mess.getAuthor()) == null) {
            userPreferences.put(mess.getAuthor(), new UserSettings());
        }

        if(userPreferences.get(mess.getAuthor()).autoTranslate && !(mess.getMessage().getContentRaw().charAt(0) == commandKey)){
            String translated = translate.translate(mess.getMessage().getContentRaw(), translate.detect((mess.getMessage().getContentRaw())), userPreferences.get(mess.getAuthor()).defaultLanguage);
            respond(translated, mess);
        }
        else if(mess.getMessage().getContentRaw().charAt(0) == commandKey) {

            if(userPreferences.get(mess.getAuthor()) == null) {
                userPreferences.put(mess.getAuthor(), new UserSettings());
            }

            handleCommands(mess);

        }

    }

    public void respond(CharSequence message, MessageReceivedEvent mess){


        mess.getChannel().sendMessage(message).queue();

    }

    public void handleCommands(@NotNull MessageReceivedEvent command){

        String[] commandList = command.getMessage().getContentRaw().split("-");
        String commandTrigger = commandList[0].substring(1);
        switch(commandTrigger){

            case "setDefaultLanguage":


                try{

                    if(testLanguage(commandList[1])){
                        userPreferences.get(command.getAuthor()).setDefaultLanguage(commandList[1]);
                    }
                    else{
                        respond("Language not recognized. Use " + commandKey +"languages to get a list of languages", command);
                    }

                }
                catch(IndexOutOfBoundsException e) {
                    respond(commandKey + "setDefaultLanguage-[Language]. Use " + commandKey + "languages to get a list of languages", command);
                }
                break;

            case ("translate"):
                try{



                    if(userPreferences.get(command.getAuthor()).defaultLanguage != null){
                        if(!testLanguage(commandList[1])){
                            System.out.println(commandList[1]);
                            //throw new RuntimeException();
                        }
                        respond(translate.translate(commandList[2], commandList[1],userPreferences.get(command.getAuthor()).defaultLanguage), command);
                    }
                    else{

                        if(!testLanguage(commandList[1]) || !testLanguage(commandList[2])){
                            System.out.println(commandList[1]);
                            System.out.println(commandList[2]);
                            //throw new RuntimeException();
                        }
                        System.out.println(commandList[1]);
                        System.out.println(commandList[2]);
                        System.out.println(commandList[3]);
                        String transed = translate.translate(commandList[3], commandList[1], commandList[2]);
                        System.out.println(transed == null);
                        System.out.println(transed);
                        respond(transed, command);

                    }

                }
                catch(IndexOutOfBoundsException e){
                    respond(commandKey+"translate-[Original Language]-[Target Language]-[Message]. If you have a default language set, this overrides to "+ commandKey +"translate [Original Language] [Message]. Use " + commandKey + "languages to get a list of languages", command);
                }
                //catch(NullPointerException r){
                   // respond("Language invalid, please consult " + commandKey +"languages to get a list of languages", command);
                //}
                break;

            case "enableAutoTranslate":
                try{
                    if(userPreferences.get(command.getAuthor()).defaultLanguage != null) {
                        userPreferences.get(command.getAuthor()).autoTranslate = true;
                    }
                    else{
                        respond("To use autoTranslate, you must select a defaultLanguage", command);
                    }
                }
                catch(IndexOutOfBoundsException e){
                    respond(commandKey+"enableAutoTranslate-[Target Language]. Use " + commandKey + "languages to get a list of languages", command);
                }
                /*catch(RuntimeException r){
                    respond("Language invalid, please consult " + commandKey +"languages to get a list of languages", command);
                }*/
                break;

            case "disableAutoTranslate":
                userPreferences.get(command.getAuthor()).autoTranslate = false;
                break;

            case "help":
                break;

            case "setCommandKey":

                try{
                    if(commandList[1].length() == 1) {
                        commandKey =  commandList[1].charAt(0);
                        respond("CommandKey set to " + commandKey + "!", command);
                    }
                    else{
                        respond("Command keys may only consist of one character", command);
                    }
                }
                catch(IndexOutOfBoundsException f){
                    respond(commandKey + "setCommandKey-[New CommandKey]", command);
                }
                break;

            case "languages":
                break;

            case "detectLanguageTranslate":
                try{

                    if(!testLanguage(commandList[1])){
                        //throw new RuntimeException();
                    }

                    respond(translate.translate(commandList[2], translate.detect(commandList[2]), commandList[1]), command);

                }
                catch(IndexOutOfBoundsException e){
                    respond(commandKey + "detectLanguageTranslate-[Target Language]-[Message]", command);
                }
                /*catch(RuntimeException r){
                    respond("Language invalid, please consult " + commandKey +"languages to get a list of languages", command);
                }*/
                break;

            default:
                respond("Command not recognized", command);

        }


    }

    private boolean testLanguage(String lang){
        return(!lan.getNameLanguage(lang).isBlank());
    }


    public static void main(String args[]) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault("Bot");
        String token = "NzMzMjEyMTQ5MjAzMzM3MjQ2.XxAjMg.vK2etJw71IYvK63ppKl6cuuoM8U";
        builder.setToken(token);
        builder.addEventListeners(new Core());
        builder.build();

    }
}
