package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.reflections.Reflections;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Anotacoes.EComandoProgramador;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.Anotacoes.IgnoraComando;
import rode.core.ComandoGuild;
import rode.core.Executador;
import rode.core.Helper;
import rode.utilitarios.Constantes;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@EcomandoGeral
public class Tutorial extends ComandoGuild {
    private static final HashMap<Locale,EmbedBuilder> tutorial = new HashMap<>();
    public Tutorial() {
        super("tutorial", null, "help","tutorial","ajuda");
    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws IOException {
        args.poll();
        var loc = hm.bundle().getLocale();
        if(args.isEmpty()){
            if(tutorial.get(loc) == null){
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

                System.out.println(tuto.build().getLength());
                tutorial.put(loc,tuto);
            }
            hm.reply(tutorial.get(loc).build());
            return;
        }
        if(Executador.NOME_COMANDOS_GUILD.containsKey(args.getFirst())){
            var b = Constantes.builder(loc,args.getFirst());
            if(b != null){
                hm.reply(b);
                return;
            }
            var eb = Constantes.builder();
            eb.setTitle(String.format(hm.text("tutorial.cmd.title"),args.getFirst()));
            Executador.COMANDOS_GUILD.get(Executador.NOME_COMANDOS_GUILD.get(args.getFirst())).helpExtensive(eb, hm.bundle());
            Constantes.addBuilder(loc,args.getFirst(),eb);
            hm.reply(eb);
            return;
        }
        hm.reply(hm.text("tutorial.cmd.404").formatted(args.getFirst()));
    }

    @Override
    public void subscribeSlash(CommandUpdateAction cua, ResourceBundle bundle) {

    }

    private void reflexao(Reflections reflexao, EmbedBuilder eb, ResourceBundle rb, Class<? extends Annotation> a){
        var ignoreList = reflexao.getTypesAnnotatedWith(IgnoraComando.class);
        reflexao.getTypesAnnotatedWith(a).stream().filter(aClass -> !ignoreList.contains(aClass)).sorted(Comparator.comparing(Class::getName)).forEach(c->{ 
            try {
                c.getMethod("help", EmbedBuilder.class, ResourceBundle.class).invoke(c.getConstructor().newInstance(),eb,rb);
            } catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        eb.appendDescription("\n");
    }
}
