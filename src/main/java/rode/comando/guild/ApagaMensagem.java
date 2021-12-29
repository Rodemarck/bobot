package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.Helper;
import rode.model.ComandoGuild;

import java.util.ResourceBundle;

@EcomandoGeral
public class ApagaMensagem extends ComandoGuild {
    public ApagaMensagem() {
        super("apaga", Permission.ADMINISTRATOR, "apagaMensagem");
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        if(args.length < 2)
            return;
        if(args.length == 2) {
            hm.getEvent().getChannel().retrieveMessageById(args[1]).queue(message -> {
                if (message.getAuthor().getId().equals(hm.jda().getSelfUser().getId()))
                    message.delete().queue(u ->
                            hm.getMensagem().delete().queue()
                    );
                else
                    hm.replyTemp(hm.getText("apaga.exec"));
            });
        }
        if(args.length == 3){
            hm.getEvent().getGuild().getTextChannelById(args[1])
                    .retrieveMessageById(args[2]).queue(message -> {
                if (message.getAuthor().getId().equals(hm.jda().getSelfUser().getId()))
                    message.delete().queue(u-> hm.getMensagem().delete().queue());
                else
                    hm.replyTemp(hm.getText("apaga.exec"));
            });

            return;
        }
    }

    @Override
    public boolean free(String[] args, Helper.Mensagem hm) throws Exception {
        return super.free(args, hm) || hm.getId().equals("305090445283688450");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {

    }
}
