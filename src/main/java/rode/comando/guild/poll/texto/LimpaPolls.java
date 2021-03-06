package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.comando.guild.poll.conversa.ConversaLimpar;
import rode.core.ComandoGuild;
import rode.core.EventLoop2;
import rode.core.Helper;
import rode.utilitarios.Memoria;

import java.awt.*;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class LimpaPolls extends ComandoGuild {
    public LimpaPolls() {
        super("limpa", Permission.ADMINISTRATOR, "limpa","limpar","lpoll");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("limpar.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("limpar.help.ex"));
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        var guild = Memoria.guild(hm.guildId());
        if(!guild.getPolls().isEmpty())
            hm.reply(new EmbedBuilder().setColor(Color.decode("#C8A2C8")).setTitle("deletar esta poll?").appendDescription("**carregando...**").setFooter("**1/"+(guild.getPolls().size()+1)+"**"), message -> {
                hm.mensagem(message);
                EventLoop2.addReacao(new ConversaLimpar(hm));
                return null;
            });
        else
            hm.reply(hm.text("limpar.exec"));
    }
}
