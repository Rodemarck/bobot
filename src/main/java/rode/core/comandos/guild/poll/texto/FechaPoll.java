package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;

import java.util.LinkedList;

public class FechaPoll extends ComandoGuild {
    public FechaPoll() {
        super("fecha", null, "fecha","close");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args,event, (titulo, opcoes, modelGuild, query) -> {
            if(modelGuild != null){
                var poll = modelGuild.getPoll(titulo);
                if(!poll.isAberto()){
                    event.reply("esta poll já está fechada");
                    return;
                }
                poll.fecha();
                Memoria.guilds.updateOne(query, new Document("$set",modelGuild.toMongo()));
                event.reply("a poll {**" + titulo + "**} foi fechada", message -> message.addReaction("\u2705").submit());
            }
        });
    }

    @Override
    public void help(EmbedBuilder me) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me) {

    }
}
