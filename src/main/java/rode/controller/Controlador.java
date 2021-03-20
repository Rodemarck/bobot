package rode.controller;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.core.EventLoop2;
import rode.core.Executador;
import rode.utilitarios.Constantes;

public class Controlador implements EventListener {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private boolean pre(Message message) {
        return message.getContentRaw().startsWith(Constantes.PREFIXO);
    }

    public void onReady(@NotNull ReadyEvent event) {
        event.getJDA().retrieveUserById(305090445283688450l).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage("olá...?? agr é sério...").submit())
                .thenCompose(m->m.addReaction(Constantes.emote("check")).submit());
        Executador.poolExecutor.submit(()->{
            EventLoop2.getInstance(event.getJDA());
        });
    }

    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            if (pre(event.getMessage()))
                Executador.interpreta(event,event.getAuthor());
            else
                Executador.checa(event);
        }
    }

    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if(event.getUser() != null) {
            if (!event.getUser().isBot())
                Executador.interpreta(event, event.getUser());
        }else
            event.getJDA().retrieveUserById(event.getUserIdLong()).queue(u->{
                if (!u.isBot())
                    Executador.interpreta(event, u);
            });
   }

    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        if(event.getUser() != null) {
            if (!event.getUser().isBot())
                Executador.interpreta(event, event.getUser());
        }else
            event.getJDA().retrieveUserById(event.getUserIdLong()).queue(u->{
                if (!u.isBot())
                    Executador.interpreta(event, u);
            });
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof GuildMessageReceivedEvent) onGuildMessageReceived((GuildMessageReceivedEvent)event);
        else if(event instanceof GuildMessageReactionAddEvent) onGuildMessageReactionAdd((GuildMessageReactionAddEvent)event);
        else if(event instanceof GuildMessageReactionRemoveEvent) onGuildMessageReactionRemove((GuildMessageReactionRemoveEvent)event);
        else if(event instanceof ReadyEvent) onReady((ReadyEvent)event);
    }
}
