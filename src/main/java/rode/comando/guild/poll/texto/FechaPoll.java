package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import rode.core.Anotacoes.EComandoPoll;
import rode.model.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class FechaPoll extends AbrePoll {
    public FechaPoll() {
        super("close", Permission.ADMINISTRATOR, false,"fecha","close");
        setPath("fecha");
    }

    @Override
    public void subscribeSlash(CommandUpdateAction.CommandData commandData, ResourceBundle bundle) {
        var subCommand = new CommandUpdateAction.SubcommandData(getCommand(),bundle.getString(getHelp()))
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING,"titulo","titulo da poll a ser apagada").setRequired(true));
        commandData.addSubcommand(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args,hm,dp -> {
            if(dp.guild() != null){
                if(!dp.poll().isOpen()){
                    hm.replyTemp(String.format(hm.text("fecha.exec.already"),dp.titulo()));
                    return;
                }
                dp.poll().close();
                Memoria.update(dp);
                hm.reply(String.format(hm.text("fecha.exec.close"),dp.titulo()), message -> message.addReaction(Constantes.emote("check")).queue());
            }
        });
    }

    @Override
    public boolean free(String[] args, Helper.Mensagem event) throws Exception {
        return super.free(args, event) || PollHelper.livreDono(args, event);
    }
}
