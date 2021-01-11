package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;

import java.util.LinkedList;

public class VisualizaFotosPoll extends ComandoGuild {
    public VisualizaFotosPoll() {
        super("vizualizar", null, "vizualiza", "vizualizar", "view");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args,event, dp -> {
            if(dp.guild() != null){
                event.reply("pic", message ->
                        message.editMessage(dp.poll().visualiza(0)).submit()
                        .thenCompose(message1 -> {
                            message1.addReaction(Constantes.EMOTES.get("esquerda")).submit()
                                .thenRun(()->message1.addReaction(Constantes.EMOTES.get("direita")).submit())
                                .thenRun(()->PollHelper.addReaction(message1, dp.poll().getOpcoes().size()));
                            return null;
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
                
                **-vizualiza {titulo}**
                
                Aliases (comandos alternativos) : **vizualiza**, **vizualizar**, **view**
                Se a opção for um link para imagem, ela será carregada.
                É possivel votar utilizando reações, ou usando comando **votar**
                Use :arrow_left:  e :arrow_right: para navegar entre os votos.
                """);
    }
}
