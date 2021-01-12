package rode;


import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bson.Document;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.controller.Controlador;
import rode.core.ComandoGuild;
import rode.core.ComandoGuildReacoes;
import rode.core.Executador;
import rode.core.IgnoraComando;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;


import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Pattern;


public class Main {
        private static Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException, InterruptedException {



        Locale.setDefault(new Locale("pt", "BR"));
        jda();
        /*vvar s = "12/02";
        var p = Pattern.compile("\\d+(\\/|\\-)\\d+((\\/|\\-)\\d+(\\s+\\d{1,2}:\\d{1,2})?)?");
        var m = p.matcher(s);
        if(m.find()){
            var nums = new LinkedList<Integer>();
            var texto = m.group();
            p = Pattern.compile("\\d+");
            m = p.matcher(texto);
            while (m.find())
                nums.add(Integer.parseInt(m.group()));
            System.out.println(nums);
        }
        */
        /*Memoria.guilds.find().forEach(e->{
            ModelGuild m = ModelGuild.fromMongo(e);
            Poll poll = m.getPoll("ban <@581515398613172245> ");
            if(poll != null){
                m.getPolls().remove(poll);
                Document query = new Document("id",m.getId()).append("polls.titulo","ban <@581515398613172245> ");
                Memoria.guilds.updateOne(query, new Document("$set", m.toMongo()));
                System.out.println(poll);
            }
        });*/
    }


    private static void jda(){
        log.debug("logando");
        try{
            inicializaComandos();
            JDABuilder.createDefault(Constantes.env.get("token"))
                    .setActivity(Activity.playing("-tutorial"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new Controlador())
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
