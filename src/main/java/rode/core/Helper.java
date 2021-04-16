package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Helper {
    private static final Logger log = LoggerFactory.getLogger(Helper.class);
    protected Message message;
    protected final String id;
    protected Member member;
    protected final ResourceBundle bundle;
    protected JDA jda;
    protected TextChannel canal;

    public void mensagem(Message message) {
        this.message = message;
    }

    public JDA jda(){
        return jda;
    }

    public abstract String guildId();
    protected Helper(JDA jda, TextChannel canal, Message message, String id, Member member, Locale locale) {
        this.jda = jda;
        this.canal = canal;
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
        canal.sendMessage(str).queue();
    }
    public void reply(EmbedBuilder eb){
        canal.sendMessage(eb.build()).queue();
    }
    public void reply(MessageEmbed me){
        canal.sendMessage(me).queue();
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

    public void embed(File f, Function<String,EmbedBuilder> msg){
        canal.sendFile(f,"arq.png")
                .embed(msg.apply("attachment://arq.png").build()).queue(q->
                    f.delete()
        );
    }

    public void reply(String str, Consumer<Message> action){
        canal.sendMessage(str).queue(action);
    }
    public void reply(EmbedBuilder eb, Consumer<Message> action){
        canal.sendMessage(eb.build()).queue(action,err->{
            log.error(err.getMessage());
        });
    }
    public void reply(MessageEmbed me, Consumer<Message> action){

        canal.sendMessage(me).queue(action);
    }
    public void reply(File arq,Consumer<Message> action) {
        canal.sendFile(arq).queue(msg->{
            action.andThen(q->arq.delete()).accept(msg);
        });
    }


    public void dm(String str){
        jda.retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(str).queue()
                )
        );
    }
    public void dm(EmbedBuilder eb){
        jda.retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(eb.build()).queue()
                )
        );
    }
    public void dm(MessageEmbed me){
        jda.retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(me).queue()
                )
        );
    }
    public void dm(File f){
        jda.retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendFile(f).queue(q->
                            f.delete()
                        )
                )
        );
    }
    public void dm(String str, Consumer<Message> action){
        jda.retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(str).queue(action)
                )
        );
            }
    public void dm(EmbedBuilder eb, Consumer<Message> action){
        jda.retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(eb.build()).queue(action)
                )
        );
    }
    public void dm(MessageEmbed me, Consumer<Message> action){
        jda.retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendMessage(me).queue(action)
                )
        );
    }
    public void dm(File f, Consumer<Message> action){
        jda.retrieveUserById(id).queue(user ->
                user.openPrivateChannel().queue(pv->
                        pv.sendFile(f).queue(q->action.andThen(qq->f.delete()).accept(q))
                )
        );
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
        public String guildId(){
            return event.getGuild().getId();
        }
        public Mensagem(GuildMessageReceivedEvent event,Locale locale) {
            super(event.getJDA(),event.getChannel(),event.getMessage(),event.getAuthor().getId(), event.getMember(),locale);
            this.event = event;
        }

        public GuildMessageReceivedEvent getEvent() {
            return event;
        }
    }

    public static class Reacao extends Helper{
        private static Logger log = LoggerFactory.getLogger(Reacao.class);
        private GenericGuildMessageReactionEvent event;
        public String guildId(){
            return event.getGuild().getId();
        }
        public Reacao(GenericGuildMessageReactionEvent event, Message message,Locale locale) {
            super(event.getJDA(),event.getChannel(),message, event.getUserId(), event.getMember(),locale);
            this.event = event;
        }

        public GenericGuildMessageReactionEvent getEvent() {
            return event;
        }

        public String emoji(){
            return this.event.getReactionEmote().getEmoji();
        }
    }
    public static class Slash extends Helper{
        private SlashCommandEvent event;
        public Slash(SlashCommandEvent event, Message message, String id, Member member, Locale locale) {
            super(event.getJDA(),event.getTextChannel(), message, id, member, locale);
        }
        public String guildId(){
            return event.getGuild().getId();
        }

        public void replySlash(EmbedBuilder b) {
            event.reply(b.build()).setEphemeral(true).queue();
        }
    }

}
