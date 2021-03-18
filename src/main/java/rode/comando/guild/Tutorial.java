package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import org.reflections.Reflections;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.ComandoGuild;
import rode.core.Executador;
import rode.core.Helper;
import rode.utilitarios.Constantes;

import java.awt.*;
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
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws IOException {
        args.poll();
        var loc = hm.bundle().getLocale();
        if(args.isEmpty()){
            if(tutorial.get(loc) == null){

                var tuto = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
                tuto.setTitle(hm.text("tutorial.title"),"https://cdn.discordapp.com/avatars/305090445283688450/b1af2bade4b94a08e31091db153c4aae.png");
                tuto.appendDescription(hm.text("tutorial.descri"));
                tuto.appendDescription(hm.text("tutorial.comando.poll"));
                var reflections = new Reflections("rode.comando.guild");
                reflexao(reflections,tuto,hm.bundle(),EComandoPoll.class);
                tuto.appendDescription(hm.text("tutorial.comando.geral"));
                reflexao(reflections,tuto,hm.bundle(),EcomandoGeral.class);
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
            var eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
            eb.setTitle(String.format(hm.text("tutorial.cmd.title"),args.getFirst()));
            Executador.COMANDOS_GUILD.get(Executador.NOME_COMANDOS_GUILD.get(args.getFirst())).helpExtensive(eb, hm.bundle());
            Constantes.addBuilder(loc,args.getFirst(),eb);
            hm.reply(eb);
            return;
        }
        hm.reply(String.format(hm.text("tutorial.cmd.404"), args.getFirst() ));
    }
    private void reflexao(Reflections reflexao,EmbedBuilder eb,ResourceBundle rb,Class<? extends Annotation> a){
        reflexao.getTypesAnnotatedWith(a).stream().sorted(Comparator.comparing(Class::getName)).forEach(c->{
            try {
                c.getMethod("help", EmbedBuilder.class, ResourceBundle.class).invoke(c.getConstructor().newInstance(),eb,rb);
            } catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        eb.appendDescription("\n");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle bd) {
        me.appendDescription(bd.getString("tutorial.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me,ResourceBundle bd) {
        me.appendDescription(bd.getString("tutorial.hep.ex"));
    }
}
