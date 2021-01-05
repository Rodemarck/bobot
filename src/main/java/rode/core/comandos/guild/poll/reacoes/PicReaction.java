package rode.core.comandos.guild.poll.reacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.core.Executador;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Regex;

import java.io.IOException;
import java.util.LinkedList;

public class PicReaction {
    private static Logger log = LoggerFactory.getLogger(PicReaction.class);
    public static void executa(LinkedList<String> args, Helper.Reacao event, String discriminator) throws IOException, Exception {
        int aux;
        if(event.emoji().equals(Constantes.EMOTES.get("esquerda")))
            aux = -1;
        else if(event.emoji().equals(Constantes.EMOTES.get("direita")))
            aux = 1;
        else{
            Executador.COMANDOS_REACOES_GUILD.get(
                    Executador.NOME_COMANDOS_REACOES_GUILD.get("poll" + discriminator)
            ).executa(args,event);
            return;
        }

        final int n = aux;
        PollHelper.getPollFromEmote(args, event, dp->{
            var emb = event.getMessage().getEmbeds().get(0);
            LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
            int index = Integer.parseInt(param.getFirst());
            index = (index + n);
            if(index >= dp.poll().getOpcoes().size())
                index = 0;
            else if (index < 0)
                index = dp.poll().getOpcoes().size() - 1;
            event.getMessage().editMessage(dp.poll().visualiza(index)).submit();
        });
        final var emb = event.getMessage().getEmbeds().get(0);
    }
}
