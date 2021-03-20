package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class ListarPolls extends ComandoGuild {

    public ListarPolls() {
        super("listar", null, "lpoll","list","listar","listpoll","listarpoll");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws IOException, Exception {
        Document doc = Memoria.guilds.find(new Document("id", hm.guildId())).first();
        if(doc != null){
            ModelGuild g = ModelGuild.fromMongo(doc);

            EmbedBuilder eb = Constantes.builder();
            eb.setTitle("polls abertas");
            for(Poll p: g.getPolls()) {
                String t = p.titulo();
                eb.appendDescription(String.format(hm.text("list.exec.line"), t, p.criadorId()));
            }
            hm.reply(eb);
            return;
        }
        hm.reply(hm.text("list.exec.empty"));
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("list.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("list.help.ex"));
    }
}
