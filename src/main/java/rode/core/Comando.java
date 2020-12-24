package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public abstract class Comando {
    public String comando;
    public String[] alias;
    public Permission cargo;

    public Comando(String comando, String[] alias, Permission cargo) {
        this.comando = comando;
        this.alias = alias;
        this.cargo = cargo;
    }
    public abstract void help(EmbedBuilder me);
    public abstract void helpExtensive(EmbedBuilder me);

    protected void falha(Helper event){
        event.reply("o comando **" + comando + "** requer permissão que você não tem");
    }
}
