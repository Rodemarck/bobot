package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SetDataPoll  extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(SetDataPoll.class);

    public SetDataPoll() {
        super("data", Permission.ADMINISTRATOR, "data","date");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args,event,dp -> {
            if(dp.guild() != null){
                log.info("ue...");
                Poll poll = dp.guild().getPoll(dp.titulo());
                String s = args.stream().collect(Collectors.joining()).replace("\\{([^\\}]+)\\}|\\[([^\\]]+)\\]","");
                LinkedList<LocalDateTime> times = new LinkedList<>();
                times.addFirst(LocalDateTime.now());
                LocalDateTime controle = times.getFirst().plusMinutes(0);



                passaTempo("\\d+(d((ia|ay)s?)?)",s,n -> times.addFirst(times.getFirst().plusDays(n)));
                passaTempo("\\d+((w(eek)?|s(emana)?)s?)",s,n -> times.addFirst(times.getFirst().plusWeeks(n)));
                passaTempo("\\d+(h(o(ra|ur))?s?)",s,n -> times.addFirst(times.getFirst().plusHours(n)));
                passaTempo("\\d+m((inut[eo])s?)?",s,n -> times.addFirst(times.getFirst().plusMinutes(n)));

                if(times.getFirst().equals(controle)){
                    event.reply("não consegui entender o comando dirieto");
                    return;
                }

                if(ChronoUnit.MONTHS.between(controle,times.getFirst()) > 2){
                    poll.setDataLimite(controle.plusMonths(2));
                    event.reply("tempo máximo é de 2 meses");
                }else
                    poll.setDataLimite(times.getFirst());
                Memoria.guilds.updateOne(dp.query(), new Document("$set", dp.guild().toMongo()));
                event.reply("o limite agora é " + poll.getDataLimite().toString());
                times.clear();
            }
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || PollHelper.livreDono(args, event);
    }

    private void passaTempo(String pattern, String input, Tempx function){
        LinkedList<String> param = Regex.extract(pattern,input);
        if(param.size() > 0){
            param.forEach(p->{
                var m = Pattern.compile("\\d+").matcher(p);
                if(m.find())
                    function.apply(Long.parseLong(m.group()));
            });
        }
    }

    private interface Tempx{
        void apply(long n);
    }
    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-data {titulo} 1 dia** : define tempo restante para encerrar a poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para adicionar um novo tempo restante para uma poll (enquete).
                
                **-data {titulo} 1 semana 2 dias 6 horas 23 minutos**
                
                Aliases (comandos alternativos) : **date**, **data**.
                O tempo limite para votação é contado somando o tempo digitado com o horario atual.
                O tempo maximo que uma poll pode ficar aberta são 2 meses.
                É possivel utilizar apenas as inicias assim ficaria
                
                **-data {titulo} 1s 2d 6h 23m**.
                """);
    }
}
