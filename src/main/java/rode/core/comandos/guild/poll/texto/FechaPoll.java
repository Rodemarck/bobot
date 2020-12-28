package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class FechaPoll extends ComandoGuild {
    public FechaPoll() {
        super("fecha", Permission.ADMINISTRATOR, "fecha","close");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args,event, (titulo, opcoes, modelGuild, query) -> {
            if(modelGuild != null){
                var poll = modelGuild.getPoll(titulo);
                if(!poll.isAberto()){
                    event.reply("esta poll já está fechada", message -> message.delete().submitAfter(5, TimeUnit.SECONDS));
                    return;
                }
                poll.fecha();
                Memoria.guilds.updateOne(query, new Document("$set",modelGuild.toMongo()));
                event.reply("a poll {**" + titulo + "**} foi fechada", message -> message.addReaction(Constantes.EMOTES.get("check")).submit());
            }
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || PollHelper.livreDono(args, event);
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-fechar {titulo}** : fecha uma poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para fechar uma poll (enquete)
                
                **-fecha {titulo}**
                
                Aliases (comandos alternativos) : **fecha**, **close**
                """);

    }
}
