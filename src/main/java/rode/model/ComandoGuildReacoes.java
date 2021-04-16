package rode.model;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.Helper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

public abstract class ComandoGuildReacoes extends Comando {
    public ComandoGuildReacoes(String comando, Permission cargo, String... alias) {
        super(comando, alias, cargo);
    }

    public boolean livre(String[] args, Helper.Reacao event) throws IOException{
        return true;
    }
    public abstract void executa(String[] args, Helper.Reacao event) throws IOException, Exception;

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {

    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle loc) {

    }

    public void falha(String[] args, Helper.Reacao event) throws Exception{
        super.fail(event);
    }
}
