package rode.comando.guild.poll.reacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.IgnoraComando;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ComandoGuildReacoes;
import rode.utilitarios.Memoria;

import java.io.IOException;

@IgnoraComando
public class PollReactionRem extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PollReactionRem.class);
    public PollReactionRem() {
        super("--pol--", null, "poll---");
    }

    @Override
    public void executa(String[] args, Helper.Reacao hm) throws IOException, Exception {
        log.debug("inicio");
        final String tipo = args[0];
        PollHelper.getPollFromEmote(args,hm, dp -> {
            log.debug("callback");
            dp.poll().remove(dp.index(), hm.getId());
            PollHelper.reRender(hm,tipo,dp);
            Memoria.update(dp);
            return;
        });
    }

    @Override
    public boolean livre(String[] args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args,event);
    }
}
