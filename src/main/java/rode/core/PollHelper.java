package rode.core;

import net.dv8tion.jda.api.entities.Message;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            m.addReaction(Constantes.emotePoll(i)).submit();
        }
        return null;
    }
    public static void getPoll(LinkedList<String> args, Helper helper, PollFunction pf) throws IOException {
        args.poll();
        String txt = args.stream().collect(Collectors.joining(" "));
        LinkedList<String> param = Regex.extractInside("\\{([^\\}])+\\}", txt);
        if(param.size() == 0){
            log.debug("poll [{}] não encontrada", txt);
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
            pf.apply(new DadosPoll(titulo,opcoes,g,query,g.getPoll(titulo),0));
            return;
        }
        pf.apply(new DadosPoll(titulo,opcoes,null,query,null,0));

    }
    public static void getPollFromEmote(LinkedList<String> args, Helper.Reacao helper, PollFunction function) throws IOException {
        final String tipo = args.getFirst();
        log.debug("do tipo {}", tipo);
        if(Constantes.POOL_EMOTES.contains(helper.emoji())) {
            int index = Constantes.POOL_EMOTES.indexOf(helper.emoji());
            String titulo = helper.mensagem().getEmbeds().get(0).getTitle();
            args.add('{' + titulo + '}');
            log.debug("titulo da poll {}", titulo);
            getPoll(args, helper, dp -> {
                if(dp.guild() == null){
                    helper.replyTemp(String.format(helper.text("helper.404"),titulo));
                    return;
                }
                Poll poll = dp.guild().getPoll(dp.titulo());
                if(!poll.aberto()){
                    helper.replyTemp(String.format(helper.text("helper.close"),poll.titulo()));
                    return;
                }
                function.apply(new DadosPoll(dp.titulo, dp.opcoes, dp.guild, dp.query, dp.poll, index));
            });
        }
        else
            helper.reply(String.format(helper.text("helper.troll"),helper.getEvent().getUser().getName(),helper.emoji()), message->
                    message.delete().submitAfter(15, TimeUnit.SECONDS)
                    .thenRunAsync(()->helper.mensagem().clearReactions(helper.emoji()).submit())
            );
    }
    public static boolean livreSiMesmo(LinkedList<String> args, Helper.Reacao event) throws IOException {
        return event.mensagem().getAuthor().getId().equals(event.jda().getSelfUser().getId());
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
            if(poll.criadorId().equals(event.id()))
                return true;
            event.jda().retrieveUserById(poll.criadorId()).submit()
                    .thenCompose(u ->{
                        event.reply("pertence a " + u.getName());
                        return null;
                    });
        }
        return false;
    }

    public static void contaVoto(Helper.Reacao hr, int index) {
        hr.jda().retrieveUserById(hr.id()).queue(user->
            hr.replyTemp(String.format(hr.text("helper.vote"),user.getName(),Constantes.LETRAS.get(index)))
        );
    }
    public static void removeVoto(Helper.Reacao hr, int index) {
        hr.jda().retrieveUserById(hr.id()).queue(user->{
            hr.replyTemp(String.format(hr.text("helper.remove"), user.getName(), Constantes.LETRAS.get(index)));
        });
    }

    public static void jaVotou(Helper.Reacao hr, int index) {
        hr.replyTemp(String.format(hr.text("helper.already"), hr.getEvent().getUser().getName(), Constantes.LETRAS.get(index)));
    }

    public static void reRender(Helper.Reacao hr,String tipo, DadosPoll dp) {
        if(tipo.contains("poll"))
            hr.mensagem().editMessage(dp.poll().me(hr.bundle)).submit();
        else if(tipo.contains("pic")){
            final var emb = hr.mensagem().getEmbeds().get(0);
            LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
            int i = Integer.parseInt(param.getFirst());
            hr.mensagem().editMessage(dp.poll().visualiza(i,hr.bundle)).submit();
        }
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
