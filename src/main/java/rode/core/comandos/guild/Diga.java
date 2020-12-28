package rode.core.comandos.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Diga extends ComandoGuild {
    public Diga() {
        super("diz", null,"diga", "say");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        event.getEvent().getMessage().delete().queue();
        args.poll();
        event.reply(args.stream().collect(Collectors.joining(" ")));
    }

    @Override
    public void help(EmbedBuilder me) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
        comando secreto que faz o bot dizer algo.
        
        **-diga aa**
        
        Aliases (comandos alternativos) : **diga**, **say**
        assim que o comando é entendido pelo bot ele apaga sua mensagem para enganar os fracos.
        """);
    }
}
