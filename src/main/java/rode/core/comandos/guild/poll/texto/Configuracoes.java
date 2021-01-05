package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;

import java.util.LinkedList;

public class Configuracoes extends ComandoGuild {
    public Configuracoes() {
        super("configuracao", null, "config","configuracao","configuração","def","definicoes","definições","configuracoes","configurações","settings");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args, event,dp -> {
            if(dp.guild() != null){
                var poll = dp.guild().getPoll(dp.titulo());
                event.reply(poll.config(), message -> {
                    message.editMessage("config").submit();
                    return null;
                });
            }
        });
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-config {titulo}** : mostra informações sobre uma poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                comando para exibir informações de uma poll(enquete)
                
                **-config {titulo}**
                
                Aliases (comandos alternativos) : **config**, **configuracao**, **configuração**, **def**, **definicoes**, **definições**, **configuracoes**, **configurações**, **settings**.
                Exibe de forma compacta sobre quem criou, data de criação (preview), se ainda está aberta, se há data limite de votação, e o resultado atual.  
                """);
    }
}
