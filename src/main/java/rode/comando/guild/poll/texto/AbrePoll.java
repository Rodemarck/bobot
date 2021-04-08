package rode.comando.guild.poll.texto;

import org.bson.BsonValue;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.utilitarios.Memoria;

import java.util.LinkedList;
import java.util.regex.Pattern;

@EComandoPoll
public class AbrePoll extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(AbrePoll.class);
    public AbrePoll() {
        super("abre",null,"poll","enquete","enq");
    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        log.debug("start");
        PollHelper.getPoll(args, hm, (dp) -> {
            log.debug("callback");
            if(dp.titulo() == null){
                hm.reply(hm.text("abre.exec.title"));
                return;
            }
            if(dp.guild() != null){
                log.debug("poll encontrada");
                Poll poll = dp.guild().getPoll(dp.titulo());
                hm.reply("poll", message -> {
                            log.info("poll respondida");
                            hm.reply("carregando");
                            message.editMessage(poll.me(hm.bundle())).queue();
                        }
                );
                return;
            }
            if(dp.opcoes() == null){
                dp.opcoes(new LinkedList<>());
                log.debug("ops vazias? {}", dp.opcoes().isEmpty());
            }
            if(dp.opcoes().isEmpty()){
                log.debug("opcoes vazias");
                dp.opcoes().add(hm.text("poll.yes"));
                dp.opcoes().add(hm.text("poll.no"));
            }
            else{
                for(String s: dp.opcoes())
                    if(Pattern.matches(".*<@!?\\d+>.*",s)){
                        hm.reply(hm.text("abre.exec.opmention"));
                        return;
                    }
            }
            if(Pattern.matches(".*<@!?\\d+>.*",dp.titulo())){
                hm.reply(hm.text("abre.exec.timention"));
                return;
            }
            final Poll  poll = new Poll(dp.titulo(),dp.opcoes(), hm.getEvent());
            hm.reply("poll", message ->
                    message.editMessage(poll.me(hm.bundle())).queue(message1 ->
                            PollHelper.addReaction(message1,poll.opcoes().size())
                    )
            );
            Document query = new Document("id",hm.guildId());
            Document doc = Memoria.guilds.find(query).first();

            if(doc == null){
                ModelGuild guild = new ModelGuild(hm.guildId());
                guild.getPolls().add(poll);
                BsonValue a = Memoria.guilds.insertOne(guild.toMongo()).getInsertedId();
                return;
            }
            ModelGuild guild = ModelGuild.fromMongo(doc);
            guild.getPolls().add(poll);
            Memoria.guilds.updateOne(query, new Document("$set",guild.toMongo()));
        });
    }
}
