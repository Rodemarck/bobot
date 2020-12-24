package rode.core.comandos.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;

public class PingGuild extends ComandoGuild {
    public PingGuild() {
        super("ping", null, "ping","pong");
    }

    public boolean livre(LinkedList<String> args, Helper.Mensagem event) {
        return true;
    }

    public void executa(LinkedList<String> args, Helper.Mensagem event) {
        event.reply("o ping é de mais de **" + event.getEvent().getJDA().getGatewayPing() + "ms**!!");
    }

    public void help(EmbedBuilder me) {
        me.appendDescription("**-ping**: exibe a minha lerdeza.\n\n");
    }

    public void helpExtensive(EmbedBuilder me) {
        help(me);
    }
}
