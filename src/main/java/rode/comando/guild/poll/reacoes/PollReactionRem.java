package rode.comando.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.io.IOException;
import java.util.LinkedList;

public class PollReactionRem extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PollReactionRem.class);
    public PollReactionRem() {
        super("--pol--", null, "poll---");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        log.info("inicio");
        final String tipo = args.getFirst();
        PollHelper.getPollFromEmote(args,event, dp -> {
            log.info("callback");
            dp.poll().rem(dp.index(), event.getId());
            if(tipo.contains("poll"))
                event.getMessage().editMessage(dp.poll().me()).submit();
            else if(tipo.contains("pic")){
                final var emb = event.getMessage().getEmbeds().get(0);
                LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
                int i = Integer.parseInt(param.getFirst());
                event.getMessage().editMessage(dp.poll().visualiza(i)).submit();
            }
            Memoria.guilds.updateOne(dp.query(), new Document("$set",dp.guild().toMongo()));
            return;
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
