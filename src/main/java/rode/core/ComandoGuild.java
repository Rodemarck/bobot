package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.LinkedList;

public abstract class ComandoGuild extends Comando{

    public ComandoGuild(String comando, Permission cargo, String ... alias) {
        super(comando, alias, cargo);
    }

    public boolean livre(LinkedList<String> args, Helper.Mensagem event)throws Exception {
        if(cargo == null)
            return true;
        return  event.getEvent().getMember().hasPermission(cargo);
    }
    public abstract void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception;
    public void falha(LinkedList<String> args, Helper.Mensagem event) throws Exception{
        super.falha(event);
    }
}
