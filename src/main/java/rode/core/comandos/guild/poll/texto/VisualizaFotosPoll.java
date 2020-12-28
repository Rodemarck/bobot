package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Constantes;

import java.util.LinkedList;

public class VisualizaFotosPoll extends ComandoGuild {
    public VisualizaFotosPoll() {
        super("vizualizar", null, "vizualiza", "vizualizar", "view");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args,event, (titulo, opcoes, modelGuild, query) -> {
            if(modelGuild != null){
                Poll poll = modelGuild.getPoll(titulo);
                event.reply("pic", message ->
                        message.editMessage(poll.visualiza(0)).submit()
                        .thenCompose(message1 -> {
                            message1.addReaction(Constantes.EMOTES.get("esquerda")).submit();
                            return message1.addReaction(Constantes.EMOTES.get("direita")).submit();
                        })
                );
            }
        });
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-vizualiza {titulo}** : exibe as opções de uma poll, uma a uma.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para exibir as opções de uma poll de forma mais detalhada
                
                **-fotos {titulo}**
                
                Aliases (comandos alternativos) : **vizualiza**, **vizualizar**, **view**
                Se a opção for um link para imagem, ela será carregada.
                Só é possivel votar utilizando o comando **votar**
                Use :arrow_left:  e :arrow_right: para navegar entre os votos.
                """);
    }
}
