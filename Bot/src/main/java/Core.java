
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;


public class Core extends ListenerAdapter {

    //Add Functionality, defaultResponseLanguage
    //Add Functionality, autoTranslate
    //Add Functionality, manual translate
    //Add Functionality, help command

    Language defaultLanguage;
    char commandKey = '%';

    public void onMessageReceived(MessageReceivedEvent mess){
        System.out.println("Message received from " + mess.getAuthor().getName() + " : " + mess.getMessage().getContentDisplay());


        if(mess.getMessage().getContentRaw().charAt(0) == commandKey) {
            handleCommands(mess);
        }

    }

    public void respond(String message, MessageReceivedEvent mess){

        mess.getChannel().sendMessage(message);

    }

    public void handleCommands(MessageReceivedEvent command){

        String[] commandList = command.getMessage().getContentRaw().split(" ");
        String commandTrigger = commandList[0].substring(1);
        switch(commandTrigger){

            /*case "setDefaultLanguage":

                try{

                   Author user = (Author) command.getAuthor();
                   user.setDefaultLanguage(Language.valueOf(commandList[1]));

                }
                catch(IndexOutOfBoundsException e){
                    respond(commandKey+"setDefaultLanguage [Language]. Use " + commandKey + "languages to get a list of languages", command);
                }
                catch(IllegalArgumentException l){
                    respond("Language not recognized. Use " + commandKey +"languages to get a list of languages", command);
                }
                break;*/

            case ("translate"):
                break;

            case "autoTranslate":
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
                    respond(commandKey + "setCommandKey [New CommandKey]", command);
                }
                break;

            default:
                respond("Command not recognized", command);

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
