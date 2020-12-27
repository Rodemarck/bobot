package rode;


import kotlin.jvm.internal.Reflection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.controller.Controlador;
import rode.core.ComandoGuild;
import rode.core.ComandoGuildReacoes;
import rode.core.Executador;
import rode.core.UseComande;
import rode.core.comandos.guild.*;
import rode.core.comandos.guild.poll.reacoes.PollReactionAdd;
import rode.core.comandos.guild.poll.reacoes.PollReactionRem;
import rode.core.comandos.guild.poll.texto.*;
import rode.utilitarios.Constantes;
import rode.utilitarios.Regex;


import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            final JDA jda = JDABuilder.createDefault(Constantes.env.get("token"))
                    .setActivity(Activity.playing("-tutorial"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new Controlador())
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
    private static void inicializaComandos() {
        cadastraComando(new MostraPoll());
        cadastraComando(new AdicionaOpcoesPoll());
        cadastraComando(new DeletaPoll());
        cadastraComando(new ListarPolls());
        cadastraComando(new MostraVotosPoll());
        cadastraComando(new PollReactionAdd());
        cadastraComando(new PollReactionRem());
        cadastraComando(new RemoveOpcoesPoll());
        cadastraComando(new Tutorial());
        cadastraComando(new PingGuild());
        cadastraComando(new GraficoPoll());
        cadastraComando(new Delay());
        cadastraComando(new Diga());
        cadastraComando(new FechaPoll());
        cadastraComando(new Configuracoes());
        cadastraComando(new SetDataPoll());
    }


    private static void cadastraComando(ComandoGuild comando) {
        int id_comando = Executador.COMANDOS_GUILD.size() + 1;
        Executador.COMANDOS_GUILD.put(id_comando, comando);
        for(String s: comando.alias)
            Executador.NOME_COMANDOS_GUILD.put(s,id_comando);
    }
    private static void cadastraComando(ComandoGuildReacoes comando) {
        int id_comando = Executador.COMANDOS_REACOES_GUILD.size() + 1;
        Executador.COMANDOS_REACOES_GUILD.put(id_comando, comando);
        for(String s: comando.alias)
            Executador.NOME_COMANDOS_REACOES_GUILD.put(s,id_comando);
    }
}
