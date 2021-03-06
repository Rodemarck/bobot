package rode.comando.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class PicReactionRem extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PicReactionRem.class);
    public PicReactionRem() {
        super("--pic--", null, "pic---");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao hr) throws IOException, Exception {
        log.trace("chamando ->>" + PicReaction.class);
        PicReaction.executa(args, hr,"---");
    }




    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {

    }
}
