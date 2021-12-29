package rode;


import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Controlador;
import rode.core.Anotacoes.IgnoraComando;
import rode.core.Executador;
import rode.model.ComandoGuild;
import rode.model.ComandoGuildReacoes;
import rode.utilitarios.ClienteHttp;
import rode.utilitarios.Constantes;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        //System.out.println(ClienteHttp.get("https://github.com/public-apis/public-apis"));
        jda();
    }
    private static void jda(){
        log.debug("logando");
        try{
            var jda = JDABuilder.createLight(Constantes.env("token"),Set.of(
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES
                    )).setActivity(Activity.playing("-help"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new Controlador())
                    .build();
            inicializaComandos(jda.updateCommands());
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    private static void inicializaComandos(CommandListUpdateAction cua) {
        var reflections = new Reflections("rode.comando.guild");
        var locale = new Locale("pt","BR");
        var bundle = ResourceBundle.getBundle("messages", locale);
        var ignoreList = reflections.getTypesAnnotatedWith(IgnoraComando.class);
        reflections.getSubTypesOf(ComandoGuild.class)
                .stream().filter(aClass -> !ignoreList.contains(aClass))
                .forEach(clazz->cadastraComando(clazz,cua,bundle,reflections));
        reflections.getSubTypesOf(ComandoGuildReacoes.class)
                .stream().filter(aClass -> !ignoreList.contains(aClass))
                .forEach(Main::cadastraComandoReacao);
        log.info("finalizado");
        cua.queue();
    }


    public static void cadastraComando(Class<? extends ComandoGuild> clazz,CommandListUpdateAction cua, ResourceBundle bundle, Reflections reflections) {
        ComandoGuild command;
        try {
            command = clazz.getConstructor().newInstance();
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        int id_comando = Executador.COMANDOS_GUILD.size() + 1;
        Executador.COMANDOS_GUILD.put(id_comando, command);
        for(String s: command.alias)
            Executador.NOME_COMANDOS_GUILD.put(s,id_comando);
        command.findSons(reflections,clazz);
        if(command.isSlash()){
            command.subscribeSlash(cua,bundle);
        }
    }
    private static void cadastraComandoReacao(Class<? extends ComandoGuildReacoes> clazz) {
        ComandoGuildReacoes comando;
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
