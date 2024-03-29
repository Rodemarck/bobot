package rode.model;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.reflections.Reflections;
import rode.core.Helper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.ResourceBundle;

public abstract class ComandoGuild extends Comando {
    private String help;
    private String helpEx;
    private boolean slash;
    private final HashMap<String, ComandoGuild> sons;
    public ComandoGuild(String comando, Permission cargo, String ... alias) {
        super(comando, alias, cargo);
        this.sons = new HashMap<>();
        this.slash = true;
        if (comando != null) {
            help = comando + ".help";
            helpEx = help + ".ex";
        }
    }
    public ComandoGuild(String comando, Permission cargo,boolean slash, String ... alias) {
        super(comando, alias, cargo);
        this.sons = new HashMap<>();
        this.slash = slash;
        if (comando != null) {
            help = comando + ".help";
            helpEx = help + ".ex";
        }
    }

    public void subscribeSlash(CommandListUpdateAction cua, ResourceBundle bundle){
        /*System.out.println(getCommand());
        cua.addCommands(
                new CommandUpdateAction.CommandData(command,bundle.getString(getHelp()))
        );*/
    }
    public void subscribeSlash(CommandData commandData, ResourceBundle bundle){

    }

    public void setPath(String path){
        help = path + ".help";
        helpEx = help + ".ex";
    }
    public String getHelp() {
        return help;
    }

    public String getHelpEx() {
        return helpEx;
    }

    public boolean free(String[] args, Helper.Mensagem event)throws Exception {
        if(permission == null)
            return true;
        return  event.getEvent().getMember().hasPermission(permission);
    }
    public abstract void execute(String[] args, Helper.Mensagem event) throws Exception;
    public void fail(String[] args, Helper.Mensagem event) throws Exception{
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

    public HashMap<String, ComandoGuild> getSons() {
        return sons;
    }

    public void findSons(Reflections reflections, Class<? extends ComandoGuild> clazz) {
        reflections.getSubTypesOf(clazz).forEach(subClass->{
            try {
                var subObject = subClass.getConstructor().newInstance();
                sons.put(subObject.getCommand(), subObject);
            } catch (InstantiationException
                    |IllegalAccessException
                    |InvocationTargetException
                    |NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void executeSlash(SlashCommandEvent slash, Helper.Slash hs){}
}
