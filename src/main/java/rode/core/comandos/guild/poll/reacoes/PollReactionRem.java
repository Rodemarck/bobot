package rode.core.comandos.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;
import rode.utilitarios.Constantes;
import rode.utilitarios.Regex;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class PollReactionRem extends ComandoGuildReacoes {
    public PollReactionRem() {
        super("--pol--", null, "poll---");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        final String tipo = args.getFirst();
        String titulo = event.getMessage().getEmbeds().get(0).getTitle();
        args.add('{' + titulo + '}');
        PollHelper.getPoll(args,event,(titulo1, opcoes, guild, query) -> {
            if(guild != null){
                Poll poll = guild.getPoll(titulo);
                if(!poll.isAberto()){
                    event.reply("a poll {**" + poll.getTitulo() + "**} foi fechada", message -> message.delete().submitAfter(5, TimeUnit.SECONDS));
                    return;
                }
                int index = Constantes.POOL_EMOTES.indexOf(event.emoji());
                poll.rem(index, event.getId());
                if(tipo.contains("poll"))
                    event.getMessage().editMessage(poll.me()).submit();
                else if(tipo.contains("pic")){
                    final var emb = event.getMessage().getEmbeds().get(0);
                    LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
                    int i = Integer.parseInt(param.getFirst());
                    event.getMessage().editMessage(poll.visualiza(i)).submit();
                }
                Memoria.guilds.updateOne(query, new Document("$set",guild.toMongo()));
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
