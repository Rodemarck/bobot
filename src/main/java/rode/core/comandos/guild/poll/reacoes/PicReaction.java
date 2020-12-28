package rode.core.comandos.guild.poll.reacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Regex;

import java.io.IOException;
import java.util.LinkedList;

public class PicReaction {
    private static Logger log = LoggerFactory.getLogger(PicReaction.class);
    public static void executa(LinkedList<String> args, Helper.Reacao event, int n) throws IOException, Exception {
        final var emb = event.getMessage().getEmbeds().get(0);
        args.add('{' + emb.getTitle() + '}');
        PollHelper.getPoll(args,event, (titulo, opcoes, modelGuild, query) -> {
            if(modelGuild != null){
                Poll poll = modelGuild.getPoll(titulo);
                LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
                int index = Integer.parseInt(param.getFirst());
                index = (index + n);
                if(index >= poll.getOpcoes().size())
                    index = 0;
                else if (index < 0)
                    index = poll.getOpcoes().size() - 1;
                event.getMessage().editMessage(poll.visualiza(index)).submit();
            }
        });
    }
}
