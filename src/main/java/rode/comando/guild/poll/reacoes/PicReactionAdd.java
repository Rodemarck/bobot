package rode.comando.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;

import java.io.IOException;
import java.util.LinkedList;

public class PicReactionAdd extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PicReactionAdd.class);
    public PicReactionAdd() {
        super("++pic++", null, "pic+++");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        log.trace("chamando ->>" + PicReaction.class.getName());
        PicReaction.executa(args, event,"+++");
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args, event);
    }


    @Override
    public void help(EmbedBuilder me) {

    }
}
