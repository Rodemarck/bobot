package rode.comando.guild.poll.conversa;

import net.dv8tion.jda.api.Permission;
import rode.core.Helper;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Memoria;

import java.util.HashMap;
import java.util.ResourceBundle;

public class ConversaDeletar extends MensagemReacao {
    private String titulo;
    public ConversaDeletar(Helper hr,String titulo) {
        super(hr,hr.mensagem(), hr.id(), System.currentTimeMillis()+20000, Permission.ADMINISTRATOR, null);
        this.titulo = titulo;
        src(new HashMap<>(){{
            var guild = Memoria.guild(hr.guildId());
            var poll = guild.getPoll(titulo);
        }});
    }

    @Override
    public void acao() {
        mensagem().getTextChannel().sendMessage("").queue();
        mensagem().delete().queue();

    }

    @Override
    public void rerender(ResourceBundle rb) {

    }

    private void deleta(String titulo){

    }

}
