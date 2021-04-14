package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.LinkedList;

@EComandoPoll
public class FechaPoll extends ComandoGuild {
    public FechaPoll() {
        super("fecha", Permission.ADMINISTRATOR, "fecha","close");
    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args,hm,dp -> {
            if(dp.guild() != null){
                if(!dp.poll().isOpen()){
                    hm.replyTemp(String.format(hm.text("fecha.exec.already"),dp.titulo()));
                    return;
                }
                dp.poll().close();
                Memoria.update(dp);
                hm.reply(String.format(hm.text("fecha.exec.close"),dp.titulo()), message -> message.addReaction(Constantes.emote("check")).queue());
            }
        });
    }

    @Override
    public boolean free(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        return super.free(args, event) || PollHelper.livreDono(args, event);
    }
}
