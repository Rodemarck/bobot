package rode.comando.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.utilitarios.Memoria;
import rode.utilitarios.Constantes;
import rode.utilitarios.Regex;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class PollReactionAdd extends ComandoGuildReacoes {
    private static Logger log = LoggerFactory.getLogger(PollReactionAdd.class);
    public PollReactionAdd() {
        super("++poll++", null, "poll+++");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Reacao event) throws IOException, Exception {
        final String tipo = args.getFirst();
        log.info("inicio");
        PollHelper.getPollFromEmote(args, event,dp -> {
            log.info("callback");
            if(dp.poll().getOpcoes().size() > dp.index()){
                if(dp.poll().hasUser(event.getId())){
                    event.getMessage().removeReaction(event.emoji(), event.getEvent().getUser()).queue(mm->{
                        if(dp.index() == dp.poll().getOriginal(event.getId())){
                            dp.poll().rem(dp.index(),event.getId());
                            event.jda().retrieveUserById(event.getId()).queue(user->{
                                event.reply("**" + user.getName() + "** seu voto foi removido de " + Constantes.POOL_votos.get(dp.index()),
                                        message -> message.delete().submitAfter(5,TimeUnit.SECONDS)
                                );
                            });
                            Memoria.guilds.updateOne(dp.query(), new Document("$set",dp.guild().toMongo()));
                            if(tipo.contains("poll"))
                                event.getMessage().editMessage(dp.poll().me()).submit();
                            else if(tipo.contains("pic")){
                                final var emb = event.getMessage().getEmbeds().get(0);
                                LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
                                int i = Integer.parseInt(param.getFirst());
                                event.getMessage().editMessage(dp.poll().visualiza(i)).submit();
                            }
                            return;
                        }
                        event.reply("**"+ event.getEvent().getUser().getName()+"** você já votou [**"+ Constantes.POOL_votos.get(dp.poll().getOriginal(event.getId()))+ "**] nessa poll!",message->
                                message.delete().submitAfter(5, TimeUnit.SECONDS)
                        );
                    });
                    return;
                }
                dp.poll().add(dp.index(), event.getId());
                event.jda().retrieveUserById(event.getId()).queue(user->{
                    event.reply("**" + user.getName() + "** seu voto foi contabilizado para" + Constantes.POOL_votos.get(dp.index()),
                            message->message.delete().submitAfter(5,TimeUnit.SECONDS));
                });
                if(tipo.contains("poll"))
                    event.getMessage().editMessage(dp.poll().me()).submit();
                else if(tipo.contains("pic")){
                    final var emb = event.getMessage().getEmbeds().get(0);
                    LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
                    int i = Integer.parseInt(param.getFirst());
                    event.getMessage().editMessage(dp.poll().visualiza(i)).submit();
                }
                Memoria.guilds.updateOne(dp.query(), new Document("$set",dp.guild().toMongo()));
                return;
            }
        });
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args,event);
    }

    @Override
    public void help(EmbedBuilder me) {

    }
}
