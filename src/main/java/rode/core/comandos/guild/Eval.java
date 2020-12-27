package rode.core.comandos.guild;

import jdk.jshell.JShell;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.UseComande;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.LinkedList;
import java.util.stream.Collectors;

@UseComande
public class Eval extends ComandoGuild {
    public Eval() {
        super("eval", null, "eval", "js");
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

    }

    @Override
    public void helpExtensive(EmbedBuilder me) {

    }
}
