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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class PollHelper {
    private static Logger log = LoggerFactory.getLogger(PollHelper.class);
    private PollHelper(){}
    public static CompletionStage<Void> addReaction(Message m, int n){
        for(int i=0; i<n; i++){
            m.addReaction(Constantes.emotePoll(i)).queue();
        }
        return null;
    }
    public static void getPoll(String[] args, Helper helper, PollFunction pf) throws IOException {
        String txt = Arrays.stream(args).sequential().collect(Collectors.joining(" "));
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
    public static void getPollFromEmote(String[] args, Helper.Reacao helper, PollFunction function) throws IOException {
        final var tipo = args[0];
        final var args2 = new String[args.length+1];
        System.arraycopy(args,0,args2,0,args.length);
        log.debug("do tipo {}", tipo);
        if(Constantes.POOL_EMOTES.contains(helper.emoji())) {
            var index = Constantes.POOL_EMOTES.indexOf(helper.emoji());
            var titulo = helper.mensagem().getEmbeds().get(0).getTitle();
            args2[args.length] = '{' + titulo + '}';
            log.debug("titulo da poll {}", titulo);
            getPoll(args2, helper, dp -> {
                if(dp.guild() == null){
                    helper.replyTemp(helper.text("helper.404").formatted(titulo));
                    return;
                }
                Poll poll = dp.guild().getPoll(dp.titulo());
                if(!poll.isOpen()){
                    helper.replyTemp(helper.text("helper.close").formatted(poll.getTitle()));
                    return;
                }
                function.apply(new DadosPoll(dp.titulo, dp.opcoes, dp.guild, dp.query, dp.poll, index));
            });
        }
        else
            helper.reply(String.format(helper.text("helper.troll"),helper.getEvent().getUser().getName(),helper.emoji()), message->
                    message.delete().queueAfter(15, TimeUnit.SECONDS,x->
                            helper.mensagem().clearReactions(helper.emoji()).queue())
            );
    }
    public static boolean livreSiMesmo(String[] args, Helper.Reacao event) throws IOException {
        return event.mensagem().getAuthor().getId().equals(event.jda().getSelfUser().getId());
    }
    public static boolean livreDono(String[] args, Helper.Mensagem event){

        String txt = Arrays.stream(args).sequential().collect(Collectors.joining(" "));
        String titulo = Regex.extractInside("\\{([^\\}])+\\}", txt).getFirst();

        Document query = new Document("id",event.guildId()).append("polls.titulo",titulo);
        Document doc = Memoria.guilds.find(query).first();
        System.out.println("doc = " + doc);
        if(doc != null) {
            System.out.println("not");
            ModelGuild g = ModelGuild.fromMongo(doc);
            Poll poll = g.getPoll(titulo);
            System.out.println("poll = " + poll);
            if(poll.creatorId().equals(event.id()))
                return true;
            event.jda().retrieveUserById(poll.creatorId()).queue(u ->{
                event.reply("pertence a " + u.getName());
            });
        }
        return false;
    }

    public static void contaVoto(Helper.Reacao hr, int index) {
        hr.jda().retrieveUserById(hr.id()).queue(user->
            hr.replyTemp(hr.text("helper.vote").formatted(user.getName(),Constantes.LETRAS.get(index)))
        );
    }
    public static void removeVoto(Helper.Reacao hr, int index) {
        hr.jda().retrieveUserById(hr.id()).queue(user->{
            hr.replyTemp(hr.text("helper.remove").formatted( user.getName(), Constantes.LETRAS.get(index)));
        });
    }

    public static void jaVotou(Helper.Reacao hr, int index) {
        hr.replyTemp(hr.text("helper.already").formatted( hr.getEvent().getUser().getName(), Constantes.LETRAS.get(index)));
    }

    public static void reRender(Helper.Reacao hr,String tipo, DadosPoll dp) {
        if(tipo.contains("poll"))
            hr.mensagem().editMessage(dp.poll().makeDefaultEmbed(hr.bundle)).queue();
        else if(tipo.contains("pic")){
            final var emb = hr.mensagem().getEmbeds().get(0);
            LinkedList<String> param = Regex.extract("\\d+", emb.getFooter().getText());
            int i = Integer.parseInt(param.getFirst());
            hr.mensagem().editMessage(dp.poll().makeDisplayEmbed(i,hr.bundle)).queue();
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
