package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.Executador;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.io.IOException;
import java.util.LinkedList;

public class Tutorial extends ComandoGuild {
    private static EmbedBuilder tutorial;

    public Tutorial() {
        super("tutorial", null, "help","tutorial","ajuda");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException {
        args.poll();
        if(args.isEmpty()){
            if(tutorial == null){
                tutorial = new EmbedBuilder();
                tutorial.setTitle("Tutorial do bot","https://cdn.discordapp.com/avatars/305090445283688450/b1af2bade4b94a08e31091db153c4aae.png");
                tutorial.appendDescription("Tutorial para utilização do bot mais útil do Brasil.\n");
                tutorial.appendDescription("Para instruções mais detalhadas utilize **-tutorial comando**.\n\n");

                Executador.COMANDOS_GUILD.forEach((id,comando)-> comando.help(tutorial));
            }
            event.reply(tutorial.build());
            return;
        }
        if(Executador.NOME_COMANDOS_GUILD.containsKey(args.getFirst())){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Tutorial do bot **[" + args.getFirst() + "**]");
            Executador.COMANDOS_GUILD.get(Executador.NOME_COMANDOS_GUILD.get(args.getFirst())).helpExtensive(eb);
            event.reply(eb);
            return;
        }
        event.reply("comando **" + args.getFirst() + "** não encontado.");
    }
    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-tutorial comando** : exibe uma explicação detalhada de um comando.");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Tutorial para utilização do (futuro) bot mais útil do mundo (mas que no momneto só faz enquetes).
                
                **-tutorial comando**.
                
                exibe uma explicação detalhada de um comando.
                """);
    }
}
