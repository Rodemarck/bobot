package rode.core.comandos.guild.poll;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.model.Poll;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;

public class DeletaPoll extends ComandoGuild {
    public DeletaPoll() {
        super("deleta", Permission.ADMINISTRATOR, "delpoll","delet","deleta");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException, Exception {
        PollHelper.getPoll(args, event,(titulo, opcoes, guild, query)->{
            if(guild != null){
                Poll poll = guild.getPoll(titulo);
                guild.getPolls().remove(poll);
                Memoria.guilds.updateOne(query,new Document("$set",guild.toMongo()));
                event.reply("a poll {**" + titulo + "**} foi deletada", message -> message.addReaction("\u2705").submit());
                return;
            }
            event.reply("a poll {**" + titulo + "**} não foi encontrada");
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || PollHelper.livre(args, event);
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-deleta {titulo}**: deleta uma poll especifica.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("Comando para deletar uma poll (enquete) aberta neste servidor.\n\n");
        me.appendDescription("**-deleta {titulo}***.\n\n");
        me.appendDescription("Aliases (comandos alternativos) : **delpoll**, **delet**, **deleta**\n\n");
    }
}
