package rode.core.comandos.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.util.LinkedList;

public class PicReactionRem extends ComandoGuildReacoes {
    public PicReactionRem() {
        super("--pic--", null, "pic---");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        if(event.emoji().equals(Constantes.EMOTES.get("esquerda"))){
            PicReaction.executa(args, event, -1);
            return;
        }
        if(event.emoji().equals(Constantes.EMOTES.get("direita"))){
            PicReaction.executa(args, event, 1);
            return;
        }
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args, event);
    }


    @Override
    public void help(EmbedBuilder me) {

    }
}
