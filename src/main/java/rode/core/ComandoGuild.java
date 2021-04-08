package rode.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;

import java.util.LinkedList;
import java.util.ResourceBundle;

public abstract class ComandoGuild extends Comando{
    private String help;
    private String helpEx;
    private boolean slash;
    public ComandoGuild(String comando, Permission cargo, String ... alias) {
        super(comando, alias, cargo);
        this.slash = false;
        if (comando != null) {
            help = comando + ".help";
            helpEx = help + ".ex";
        }
    }
    public ComandoGuild(String comando, Permission cargo,boolean slash, String ... alias) {
        super(comando, alias, cargo);
        this.slash = slash;
        if (comando != null) {
            help = comando + ".help";
            helpEx = help + ".ex";
        }
    }

    public void subscribeSlash(CommandUpdateAction cua, ResourceBundle bundle){
        cua.addCommands(
                new CommandUpdateAction.CommandData(command,bundle.getString(getHelp()))
                        .addOption(new CommandUpdateAction.OptionData(Op))
        );
    }

    public String getHelp() {
        return help;
    }

    public String getHelpEx() {
        return helpEx;
    }

    public boolean free(LinkedList<String> args, Helper.Mensagem event)throws Exception {
        if(permission == null)
            return true;
        return  event.getEvent().getMember().hasPermission(permission);
    }
    public abstract void execute(LinkedList<String> args, Helper.Mensagem event) throws Exception;
    public void fail(LinkedList<String> args, Helper.Mensagem event) throws Exception{
        super.fail(event);
    }

    public void help(EmbedBuilder me, ResourceBundle rb) {
        if(help != null)
            me.appendDescription(rb.getString(help));
    }

    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        if(helpEx != null)
            me.appendDescription(rb.getString(helpEx));
    }

    public boolean isSlash(){
        return slash;
    }
}
