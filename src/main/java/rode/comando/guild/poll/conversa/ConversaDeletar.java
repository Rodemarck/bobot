package rode.comando.guild.poll.conversa;

import net.dv8tion.jda.api.Permission;
import rode.core.Helper;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Memoria;

import java.util.HashMap;

public class ConversaDeletar extends MensagemReacao {

    public ConversaDeletar(Helper hr,String titulo) {
        super(hr, hr.id(), System.currentTimeMillis()+20000, Permission.ADMINISTRATOR, new HashMap<>(){{
            var guild = Memoria.guild(hr.guildId());

            guild.getPoll(titulo);
        }});
    }

    @Override
    public void acao() {

    }

    @Override
    public void rerender() {

    }
}
