package rode.comando.guild.poll.reacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ComandoGuildReacoes;
import rode.utilitarios.Memoria;

import java.io.IOException;

public class PollReactionAdd extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PollReactionAdd.class);
    public PollReactionAdd() {
        super("++poll++", null, "poll+++");
    }

    @Override
    public void executa(String[] args, Helper.Reacao hr) throws IOException, Exception {
        final String tipo = args[0];
        log.debug("inicio");
        PollHelper.getPollFromEmote(args, hr,dp -> {
            log.debug("callback");
            if(dp.poll().getOptions().size() > dp.index()){
                if(dp.poll().hasUser(hr.id())){
                    hr.mensagem().removeReaction(hr.emoji(), hr.getEvent().getUser()).queue(mm->{
                        int v = dp.poll().votesTo(hr.id());
                        if(dp.index() == v){
                            dp.poll().remove(dp.index(),hr.id());
                            PollHelper.removeVoto(hr,dp.index());
                            Memoria.update(dp);
                            PollHelper.reRender(hr,tipo,dp);
                            return;
                        }
                        PollHelper.jaVotou(hr,v);
                        dp.poll().remove(v,hr.id());
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
    public boolean livre(String[] args, Helper.Reacao hr) throws IOException {
        return PollHelper.livreSiMesmo(args,hr);
    }
}
