package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.Executador;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

public class Tutorial extends ComandoGuild {
    private static final HashMap<Locale,EmbedBuilder> tutorial = new HashMap<>();
    public Tutorial() {
        super("tutorial", null, "help","tutorial","ajuda");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws IOException {
        args.poll();
        if(args.isEmpty()){
            if(tutorial.get(hm.bundle().getLocale()) == null){
                var tuto = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
                tuto.setTitle(hm.text("tutorial.title"),"https://cdn.discordapp.com/avatars/305090445283688450/b1af2bade4b94a08e31091db153c4aae.png");
                tuto.appendDescription(hm.text("tutorial.descri"));
                Executador.COMANDOS_GUILD.forEach((id,comando)-> comando.help(tuto,hm.bundle()));
                tutorial.put(hm.bundle().getLocale(),tuto);
            }
            hm.reply(tutorial.get(hm.bundle().getLocale()).build());
            return;
        }
        if(Executador.NOME_COMANDOS_GUILD.containsKey(args.getFirst())){
            EmbedBuilder eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
            eb.setTitle(String.format(hm.text("tutorial.cmd.title"),args.getFirst()));
            Executador.COMANDOS_GUILD.get(Executador.NOME_COMANDOS_GUILD.get(args.getFirst())).helpExtensive(eb, hm.bundle());
            hm.reply(eb);
            return;
        }
        hm.reply(String.format(hm.text("tutorial.cmd.404"), args.getFirst() ));
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
