package rode.model;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelGuild {
    private static Logger log = LoggerFactory.getLogger(ModelGuild.class);
    private String id;
    private LinkedList<Poll> polls;

    public ModelGuild() {
    }

    public ModelGuild(String id) {
        this.id = id;
        this.polls = new LinkedList<>();
    }


    public String getId() {
        return id;
    }

    public List<Poll> getPolls() {
        return polls;
    }

    public Document toMongo() {
        return new Document()
                .append("id", id)
                .append("polls",polls.stream().map(p->p.toMongo()).collect(Collectors.toList()));
    }

    public static ModelGuild fromMongo(Document doc){
        ModelGuild g = new ModelGuild(doc.getString("id"));
        g.polls.addAll(((List<Document>)doc.get("polls")).stream().map(d->Poll.fromMongo(d)).collect(Collectors.toList()));
        return g;
    }

    public Poll getPoll(String titulo){
        for(Poll p:polls) {
            if (p.getTitulo().equalsIgnoreCase(titulo)) {
                log.info("getPoll = ", p.toString());
                return p;
            }
        }
        log.info("getPoll : não encontrdo");
        return null;
    }

    @Override
    public String toString() {
        return "Guild{" +
                //"_id=" + _id +
                ", id=" + id +
                ", polls=" + polls +
                '}';
    }
}
