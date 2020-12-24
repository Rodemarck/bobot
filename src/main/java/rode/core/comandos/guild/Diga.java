package rode.core.comandos.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.Helper;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Diga extends ComandoGuild {
    public Diga() {
        super("diz", null,"diz","diga", "say");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        args.poll();
        event.reply(args.stream().collect(Collectors.joining(" ")));
        event.getEvent().getMessage().delete().queue();
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return event.getEvent().getAuthor().getId().equals("305090445283688450");
    }

    @Override
    protected void falha(Helper event) {

    }

    @Override
    public void help(EmbedBuilder me) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me) {

    }
}
