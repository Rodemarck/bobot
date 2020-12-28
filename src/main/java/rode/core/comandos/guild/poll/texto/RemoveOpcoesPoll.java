package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;

public class RemoveOpcoesPoll extends ComandoGuild {
    public RemoveOpcoesPoll() {
        super("remop", Permission.ADMINISTRATOR,  "rempoll","remop","remoptions","remopções","removeop","removeoptions","removeopções");
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || PollHelper.livreDono(args, event);
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException, Exception {
        PollHelper.getPoll(args, event, (titulo, opcoes, guild, query) -> {
            if(opcoes.isEmpty()){
                EmbedBuilder eb = new EmbedBuilder();
                help(eb);
                event.reply(eb);
                return;
            }
            if(guild != null){
                Poll poll = guild.getPoll(titulo);
                poll.remOpcoes(opcoes);
                final Document d = guild.toMongo();
                Memoria.guilds.updateOne(query, new Document("$set",d));
                event.reply(poll.me(), message->
                    PollHelper.addReaction(message,poll.getOpcoes().size())
                );
                return;
            }
            event.reply("poll **{" + titulo + "}** não encontrada");
        });
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-remop {titulo} [opção 1] [opção 1]** : remove opções já existente da poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para remover opções de uma poll (enquete), se a poll tiver essas opções.
                
                **-remop {título} [opção 1] [opção 2]**
                
                Aliases (comandos alternativos) : **rempoll**, **remop**, **remoptions**, **remopções**,**removeop**, **removeoptions**, **removeopções**.
                """);
    }
}
