package rode.core;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.utilitarios.Constantes;

public class Controlador implements EventListener {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private PrivateChannel pv;
    private boolean pre(Message message) {
        return message.getContentRaw().startsWith(Constantes.PREFIXO);
    }

    public void onReady(@NotNull ReadyEvent event) {
        event.getJDA().retrieveUserById(305090445283688450l).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> {
                    pv = privateChannel;
                    return privateChannel.sendMessage("número 1").submit();
                });
        Executador.poolExecutor.execute(()->{
            boolean feonline = false;
            while(true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {e.printStackTrace();}
                event.getJDA().getGuildById(286964489406906368l).retrieveMemberById(287269006753267713l).queue(member -> {

                    System.out.println(member.getEffectiveName() + " :: " + member.getOnlineStatus());
                });

            }
        });
        Executador.poolExecutor.execute(()->
            EventLoop.getInstance(event.getJDA())
        );
    }

    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        log.info("msg");
        if (!event.getAuthor().isBot()) {
            if (pre(event.getMessage()))
                Executador.interpreta(event,event.getAuthor());
            else
                Executador.checa(event);
        }
    }

    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if(event.getMember() != null) {
            if (!event.getUser().isBot())
                Executador.interpreta(event, event.getMember());
        }else
            event.getGuild().retrieveMemberById(event.getUserIdLong()).queue(u->{
                if (!u.getUser().isBot()) {
                    log.trace("chamanda para evento");
                    Executador.interpreta(event, u);
                }
            });
   }

    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        if(event.getMember() != null) {
            if (!event.getMember().getUser().isBot())
                Executador.interpreta(event, event.getMember());
        }else
            event.getGuild().retrieveMemberById(event.getUserIdLong()).queue(u->{
                if (!u.getUser().isBot())
                    Executador.interpreta(event, u);
            });
    }

    public void slashCommandEvent(@NotNull SlashCommandEvent event){
        log.debug("slah!!!");
        Executador.interpreta(event);
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

        if(event instanceof GuildMessageReceivedEvent e) onGuildMessageReceived(e);
        else if(event instanceof SlashCommandEvent e) slashCommandEvent(e);
        else if(event instanceof GuildMessageReactionAddEvent e) onGuildMessageReactionAdd(e);
        else if(event instanceof GuildMessageReactionRemoveEvent e) onGuildMessageReactionRemove(e);
        else if(event instanceof ReadyEvent e) onReady(e);
    }
}
