package rode.controller;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.core.Executador;
import rode.utilitarios.Constantes;

public class Controlador extends ListenerAdapter {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private boolean pre(Message message) {
        return message.getContentRaw().startsWith(Constantes.PREFIXO);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        /*event.getJDA().retrieveUserById(305090445283688450l).submit()
                    .thenCompose(user -> user.openPrivateChannel().submit())
                    .thenCompose(privateChannel -> privateChannel.sendMessage("nhe nhe").submit())
                    .thenCompose(m->m.addReaction("\u2705").submit());*/
        event.getJDA().retrieveUserById(305090445283688450l).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel ->
                    privateChannel.sendMessage("poll\nhttps://media.discordapp.net/attachments/465380712405532694/704979755417468969/f9c.gif").submit()
                )
                .thenCompose(message -> message.editMessage(new EmbedBuilder().appendDescription("https://media.discordapp.net/attachments/465380712405532694/704979755417468969/f9c.gif").setImage("https://media.discordapp.net/attachments/465380712405532694/704979755417468969/f9c.gif").build()).submit())
                .thenCompose(m->m.addReaction(Constantes.EMOTES.get("check")).submit());

    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        event.getJDA().retrieveUserById(305090445283688450l).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage(new EmbedBuilder().appendDescription("https://media.discordapp.net/attachments/465380712405532694/704979755417468969/f9c.gif").build()).submit())
                .thenCompose(m->m.addReaction(Constantes.EMOTES.get("check")).submit());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && pre(event.getMessage()))
            Executador.interpreta(event);
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
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
