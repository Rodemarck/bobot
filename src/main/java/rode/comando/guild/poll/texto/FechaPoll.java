package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.LinkedList;

public class FechaPoll extends ComandoGuild {
    public FechaPoll() {
        super("fecha", Permission.ADMINISTRATOR, "fecha","close");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args,event,dp -> {
            if(dp.guild() != null){
                if(!dp.poll().isAberto()){
                    event.replyTemp("esta poll já está fechada");
                    return;
                }
                dp.poll().fecha();
                Memoria.update(dp);
                event.reply("a poll {**" + dp.query() + "**} foi fechada", message -> message.addReaction(Constantes.emote("check")).submit());
            }
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || PollHelper.livreDono(args, event);
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-fechar {titulo}** : fecha uma poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para fechar uma poll (enquete)
                
                **-fecha {titulo}**
                
                Aliases (comandos alternativos) : **fecha**, **close**
                """);

    }
}
