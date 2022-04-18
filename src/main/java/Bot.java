import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.EventListener;

public class Bot extends ListenerAdapter{

    public static char prefixAdmin = '!';

    public static void main(String[] args) throws LoginException {
        final String token = "~Your Bot Token~";
        JDABuilder.createLight(token).addEventListeners(new Commands()).setActivity(Activity.playing("Question Competetion")).build();
    }

}
