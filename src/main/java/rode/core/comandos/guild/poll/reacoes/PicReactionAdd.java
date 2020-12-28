package rode.core.comandos.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.core.comandos.guild.poll.texto.AbrePoll;
import rode.model.Poll;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class PicReactionAdd extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PicReactionAdd.class);
    public PicReactionAdd() {
        super("++pic++", null, "pic+++");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        if(event.emoji().equals(Constantes.EMOTES.get("esquerda"))){
            log.info("esq");
            PicReaction.executa(args, event, -1);
            return;
        }
        if(event.emoji().equals(Constantes.EMOTES.get("direita"))){
            log.info("dir");
            PicReaction.executa(args, event, 1);
            return;
        }
        event.reply("**"+event.getEvent().getUser().getName() + "** pare de trolar.", message -> message.delete().submitAfter(5, TimeUnit.SECONDS));
        event.getMessage().removeReaction(event.emoji());
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args, event);
    }


    @Override
    public void help(EmbedBuilder me) {

    }
}
