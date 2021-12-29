package rode.model;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.utilitarios.Memoria;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
@Data
public class ModelGuild {
    private static Logger log = LoggerFactory.getLogger(ModelGuild.class);
    private String id;
    private String chat;
    private LinkedList<Poll> polls;

    public ModelGuild() {
    }

    public ModelGuild(String id) {
        this.id = id;
        this.polls = new LinkedList<>();
        this.chat = null;
    }

    public Document toMongo() {
        return new Document()
                .append("id", id)
                .append("chat", chat)
                .append("polls",polls.stream().map(p->p.toMongo()).collect(Collectors.toList()));
    }

    public static ModelGuild fromMongo(Document doc){
        ModelGuild g = new ModelGuild(doc.getString("id"));
        g.chat = doc.getString("chat");
        g.polls.addAll(((List<Document>)doc.get("polls")).stream().map(d->Poll.fromMongo(d)).collect(Collectors.toList()));
        return g;
    }

    public Poll getPoll(String titulo){
        for(Poll p:polls) {
            if (p.getTitulo().equalsIgnoreCase(titulo)) {
                return p;
            }
        }
        log.debug("getPoll : não encontrdo");
        return null;
    }

    @Override
    public String toString() {
        return "Guild{" +
                ", id=" + id +
                ", polls=" + polls +
                '}';
    }

    public void notify_(JDA jda) {
        var lista = new LinkedList<Poll>();
        for(var p:polls)
            if(!p.isOpen())
                lista.add(p);
        if(!lista.isEmpty()) {
            var c = Memoria.config(id);
            var canal = jda.getTextChannelById(c.canalPoll());

        }
    }
}
