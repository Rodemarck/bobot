package rode.core;

import net.dv8tion.jda.api.entities.Message;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.comandos.guild.poll.reacoes.PollReactionAdd;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;
import rode.utilitarios.Regex;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public final class PollHelper {
    private static Logger log = LoggerFactory.getLogger(PollHelper.class);
    private PollHelper(){}
    public static CompletionStage<Void> addReaction(Message m, int n){
        for(int i=0; i<n; i++){
            m.addReaction(Constantes.POOL_EMOTES.get(i)).submit();
        }
        return null;
    }
    public static void getPoll(LinkedList<String> args, Helper event, PollFunction pf) throws IOException {
        args.poll();
        String txt = args.stream().collect(Collectors.joining(" "));
        LinkedList<String> param = Regex.extractInside("\\{([^\\}])+\\}", txt);
        if(param.size() == 0){
            log.info("não achei nada em {}", txt);
            pf.apply(null, null,null,null);
            return;
        }
        String titulo =param.getFirst();
        LinkedList<String> opcoes = new LinkedList<>();
        for(String s:Regex.extractInside("\\[([^\\]])+\\]", txt))
            if(!opcoes.contains(s))
                opcoes.add(s);
        Document query = new Document("id", event.guildId()).append("polls.titulo",titulo);
        Document doc = Memoria.guilds.find(query).first();


        ModelGuild g = null;
        if(doc != null)
            g = ModelGuild.fromMongo(doc);
        pf.apply(titulo,opcoes,g, query);
    }

    public static boolean livreSiMesmo(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return event.getMessage().getAuthor().getId().equals(event.getEvent().getJDA().getSelfUser().getId());
    }
    public static boolean livreDono(LinkedList<String> args, Helper.Mensagem event){
        LinkedList<String>args2 = new LinkedList<>(args);
        args2.poll();
        String txt = args.stream().collect(Collectors.joining(" "));
        String titulo = Regex.extractInside("\\{([^\\}])+\\}", txt).getFirst();

        Document query = new Document("id",event.guildId()).append("polls.titulo",titulo);
        Document doc = Memoria.guilds.find(query).first();
        if(doc != null) {
            ModelGuild g = ModelGuild.fromMongo(doc);
            Poll poll = g.getPoll(titulo);
            if(poll.getCriadorId().equals(event.getId()))
                return true;
            event.getEvent().getJDA().retrieveUserById(poll.getCriadorId()).submit()
                    .thenCompose(u ->{
                        event.reply("pertence a " + u.getName());
                        return null;
                    });
        }
        return false;
    }


    public interface PollFunction{
        void apply(String titulo, LinkedList<String> opcoes, ModelGuild modelGuild, Document query) throws IOException;
    }
}
