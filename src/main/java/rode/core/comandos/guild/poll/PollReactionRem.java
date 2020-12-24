package rode.core.comandos.guild.poll;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.model.Poll;
import rode.utilitarios.Memoria;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.util.LinkedList;

public class PollReactionRem extends ComandoGuildReacoes {
    public PollReactionRem() {
        super("--pol--", null, "poll---");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        String titulo = event.getMessage().getEmbeds().get(0).getTitle();
        args.add('{' + titulo + '}');
        PollHelper.getPoll(args,event,(titulo1, opcoes, guild, query) -> {
            if(guild != null){
                Poll poll = guild.getPoll(titulo);
                int index = Constantes.POOL_EMOTES.indexOf(event.emoji());
                poll.rem(index, event.getId());
                event.getMessage().editMessage(poll.me()).queue();
                Memoria.guilds.updateOne(query, new Document("$set",guild.toMongo()));
                return;
            }
        });
    }

    @Override
    public void help(EmbedBuilder me) {

    }
}
