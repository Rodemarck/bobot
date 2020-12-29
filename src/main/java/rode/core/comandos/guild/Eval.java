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
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception{
        synchronized (this){
            time = LocalDateTime.now();
        }
        args.poll();
        long i = System.currentTimeMillis();
        String str = args.stream().collect(Collectors.joining(" "));
        try{
            System.out.println(str);
            event.reply(">> " + shell.eval(str).get(0).value());
            long k = System.currentTimeMillis();
            System.out.println((k-i) + "ms de delai de pqp");
        } catch (Exception e) {
            event.reply(e.getMessage());
            throw e;
        }
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

    private class ShellEventLoop extends TimerTask{
        @Override
        public void run() {
            System.out.println("ha ha!!");
        }
    }
}
