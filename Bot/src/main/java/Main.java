
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;


public class Main extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent mess){
        System.out.println("Message received from " + mess.getAuthor().getName() + " : " + mess.getMessage().getContentDisplay());

        if(mess.getMessage().getContentRaw().equals("!Test")){
            mess.getMessage().getChannel().sendMessage("It's working!").queue();
        }

    }


    public static void main(String args[]) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault("Bot");
        String token = "NzMzMjEyMTQ5MjAzMzM3MjQ2.XxAjMg.vK2etJw71IYvK63ppKl6cuuoM8U";
        builder.setToken(token);
        builder.addEventListeners(new Main());
        builder.build();

    }
}
