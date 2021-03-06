package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;
import java.util.ResourceBundle;

public class ApagaMensagem extends ComandoGuild {
    public ApagaMensagem() {
        super("apagaMensagem", Permission.ADMINISTRATOR, "apagaMensagem");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        args.poll();
        if(args.size() == 0)
            return;
        if(args.size() == 1) {
            hm.getEvent().getChannel().retrieveMessageById(args.getFirst()).submit()
                    .thenCompose(message -> {
                        if (message.getAuthor().getId().equals(hm.jda().getSelfUser().getId()))
                            return message.delete().submit()
                                    .thenCompose(u -> hm.mensagem().delete().submit());
                        else
                            hm.replyTemp(hm.text("apaga.exec"));
                        return null;
                    });
            return;
        }
        if(args.size() == 2){
            hm.getEvent().getGuild().getTextChannelById(args.getFirst())
                    .retrieveMessageById(args.getLast()).submit()
                    .thenCompose(message -> {
                        if (message.getAuthor().getId().equals(hm.jda().getSelfUser().getId()))
                            return message.delete().submit()
                                    .thenCompose(u-> hm.mensagem().delete().submit());
                        else
                            hm.replyTemp(hm.text("apaga.exec"));
                        return null;
                    });

            return;
        }
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        return super.livre(args, hm) || hm.id().equals("305090445283688450");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me,ResourceBundle rb) {
        me.appendDescription(rb.getString("apaga.help.ex"));
    }
}
