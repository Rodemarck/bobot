package rode.comando.guild;

import rode.core.Anotacoes.EcomandoGeral;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;
import java.util.stream.Collectors;

@EcomandoGeral
public class Diga extends ComandoGuild {
    public Diga() {
        super("diz", null,"diga", "say");
    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        hm.getEvent().getMessage().delete().queue();
        args.poll();
        hm.reply(args.stream().collect(Collectors.joining(" ")));
    }
}
