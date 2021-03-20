package rode.comando.guild.dama;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.model.Dama;

import java.util.LinkedList;
import java.util.ResourceBundle;

public class TabuleiroDama extends ComandoGuild {
    public TabuleiroDama() {
        super("Dama", null, "dama");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        var dama = new Dama();
        dama.sendPlot(event);
    }
}
