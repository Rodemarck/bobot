package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Grafico;

import java.io.File;
import java.util.LinkedList;

public class GraficoPoll extends ComandoGuild {
    public GraficoPoll() {
        super("grafico", null, "grafico","grpah","gpoll");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args, event, dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                File arq = Grafico.poll(poll, event.getEvent().getGuild());
                event.reply(arq);
                return;
            }
            event.reply("poll {**" + dp.titulo() + "**} não encontrada.");
        });
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-grafico {titulo}** : envia um grafico dos votos da poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        help(me);
    }
}