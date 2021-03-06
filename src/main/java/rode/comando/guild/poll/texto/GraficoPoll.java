package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Grafico;

import java.io.File;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class GraficoPoll extends ComandoGuild {
    public GraficoPoll() {
        super("grafico", null, "grafico","grpah","gpoll");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args, hm, dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                File arq = Grafico.poll(poll, hm.getEvent().getGuild());
                hm.reply(arq);
                return;
            }
            hm.reply(String.format(hm.text("grafico.exec"),dp.titulo()));
        });
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("grafico.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        help(me,rb);
    }
}
