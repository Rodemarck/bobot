package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.Anotacoes.EcomandoGeral;
import rode.model.ComandoGuild;
import rode.core.Helper;

import java.util.ResourceBundle;

@EcomandoGeral
public class PingGuild extends ComandoGuild {
    public PingGuild() {
        super("ping", null, "ping","pong");
    }

    public boolean free(String[] args, Helper.Mensagem hm) {
        return true;
    }

    public void execute(String[] args, Helper.Mensagem hm) {
        hm.reply(String.format(hm.getText("ping.exec"),hm.jda().getGatewayPing()));
    }

    public void helpExtensive(EmbedBuilder me, ResourceBundle bd) {
        help(me, bd);
    }
}
