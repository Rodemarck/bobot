package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.BsonValue;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.utilitarios.*;

import java.util.LinkedList;
import java.util.regex.Pattern;

public class AbrePoll extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(AbrePoll.class);
    public AbrePoll() {
        super("poll",null,"poll","enquete","enq");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        log.debug("start");
        PollHelper.getPoll(args, event, (dp) -> {
            log.debug("callback");
            if(dp.titulo() == null){
                event.reply("É preciso um título para a poll.");
                return;
            }
            if(dp.guild() != null){
                log.debug("poll encontrada");
                Poll poll = dp.guild().getPoll(dp.titulo());
                event.reply("poll", message ->
                        message.editMessage(poll.me()).submit()
                                .thenCompose(message1 -> PollHelper.addReaction(message1,poll.getOpcoes().size()))
                );
                return;
            }
            if(dp.opcoes() == null){
                dp.opcoes(new LinkedList<>());
                log.debug("ops vazias? {}", dp.opcoes().isEmpty());
            }
            if(dp.opcoes().isEmpty()){
                log.debug("opcoes vazias");
                dp.opcoes().add("sim");
                dp.opcoes().add("não");
            }
            else{
                for(String s: dp.opcoes())
                    if(Pattern.matches(".*<@!?\\d+>.*",s)){
                        event.reply("Não pode haver menções em opções de poll");
                        return;
                    }
            }
            if(Pattern.matches(".*<@!?\\d+>.*",dp.titulo())){
                event.reply("não pode haver menções em título de poll");
                return;
            }
            final Poll  poll = new Poll(dp.titulo(),dp.opcoes(), event.getEvent());
            event.reply("poll", message ->
                    message.editMessage(poll.me()).submit()
                            .thenCompose(message1 -> PollHelper.addReaction(message1,poll.getOpcoes().size()))
            );
            Document query = new Document("id",event.guildId());
            Document doc = Memoria.guilds.find(query).first();

            if(doc == null){
                ModelGuild guild = new ModelGuild(event.guildId());
                guild.getPolls().add(poll);
                BsonValue a = Memoria.guilds.insertOne(guild.toMongo()).getInsertedId();
                return;
            }
            ModelGuild guild = ModelGuild.fromMongo(doc);
            guild.getPolls().add(poll);
            Memoria.guilds.updateOne(query, new Document("$set",guild.toMongo()));
        });
    }


    @Override
    public void help(EmbedBuilder me) {
        me.appendDescription("**-poll {titulo} [opção 1] [opção 2] ...**: cria uma poll.\n\n");
    }

    @Override
    public void helpExtensive(EmbedBuilder me) {
        me.appendDescription("""
                Comando para abrir uma nova poll (enquete) no servidor.
                
                **-poll {titulo} [opção 1] [opção 2] ...**
                
                Aliases (comandos alternativos) : **poll**, **enquete**, **enq**.
                Se não for informado nenhuma opção, por padrão será adicionado as opções **sim** e **não**.
                O limite de opções é 20.
                É possível votar em apenas uma opção por vez, reagindo à um dos emojis correspondentes a opção, ou utilizando o comando **votar**.
                Para retirar seu voto, basta votar novamente na mesma opção.
                """);
    }
}
