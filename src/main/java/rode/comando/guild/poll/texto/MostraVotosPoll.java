package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.util.ResourceBundle;

@EComandoPoll
public class MostraVotosPoll extends AbrePoll {
    public MostraVotosPoll() {
        super("show", null, false,"votos","votes","vpoll");
        setPath("votos");
    }

    @Override
    public void subscribeSlash(CommandData commandData, ResourceBundle bundle) {
        var subCommand = new SubcommandData(getCommand(),bundle.getString(getHelp()))
                .addOptions(new OptionData(OptionType.STRING,"titulo","titulo da poll desejada").setRequired(true));
        commandData.addSubcommands(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException, Exception {
        PollHelper.getPoll(args,hm,dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                EmbedBuilder eb = Constantes.builder();
                eb.setTitle(String.format(hm.getText("votos.exec.title"),dp.titulo()));
                poll.getVotes(eb, hm.getBundle());
                hm.reply(eb);
                return;
            }
            hm.reply(String.format(hm.getText("votos.exec.404"),dp.titulo()));
        });
    }
}
