package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.ResourceBundle;

@EComandoPoll
public class DeletaPoll extends AbrePoll {
    public DeletaPoll() {
        super("delete", Permission.MANAGE_CHANNEL, false,"delpoll","delete","deleta");
        setPath("del");
    }

    @Override
    public void subscribeSlash(CommandData commandData, ResourceBundle bundle) {
        var subCommand = new SubcommandData(getCommand(), bundle.getString(getHelp()))
                .addOptions(new OptionData(OptionType.STRING,"titulo", "titulo da poll a ser deletada").setRequired(true));
        commandData.addSubcommands(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException, Exception {

        //EventLoop2.addTexto(new MensagemTexto(hm.getEvent().getChannel(), null,"tempo expirado",System.currentTimeMillis()+20000,50,Permission.ADMINISTRATOR));

        PollHelper.getPoll(args, hm,dp->{
            if(dp.guild() != null){
                var poll = dp.guild().getPoll(dp.titulo());
                dp.guild().getPolls().remove(poll);
                Memoria.guilds.updateOne(dp.query(),new Document("$set",dp.guild().toMongo()));
                hm.reply(String.format(hm.getText("del.exec.delete"), dp.titulo()), message -> message.addReaction(Constantes.emote("check")).queue());
                return;
            }
            hm.reply(String.format(hm.getText("del.exec.404"), dp.titulo()));
        });
    }

    @Override
    public boolean free(String[] args, Helper.Mensagem event) throws Exception {
        return super.free(args, event) || PollHelper.livreDono(args, event);
    }
}
