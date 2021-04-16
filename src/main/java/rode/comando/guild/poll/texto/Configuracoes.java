package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ComandoGuild;

import java.util.ResourceBundle;

@EComandoPoll
public class Configuracoes extends AbrePoll {
    public Configuracoes() {
        super("config", null, false,"config","configuracao","configuração","def","definicoes","definições","configuracoes","configurações","settings");
        setPath("config");
    }

    @Override
    public void subscribeSlash(CommandUpdateAction.CommandData commandData, ResourceBundle bundle) {
        commandData.addSubcommand(new CommandUpdateAction.SubcommandData(getCommand(),bundle.getString(getHelp())));
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
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
