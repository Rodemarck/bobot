package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class Helper {
    private static final Logger log = LoggerFactory.getLogger(Helper.class);
    protected final GenericGuildMessageEvent event;
    protected Message message;
    protected final String id;
    protected final Member member;
    protected final ResourceBundle bundle;

    public void mensagem(Message message) {
        this.message = message;
    }

    public JDA jda(){
        return event.getJDA();
    }

    public GenericGuildMessageEvent event() {
        return event;
    }

    protected Helper(GenericGuildMessageEvent event, Message message, String id, Member member, Locale locale) {
        this.event = event;
        this.message = message;
        this.id = id;
        this.member = member;
        this.bundle = ResourceBundle.getBundle("messages", locale);
    }

    public ResourceBundle bundle() {
        return bundle;
    }

    public String text(String s){
        return bundle.getString(s);
    }

    public void responde(Message mensagem, String str){
        mensagem.reply(str).submit();
    }
    public void responde(Message mensagem,EmbedBuilder eb){
        mensagem.reply(eb.build()).submit();
        this.event.getChannel().sendMessage(eb.build()).submit();
    }
    public void responde(Message mensagem,MessageEmbed me){
        mensagem.reply(me).submit();
    }
    public void responde(Message mensagem,File arq){
        mensagem.reply(arq).submit().thenRun(()->arq.delete());
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
    public void reply(File arq){
        reply(arq,message -> message.delete().submitAfter(15,TimeUnit.SECONDS));
    }

    public void replyTemp(String str){
        reply(str,message -> message.delete().submitAfter(15, TimeUnit.SECONDS));
    }
    public void replyTemp(EmbedBuilder eb){
        reply(eb,message -> message.delete().submitAfter(15, TimeUnit.SECONDS));
    }
    public void replyTemp(MessageEmbed me){
        reply(me, message->message.delete().submitAfter(15, TimeUnit.SECONDS));
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
            .thenCompose(action)
            .thenRun(()-> arq.delete());
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
    public void dm(File f){
        this.jda().retrieveUserById(id).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendFile(f).submit())
                .thenRun(()->f.delete());
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
    public void dm(File f, Function<Message, CompletionStage<Void>> action){
        this.jda().retrieveUserById(id).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendFile(f).submit())
                .thenCompose(action)
                .thenRun(()->f.delete());
    }

    public String guildId(){
        return event.getGuild().getId();
    }
    public Message mensagem() {
        return message;
    }
    public String id(){
        return id;
    }
    public Member membro() {
        return member;
    }



    public static class Mensagem extends Helper{
        private static Logger log = LoggerFactory.getLogger(Mensagem.class);
        private GuildMessageReceivedEvent event;

        public Mensagem(GuildMessageReceivedEvent event,Locale locale) {
            super(event,event.getMessage(),event.getAuthor().getId(), event.getMember(),locale);
            log.debug("id = [{}]",event.getAuthor().getId());
            this.event = event;
        }

        public GuildMessageReceivedEvent getEvent() {
            return event;
        }
    }

    public static class Reacao extends Helper{
        private static Logger log = LoggerFactory.getLogger(Reacao.class);
        private GenericGuildMessageReactionEvent event;

        public Reacao(GenericGuildMessageReactionEvent event, Message message,Locale locale) {
            super(event,message, event.getUserId(), event.getMember(),locale);
            log.debug("id = [{}]",event.getUserId());
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
