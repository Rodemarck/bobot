package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
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
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@EComandoPoll
public class SetDataPoll  extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(SetDataPoll.class);

    public SetDataPoll() {
        super("data", Permission.ADMINISTRATOR, "data","date");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args,hm,dp -> {
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
                            hm.reply(hm.text("data.exec.invalid"));
                            return;
                        }
                        if(ChronoUnit.MONTHS.between(agora,data) > 2) {
                            data = agora.plusMonths(2);
                            hm.reply(hm.text("data.exec.max"));
                        }
                        poll.setDataLimite(data);
                        Memoria.update(dp.query(), dp.guild());
                        hm.reply(poll.config(hm.bundle()));
                    }catch (DateTimeException e){
                        hm.reply(hm.text("data.exec.invalid"));
                        return;
                    }
                }else{
                    hm.reply(hm.text("data.exec.invalid"));
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
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("data.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        var agora = LocalDateTime.now();
        agora.plusWeeks(1);
        me.appendDescription(String.format(String.format("data.help.ex"), agora.format(DateTimeFormatter.ofPattern("d/m/Y H:m"))));
    }
}
