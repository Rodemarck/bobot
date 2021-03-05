package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;

public class ApagaMensagem extends ComandoGuild {
    public ApagaMensagem() {
        super("apagaMensagem", Permission.ADMINISTRATOR, "apagaMensagem");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        args.poll();
        if(args.size() == 0)
            return;
        if(args.size() == 1) {
            event.getEvent().getChannel().retrieveMessageById(args.getFirst()).submit()
                    .thenCompose(message -> {
                        if (message.getAuthor().getId().equals(event.jda().getSelfUser().getId()))
                            return message.delete().submit()
                                    .thenCompose(u -> event.mensagem().delete().submit());
                        else
                            event.replyTemp("tenho vergonha apagar mensagem dos outros...:point_right: :point_left:.");
                        return null;
                    });
            return;
        }
        if(args.size() == 2){
            event.getEvent().getGuild().getTextChannelById(args.getFirst())
                    .retrieveMessageById(args.getLast()).submit()
                    .thenCompose(message -> {
                        if (message.getAuthor().getId().equals(event.jda().getSelfUser().getId()))
                            return message.delete().submit()
                                    .thenCompose(u-> event.mensagem().delete().submit());
                        else
                            event.replyTemp("tenho vergonha apagar mensagem dos outros...:point_right: :point_left:.");
                        return null;
                    });

            return;
        }
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || event.id().equals("305090445283688450");
    }

    @Override
    public void help(EmbedBuilder me) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando secreto que faz o bot apagar uma das minhas mensagem
                
                **-apagaMensagem id**
                
               
                Pode ser útil para organizar, eu espero.
                """);
    }
}
