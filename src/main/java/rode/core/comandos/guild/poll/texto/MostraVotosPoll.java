package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;

import java.io.IOException;
import java.util.LinkedList;

public class MostraVotosPoll extends ComandoGuild {
    public MostraVotosPoll() {
        super("votos", null, "votos","votes","vpoll");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException, Exception {
        PollHelper.getPoll(args,event,((titulo, opcoes, guild, query) -> {
            if(guild != null){
                Poll poll = guild.getPoll(titulo);
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("votação para **{" + titulo + "}**");
                poll.getVotos(eb, event.getEvent().getJDA());
                event.reply(eb);
                return;
            }
            event.reply("poll **{" + titulo + "}** não encontrada");
        }));
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-votos {titulo}** : mostra os votos de uma poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("Comando para listar todos os membros que votaram e seus respectivos votos de uma poll (enquete) especifica.\n\n");
        me.appendDescription("**-votos {titulo}***.\n\n");
        me.appendDescription("Aliases (comandos alternativos) : **votos**, **votes**, **vpoll**");
    }
}
