package rode;


import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.controller.Controlador;
import rode.core.ComandoGuild;
import rode.core.ComandoGuildReacoes;
import rode.core.Executador;
import rode.core.IgnoraComando;
import rode.core.comandos.guild.*;
import rode.core.comandos.guild.poll.reacoes.PollReactionAdd;
import rode.core.comandos.guild.poll.reacoes.PollReactionRem;
import rode.core.comandos.guild.poll.texto.*;
import rode.utilitarios.Constantes;
import sun.reflect.ReflectionFactory;


import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Pattern;


public class Main {
        private static Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException, InterruptedException {
        jda();
    }


    private static void jda(){
        log.debug("logando");
        try{
            inicializaComandos();
            JDABuilder.createDefault(Constantes.env.get("token_teste"))
                    .setActivity(Activity.playing("-tutorial"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new Controlador())
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
    private static void inicializaComandos() {
        Reflections reflections = new Reflections("rode.core.comandos.guild");
        var ignoreList = reflections.getTypesAnnotatedWith(IgnoraComando.class);
        reflections.getSubTypesOf(ComandoGuild.class)
                .stream().filter(aClass -> !ignoreList.contains(aClass))
                .forEach(Main::cadastraComando);
        reflections.getSubTypesOf(ComandoGuildReacoes.class)
                .stream().filter(aClass -> !ignoreList.contains(aClass))
                .forEach(Main::cadastraComandoReacao);
    }


    public static void cadastraComando(Class<? extends ComandoGuild> clazz) {
        ComandoGuild comando = null;
        try {
            comando = clazz.getConstructor().newInstance();
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        int id_comando = Executador.COMANDOS_GUILD.size() + 1;
        Executador.COMANDOS_GUILD.put(id_comando, comando);
        for(String s: comando.alias)
            Executador.NOME_COMANDOS_GUILD.put(s,id_comando);
    }
    private static void cadastraComandoReacao(Class<? extends ComandoGuildReacoes> clazz) {
        ComandoGuildReacoes comando = null;
        try {
            comando = clazz.getConstructor().newInstance();
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        int id_comando = Executador.COMANDOS_REACOES_GUILD.size() + 1;
        Executador.COMANDOS_REACOES_GUILD.put(id_comando, comando);
        for(String s: comando.alias)
            Executador.NOME_COMANDOS_REACOES_GUILD.put(s,id_comando);
    }
}
