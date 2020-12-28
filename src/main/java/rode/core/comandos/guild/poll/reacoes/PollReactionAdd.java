package rode.core.comandos.guild.poll.reacoes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuildReacoes;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;
import rode.utilitarios.Memoria;
import rode.utilitarios.Constantes;

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
        if(Constantes.POOL_EMOTES.contains(event.emoji())){
            int index = Constantes.POOL_EMOTES.indexOf(event.emoji());
            String titulo = event.getMessage().getEmbeds().get(0).getTitle();
            args.add('{' + titulo + '}');
            log.info("titulo da poll {}", titulo);
            PollHelper.getPoll(args, event, (titulo1, opcoes, guild, query) -> {
                if(guild != null){
                    Poll poll = guild.getPoll(titulo);
                    if(!poll.isAberto()){
                        event.reply("a poll {**" + poll.getTitulo() + "**} foi fechada", message -> message.delete().submitAfter(5,TimeUnit.SECONDS));
                        return;
                    }
                    if(poll.getOpcoes().size() > index){
                        if(poll.hasUser(event.getId())){
                            event.getMessage().removeReaction(event.emoji(), event.getEvent().getUser()).queue(mm->{
                                if(index == poll.getOriginal(event.getId())){
                                    poll.rem(index,event.getId());
                                    Memoria.guilds.updateOne(query, new Document("$set",guild.toMongo()));
                                    event.getMessage().editMessage(poll.me()).queue();
                                    return;
                                }
                                event.reply("**"+ event.getEvent().getUser().getName()+"** você já votou [**"+poll.getOpcoes().get(poll.getOriginal(event.getId()))+ "**] nessa poll!",message->
                                        message.delete().submitAfter(15, TimeUnit.SECONDS)
                                );
                            });
                            return;
                        }
                        poll.add(index, event.getId());
                        event.getMessage().editMessage(poll.me()).queue();
                        Memoria.guilds.updateOne(query, new Document("$set",guild.toMongo()));
                        return;
                    }
                }else {
                    event.reply("poll **{" + titulo + "}** não encontrada");
                }
            });
            return;
        }
        event.reply("**" + event.getEvent().getUser().getName() + "** pare de trolar," + event.emoji() + " não é uma opção para essa poll.", message->
            message.delete().submitAfter(15, TimeUnit.SECONDS)
        );
    }

    @Override
    public boolean livre(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return PollHelper.livreSiMesmo(args,event);
    }

    @Override
    public void help(EmbedBuilder me) {

    }
}
