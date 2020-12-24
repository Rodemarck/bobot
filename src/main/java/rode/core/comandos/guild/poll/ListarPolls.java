package rode.core.comandos.guild.poll;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.model.ModelGuild;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;
import rode.model.Poll;

public class ListarPolls extends ComandoGuild {

    public ListarPolls() {
        super("listar", null, "lpoll","list","listar","listpoll","listarpoll");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException, Exception {
        System.out.println("listar");
        Document doc = Memoria.guilds.find(new Document("id", event.guildId())).first();
        if(doc != null){
            System.out.println("tem     guild");
            ModelGuild g = ModelGuild.fromMongo(doc);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("polls abertas");
            for(Poll p: g.getPolls()) {
                String t = p.getTitulo();
                eb.appendDescription("{**" + t + "**} feita por **<@" + p.getCriadorId() + ">**\n\n");
            }
            event.reply(eb);
            return;
        }
        event.reply("nenhuma poll achada.");
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-listar**: lista todas as polls existentes.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("Comando para listar todas as polls (enquetes) abertas neste servidor.\n\n");
        me.appendDescription("**-listar**.\n\n");
        me.appendDescription("Aliases (comandos alternativos) : **lpoll**, **list**, **listar**, **listpoll**, **listarpoll**");
    }
}
