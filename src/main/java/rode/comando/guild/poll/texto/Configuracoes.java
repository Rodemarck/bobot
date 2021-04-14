package rode.comando.guild.poll.texto;

import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;

import java.util.LinkedList;

@EComandoPoll
public class Configuracoes extends ComandoGuild {
    public Configuracoes() {
        super("config", null, "config","configuracao","configuração","def","definicoes","definições","configuracoes","configurações","settings");
    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args, hm,dp -> {
            if(dp.guild() != null){
                var poll = dp.guild().getPoll(dp.titulo());
                hm.reply(poll.makeSettingsEmbed(hm.bundle()), message -> {
                    message.editMessage("config").queue();
                });
            }
        });
    }
}
