package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@EComandoPoll
public class AdicionaOpcoesPoll extends ComandoGuild {
    public AdicionaOpcoesPoll() {
        super("addop", null, "addpoll","addop","addoptions","addopções","addoveop","addoveoptions","addoveopções");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws IOException, Exception {
        PollHelper.getPoll(args, hm, dp -> {
            if(dp.guild() != null){
                if(dp.opcoes().isEmpty()){
                    EmbedBuilder eb = Constantes.builder();
                    eb.setTitle(hm.text("opcao.exec.empty"));
                    help(eb,hm.bundle());
                    hm.reply(eb);
                    return;
                }
                for(String s:dp.opcoes())
                    if(Pattern.matches(".*<@!?\\d+>.*",s)){
                        hm.reply(hm.text("opcao.exec.mention"));
                        return;
                    }
                Poll poll = dp.guild().getPoll(dp.titulo());
                if(!poll.aberto()){
                    hm.replyTemp(String.format(hm.text("opcao.exec.close"), poll.titulo()));
                    return;
                }

                poll.addOpcoes(dp.opcoes());
                Memoria.guilds.updateOne(dp.query(),new Document("$set",dp.guild().toMongo()));
                hm.reply(poll.me(hm.bundle()),message->PollHelper.addReaction(message, poll.opcoes().size()));
                return;
            }
            hm.reply(String.format(hm.text("opcao.exec.404"),dp.titulo()));
        });
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("opcao.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("opcao.help.ex"));
        }
}
