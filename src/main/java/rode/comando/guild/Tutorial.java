package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.*;
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
        var loc = hm.getBundle().getLocale();
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
            eb.setTitle(String.format(hm.getText("tutorial.cmd.title"),args[1]));
            Executador.COMANDOS_GUILD.get(Executador.NOME_COMANDOS_GUILD.get(args[1])).helpExtensive(eb, hm.getBundle());
            Constantes.addBuilder(loc,args[1],eb);
            hm.reply(eb);
            return;
        }
        hm.reply(String.format(hm.getText("tutorial.cmd.404"),args[1]));
    }
    private EmbedBuilder makeTutorial(Helper hm){
        var reflections = new Reflections("rode.comando.guild");
        var tuto = Constantes.builder();
        tuto.setTitle(hm.getText("tutorial.title"))
                .setThumbnail("https://cdn.discordapp.com/attachments/305767530021126154/828477225371959306/20210405_005226.jpg")
                .appendDescription(hm.getText("tutorial.descri"));

        var ignoreList = reflections.getTypesAnnotatedWith(IgnoraComando.class);
        tuto.appendDescription(hm.getText("tutorial.comando.poll"));
        reflexao(reflections,tuto,hm.getBundle(),EComandoPoll.class,ignoreList);

        tuto.appendDescription(hm.getText("tutorial.comando.program"));
        reflexao(reflections,tuto,hm.getBundle(), EComandoProgramador.class,ignoreList);

        tuto.appendDescription(hm.getText("tutorial.comando.geral"));
        reflexao(reflections,tuto,hm.getBundle(),EcomandoGeral.class,ignoreList);

        tuto.appendDescription(hm.getText("tutorial.comando.jogo"));//tutorial.comando.domo
        reflexao(reflections,tuto,hm.getBundle(), EComandoJogo.class,ignoreList);

        tuto.appendDescription(hm.getText("tutorial.comando.domo"));//
        reflexao(reflections,tuto,hm.getBundle(), EComandoDomotica.class,ignoreList);

        tutorial.put(hm.getBundle().getLocale(),tuto);
        return tuto;
    }

    @Override
    public void subscribeSlash(CommandListUpdateAction cua, ResourceBundle bundle) {
        var reflections = new Reflections("rode.comando.guild");
        var opcoes = new OptionData(OptionType.STRING,"comando","tutorial para um comando");
        reflexao(reflections,opcoes,EComandoPoll.class);
        reflexao(reflections,opcoes,EcomandoGeral.class);
        reflexao(reflections,opcoes,EComandoProgramador.class);
        opcoes.setRequired(false);
        Executador.NOME_COMANDOS_SLASH.put(command,Executador.NOME_COMANDOS_GUILD.get(command));
        cua.addCommands(
                new CommandData(command, bundle.getString("tutorial.help"))
                        .addOptions(opcoes)
        );
        log.info("acuado!!");
    }
    private void reflexao(Reflections reflexao, OptionData optionData,Class<? extends Annotation> a){
        var ignoreList = reflexao.getTypesAnnotatedWith(IgnoraComando.class);
        reflexao.getTypesAnnotatedWith(a).stream().filter(aClass -> !ignoreList.contains(aClass)).sorted(Comparator.comparing(Class::getName)).forEach(c->{
            try {
                var subObject = c.getConstructor().newInstance();
                var method = c.getMethod("isSlash");
                if(method !=null && subObject != null && (boolean) method.invoke(subObject))
                    optionData.addChoice(c.getSimpleName(),c.getSimpleName());
            } catch (NoSuchMethodException
                    |InstantiationException
                    |IllegalAccessException
                    |InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private void reflexao(Reflections reflexao, EmbedBuilder eb, ResourceBundle rb, Class<? extends Annotation> a,Set<Class<?>> ignoreList){
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
        var loc = hs.getBundle().getLocale();
        if(command == null){
            if(tutorial.get(loc) == null){
                makeTutorial(hs);
            }
            hs.reply(tutorial.get(loc));
        }else if(Executador.NOME_COMANDOS_GUILD.containsKey(command)){
            var b = Constantes.builder(loc,command);
            if(b != null){
                hs.reply(b);
                return;
            }
            var eb = Constantes.builder();
            eb.setTitle(String.format(hs.getText("tutorial.cmd.title"),command));
            Executador.COMANDOS_GUILD.get(Executador.NOME_COMANDOS_GUILD.get(command)).helpExtensive(eb, hs.getBundle());
            Constantes.addBuilder(loc,command,eb);
            hs.reply(eb);
            return;
        }
        hs.reply(String.format(hs.getText("tutorial.cmd.404"),command));
    }
}
