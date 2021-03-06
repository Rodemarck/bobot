package rode.comando.guild.oculto;

import net.dv8tion.jda.api.EmbedBuilder;
import rode.core.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.core.IgnoraComando;
import rode.utilitarios.Constantes;

import java.util.LinkedList;
import java.util.ResourceBundle;

@IgnoraComando
public class DH extends ComandoGuild {
    public DH() {
        super("dump heap", null, "rode-dump");
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        System.out.println(event.getEvent().getAuthor().getName());
        return event.getEvent().getAuthor().getId().equals(Constantes.env.get("dono"));
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {

    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
       event.dm(EventLoop.size() + "instancias");
    }
}
