package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@EcomandoGeral
public class Diga extends ComandoGuild {
    public Diga() {
        super("diz", null,"diga", "say");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        hm.getEvent().getMessage().delete().queue();
        args.poll();
        hm.reply(args.stream().collect(Collectors.joining(" ")));
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("say.help.ex"));
    }
}
