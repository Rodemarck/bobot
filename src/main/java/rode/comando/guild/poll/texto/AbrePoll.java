package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.bson.BsonValue;
import org.bson.Document;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.model.ComandoGuild;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.utilitarios.Memoria;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@EComandoPoll
public class AbrePoll extends ComandoGuild {
    private static final HashMap<String, AbrePoll> subObjects = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(AbrePoll.class);
    public AbrePoll() {
        super("poll",null,"poll","enquete","enq");
        setPath("poll");
    }

    public AbrePoll(String comando, Permission cargo, boolean slash, String... alias) {
        super(comando, cargo, slash, alias);
        setPath("poll");
    }

    @Override
    public void subscribeSlash(CommandUpdateAction cua, ResourceBundle bundle) {
        var reflections = new Reflections("rode.comando.guild.poll.texto");
        var commandData = new CommandUpdateAction.CommandData(command, bundle.getString("tutorial.help"));
        reflections.getSubTypesOf(getClass()).stream().sorted(Comparator.comparing(Class::getName)).forEach(e->{
            AbrePoll subObject = null;
            log.info("inscrevendo a comando {}",e.getName());
            try {
                subObject = e.getConstructor().newInstance();
                subObject.subscribeSlash(commandData,bundle);
            } catch (InstantiationException instantiationException) {
                instantiationException.printStackTrace();
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            }
        });
        cua.addCommands(commandData);
    }

    private void function(String[] args, Helper hm,BiConsumer<String, MessageEmbed> reply){
        log.debug("start");

        PollHelper.getPoll(args, hm, (dp) -> {
            log.debug("callback");
            if(dp.titulo() == null){
                reply.accept(hm.text("abre.exec.title"),null);
                return;
            }
            if(dp.guild() != null){
                log.debug("poll encontrada");
                Poll poll = dp.guild().getPoll(dp.titulo());
                hm.reply("poll", message -> {
                            log.info("poll respondida");
                            message.editMessage(poll.makeDefaultEmbed(hm.bundle())).queue(msg->{
                                PollHelper.addReaction(message,dp.poll().getOptions().size());
                            });
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
                        reply.accept(hm.text("abre.exec.opmention"),null);
                        return;
                    }
            }
            if(Pattern.matches(".*<@!?\\d+>.*",dp.titulo())){
                hm.reply(hm.text("abre.exec.timention"));
                return;
            }
            final Poll  poll = new Poll(dp.titulo(),dp.opcoes(), hm.getEvent());
            hm.reply("poll", message ->
                    message.editMessage(poll.makeDefaultEmbed(hm.bundle())).queue(message1 ->
                            PollHelper.addReaction(message1,poll.getOptions().size())
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

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
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
                            message.editMessage(poll.makeDefaultEmbed(hm.bundle())).queue(msg->{
                                PollHelper.addReaction(message,dp.poll().getOptions().size());
                            });
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
                    message.editMessage(poll.makeDefaultEmbed(hm.bundle())).queue(message1 ->
                            PollHelper.addReaction(message1,poll.getOptions().size())
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
