package rode.core.comandos.guild;

import jdk.jshell.JShell;
import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Eval extends ComandoGuild {
    public Eval() {
        super("eval", null, "eval", "shell");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception{
        args.poll();
        String str = args.stream().collect(Collectors.joining(" "));
        try{
            JShell shell =JShell.create();
            event.reply(">> " + shell.eval(str).get(0).value());
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
}
