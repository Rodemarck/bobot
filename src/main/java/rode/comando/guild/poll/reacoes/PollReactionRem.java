package rode.comando.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.IgnoraComando;
import rode.core.PollHelper;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;

@IgnoraComando
public class PollReactionRem extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PollReactionRem.class);
    public PollReactionRem() {
        super("--pol--", null, "poll---");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        log.debug("inicio");
        final String tipo = args.getFirst();
        PollHelper.getPollFromEmote(args,event, dp -> {
            log.debug("callback");
            dp.poll().rem(dp.index(), event.id());
            PollHelper.reRender(event,tipo,dp);
            Memoria.update(dp);
            return;
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args,event);
    }

    @Override
    public void help(EmbedBuilder me) {

    }
}
