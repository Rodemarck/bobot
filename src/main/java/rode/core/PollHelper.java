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
import java.util.concurrent.TimeUnit;
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
    public static void getPoll(LinkedList<String> args, Helper helper, PollFunction pf) throws IOException {
        args.poll();
        String txt = args.stream().collect(Collectors.joining(" "));
        LinkedList<String> param = Regex.extractInside("\\{([^\\}])+\\}", txt);
        if(param.size() == 0){
            log.info("não achei nada em {}", txt);
            pf.apply(new DadosPoll(null, null,null,null,null,0));
            return;
        }
        String titulo =param.getFirst();
        LinkedList<String> opcoes = new LinkedList<>();
        for(String s:Regex.extractInside("\\[([^\\]])+\\]", txt))
            if(!opcoes.contains(s))
                opcoes.add(s);

        Document query = new Document("id", helper.guildId()).append("polls.titulo",titulo);
        Document doc = Memoria.guilds.find(query).first();

        if(doc != null){
            ModelGuild g = ModelGuild.fromMongo(doc);
            pf.apply(new DadosPoll(titulo,args,g,query,null,0));
        }

    }
    public static void getPollFromEmote(LinkedList<String> args, Helper.Reacao helper, PollFunction function) throws IOException {
        final String tipo = args.getFirst();
        log.info("do tipo {}", tipo);
        if(Constantes.POOL_EMOTES.contains(helper.emoji())) {
            int index = Constantes.POOL_EMOTES.indexOf(helper.emoji());
            String titulo = helper.getMessage().getEmbeds().get(0).getTitle();
            args.add('{' + titulo + '}');
            log.info("titulo da poll {}", titulo);
            getPoll(args, helper, dp -> {
                if(dp.guild() == null){
                    helper.reply("sem registo da poll **" + titulo + "**", message -> message.delete().submitAfter(5,TimeUnit.SECONDS));
                    return;
                }
                Poll poll = dp.guild().getPoll(dp.titulo());
                if(!poll.isAberto()){
                    helper.reply("a poll {**" + poll.getTitulo() + "**} foi fechada", message -> message.delete().submitAfter(5, TimeUnit.SECONDS));
                    return;
                }
                function.apply(new DadosPoll(dp.titulo, dp.opcoes, dp.guild, dp.query, dp.poll, index));
            });
        }
        else
            helper.reply("**" + helper.getEvent().getUser().getName() + "** pare de trolar," + helper.emoji() + " não é uma opção para essa poll.", message->
                    message.delete().submitAfter(15, TimeUnit.SECONDS)
            );
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
        System.out.println("doc = " + doc);
        if(doc != null) {
            System.out.println("not");
            ModelGuild g = ModelGuild.fromMongo(doc);
            Poll poll = g.getPoll(titulo);
            System.out.println("poll = " + poll);
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

    public static class DadosPoll{
        private String titulo;
        private LinkedList<String> opcoes;
        private ModelGuild guild;
        private Document query;
        private Poll poll;
        private int index;

        public DadosPoll(String titulo, LinkedList<String> opcoes, ModelGuild guild, Document query, Poll poll, int index) {
            this.titulo = titulo;
            this.opcoes = opcoes;
            this.guild = guild;
            this.query = query;
            this.poll = poll;
            this.index = index;
        }

        public String titulo() {
            return titulo;
        }

        public void titulo(String titulo) {
            this.titulo = titulo;
        }

        public LinkedList<String> opcoes() {
            return opcoes;
        }

        public void opcoes(LinkedList<String> opcoes) {
            this.opcoes = opcoes;
        }

        public ModelGuild guild() {
            return guild;
        }

        public void guild(ModelGuild guild) {
            this.guild = guild;
        }

        public Document query() {
            return query;
        }

        public void query(Document query) {
            this.query = query;
        }

        public Poll poll() {
            return poll;
        }

        public void poll(Poll poll) {
            this.poll = poll;
        }

        public int index() {
            return index;
        }

        public void index(int index) {
            this.index = index;
        }
    };
    public interface PollFunction{
        void apply(DadosPoll dp) throws IOException;
    }
}
