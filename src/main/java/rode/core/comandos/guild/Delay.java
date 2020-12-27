package rode.core.comandos.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.UseComande;

import java.util.LinkedList;
import java.util.stream.Collectors;

@UseComande
public class Delay extends ComandoGuild {
    public Delay() {
        super("delay", null, "delay");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        long milis = 0;
        args.poll();
        String fst = args.poll();
        try{
            milis = Long.parseLong(fst);
        }catch (Exception e){
            event.reply("não entendi esse tempo esse tempo");
            throw e;
        }
        String msg = args.stream().collect(Collectors.joining(" "));
        Thread.sleep(milis);
        event.reply(msg);
    }

    @Override
    public void help(EmbedBuilder me) {

    }

    @Override
    protected void falha(Helper event) {

    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return event.getEvent().getAuthor().getId().equals("305090445283688450");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {

    }
}
