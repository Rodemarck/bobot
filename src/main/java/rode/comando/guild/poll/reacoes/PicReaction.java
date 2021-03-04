package rode.comando.guild.poll.reacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Executador;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.io.IOException;
import java.util.LinkedList;

public class PicReaction {
    private static Logger log = LoggerFactory.getLogger(PicReaction.class);
    public static void executa(LinkedList<String> args, Helper.Reacao event, String discriminator) throws IOException, Exception {
        int aux;
        log.debug("verificando seta");
        if(event.emoji().equals(Constantes.emote("esquerda")))
            aux = -1;
        else if(event.emoji().equals(Constantes.emote("direita")))
            aux = 1;
        else if(event.emoji().equals(Constantes.emote("check")))
            aux = 0;
        else{

            Executador.COMANDOS_REACOES_GUILD.get(
                    Executador.NOME_COMANDOS_REACOES_GUILD.get("poll" + discriminator)
            ).executa(args,event);
            return;
        }
        log.debug("não é troca");
        final int n = aux;
        PollHelper.getPollFromEmote(args, event, dp->{
            var emb = event.getMessage().getEmbeds().get(0);
            LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
            int index = Integer.parseInt(param.getFirst());
            if(n == 0){
                if(dp.poll().hasUser(event.getId())){
                    int i = dp.poll().votouPara(event.getId());
                    if(i == index){
                        dp.poll().rem(index,event.getId());
                        Memoria.update(dp);
                        PollHelper.removeVoto(event, index);
                        PollHelper.reRender(event,"pic", dp);
                        return;
                    }
                    PollHelper.jaVotou(event,i);
                    return;
                }
                dp.poll().add(index, event.getId());
                Memoria.update(dp);
                PollHelper.contaVoto(event, index);
                PollHelper.reRender(event,"pic", dp);
                return;
            }
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
