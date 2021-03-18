package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class MostraVotosPoll extends ComandoGuild {
    public MostraVotosPoll() {
        super("votos", null, "votos","votes","vpoll");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws IOException, Exception {
        PollHelper.getPoll(args,hm,dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                EmbedBuilder eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
                eb.setTitle(String.format(hm.text("votos.exec.title"),dp.titulo()));
                poll.getVotos(eb, hm.bundle());
                hm.reply(eb);
                return;
            }
            hm.reply(String.format(hm.text("votos.exec.404"),dp.titulo()));
        });
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("votos.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("votos.help.ex"));
    }
}
