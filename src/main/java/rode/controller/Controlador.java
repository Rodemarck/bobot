package rode.controller;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.core.EventLoop;
import rode.core.Executador;
import rode.utilitarios.Constantes;

import java.time.LocalDateTime;

public class Controlador extends ListenerAdapter {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static LocalDateTime ultimaMsg;
    private boolean pre(Message message) {
        return message.getContentRaw().startsWith(Constantes.PREFIXO);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ultimaMsg = LocalDateTime.now();
        event.getJDA().retrieveUserById(305090445283688450l).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage("olá...??").submit())
                .thenCompose(m->m.addReaction(Constantes.emote("check")).submit())
                .thenRunAsync(()->EventLoop.getInstance());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot() ) {
            if (pre(event.getMessage()))
                Executador.interpreta(event);
            else
                Executador.checa(event);
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        event.getJDA().retrieveUserById(event.getUserId()).submit()
            .thenCompose(user -> {
                if(!event.getMember().getUser().isBot())
                    Executador.interpreta(event);
                return null;
            });
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        if(event.getMember() == null){
            event.getJDA().retrieveUserById(event.getUserId()).submit()
                    .thenCompose(user -> {
                        if(!event.getMember().getUser().isBot())
                            Executador.interpreta(event);
                        return null;
                    });
        }
        else if(!event.getMember().getUser().isBot())
            Executador.interpreta(event);
    }

    @Override
    public void onException(@NotNull ExceptionEvent event) {
        event.getJDA().retrieveUserById(305090445283688450l).submit()
                .thenCompose(u -> u.openPrivateChannel().submit())
                .thenCompose(pv->pv.sendMessage(event.getCause().getMessage()).submit());
    }

}
