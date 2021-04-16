package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import rode.core.Anotacoes.EComandoPoll;
import rode.model.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class MostraVotosPoll extends AbrePoll {
    public MostraVotosPoll() {
        super("show", null, false,"votos","votes","vpoll");
        setPath("votos");
    }

    @Override
    public void subscribeSlash(CommandUpdateAction.CommandData commandData, ResourceBundle bundle) {
        var subCommand = new CommandUpdateAction.SubcommandData(getCommand(),bundle.getString(getHelp()))
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING,"titulo","titulo da poll desejada").setRequired(true));
        commandData.addSubcommand(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException, Exception {
        PollHelper.getPoll(args,hm,dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                EmbedBuilder eb = Constantes.builder();
                eb.setTitle(String.format(hm.text("votos.exec.title"),dp.titulo()));
                poll.getVotes(eb, hm.bundle());
                hm.reply(eb);
                return;
            }
            hm.reply(String.format(hm.text("votos.exec.404"),dp.titulo()));
        });
    }
}
