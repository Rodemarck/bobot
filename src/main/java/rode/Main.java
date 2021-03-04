package rode;


import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.controller.Controlador;
import rode.core.ComandoGuild;
import rode.core.ComandoGuildReacoes;
import rode.core.Executador;
import rode.core.IgnoraComando;
import rode.utilitarios.Constantes;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.regex.Pattern;


public class Main {
        private static Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException, InterruptedException {
        Locale.setDefault(new Locale("pt", "BR"));
        //jda();
        var p = Pattern.compile("oi");
        System.out.println(p.matcher("zo-i").find());
    }


    private static void jda(){
        log.debug("logando");

        try{
            inicializaComandos();
            JDABuilder.createDefault(Constantes.env.get("token"))
                    .setActivity(Activity.playing("-tutorial"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new Controlador())
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .setCompression(Compression.ZLIB)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
    private static void inicializaComandos() {
        Reflections reflections = new Reflections("rode.comando.guild");
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
