package rode.comando.guild.domotica;

import net.dv8tion.jda.api.Permission;
import rode.core.Anotacoes.EComandoDomotica;
import rode.core.Helper;
import rode.core.domotica.GerenciadoSessao;
import rode.model.ComandoGuild;

@EComandoDomotica
public class ModificaEstado extends ComandoGuild {
    public ModificaEstado() {
        super("modifica_estado", null, "modifica_estado","change_state","ligar","turn-on","desligar","turn-off");
    }

    @Override
    public void execute(String[] args, Helper.Mensagem msg) throws Exception {
        if(GerenciadoSessao.logado(msg)){

        }
        msg.reply(msg.getText(""));
    }
}
