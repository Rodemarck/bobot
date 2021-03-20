package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class DeletaPoll extends ComandoGuild {
    public DeletaPoll() {
        super("deleta", Permission.MANAGE_CHANNEL, "delpoll","delete","deleta");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws IOException, Exception {

        //EventLoop2.addTexto(new MensagemTexto(hm.getEvent().getChannel(), null,"tempo expirado",System.currentTimeMillis()+20000,50,Permission.ADMINISTRATOR));

        PollHelper.getPoll(args, hm,dp->{
            if(dp.guild() != null){
                var poll = dp.guild().getPoll(dp.titulo());
                dp.guild().getPolls().remove(poll);
                Memoria.guilds.updateOne(dp.query(),new Document("$set",dp.guild().toMongo()));
                hm.reply(String.format(hm.text("del.exec.delete"), dp.titulo()), message -> message.addReaction(Constantes.emote("check")).queue());
                return;
            }
            hm.reply(String.format(hm.text("del.exec.404"), dp.titulo()));
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || PollHelper.livreDono(args, event);
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("del.help"));
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        me.appendDescription(rb.getString("del.help.ex"));
    }
}
