package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;

import java.util.LinkedList;
import java.util.ResourceBundle;

public class Configuracoes extends ComandoGuild {
    public Configuracoes() {
        super("configuracao", null, "config","configuracao","configuração","def","definicoes","definições","configuracoes","configurações","settings");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args, hm,dp -> {
            if(dp.guild() != null){
                var poll = dp.guild().getPoll(dp.titulo());
                hm.reply(poll.config(hm.bundle()), message -> {
                    message.editMessage("config").submit();
                    return null;
                });
            }
        });
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("config.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("config.help.ex"));
    }
}
