package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.core.UseComande;

import java.util.LinkedList;

@UseComande
public class Configuracoes extends ComandoGuild {
    public Configuracoes() {
        super("configuracao", null, "config","configuracao","configuração","def","definicoes","definições","configuracoes","configurações","settings");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args, event,(titulo, opcoes, modelGuild, query) -> {
            if(modelGuild != null){
                var poll = modelGuild.getPoll(titulo);
                event.reply(poll.config(), message -> {
                    message.editMessage("config").submit();
                    return null;
                });
            }
        });
    }

    @Override
    public void help(EmbedBuilder me) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me) {

    }
}
