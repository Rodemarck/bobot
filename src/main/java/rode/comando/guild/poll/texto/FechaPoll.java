package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.ResourceBundle;

@EComandoPoll
public class FechaPoll extends AbrePoll {
    public FechaPoll() {
        super("close", Permission.ADMINISTRATOR, false,"fecha","close");
        setPath("fecha");
    }

    @Override
    public void subscribeSlash(CommandData commandData, ResourceBundle bundle) {
        var subCommand = new SubcommandData(getCommand(),bundle.getString(getHelp()))
                .addOptions(new OptionData(OptionType.STRING,"titulo","titulo da poll a ser apagada").setRequired(true));
        commandData.addSubcommands(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args,hm,dp -> {
            if(dp.guild() != null){
                if(!dp.poll().isOpen()){
                    hm.replyTemp(String.format(hm.getText("fecha.exec.already"),dp.titulo()));
                    return;
                }
                dp.poll().close();
                Memoria.update(dp);
                hm.reply(String.format(hm.getText("fecha.exec.close"),dp.titulo()), message -> message.addReaction(Constantes.emote("check")).queue());
            }
        });
    }

    @Override
    public boolean free(String[] args, Helper.Mensagem event) throws Exception {
        return super.free(args, event) || PollHelper.livreDono(args, event);
    }
}
