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
import java.util.stream.Collectors;

public class VotarPoll extends ComandoGuild {
    public VotarPoll() {
        super("votar", null, "v","votar", "vote");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem helper) throws Exception {
        PollHelper.getPoll(args, helper, dp -> {
            if(dp.guild() != null){
                if(!dp.poll().isAberto()){
                    helper.replyTemp("a poll {**" + dp.poll().getTitulo() + "**} foi fechada");
                    return;
                }
                String str = args.stream().collect(Collectors.joining());
                LinkedList<String> votos = Regex.extractInside("\\[([^\\]])\\]",str);
                if(votos.size() == 0){
                    helper.replyTemp("seu voto se perdeu!");
                    return;
                }
                if(Constantes.POOL_votos.contains(votos.getFirst().toLowerCase())){
                    int index = Constantes.POOL_votos.indexOf(votos.getFirst().toLowerCase());
                    if(index < dp.poll().getOpcoes().size()){
                        if(dp.poll().hasUser(helper.getEvent().getAuthor().getId())){
                            int original = dp.poll().votouPara(helper.getEvent().getAuthor().getId());
                            if(index == original){
                                helper.getMessage().delete().submit();
                                dp.poll().rem(index,helper.getId());
                                helper.replyTemp("**" + helper.getEvent().getAuthor().getName() + "** seu voto foi retirado de com sucesso"); ;
                                Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                                return;
                            }
                            else{
                                helper.replyTemp("**"+ helper.getEvent().getAuthor().getName()+"** você já votou [**"+ Constantes.POOL_votos.get(dp.poll().votouPara(helper.getId()))+ "**] nessa poll!");
                            }
                        }
                        else{
                            helper.getMessage().delete().submit();
                            dp.poll().add(index, helper.getId());
                            helper.replyTemp("**" + helper.getEvent().getAuthor().getName() + "** voto computado com sucesso ");
                            Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                            return;
                        }
                    }
                }
                helper.replyTemp("**" + helper.getEvent().getAuthor().getName() + "** pare de trolar,[" + votos.getFirst() + "] não é uma opção para essa poll.");

            }
        });
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-votar {titulo} [a]** : vota em uma poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para vota em uma poll (enquete).
                
                **-votar {titulo} [a]**
                
                Aliases (comandos alternativos) : **v**, **votar**, **vote**.
                Computa um voto para uma poll, sendo permitido apenas um voto por pessoa.
                Caso queria mudar o voto, vote na mesma opção
                """);

    }
}
