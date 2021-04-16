package rode;


import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.controller.Controlador;
import rode.core.Anotacoes.IgnoraComando;
import rode.core.ComandoGuild;
import rode.core.ComandoGuildReacoes;
import rode.core.Executador;
import rode.utilitarios.Constantes;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Locale;
import java.util.ResourceBundle;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args){
        jda();
    }

    private static void jda(){
        log.debug("logando");
        try{
            var jda = JDABuilder.createLight(Constantes.env("token_teste"), EnumSet.noneOf(GatewayIntent.class))
                    .setActivity(Activity.playing("-help"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new Controlador())
                    .build();
            inicializaComandos(jda.updateCommands());
            var comandos = jda.updateCommands();
            comandos.addCommands(
                    new CommandUpdateAction.CommandData("sexo","você transa").
                            addOption(new CommandUpdateAction.OptionData(Command.OptionType.USER, "user","com quem você vai transar").setRequired(true))
            );
            comandos.queue(q->{
                log.info("foi...");
            });
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    private static void inicializaComandos(CommandUpdateAction cua) {
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
        cua.queue();
    }


    public static void cadastraComando(Class<? extends ComandoGuild> clazz,CommandUpdateAction cua, ResourceBundle bundle, Reflections reflections) {
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
