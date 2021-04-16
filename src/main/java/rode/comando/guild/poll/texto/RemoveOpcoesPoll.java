package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.model.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class RemoveOpcoesPoll extends AbrePoll {
    private static Logger log = LoggerFactory.getLogger(RemoveOpcoesPoll.class);
    public RemoveOpcoesPoll() {
        super("remove", Permission.ADMINISTRATOR,  false,"rempoll","remop","remoptions","remopções","removeop","removeoptions","removeopções");
        setPath("remp");
    }
    @Override
    public void subscribeSlash(CommandUpdateAction.CommandData commandData, ResourceBundle bundle) {
        var subCommand = new CommandUpdateAction.SubcommandData(getCommand(), bundle.getString(getHelp()))
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING,"titulo","titulo da poll desejada").setRequired(true))
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING,"opção","opção a ser removida").setRequired(true));
        commandData.addSubcommand(subCommand);
    }

    @Override
    public boolean free(String[] args, Helper.Mensagem hm) throws Exception {
        return super.free(args, hm) || PollHelper.livreDono(args, hm);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException, Exception {
        log.debug("inicio");
        PollHelper.getPoll(args, hm, dp-> {
            log.debug("callback");
            if(dp.opcoes() == null || dp.opcoes().isEmpty()){
                var eb = Constantes.builder();
                help(eb,hm.bundle());
                hm.reply(eb);
                return;
            }
            if(dp.guild() != null){
                log.debug("não nulo");
                Poll poll = dp.guild().getPoll(dp.titulo());
                poll.remOptions(dp.opcoes());
                Document d = dp.guild().toMongo();
                Memoria.guilds.updateOne(dp.query(), new Document("$set",d));
                hm.reply(poll.makeDefaultEmbed(hm.bundle()), message->
                    PollHelper.addReaction(message,poll.getOptions().size())
                );
                return;
            }
            hm.reply(String.format(hm.text("remp.exec"),dp.titulo()));
        });
    }
}
