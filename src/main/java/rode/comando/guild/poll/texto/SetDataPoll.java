package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public void executa(LinkedList<String> args, Helper.Mensagem helper) throws Exception {
        PollHelper.getPoll(args,helper,dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                String s = args.stream().collect(Collectors.joining(" ")).replaceAll("\\{([^\\}]+)\\}|\\[([^\\]]+)\\]"," ");
                log.debug(s);
                var p = Pattern.compile("\\d+(/|-)\\d+((/|-)\\d+)?(\\s+\\d+:\\d+)?");
                var m = p.matcher(s);
                if(m.find()){
                    var nums = new LinkedList<Integer>();
                    var texto = m.group();
                    p = Pattern.compile("\\d+");
                    m = p.matcher(texto);
                    while (m.find())
                        nums.add(Integer.parseInt(m.group()));

                    var agora = LocalDateTime.now();
                    int[] numeros = switch (nums.size()){
                        case 2 -> new int[]{agora.getYear(),nums.get(1),nums.get(0),12,0};
                        case 3 -> new int[]{nums.get(2), nums.get(1), nums.get(0), 12, 0};
                        case 4 -> new int[]{agora.getYear(), nums.get(1), nums.get(0), nums.get(2),nums.get(3)};
                        case 5 -> new int[]{nums.get(2), nums.get(1), nums.get(0), nums.get(3), nums.get(4)};
                        default -> null;
                    };
                    try{
                        var data = LocalDateTime.of(numeros[0],numeros[1],numeros[2],numeros[3],numeros[4]);
                        if(agora.isAfter(data)){
                            helper.reply("Data inválida.");
                            return;
                        }
                        if(ChronoUnit.MONTHS.between(agora,data) > 2) {
                            data = agora.plusMonths(2);
                            helper.reply("O tempo máximo é dois meses");
                        }
                        poll.setDataLimite(data);
                        Memoria.update(dp.query(), dp.guild());
                        helper.reply(poll.config());
                    }catch (DateTimeException e){
                        helper.reply("Data inválida.");
                        return;
                    }
                }else{
                    helper.reply("Data inválida.");
                    return;
                }
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
        var agora = LocalDateTime.now();
        agora.plusWeeks(1);
        me.appendDescription(String.format("""
                Comando para adicionar um novo tempo restante para uma poll (enquete).
                
                **-data {titulo} %s**
                
                Aliases (comandos alternativos) : **date**, **data**.
                O tempo limite para votação é contado somando o tempo digitado com o horario atual.
                O tempo maximo que uma poll pode ficar aberta são 2 meses.
                É possivel utilizar apenas as inicias assim ficaria.
                """, agora.format(DateTimeFormatter.ofPattern("d/m/Y H:m"))));
    }
}
