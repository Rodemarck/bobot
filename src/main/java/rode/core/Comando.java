package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.ResourceBundle;

public abstract class Comando {
    public String comando;
    public String[] alias;
    public Permission cargo;

    public Comando(String comando, String[] alias, Permission cargo) {
        this.comando = comando;
        this.alias = alias;
        this.cargo = cargo;
    }
    public abstract void help(EmbedBuilder me, ResourceBundle loc);
    public abstract void helpExtensive(EmbedBuilder me, ResourceBundle loc);

    protected void falha(Helper h){
        h.reply(String.format(h.text("cmd.fail"),comando,h.text(cargo.getName())));
    }
}
