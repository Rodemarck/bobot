package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.comando.guild.poll.conversa.ConversaLimpar;
import rode.core.ComandoGuild;
import rode.core.EventLoop2;
import rode.core.Helper;
import rode.utilitarios.Memoria;

import java.awt.*;
import java.util.LinkedList;

public class LimpaPolls extends ComandoGuild {
    public LimpaPolls() {
        super("limpa", Permission.ADMINISTRATOR, "limpa","limpar","lpoll");
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-limpar** enumera polls para serem deletadas.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para enumerar polls para que sejam apagas. Só funciona para o Administrador que executou o comando.
                
                **-limpar**.
                
                Aliases (comandos alternativos) : **limpar**, **limpa**, **clear**.
                """);
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        var guild = Memoria.guild(event.guildId());
        if(!guild.getPolls().isEmpty())
            event.reply(new EmbedBuilder().setColor(Color.decode("#C8A2C8")).setTitle("deletar esta poll?").appendDescription("**carregando...**").setFooter("**1/"+(guild.getPolls().size()+1)+"**"), message -> {
                EventLoop2.addReacao(new ConversaLimpar(event));
                return null;
            });
        else
            event.reply("não há polls abertas");
    }
}
