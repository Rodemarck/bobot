package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Helper;
import rode.core.PollHelper;

import java.util.ResourceBundle;

@EComandoPoll
public class Configuracoes extends AbrePoll {
    public Configuracoes() {
        super("config", null, false,"config","configuracao","configuração","def","definicoes","definições","configuracoes","configurações","settings");
        setPath("config");
    }

    @Override
    public void subscribeSlash(CommandData commandData, ResourceBundle bundle) {
        commandData.addSubcommands(new SubcommandData(getCommand(),bundle.getString(getHelp())));
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args, hm,dp -> {
            if(dp.guild() != null){
                var poll = dp.guild().getPoll(dp.titulo());
                hm.reply(poll.makeSettingsEmbed(hm.getBundle()), message -> {
                    message.editMessage("config").queue();
                });
            }
        });
    }
}
