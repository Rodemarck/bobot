package rode.comando.guild;

import jdk.jshell.JShell;
import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Executador;
import rode.core.Helper;
import rode.model.ModelMensagem;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

public class Eval extends ComandoGuild {
    public static JShell shell;
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
        String comando = event.mensagem().getContentStripped();
        if(comando.startsWith("-eval"))
            comando = comando.replaceFirst("-eval","");
        else if(comando.startsWith("-shell"))
            comando = comando.replaceFirst("-shell","");

        try{
            final var id = event.getEvent().getAuthor().getIdLong();
            final var gId = event.getEvent().getChannel().getIdLong();
            if(!comando.isBlank()){
                event.reply(">> " + shell.eval(comando).get(0).value());
                return;
            }
            final var realComando = comando;

            EventLoop.mensagem(id, new ModelMensagem(id,gId,null) {
                @Override
                public void executa(LinkedList<String> args, Helper.Mensagem hm) {
                    log.debug("executando");
                    if(mensagemId()==hm.getEvent().getChannel().getIdLong()){
                        String comando = hm.mensagem().getContentStripped();
                        if(comando.equals("exit")){
                            EventLoop.deleta(id);
                            hm.reply("Console fechado " + event.getEvent().getAuthor().getName() + " .");
                            return;
                        }
                        hm.reply(">> " + shell.eval(comando).get(0).value());
                        atualiza();
                    }
                }
            });
            event.reply("Console aberto.");
            System.out.println("foi porra olha só");
            System.out.println(EventLoop.mensagem(id));
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
