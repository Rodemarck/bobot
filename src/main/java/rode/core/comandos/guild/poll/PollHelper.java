package rode.core.comandos.guild.poll;

import net.dv8tion.jda.api.entities.Message;
import org.bson.Document;
import rode.core.Helper;
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
    private PollHelper(){}
    public static CompletionStage<Void> addReaction(Message m, int n){
        m.editMessage("poll").queue();
        for(int i=0; i<n; i++){
            m.addReaction(Constantes.POOL_EMOTES.get(i)).queue();
        }
        return null;
    }
    public static void getPoll(LinkedList<String> args, Helper event, PollFunction pf) throws IOException {
        args.poll();
        String txt = args.stream().collect(Collectors.joining(" "));
        String titulo = Regex.extract("\\{([^\\}])+\\}", txt).getFirst();
        LinkedList<String> opcoes = new LinkedList<>();
        for(String s:Regex.extract("\\[([^\\]])+\\]", txt))
            if(!opcoes.contains(s))
                opcoes.add(s);
        Document query = new Document("id", event.guildId()).append("polls.titulo",titulo);
        Document doc = Memoria.guilds.find(query).first();


        ModelGuild g = null;
        if(doc != null)
            g = ModelGuild.fromMongo(doc);
        pf.apply(titulo,opcoes,g, query);
    }

    public static boolean livre(LinkedList<String> args, Helper.Mensagem event){
        LinkedList<String>args2 = new LinkedList<>(args);
        args2.poll();
        String txt = args.stream().collect(Collectors.joining(" "));
        String titulo = Regex.extract("\\{([^\\}])+\\}", txt).getFirst();

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
