package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.model.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class DeletaPoll extends AbrePoll {
    public DeletaPoll() {
        super("delete", Permission.MANAGE_CHANNEL, false,"delpoll","delete","deleta");
        setPath("del");
    }

    @Override
    public void subscribeSlash(CommandUpdateAction.CommandData commandData, ResourceBundle bundle) {
        var subCommand = new CommandUpdateAction.SubcommandData(getCommand(), bundle.getString(getHelp()))
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING,"titulo", "titulo da poll a ser deletada").setRequired(true));
        commandData.addSubcommand(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException, Exception {

        //EventLoop2.addTexto(new MensagemTexto(hm.getEvent().getChannel(), null,"tempo expirado",System.currentTimeMillis()+20000,50,Permission.ADMINISTRATOR));

        PollHelper.getPoll(args, hm,dp->{
            if(dp.guild() != null){
                var poll = dp.guild().getPoll(dp.titulo());
                dp.guild().getPolls().remove(poll);
                Memoria.guilds.updateOne(dp.query(),new Document("$set",dp.guild().toMongo()));
                hm.reply(String.format(hm.text("del.exec.delete"), dp.titulo()), message -> message.addReaction(Constantes.emote("check")).queue());
                return;
            }
            hm.reply(String.format(hm.text("del.exec.404"), dp.titulo()));
        });
    }

    @Override
    public boolean free(String[] args, Helper.Mensagem event) throws Exception {
        return super.free(args, event) || PollHelper.livreDono(args, event);
    }
}
