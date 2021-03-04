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

public class PollReactionAdd extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PollReactionAdd.class);
    public PollReactionAdd() {
        super("++poll++", null, "poll+++");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        final String tipo = args.getFirst();
        log.debug("inicio");
        PollHelper.getPollFromEmote(args, event,dp -> {
            log.debug("callback");
            if(dp.poll().getOpcoes().size() > dp.index()){
                if(dp.poll().hasUser(event.getId())){
                    event.getMessage().removeReaction(event.emoji(), event.getEvent().getUser()).queue(mm->{
                        int v = dp.poll().votouPara(event.getId());
                        if(dp.index() == v){
                            dp.poll().rem(dp.index(),event.getId());
                            PollHelper.removeVoto(event,dp.index());
                            Memoria.update(dp);
                            PollHelper.reRender(event,tipo,dp);
                            return;
                        }
                        PollHelper.jaVotou(event,v);
                        dp.poll().rem(v,event.getId());
                        PollHelper.removeVoto(event, v);
                        dp.poll().add(dp.index(),event.getId());
                        PollHelper.contaVoto(event, dp.index());
                        PollHelper.reRender(event,tipo,dp);
                        Memoria.update(dp);
                    });
                    return;
                }
                dp.poll().add(dp.index(), event.getId());
                Memoria.update(dp);
                PollHelper.contaVoto(event, dp.index());
                PollHelper.reRender(event,toString(),dp);
                return;
            }
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args,event);
    }

    @Override
    public void help(EmbedBuilder me) {

    }
}
