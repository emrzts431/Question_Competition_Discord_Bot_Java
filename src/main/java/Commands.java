import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Commands extends ListenerAdapter {

    List<String> allNicks = new LinkedList<>();
    List<String> nicksA = new LinkedList<>();
    List<String> nicksB = new LinkedList<>();
    List<String> nicksC = new LinkedList<>();
    List<String> nicksD = new LinkedList<>();

    List<String> _messagesToDelete = new LinkedList<>();

    private boolean checkIfUserHaveAdminRoles(List<Role> roles){
        if(roles.size() == 0){
            return false;
        }
        else{
            for(Role r : roles){
                if(r.getName().equals("Bot's") || r.getName().equals("Dijipin Moderatör") ||r.getName().equals("Dijipin Yönetim") ||  r.getName().equals("Yönetim") || r.getName().equals("Dijipin Yayıncı") || r.getName().equals("Dijipin Admin")){
                    return true;
                }
            }
            return false;
        }
    }

    private boolean userGaveVote(String userName){
        for(String name : allNicks){
            if(name.equals(userName)) return true;
        }
        return false;
    }

    private boolean isInvalidReaction(String reactionCode){
        switch (reactionCode) {
            case "\uD83C\uDDE6":
            case "\uD83C\uDDE7":
            case "\uD83C\uDDE8":
            case "\uD83C\uDDE9":
                return false;
            default:
                return true;
        }
    }

    public void resetAndDeleteMessages(MessageReceivedEvent event){
        for(String messageId : this._messagesToDelete){
            event.getChannel().deleteMessageById(messageId).queue();
        }
        this._messagesToDelete.clear();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getChannel().getId().equals("959529716090548234") || event.getChannel().getId().equals("958777115375980588")) {
            Message msg = event.getMessage();
            String messageString = msg.getContentRaw();
            boolean eventOwnerHasAdminRights;

            try{
                eventOwnerHasAdminRights = checkIfUserHaveAdminRoles(event.getMember().getRoles());
            }
            catch(NullPointerException e){
                eventOwnerHasAdminRights = false;
            }

            if(messageString.length() != 0){
                if (messageString.charAt(0) == Bot.prefixAdmin) {
                    if (messageString.equals(Bot.prefixAdmin + "Kazanan " + "A") ||
                            messageString.equals(Bot.prefixAdmin + "Kazanan " + "B") ||
                            messageString.equals(Bot.prefixAdmin + "Kazanan " + "C") ||
                            messageString.equals(Bot.prefixAdmin + "Kazanan " + "D")) {
                        System.out.println("Setting winner..");
                        String answer = Character.toString(messageString.charAt(messageString.length() - 1));
                        System.out.println("Answer : " + answer);
                        MessageChannel channel = event.getChannel();
                        channel.sendMessage(setWinner(answer)).queue();
                        this._messagesToDelete.add(event.getMessageId());
                    } else if (messageString.equals(Bot.prefixAdmin + "R")) {
                        allNicks.clear();
                        nicksA.clear();
                        nicksB.clear();
                        nicksC.clear();
                        nicksD.clear();
                        this._messagesToDelete.add(event.getMessageId());
                        resetAndDeleteMessages(event);
                        System.out.println("Lists and messages are cleared");
                    }
                    else {
                        MessageChannel channel = event.getChannel();
                        channel.sendMessage("Message from Bot : Invalid Request!!\n" +
                                " Valid Requests are : \n" +
                                "1- !Kazanan ~cevap~\n" +
                                "2- !R").queue();
                    }

                }
            }
            else{
                try{
                    String messageIDQuestion = msg.getId();
                    this._messagesToDelete.add(messageIDQuestion);
                    event.getChannel().addReactionById(messageIDQuestion, "\uD83C\uDDE6").queue();
                    event.getChannel().addReactionById(messageIDQuestion, "\uD83C\uDDE7").queue();
                    event.getChannel().addReactionById(messageIDQuestion, "\uD83C\uDDE8").queue();
                    event.getChannel().addReactionById(messageIDQuestion, "\uD83C\uDDE9").queue();
                }
                catch (Exception e){
                    System.out.println("Non Image message received");
                }
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if(event.getChannel().getId().equals("958777115375980588") || event.getChannel().getId().equals("959529716090548234")){
            String userName = event.getUser().getName();
            String emojiName = event.getReaction().getReactionEmote().getName();
            boolean eventOwnerHasAdminRights;

            try{
                eventOwnerHasAdminRights = checkIfUserHaveAdminRoles(event.getMember().getRoles());
            }
            catch(NullPointerException e){
                eventOwnerHasAdminRights = false;
            }
            if(!userGaveVote(userName) && !eventOwnerHasAdminRights){
                allNicks.add(userName);
                switch (emojiName){
                    case "\uD83C\uDDE6":
                        nicksA.add(userName);
                        System.out.println("UserName added");
                        break;
                    case "\uD83C\uDDE7":
                        nicksB.add(userName);
                        System.out.println("UserName added");
                        break;
                    case "\uD83C\uDDE8":
                        nicksC.add(userName);
                        System.out.println("UserName added");
                        break;
                    case "\uD83C\uDDE9":
                        nicksD.add(userName);
                        System.out.println("UserName added");
                        break;
                    default:
                        event.getReaction().removeReaction(event.getUser()).queue();
                        System.out.println("Emoji unknown");
                }
            }
            else if(eventOwnerHasAdminRights){
                if(isInvalidReaction(emojiName)){
                    event.getReaction().removeReaction(event.getUser()).queue();
                    System.out.println("Attempt to add an invalid reaction is taken cared of!");
                }
                else{
                    System.out.println("Reaction added!");
                }
            }
            else{
                System.out.println("gave vote");
                if(!eventOwnerHasAdminRights) {
                    event.getReaction().removeReaction(event.getUser()).queue();
                    System.out.println("Reaction request declined");
                }
            }
        }
    }

    public String setWinner(String correctAnswer){
        Random rnd = new Random();
        if(correctAnswer.equals("A")){
            if(nicksA.size() != 0){
                int index = rnd.nextInt(nicksA.size());
                return "Tebrikler " + nicksA.get(index) + " siz kazandınız !!";
            }
        }
        else if(correctAnswer.equals("B")){
            if(nicksB.size() != 0) {
                int index = rnd.nextInt(nicksB.size());
                return "Tebrikler " + nicksB.get(index) + " siz kazanınız !!";
            }
        }
        else if(correctAnswer.equals("C")){
            if(nicksC.size() != 0){
                int index = rnd.nextInt(nicksC.size());
                return "Tebrikler " + nicksC.get(index) + " siz kazandınız !!";
            }
        }
        else if(correctAnswer.equals("D")){
            if(nicksD.size() != 0){
                int index = rnd.nextInt(nicksD.size());
                return "Tebrikler " + nicksD.get(index) + " siz kazandiniz !!";
            }
        }
        return "List is empty!";
    }
}
