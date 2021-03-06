package rode.comando.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class PollReactionAdd extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PollReactionAdd.class);
    public PollReactionAdd() {
        super("++poll++", null, "poll+++");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao hr) throws IOException, Exception {
        final String tipo = args.getFirst();
        log.debug("inicio");
        PollHelper.getPollFromEmote(args, hr,dp -> {
            log.debug("callback");
            if(dp.poll().getOpcoes().size() > dp.index()){
                if(dp.poll().hasUser(hr.id())){
                    hr.mensagem().removeReaction(hr.emoji(), hr.getEvent().getUser()).queue(mm->{
                        int v = dp.poll().votouPara(hr.id());
                        if(dp.index() == v){
                            dp.poll().rem(dp.index(),hr.id());
                            PollHelper.removeVoto(hr,dp.index());
                            Memoria.update(dp);
                            PollHelper.reRender(hr,tipo,dp);
                            return;
                        }
                        PollHelper.jaVotou(hr,v);
                        dp.poll().rem(v,hr.id());
                        PollHelper.removeVoto(hr, v);
                        dp.poll().add(dp.index(),hr.id());
                        PollHelper.contaVoto(hr, dp.index());
                        PollHelper.reRender(hr,tipo,dp);
                        Memoria.update(dp);
                    });
                    return;
                }
                dp.poll().add(dp.index(), hr.id());
                Memoria.update(dp);
                PollHelper.contaVoto(hr, dp.index());
                PollHelper.reRender(hr,toString(),dp);
                return;
            }
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Reacao hr) throws IOException {
        return PollHelper.livreSiMesmo(args,hr);
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {

    }
}
