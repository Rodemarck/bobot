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
    public static void executa(String[] args, Helper.Reacao hr, String discriminator) throws IOException, Exception {
        int aux;
        log.debug("verificando seta");
        if(hr.emoji().equals(Constantes.emote("esquerda")))
            aux = -1;
        else if(hr.emoji().equals(Constantes.emote("direita")))
            aux = 1;
        else if(hr.emoji().equals(Constantes.emote("check")))
            aux = 0;
        else{

            Executador.COMANDOS_REACOES_GUILD.get(
                    Executador.NOME_COMANDOS_REACOES_GUILD.get("poll" + discriminator)
            ).executa(args,hr);
            return;
        }
        log.debug("não é troca");
        final int n = aux;
        PollHelper.getPollFromEmote(args, hr, dp->{
            var emb = hr.mensagem().getEmbeds().get(0);
            LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
            int index = Integer.parseInt(param.getFirst());
            if(n == 0){
                if(dp.poll().hasUser(hr.id())){
                    int i = dp.poll().votesTo(hr.id());
                    if(i == index){
                        dp.poll().remove(index,hr.id());
                        Memoria.update(dp);
                        PollHelper.removeVoto(hr, index);
                        PollHelper.reRender(hr,"pic", dp);
                        return;
                    }
                    PollHelper.jaVotou(hr,i);
                    return;
                }
                dp.poll().add(index, hr.id());
                Memoria.update(dp);
                PollHelper.contaVoto(hr, index);
                PollHelper.reRender(hr,"pic", dp);
                return;
            }
            index = (index + n);
            if(index >= dp.poll().getOptions().size())
                index = 0;
            else if (index < 0)
                index = dp.poll().getOptions().size() - 1;
            hr.mensagem().editMessage(dp.poll().makeDisplayEmbed(index,hr.bundle())).queue();
        });
        final var emb = hr.mensagem().getEmbeds().get(0);
    }
}
