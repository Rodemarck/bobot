package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.ResourceBundle;

public abstract class Comando {
    public String command;
    public String[] alias;
    public Permission permission;

    public Comando(String comando, String[] alias, Permission permission) {
        this.command = comando;
        this.alias = alias;
        this.permission = permission;
    }

    public String getCommand() {
        return command;
    }

    public abstract void help(EmbedBuilder me, ResourceBundle loc);
    public abstract void helpExtensive(EmbedBuilder me, ResourceBundle loc);

    protected void fail(Helper h){
        h.reply(String.format(h.text("cmd.fail"), command,h.text(permission.getName())));
    }
}
