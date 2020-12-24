package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public abstract class Helper {
    private static final Logger log = LoggerFactory.getLogger(Helper.class);
    protected final GenericGuildMessageEvent event;
    protected final Message message;
    protected final String id;


    protected Helper( GenericGuildMessageEvent event, Message message, String id) {
        this.event = event;
        this.message = message;
        this.id = id;
    }

    public void reply(String str){
        this.event.getChannel().sendMessage(str).submit();
    }
    public void reply(EmbedBuilder eb){
        this.event.getChannel().sendMessage(eb.build()).submit();
    }
    public void reply(MessageEmbed me){
        this.event.getChannel().sendMessage(me).submit();
    }
    public void reply(File arq) {
        this.event.getChannel().sendFile(arq).submit()
        .thenCompose(m->{
            arq.delete();
            return null;
        });
    }
    public void reply(String str, Function<Message, CompletionStage<Void>> action){
        this.event.getChannel().sendMessage(str).submit()
                .thenCompose(action);
    }
    public void reply(EmbedBuilder eb, Function<Message, CompletionStage<Void>> action){
        this.event.getChannel().sendMessage(eb.build()).submit()
                .thenCompose(action);
    }
    public void reply(MessageEmbed me, Function<Message, CompletionStage<Void>> action){
        this.event.getChannel().sendMessage(me).submit()
            .thenCompose(action);
    }
    public void reply(File arq,Function<Message, CompletionStage<Void>> action) {
        this.event.getChannel().sendFile(arq).submit()
            .thenCompose(action);
    }


    public void dm(String str){
        this.event.getJDA().retrieveUserById(id).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage(str).submit());
    }
    public void dm(EmbedBuilder eb){
        this.event.getJDA().retrieveUserById(id).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage(eb.build()).submit());
    }
    public void dm(MessageEmbed me){
        this.event.getJDA().retrieveUserById(id).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage(me).submit());
        this.event.getJDA().getUserById(id).openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(me);
        });
    }
    public void dm(String str, Function<Message, CompletionStage<Void>> action){
        this.event.getJDA().retrieveUserById(id).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage(str).submit())
                .thenCompose(action);
    }
    public void dm(EmbedBuilder eb, Function<Message, CompletionStage<Void>> action){
        this.event.getJDA().retrieveUserById(id).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage(eb.build()).submit())
                .thenCompose(action);
    }
    public void dm(MessageEmbed me, Function<Message, CompletionStage<Void>> action){
        this.event.getJDA().retrieveUserById(id).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage(me).submit())
                .thenCompose(action);
    }

    public String guildId(){
        return event.getGuild().getId();
    }
    public Message getMessage() {
        return message;
    }

    public String getId(){
        return id;
    }




    public static class Mensagem extends Helper{
        private static Logger log = LoggerFactory.getLogger(Mensagem.class);
        private GuildMessageReceivedEvent event;

        public Mensagem(GuildMessageReceivedEvent event) {
            super(event,event.getMessage(),event.getAuthor().getId());
            log.info("id = [{}]",event.getAuthor().getId());
            this.event = event;
        }

        public GuildMessageReceivedEvent getEvent() {
            return event;
        }
    }

    public static class Reacao extends Helper{
        private static Logger log = LoggerFactory.getLogger(Reacao.class);
        private GenericGuildMessageReactionEvent event;

        public Reacao(GenericGuildMessageReactionEvent event, Message message) {
            super(event,message, event.getUserId());
            log.info("id = [{}]",event.getUserId());
            this.event = event;
        }

        public GenericGuildMessageReactionEvent getEvent() {
            return event;
        }

        public String emoji(){
            return this.event.getReactionEmote().getEmoji();
        }
    }


}
