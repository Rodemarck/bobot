package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.model.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;

@EComandoPoll
public class RemoveOpcoesPoll extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(RemoveOpcoesPoll.class);
    public RemoveOpcoesPoll() {
        super("remp", Permission.ADMINISTRATOR,  "rempoll","remop","remoptions","remopções","removeop","removeoptions","removeopções");
    }

    @Override
    public boolean free(String[] args, Helper.Mensagem hm) throws Exception {
        return super.free(args, hm) || PollHelper.livreDono(args, hm);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException, Exception {
        log.debug("inicio");
        PollHelper.getPoll(args, hm, dp-> {
            log.debug("callback");
            if(dp.opcoes().isEmpty()){
                var eb = Constantes.builder();
                help(eb,hm.bundle());
                hm.reply(eb);
                return;
            }
            if(dp.guild() != null){
                log.debug("não nulo");
                Poll poll = dp.guild().getPoll(dp.titulo());
                poll.remOptions(dp.opcoes());
                Document d = dp.guild().toMongo();
                Memoria.guilds.updateOne(dp.query(), new Document("$set",d));
                hm.reply(poll.makeDefaultEmbed(hm.bundle()), message->
                    PollHelper.addReaction(message,poll.getOptions().size())
                );
                return;
            }
            hm.reply(String.format(hm.text("remp.exec"),dp.titulo()));
        });
    }
}
