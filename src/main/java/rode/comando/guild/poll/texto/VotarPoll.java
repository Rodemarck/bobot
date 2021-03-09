package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VotarPoll extends ComandoGuild {
    public VotarPoll() {
        super("votar", null, "v","votar", "vote");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args, hm, dp -> {
            if(dp.guild() != null){
                if(!dp.poll().isAberto()){
                    hm.replyTemp(hm.text("votarP.exec.close"));
                    return;
                }
                String str = args.stream().collect(Collectors.joining());
                LinkedList<String> votos = Regex.extractInside("\\[([^\\]])\\]",str);
                if(votos.size() == 0){
                    hm.replyTemp(hm.text("votarP.exec.lost"));
                    return;
                }
                if(Constantes.POOL_votos.contains(votos.getFirst().toLowerCase())){
                    int index = Constantes.POOL_votos.indexOf(votos.getFirst().toLowerCase());
                    if(index < dp.poll().getOpcoes().size()){
                        if(dp.poll().hasUser(hm.getEvent().getAuthor().getId())){
                            int original = dp.poll().votouPara(hm.getEvent().getAuthor().getId());
                            if(index == original){
                                hm.mensagem().delete().submit();
                                dp.poll().rem(index,hm.id());
                                hm.replyTemp(String.format(hm.text("votarP.exec.remove"),hm.getEvent().getAuthor().getName()));
                                Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                                return;
                            }
                            else{
                                hm.replyTemp(String.format(hm.text("votarP.exec.already"), hm.getEvent().getAuthor().getName(), Constantes.POOL_votos.get(dp.poll().votouPara(hm.id()))));
                            }
                        }
                        else{
                            hm.mensagem().delete().submit();
                            dp.poll().add(index, hm.id());
                            hm.replyTemp(String.format(hm.text("votarP.exec.vote"), hm.getEvent().getAuthor().getName()));
                            Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                            return;
                        }
                    }
                }
                hm.replyTemp(String.format("votarP.exec.troll", hm.getEvent().getAuthor().getName(), votos.getFirst() ));

            }
        });
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("votarP.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me,ResourceBundle rb) {
        me.appendDescription(rb.getString("votarP.help.ex"));

    }
}
