package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
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
                    helper.reply("a poll {**" + dp.poll().getTitulo() + "**} foi fechada", message -> message.delete().submitAfter(5,TimeUnit.SECONDS));
                    return;
                }
                String str = args.stream().collect(Collectors.joining());
                LinkedList<String> votos = Regex.extractInside("\\[([^\\]])\\]",str);
                if(votos.size() == 0){
                    helper.reply("seu voto se perdeu!", message -> message.delete().submitAfter(5, TimeUnit.SECONDS));
                    return;
                }
                if(Constantes.POOL_votos.contains(votos.getFirst().toLowerCase())){
                    int index = Constantes.POOL_votos.indexOf(votos.getFirst().toLowerCase());
                    if(index < dp.poll().getOpcoes().size()){
                        if(dp.poll().hasUser(helper.getEvent().getAuthor().getId())){
                            int original = dp.poll().getOriginal(helper.getEvent().getAuthor().getId());
                            if(index == original){
                                helper.getMessage().delete().submit();
                                dp.poll().rem(index,helper.getId());
                                helper.reply("**" + helper.getEvent().getAuthor().getName() + "** seu voto foi retirado de com sucesso" , message -> message.delete().submitAfter(5, TimeUnit.SECONDS)) ;
                                Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                                return;
                            }
                            else{
                                helper.reply("**"+ helper.getEvent().getAuthor().getName()+"** você já votou [**"+ Constantes.POOL_votos.get(dp.poll().getOriginal(helper.getId()))+ "**] nessa poll!",message->
                                        message.delete().submitAfter(15, TimeUnit.SECONDS)
                                );
                            }
                        }
                        else{
                            helper.getMessage().delete().submit();
                            dp.poll().add(index, helper.getId());
                            helper.reply("**" + helper.getEvent().getAuthor().getName() + "** voto computado com sucesso ",
                                    message -> message.delete().submitAfter(5, TimeUnit.SECONDS));
                            Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                            return;
                        }
                    }
                }
                helper.reply("**" + helper.getEvent().getAuthor().getName() + "** pare de trolar,[" + votos.getFirst() + "] não é uma opção para essa poll.", message->
                        message.delete().submitAfter(15, TimeUnit.SECONDS)
                );

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
