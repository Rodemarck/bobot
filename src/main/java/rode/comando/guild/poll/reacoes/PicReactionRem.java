package rode.comando.guild.poll.reacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.model.ComandoGuildReacoes;
import rode.core.Helper;

import java.io.IOException;
import java.util.LinkedList;

public class PicReactionRem extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PicReactionRem.class);
    public PicReactionRem() {
        super("--pic--", null, "pic---");
    }

    @Override
    public void executa(String[] args, Helper.Reacao hr) throws IOException, Exception {
        log.trace("chamando ->>" + PicReaction.class);
        PicReaction.executa(args, hr,"---");
    }
}
