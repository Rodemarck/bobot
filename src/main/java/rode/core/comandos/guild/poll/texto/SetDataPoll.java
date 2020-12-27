package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.core.UseComande;
import rode.model.Poll;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UseComande
public class SetDataPoll  extends ComandoGuild {
    public SetDataPoll() {
        super("data", Permission.ADMINISTRATOR, "data","date");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args,event,(titulo, opcoes, modelGuild, query) -> {
            if(modelGuild != null){
                Poll poll = modelGuild.getPoll(titulo);
                String s = args.stream().collect(Collectors.joining()).replace("\\{([^\\}]+)\\}|\\[([^\\]]+)\\]","");
                final LocalDateTime data = LocalDateTime.now();
                LocalDateTime controle = data.plusMinutes(0);
                LinkedList<String> param = Regex.extract("\\d+(d((ia|ay)s?)?)",s);
                if(param.size() > 0){
                    param.forEach(p->{
                        if(Pattern.compile("\\d+").matcher(p).find())
                            data.plusDays(Long.parseLong(Pattern.compile("\\d+").matcher(p).group()));
                    });
                }
                param = Regex.extract("\\d+((w(eek)?|s(emana)?)s?)",s);
                if(param.size() > 0){
                    param.forEach(p->{
                        if(Pattern.compile("\\d+").matcher(p).find())
                            data.plusWeeks(Long.parseLong(Pattern.compile("\\d+").matcher(p).group()));
                    });
                }
                param = Regex.extract("\\d+(h(o(ra|ur))?s?)",s);
                if(param.size() > 0){
                    param.forEach(p->{
                        if(Pattern.compile("\\d+").matcher(p).find())
                            data.plusHours(Long.parseLong(Pattern.compile("\\d+").matcher(p).group()));
                    });
                }
                if(data.equals(controle)){
                    event.reply("não consegui entender o comando dirieto");
                    return;
                }

                if(ChronoUnit.MONTHS.between(controle,data) > 2){
                    poll.setDataLimite(controle.plusMonths(2));
                    event.reply("tempo máximo é de 2 meses");
                }else
                    poll.setDataLimite(data);
                Memoria.guilds.updateOne(query, new Document("$set", modelGuild.toMongo()));
            }
        });
    }

    @Override
    public void help(EmbedBuilder me) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me) {

    }
}
