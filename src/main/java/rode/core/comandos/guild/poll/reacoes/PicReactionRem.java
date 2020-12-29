package rode.core.comandos.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.core.comandos.guild.poll.texto.AbrePoll;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.util.LinkedList;

public class PicReactionRem extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PicReactionRem.class);
    public PicReactionRem() {
        super("--pic--", null, "pic---");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        PicReaction.executa(args, event,"---");
    }




    @Override
    public void help(EmbedBuilder me) {

    }
}
