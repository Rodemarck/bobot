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
    private static Logger log = LoggerFactory.getLogger(Main.class);
    public AbrePoll() {
        super("poll",null,"poll","abre","open");
    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem event) throws Exception {
        PollHelper.getPoll(args, event, (titulo, opcoes, guild, query) -> {
            if(guild != null){Poll poll = guild.getPoll(titulo);
                System.out.println("uuu");
                event.reply(poll.me(),message -> PollHelper.addReaction(message, poll.getOpcoes().size()));
                return;
            }

            if(opcoes.isEmpty()){
                opcoes.add("sim");
                opcoes.add("não");
            }
            final Poll  poll = new Poll(titulo,opcoes, event.getEvent());
            event.reply(poll.me(),message->PollHelper.addReaction(message,poll.getOpcoes().size()));
            query = new Document("id",event.guildId());
            Document doc = Memoria.guilds.find(query).first();

            if(doc == null){
                System.out.println("nova guild criada");
                guild = new ModelGuild(event.guildId());
                guild.getPolls().add(poll);
                System.out.println(guild.toMongo());
                BsonValue a = Memoria.guilds.insertOne(guild.toMongo()).getInsertedId();
                System.out.println(a);
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
        me.appendDescription("Comando para abrir uma nova poll (enquete) no servidor.\n\n");
        me.appendDescription("**-poll {titulo} [opção 1] [opção 2] ...**\n\n");
        me.appendDescription("Aliases (comandos alternativos) : **poll**, **abre**, **open**.\n\n");
        me.appendDescription("Se não for informado nenhuma opção, por padrão será adicionado as opções **sim** e **não**.");
        me.appendDescription(" O limite de opções é 36.\n\n");
        me.appendDescription("É possivel votar em apenas uma opção por vez, reagindo à um dos emojois correspondentes a opção.\n");
        me.appendDescription("Para retirar seu voto, basta reagir novamente no mesmo emoji.");
    }
}
