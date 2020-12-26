package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class AdicionaOpcoesPoll extends ComandoGuild {
    public AdicionaOpcoesPoll() {
        super("addop", null, "addpoll","addop","addoptions","addopções","addoveop","addoveoptions","addoveopções");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException, Exception {
        PollHelper.getPoll(args, event, (titulo, opcoes, guild, query) -> {
            if(guild != null){
                if(opcoes.isEmpty()){
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("cade as novas opções??");
                    help(eb);
                    event.reply(eb);
                    return;
                }
                Poll poll = guild.getPoll(titulo);
                if(!poll.isAberto()){
                    event.reply("a poll {**" + poll.getTitulo() + "**} foi fechada", message -> message.delete().submitAfter(5, TimeUnit.SECONDS));
                    return;
                }
                poll.addOpcoes(opcoes);
                Memoria.guilds.updateOne(query,new Document("$set",guild.toMongo()));
                event.reply(poll.me(),message->PollHelper.addReaction(message, poll.getOpcoes().size()));
                return;
            }
            event.reply("a poll {**" + titulo + "**} não foi encontrada" );
        });
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-addop {titulo} [opção 1] [opção 2]...** : adiciona opções novas a poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("Comando para adiciona opções de uma poll (enquete), se a poll não tiver essas opções.\n\n");
        me.appendDescription("**-addop {título} [opção 1] [opção 2]**\n\n");
        me.appendDescription("Aliases (comandos alternativos) : **addpoll**, **addop**, **addoptions**, **addopções**,**addoveop**, **addoveoptions**, **addoveopções**.\n\n");
    }
}
