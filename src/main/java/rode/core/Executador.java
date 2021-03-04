package rode.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.utilitarios.Constantes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executador {
    private static Logger log = LoggerFactory.getLogger(Executador.class);
    public static ExecutorService poolExecutor =  Executors.newCachedThreadPool();

    public static final HashMap<Integer, ComandoGuild> COMANDOS_GUILD = new HashMap<>();
    public static final HashMap<String, Integer> NOME_COMANDOS_GUILD = new HashMap<>();

    public static final HashMap<Integer, ComandoGuildReacoes> COMANDOS_REACOES_GUILD = new HashMap<>();
    public static final HashMap<String, Integer> NOME_COMANDOS_REACOES_GUILD = new HashMap<>();

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
            log.debug("checando ");
            var raw = e.getMessage().getContentRaw();
            var hm = new Helper.Mensagem(e);
            var args = traduz(raw);
            var mm = EventLoop.getInstance().mensagem(hm.getEvent().getAuthor().getIdLong());
            if(mm != null)
                mm.executa(args,hm);
        });
    }
    public static void interpreta(GuildMessageReceivedEvent e) {
        tryCatch(e.getJDA(), ()->{
            String raw = e.getMessage().getContentRaw();
            LinkedList<String> args = traduz(raw);
            String comando = args.size() == 0 ? "" : args.getFirst();
            ComandoGuild mgr = COMANDOS_GUILD.get(NOME_COMANDOS_GUILD.get(comando));
            Helper.Mensagem hm = new Helper.Mensagem(e);
            if (mgr == null)
                mgr = COMANDOS_GUILD.get(null);
            if (mgr != null ) {
                log.trace("{} :: [{} <- ({})]",mgr.getClass().getSimpleName(),comando , args);
                if (mgr.livre(args, hm))
                    mgr.executa(args, hm);
                else
                    mgr.falha(args, hm);
            }
        });
    }
    private static LinkedList<String>  traduz(String raw){
        String texto = raw.startsWith(Constantes.PREFIXO) ?
                raw.replaceFirst(Constantes.PREFIXO,"")
                :raw;
        StringTokenizer tokens = new StringTokenizer(texto);
        LinkedList<String> palavras = new LinkedList<>();
        while (tokens.hasMoreTokens())
            palavras.add(tokens.nextToken());
        return palavras;
    }
    public static void interpreta(GuildMessageReactionAddEvent event) {
        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(m->{
            tryCatch(event.getJDA(),()->interpretaEmoji(event, m, "+++"));
        });
    }
    public static void interpreta(GuildMessageReactionRemoveEvent event) {
        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(m->{
            tryCatch(event.getJDA(),()->interpretaEmoji(event,m, "---"));
        });
    }

    private static void interpretaEmoji(GenericGuildMessageReactionEvent event, Message m, String discriminador) throws Exception {
        String raw = m.getContentRaw();
        LinkedList<String> args = traduz(raw);
        String comando = args.size() == 0 ? "" : args.getFirst() + discriminador;

        ComandoGuildReacoes rmg = COMANDOS_REACOES_GUILD.get(NOME_COMANDOS_REACOES_GUILD.get(comando));
        log.trace("comando [{} <- ({})] chamado",comando , discriminador);
        Helper.Reacao hr = new Helper.Reacao(event,m);
        if(rmg == null)
            rmg = COMANDOS_REACOES_GUILD.get(null);
        if(rmg != null) {
            log.trace("{} :: [{} <- ({})]",rmg.getClass().getSimpleName(),comando , args);
            if (rmg.livre(args, hr))
                rmg.executa(args, hr);
        }
        else
            EventLoop2.reacaoGuild(hr);
    }

    private interface Funcao{
        void apply() throws Exception;
    }
}
