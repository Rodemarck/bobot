package rode.core.comandos.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.BsonValue;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.core.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.utilitarios.*;

import java.util.LinkedList;

public class AbrePoll extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(AbrePoll.class);
    public AbrePoll() {
        super("poll",null,"poll","enquete","enq");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args, event, (titulo, opcoes, guild, query) -> {
            if(guild != null){
                Poll poll = guild.getPoll(titulo);
                event.reply("poll", message ->
                        message.editMessage(poll.me()).submit()
                                .thenCompose(message1 -> PollHelper.addReaction(message1,poll.getOpcoes().size()))
                );
                return;
            }

            if(opcoes.isEmpty()){
                opcoes.add("sim");
                opcoes.add("não");
            }
            final Poll  poll = new Poll(titulo,opcoes, event.getEvent());
            event.reply("poll", message ->
                    message.editMessage(poll.me()).submit()
                            .thenCompose(message1 -> PollHelper.addReaction(message1,poll.getOpcoes().size()))
            );
            query = new Document("id",event.guildId());
            Document doc = Memoria.guilds.find(query).first();

            if(doc == null){
                guild = new ModelGuild(event.guildId());
                guild.getPolls().add(poll);
                BsonValue a = Memoria.guilds.insertOne(guild.toMongo()).getInsertedId();
                return;
            }
            guild = ModelGuild.fromMongo(doc);
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
