package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import rode.core.ComandoGuild;
import rode.core.EventLoop2;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.maker.MensagemTexto;

import java.io.IOException;
import java.util.LinkedList;

public class DeletaPoll extends ComandoGuild {
    public DeletaPoll() {
        super("deleta", Permission.MANAGE_CHANNEL, "delpoll","delete","deleta");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws IOException, Exception {

        EventLoop2.addTexto(new MensagemTexto(event.getEvent().getChannel(), null,"tempo expirado",System.currentTimeMillis()+20000,50,Permission.ADMINISTRATOR));
        /*
        PollHelper.getPoll(args, event,dp->{
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                dp.guild().getPolls().remove(poll);
                Memoria.guilds.updateOne(dp.query(),new Document("$set",dp.guild().toMongo()));
                event.reply("a poll {**" + dp.titulo() + "**} foi deletada", message -> message.addReaction(Constantes.emote("check")).submit());
                return;
            }
            event.reply("a poll {**" + dp.titulo() + "**} não foi encontrada");
        });*/
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.livre(args, event) || PollHelper.livreDono(args, event);
    }

    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-deleta {titulo}**: deleta uma poll especifica.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para deletar uma poll (enquete) aberta neste servidor.
                
                **-deleta {titulo}***
                
                Aliases (comandos alternativos) : **delpoll**, **delete**, **deleta**.
                """);
    }
}
