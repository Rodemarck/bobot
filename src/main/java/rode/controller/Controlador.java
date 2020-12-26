package rode.controller;

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
        event.getJDA().retrieveUserById(305090445283688450l).submit()
                    .thenCompose(user -> user.openPrivateChannel().submit())
                    .thenCompose(privateChannel -> privateChannel.sendMessage("nhe nhe").submit())
                    .thenCompose(m->m.addReaction("\u2705").submit());

    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        event.getJDA().retrieveUserById(305090445283688450l).submit()
                .thenCompose(user -> user.openPrivateChannel().submit())
                .thenCompose(privateChannel -> privateChannel.sendMessage("bye bye").submit())
                .thenCompose(m->m.addReaction("\u1F1FD").submit());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && pre(event.getMessage()))
            Executador.interpreta(event);
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if(!event.getUser().isBot())
            Executador.interpreta(event);
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        if(!event.getUser().isBot())
            Executador.interpreta(event);
    }

    @Override
    public void onException(@NotNull ExceptionEvent event) {
        event.getJDA().getTextChannelById(568241769456599044L).sendMessage(event.toString()).queue();
    }

}
