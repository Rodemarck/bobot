package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;
import java.util.ResourceBundle;

@EcomandoGeral
public class PingGuild extends ComandoGuild {
    public PingGuild() {
        super("ping", null, "ping","pong");
    }

    public boolean free(LinkedList<String> args, Helper.Mensagem hm) {
        return true;
    }

    public void execute(LinkedList<String> args, Helper.Mensagem hm) {
        hm.reply(hm.text("ping.exec").formatted(hm.jda().getGatewayPing()));
    }

    public void helpExtensive(EmbedBuilder me, ResourceBundle bd) {
        help(me, bd);
    }
}
