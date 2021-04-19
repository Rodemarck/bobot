package rode.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.model.ComandoGuild;
import rode.model.ComandoGuildReacoes;
import rode.utilitarios.Constantes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executador {
    private static Logger log = LoggerFactory.getLogger(Executador.class);
    public static ExecutorService poolExecutor =  Executors.newCachedThreadPool();

    public static final HashMap<Integer, ComandoGuild> COMANDOS_GUILD = new HashMap<>();
    public static final HashMap<String, Integer> NOME_COMANDOS_GUILD = new HashMap<>();

    public static final HashMap<Integer, ComandoGuildReacoes> COMANDOS_REACOES_GUILD = new HashMap<>();
    public static final HashMap<String, Integer> NOME_COMANDOS_REACOES_GUILD = new HashMap<>();

    public static final HashMap<String, Integer> NOME_COMANDOS_SLASH = new HashMap<>();

    private static void tryCatch(JDA jda, Funcao f){
        poolExecutor.execute(()->{
            try{
                f.apply();
            }catch (Exception ex) {
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                ex.printStackTrace();
                jda.retrieveUserById(305090445283688450l).queue(user-> {
                    user.openPrivateChannel().queue(dm -> {
                        dm.sendMessage(LocalDateTime.now().toString()).queue();
                        String err = errors.toString();
                        int n = err.length(),k,j = 0;
                        for(int i=0; i< n; ) {
                            j = i + 1900;
                            k = (j >= n) ? n : j;
                            dm.sendMessage("```\n " + err.substring(i, k) + "```").queue();
                            i = k;
                        }
                    });
                });
            }
        });
    }
    public static void checa(GuildMessageReceivedEvent e){
        tryCatch(e.getJDA(),()->{
            log.info("uuu");
            var l = Constantes.loc(e.getGuild().getId());
            var hm = new Helper.Mensagem(e,l);
            EventLoop.textoGuild(hm);
        });
    }
    public static ComandoGuild getGuildCommand(String command, String[] args){
        var mgr = COMANDOS_GUILD.get(NOME_COMANDOS_GUILD.get(command));
        var i = 1;
        ComandoGuild aux;
        while (mgr!=null && i < args.length){
            aux = mgr.getSons().get(args[i++]);
            if(aux != null)
                mgr = aux;
            else
                break;
        }
        return mgr;
    }
    public static void interpreta(GuildMessageReceivedEvent e, User user) {
        tryCatch(e.getJDA(), () -> {
            var raw = e.getMessage().getContentRaw();
            var args = splitter(raw);
            var command = args.length == 0 ? "" : args[0];
            log.info("getuuu");
            var mgr = getGuildCommand(command,args);
            log.info("{}",mgr.getClass().getSimpleName());
            var l = Constantes.loc(e.getGuild().getId());
            var hm = new Helper.Mensagem(e, l);

            if (mgr == null)
                mgr = COMANDOS_GUILD.get(null);
            if (mgr != null) {
                log.trace("{} :: [{} <- ({})]", mgr.getClass().getSimpleName(), command, args);
                if (mgr.free(args, hm))
                    mgr.execute(args, hm);
                else
                    mgr.fail(args, hm);
            }
            else
                EventLoop.textoGuild(hm);
        });
    }
    private static String[] splitter(String raw){
        String texto = raw.startsWith(Constantes.PREFIXO) ?
                raw.replaceFirst(Constantes.PREFIXO,"")
                :raw;
        return texto.split("\\s+|\\n+");

    }
    public static void interpreta(GuildMessageReactionAddEvent event, User u) {
        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(m->{
            tryCatch(event.getJDA(),()->interpretaEmoji(event, m,u, "+++"));
        });
    }
    public static void interpreta(GuildMessageReactionRemoveEvent event, User u) {
        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(m->{
            tryCatch(event.getJDA(),()->interpretaEmoji(event,m,u, "---"));
        });
    }

    private static void interpretaEmoji(GenericGuildMessageReactionEvent e, Message m,User u, String discriminador) throws Exception {
        tryCatch(e.getJDA(),()->{
            var raw = m.getContentRaw();
            var args = splitter(raw);
            var comando = args.length == 0 ? "" : args[0] + discriminador;

            var rmg = COMANDOS_REACOES_GUILD.get(NOME_COMANDOS_REACOES_GUILD.get(comando));
            log.trace("comando [{} <- ({})] chamado",comando , discriminador);
            var l = Constantes.loc(e.getGuild().getId());
            var hr = new Helper.Reacao(e,m,l);
            if(rmg == null)
                rmg = COMANDOS_REACOES_GUILD.get(null);
            if(rmg != null) {
                log.trace("{} :: [{} <- ({})]",rmg.getClass().getSimpleName(),comando , args);
                if (rmg.livre(args, hr))
                    rmg.executa(args, hr);
            }
            else if(discriminador.equals("+++"))
                EventLoop.reacaoGuild(hr);
        });

    }

    public static void interpreta(SlashCommandEvent event) {
        tryCatch(event.getJDA(),()->{
            log.info(event.getName());
            var command = COMANDOS_GUILD.get(NOME_COMANDOS_SLASH.get(event.getName()));
            if (command!= null){
                var loc = Constantes.loc(event.getGuild().getId());
                command.executeSlash(event, new Helper.Slash(event,loc));
            }
            else {
                NOME_COMANDOS_SLASH.forEach((s, integer) -> {
                    System.out.println(s + "\t" +COMANDOS_GUILD.get(integer));
                });
            }
        });
    }

    private interface Funcao{
        void apply() throws Exception;
    }
}
