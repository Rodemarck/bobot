package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;

public class RemoveOpcoesPoll extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(RemoveOpcoesPoll.class);
    public RemoveOpcoesPoll() {
        super("remop", Permission.ADMINISTRATOR,  "rempoll","remop","remoptions","remopções","removeop","removeoptions","removeopções");
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || PollHelper.livreDono(args, event);
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException, Exception {
        log.debug("inicio");
        PollHelper.getPoll(args, event, dp-> {
            log.debug("callback");
            if(dp.opcoes().isEmpty()){
                EmbedBuilder eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
                help(eb);
                event.reply(eb);
                return;
            }
            if(dp.guild() != null){
                log.debug("não nulo");
                Poll poll = dp.guild().getPoll(dp.titulo());
                poll.remOpcoes(dp.opcoes());
                Document d = dp.guild().toMongo();
                Memoria.guilds.updateOne(dp.query(), new Document("$set",d));
                event.reply(poll.me(), message->
                    PollHelper.addReaction(message,poll.getOpcoes().size())
                );
                return;
            }
            event.reply("poll **{" + dp.titulo() + "}** não encontrada");
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
