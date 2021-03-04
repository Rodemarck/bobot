package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;

public class MostraVotosPoll extends ComandoGuild {
    public MostraVotosPoll() {
        super("votos", null, "votos","votes","vpoll");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException, Exception {
        PollHelper.getPoll(args,event,dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                EmbedBuilder eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
                eb.setTitle("votação para **{" + dp.titulo() + "}**");
                poll.getVotos(eb, event.jda());
                event.reply(eb);
                return;
            }
            event.reply("poll **{" + dp.titulo() + "}** não encontrada");
        });
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-votos {titulo}** : mostra os votos de uma poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para listar todos os membros que votaram e seus respectivos votos de uma poll (enquete) especifica.
                                
                **-votos {titulo}***.
                                
                Aliases (comandos alternativos) : **votos**, **votes**, **vpoll**
                """);
    }
}
