package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.model.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
@EComandoPoll
public class VotarPoll extends ComandoGuild {
    public VotarPoll() {
        super("vote", null, "v","votar", "vote");
        setPath("votarP");
    }
    @Override
    public void subscribeSlash(CommandData commandData, ResourceBundle bundle) {
        var subCommand = new SubcommandData(getCommand(), bundle.getString(getHelp()))
                .addOptions(new OptionData(OptionType.STRING,"titulo","titulo da poll desejada").setRequired(true),
                        new OptionData(OptionType.STRING,"voto","seu voto para a poll").setRequired(true));
        commandData.addSubcommands(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args, hm, dp -> {
            if(dp.guild() != null){
                if(!dp.poll().isOpen()){
                    hm.replyTemp(hm.getText("votarP.exec.close"));
                    return;
                }
                String str = Arrays.stream(args).sequential().collect(Collectors.joining());
                LinkedList<String> votos = Regex.extractInside("\\[([^\\]])\\]",str);
                if(votos.size() == 0){
                    hm.replyTemp(hm.getText("votarP.exec.lost"));
                    return;
                }
                if(Constantes.LETRAS.contains(votos.getFirst().toLowerCase())){
                    int index = Constantes.LETRAS.indexOf(votos.getFirst().toLowerCase());
                    if(index < dp.poll().getOpcoes().size()){
                        if(dp.poll().hasUser(hm.getEvent().getAuthor().getId())){
                            int original = dp.poll().votesTo(hm.getEvent().getAuthor().getId());
                            if(index == original){
                                hm.getMensagem().delete().queue();
                                dp.poll().remove(index,hm.getId());
                                hm.replyTemp(String.format(hm.getText("votarP.exec.remove"),hm.getEvent().getAuthor().getName()));
                                Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                                return;
                            }
                            else{
                                hm.replyTemp(String.format(hm.getText("votarP.exec.already"), hm.getEvent().getAuthor().getName(), Constantes.LETRAS.get(dp.poll().votesTo(hm.getId()))));
                            }
                        }
                        else{
                            hm.getMensagem().delete().queue();
                            dp.poll().add(index, hm.getId());
                            hm.replyTemp(String.format(hm.getText("votarP.exec.vote"), hm.getEvent().getAuthor().getName()));
                            Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                            return;
                        }
                    }
                }
                hm.replyTemp(String.format("votarP.exec.troll", hm.getEvent().getAuthor().getName(), votos.getFirst() ));

            }
        });
    }
}
