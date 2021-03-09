package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.aviso.Aviso;
import rode.core.ComandoGuild;
import rode.core.EventLoop2;
import rode.core.Helper;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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
        EventLoop2.addAviso(new Aviso(hm.getEvent().getChannel(), LocalDateTime.now().plusSeconds(20)) {
            @Override
            public void acao() {
                canal().sendMessage("viado do caralho").submit();
            }
        });

    }
}
