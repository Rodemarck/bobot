package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Anotacoes.EComandoProgramador;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.Anotacoes.IgnoraComando;
import rode.model.ComandoGuild;
import rode.core.Executador;
import rode.core.Helper;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@EcomandoGeral
public class Tutorial extends ComandoGuild {
    private static final Logger log = LoggerFactory.getLogger(Tutorial.class);
    private static final HashMap<Locale,EmbedBuilder> tutorial = new HashMap<>();
    public Tutorial() {
        super("tutorial", null, true,"help","tutorial","ajuda");
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException {
        var loc = hm.bundle().getLocale();
        if(args.length ==1){
            if(tutorial.get(loc) == null){
                makeTutorial(hm);
            }
            hm.reply(tutorial.get(loc));
            return;
        }
        if(Executador.NOME_COMANDOS_GUILD.containsKey(args[1])){
            var b = Constantes.builder(loc,args[1]);
            if(b != null){
                hm.reply(b);
                return;
            }
            var eb = Constantes.builder();
            eb.setTitle(hm.text("tutorial.cmd.title").formatted(args[1]));
            Executador.COMANDOS_GUILD.get(Executador.NOME_COMANDOS_GUILD.get(args[1])).helpExtensive(eb, hm.bundle());
            Constantes.addBuilder(loc,args[1],eb);
            hm.reply(eb);
            return;
        }
        hm.reply(hm.text("tutorial.cmd.404").formatted(args[1]));
    }
    private EmbedBuilder makeTutorial(Helper hm){
        var reflections = new Reflections("rode.comando.guild");
        var tuto = Constantes.builder();
        var ignoreList = reflections.getTypesAnnotatedWith(IgnoraComando.class);
        tuto.setTitle(hm.text("tutorial.title"))
                .setThumbnail("https://cdn.discordapp.com/attachments/305767530021126154/828477225371959306/20210405_005226.jpg")
                .appendDescription(hm.text("tutorial.descri"))
                .appendDescription(hm.text("tutorial.comando.poll"));

        reflexao(reflections,tuto,hm.bundle(),EComandoPoll.class);

        tuto.appendDescription(hm.text("tutorial.comando.program"));
        reflexao(reflections,tuto,hm.bundle(), EComandoProgramador.class);

        tuto.appendDescription(hm.text("tutorial.comando.geral"));
        reflexao(reflections,tuto,hm.bundle(),EcomandoGeral.class);
        tutorial.put(hm.bundle().getLocale(),tuto);
        return tuto;
    }

    @Override
    public void subscribeSlash(CommandUpdateAction cua, ResourceBundle bundle) {
        var reflections = new Reflections("rode.comando.guild");
        var opcoes = new CommandUpdateAction.OptionData(Command.OptionType.STRING,"comando","tutorial para um comando");
        reflexao(reflections,opcoes,EComandoPoll.class);
        reflexao(reflections,opcoes,EcomandoGeral.class);
        reflexao(reflections,opcoes,EComandoProgramador.class);
        opcoes.setRequired(false);
        Executador.NOME_COMANDOS_SLASH.put(command,Executador.NOME_COMANDOS_GUILD.get(command));
        cua.addCommands(
                new CommandUpdateAction.CommandData(command, bundle.getString("tutorial.help"))
                        .addOption(opcoes)
        );
        log.info("acuado!!");
    }
    private void reflexao(Reflections reflexao, CommandUpdateAction.OptionData optionData,Class<? extends Annotation> a){
        var ignoreList = reflexao.getTypesAnnotatedWith(IgnoraComando.class);
        reflexao.getTypesAnnotatedWith(a).stream().filter(aClass -> !ignoreList.contains(aClass)).sorted(Comparator.comparing(Class::getName)).forEach(c->{
            try {
                var subObject = c.getConstructor().newInstance();
                var method = c.getMethod("isSlash");
                //if(method !=null && subObject != null && (boolean) method.invoke(subObject))
                    optionData.addChoice(c.getSimpleName(),c.getSimpleName());
            } catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private void reflexao(Reflections reflexao, EmbedBuilder eb, ResourceBundle rb, Class<? extends Annotation> a){
        var ignoreList = reflexao.getTypesAnnotatedWith(IgnoraComando.class);
        reflexao.getTypesAnnotatedWith(a).stream().filter(aClass -> !ignoreList.contains(aClass)).sorted(Comparator.comparing(Class::getName)).forEach(c->{ 
            try {
                var subObject = c.getConstructor().newInstance();
                var method = c.getMethod("help", EmbedBuilder.class, ResourceBundle.class);
                if(method !=null && subObject != null)
                    method.invoke(subObject,eb,rb);
            } catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        eb.appendDescription("\n");
    }

    @Override
    public void executeSlash(SlashCommandEvent slash, Helper.Slash hs) {
        var command = slash.getOption("comando").getAsString();
        var loc = hs.bundle().getLocale();
        if(command == null){
            if(tutorial.get(loc) == null){
                makeTutorial(hs);
            }
            hs.replySlash(tutorial.get(loc));
        }else if(Executador.NOME_COMANDOS_GUILD.containsKey(command)){
            var b = Constantes.builder(loc,command);
            if(b != null){
                hs.replySlash(b);
                return;
            }
            var eb = Constantes.builder();
            eb.setTitle(hs.text("tutorial.cmd.title").formatted(command));
            Executador.COMANDOS_GUILD.get(Executador.NOME_COMANDOS_GUILD.get(command)).helpExtensive(eb, hs.bundle());
            Constantes.addBuilder(loc,command,eb);
            hs.reply(eb);
            return;
        }
        hs.reply(hs.text("tutorial.cmd.404").formatted(command));
    }
}
