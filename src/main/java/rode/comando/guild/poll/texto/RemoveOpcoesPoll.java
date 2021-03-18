package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class RemoveOpcoesPoll extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(RemoveOpcoesPoll.class);
    public RemoveOpcoesPoll() {
        super("remop", Permission.ADMINISTRATOR,  "rempoll","remop","remoptions","remopções","removeop","removeoptions","removeopções");
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        return super.livre(args, hm) || PollHelper.livreDono(args, hm);
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws IOException, Exception {
        log.debug("inicio");
        PollHelper.getPoll(args, hm, dp-> {
            log.debug("callback");
            if(dp.opcoes().isEmpty()){
                EmbedBuilder eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
                help(eb,hm.bundle());
                hm.reply(eb);
                return;
            }
            if(dp.guild() != null){
                log.debug("não nulo");
                Poll poll = dp.guild().getPoll(dp.titulo());
                poll.remOpcoes(dp.opcoes());
                Document d = dp.guild().toMongo();
                Memoria.guilds.updateOne(dp.query(), new Document("$set",d));
                hm.reply(poll.me(hm.bundle()), message->
                    PollHelper.addReaction(message,poll.opcoes().size())
                );
                return;
            }
            hm.reply(String.format(hm.text("remp.exec"),dp.titulo()));
        });
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("remp.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("remp.help.ex"));
    }
}
