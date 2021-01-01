package rode.core.comandos.guild;

import jdk.jshell.JShell;
import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Executador;
import rode.core.Helper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class Eval extends ComandoGuild {
    private static JShell shell;
    private static LocalDateTime time;
    public Eval() {
        super("eval", null, "eval", "shell");
        synchronized (this){
            time = LocalDateTime.now();
            if(shell == null) {
                shell = JShell.create();
                Executador.poolExecutor.execute(() -> {
                    while (true) {
                        try {Thread.sleep(60000);}
                        catch (InterruptedException e) {e.printStackTrace();}
                        synchronized (this){
                            if(ChronoUnit.MINUTES.between(time, LocalDateTime.now()) > 5){
                                shell = null;
                                shell = JShell.create();
                                time = LocalDateTime.now();
                            }
                        }
                    }

                });
            }
        }
    }

    @Override
    public void executa(LinkedList<String> __, Helper.Mensagem event) throws Exception{
        synchronized (this){
            time = LocalDateTime.now();
        }
        String comando = event.getMessage().getContentStripped().substring(5);
        try{
            event.reply(">> " + shell.eval(comando).get(0).value());
        } catch (Exception e) {
            if(e.getMessage() != null)
                event.reply(e.getMessage());
            throw e;
        }
    }

    @Override
    public String toString() {
        return "Eval{}";
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-eval expressão** : retorna o resultado da expressão\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                comando para executar e retorna o resultado da expressão javashell
                
                **-eval 1+1**
                
                Aliases (comandos alternativos) : **eval**, **shell**
                o termino da execução todas as variáveis e Métodos são apagados.
                """);
    }


}
