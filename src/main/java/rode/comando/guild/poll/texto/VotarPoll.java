package rode.comando.guild.poll.texto;

import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.util.LinkedList;
import java.util.stream.Collectors;
@EComandoPoll
public class VotarPoll extends ComandoGuild {
    public VotarPoll() {
        super("votarP", null, "v","votar", "vote");
    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args, hm, dp -> {
            if(dp.guild() != null){
                if(!dp.poll().isOpen()){
                    hm.replyTemp(hm.text("votarP.exec.close"));
                    return;
                }
                String str = args.stream().collect(Collectors.joining());
                LinkedList<String> votos = Regex.extractInside("\\[([^\\]])\\]",str);
                if(votos.size() == 0){
                    hm.replyTemp(hm.text("votarP.exec.lost"));
                    return;
                }
                if(Constantes.LETRAS.contains(votos.getFirst().toLowerCase())){
                    int index = Constantes.LETRAS.indexOf(votos.getFirst().toLowerCase());
                    if(index < dp.poll().getOptions().size()){
                        if(dp.poll().hasUser(hm.getEvent().getAuthor().getId())){
                            int original = dp.poll().votesTo(hm.getEvent().getAuthor().getId());
                            if(index == original){
                                hm.mensagem().delete().queue();
                                dp.poll().remove(index,hm.id());
                                hm.replyTemp(String.format(hm.text("votarP.exec.remove"),hm.getEvent().getAuthor().getName()));
                                Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                                return;
                            }
                            else{
                                hm.replyTemp(String.format(hm.text("votarP.exec.already"), hm.getEvent().getAuthor().getName(), Constantes.LETRAS.get(dp.poll().votesTo(hm.id()))));
                            }
                        }
                        else{
                            hm.mensagem().delete().queue();
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
}
