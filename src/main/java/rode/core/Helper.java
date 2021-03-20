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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class Helper {
    private static final Logger log = LoggerFactory.getLogger(Helper.class);
    protected final GenericGuildMessageEvent event;
    protected Message message;
    protected final String id;
    protected Member member;
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
    

    public void reply(String str){
        this.event.getChannel().sendMessage(str).queue();
    }
    public void reply(EmbedBuilder eb){
        this.event.getChannel().sendMessage(eb.build()).queue();
    }
    public void reply(MessageEmbed me){
        this.event.getChannel().sendMessage(me).queue();
    }
    public void reply(File arq){
        reply(arq,message -> arq.delete());
    }

    public void replyTemp(String str){
        reply(str,message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
    }
    public void replyTemp(EmbedBuilder eb){
        reply(eb,message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
    }
    public void replyTemp(MessageEmbed me){
        reply(me, message->message.delete().queueAfter(15, TimeUnit.SECONDS));
    }


    public void reply(String str, Consumer<Message> action){
        this.event.getChannel().sendMessage(str).queue(action);
    }
    public void reply(EmbedBuilder eb, Consumer<Message> action){
        log.info("the message is send");
        this.event.getChannel().sendMessage(eb.build()).queue(action,err->{
            log.error(err.getMessage());
        });
    }
    public void reply(MessageEmbed me, Consumer<Message> action){

        this.event.getChannel().sendMessage(me).queue(action);
    }
    public void reply(File arq,Consumer<Message> action) {
        this.event.getChannel().sendFile(arq).queue(msg->{
            action.andThen(q->arq.delete()).accept(msg);
        });
    }


    public void dm(String str){
        this.event.getJDA().retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(str).queue()
                )
        );
    }
    public void dm(EmbedBuilder eb){
        this.event.getJDA().retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(eb.build()).queue()
                )
        );
    }
    public void dm(MessageEmbed me){
        this.event.getJDA().retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(me).queue()
                )
        );
    }
    public void dm(File f){
        this.event.getJDA().retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendFile(f).queue(q->
                            f.delete()
                        )
                )
        );
    }
    public void dm(String str, Consumer<Message> action){
        this.event.getJDA().retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(str).queue(action)
                )
        );
            }
    public void dm(EmbedBuilder eb, Consumer<Message> action){
        this.event.getJDA().retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(eb.build()).queue(action)
                )
        );
    }
    public void dm(MessageEmbed me, Consumer<Message> action){
        this.event.getJDA().retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(me).queue(action)
                )
        );
    }
    public void dm(File f, Consumer<Message> action){
        this.event.getJDA().retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendFile(f).queue(q->action.andThen(qq->f.delete()).accept(q))
                )
        );
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
    public void membro(Member member) {
        this.member =  member;
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
