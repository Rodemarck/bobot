package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@EComandoPoll
public class AdicionaOpcoesPoll extends AbrePoll {
    public AdicionaOpcoesPoll() {
        super("add", null, true,"addpoll","addop","addoptions","addopções","addoveop","addoveoptions","addoveopções");
        setPath("opcao");
    }

    @Override
    public void subscribeSlash(CommandUpdateAction.CommandData commandData, ResourceBundle bundle) {
        var subCommand = new CommandUpdateAction.SubcommandData(getCommand(), bundle.getString(getHelp()))
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING,"titulo","titulo da poll desejada").setRequired(true))
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING,"opcao","opção a ser adcional").setRequired(true));
        commandData.addSubcommand(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException, Exception {
        PollHelper.getPoll(args, hm, dp -> {
            if(dp.guild() != null){
                if(dp.opcoes().isEmpty()){
                    EmbedBuilder eb = Constantes.builder();
                    eb.setTitle(hm.text("opcao.exec.empty"));
                    help(eb,hm.bundle());
                    hm.reply(eb);
                    return;
                }
                for(String s:dp.opcoes())
                    if(Pattern.matches(".*<@!?\\d+>.*",s)){
                        hm.reply(hm.text("opcao.exec.mention"));
                        return;
                    }
                Poll poll = dp.guild().getPoll(dp.titulo());
                if(!poll.isOpen()){
                    hm.replyTemp(hm.text("opcao.exec.close").formatted(poll.getTitle()));
                    return;
                }

                poll.addOptions(dp.opcoes());
                Memoria.guilds.updateOne(dp.query(),new Document("$set",dp.guild().toMongo()));
                hm.reply(poll.makeDefaultEmbed(hm.bundle()), message->PollHelper.addReaction(message, poll.getOptions().size()));
                return;
            }
            hm.reply(hm.text("opcao.exec.404").formatted(dp.titulo()));
        });
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("opcao.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("opcao.help.ex"));
    }
}
