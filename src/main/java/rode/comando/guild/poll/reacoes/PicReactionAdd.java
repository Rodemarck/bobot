package rode.comando.guild.poll.reacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ComandoGuildReacoes;

import java.io.IOException;

public class PicReactionAdd extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PicReactionAdd.class);
    public PicReactionAdd() {
        super("++pic++", null, "pic+++");
    }

    @Override
    public void executa(String[] args, Helper.Reacao hr) throws IOException, Exception {
        log.trace("chamando ->>" + PicReaction.class.getName());
        PicReaction.executa(args, hr,"+++");
    }

    @Override
    public boolean livre(String[] args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args, event);
    }
}
