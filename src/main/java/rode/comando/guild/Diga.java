package rode.comando.guild;

import rode.core.Anotacoes.EcomandoGeral;
import rode.core.Helper;
import rode.model.ComandoGuild;

import java.util.Arrays;
import java.util.stream.Collectors;

@EcomandoGeral
public class Diga extends ComandoGuild {
    public Diga() {
        super("say", null,"diga", "say");
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        hm.getEvent().getMessage().delete().queue();
        var args2 = new String[args.length-1];
        System.arraycopy(args,1,args2,0,args2.length);
        hm.reply(Arrays.stream(args).sequential().collect(Collectors.joining(" ")));
    }
}
