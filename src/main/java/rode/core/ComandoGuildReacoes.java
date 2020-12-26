package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.io.IOException;
import java.util.LinkedList;

public abstract class ComandoGuildReacoes extends Comando{
    public ComandoGuildReacoes(String comando, Permission cargo, String... alias) {
        super(comando, alias, cargo);
    }

    public boolean livre(LinkedList<String> args, Helper.Reacao event) throws IOException{
        return true;
    }
    public abstract void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception;

    @Override
    public void helpExtensive(EmbedBuilder me) {
        help(me);
    }
    public void falha(LinkedList<String> args, Helper.Reacao event) throws Exception{
        super.falha(event);
    }
}