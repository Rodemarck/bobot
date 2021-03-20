package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import rode.aviso.Aviso;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.ComandoGuild;
import rode.core.EventLoop2;
import rode.core.Helper;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EcomandoGeral
public class Sexo extends ComandoGuild {
    public Sexo() {
        super("sexo", null, "sexo");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        EventLoop2.addAviso(new Aviso(hm.getEvent().getChannel().getId(), LocalDateTime.now().plusSeconds(20)) {
            @Override
            public boolean acao(JDA jda) {
                jda.getTextChannelById(canal())
                .sendMessage("viado do caralho").queue();
                return true;
            }
        });

    }
}
