package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.util.LinkedList;

@EComandoPoll
public class MostraVotosPoll extends ComandoGuild {
    public MostraVotosPoll() {
        super("votos", null, "votos","votes","vpoll");
    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws IOException, Exception {
        PollHelper.getPoll(args,hm,dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                EmbedBuilder eb = Constantes.builder();
                eb.setTitle(String.format(hm.text("votos.exec.title"),dp.titulo()));
                poll.getVotes(eb, hm.bundle());
                hm.reply(eb);
                return;
            }
            hm.reply(String.format(hm.text("votos.exec.404"),dp.titulo()));
        });
    }
}
