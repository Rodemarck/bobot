package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.bson.BsonValue;
import org.bson.Document;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.EventLoop;
import rode.core.Executador;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.ComandoGuild;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

@EComandoPoll
public class AbrePoll extends ComandoGuild {
    private static final HashMap<String, AbrePoll> subObjects = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(AbrePoll.class);
    public AbrePoll() {
        super("poll",null,"poll","enquete","enq");
        setPath("abre");
    }

    public AbrePoll(String comando, Permission cargo, boolean slash, String... alias) {
        super(comando, cargo, slash, alias);
        setPath("poll");
    }

    @Override
    public void subscribeSlash(CommandListUpdateAction cua, ResourceBundle bundle) {
        Executador.NOME_COMANDOS_SLASH.put(command,Executador.NOME_COMANDOS_GUILD.get(command));
        var reflections = new Reflections("rode.comando.guild.poll.texto");
        var commandData = new CommandData(command, bundle.getString("tutorial.help"));
        reflections.getSubTypesOf(getClass()).stream().sorted(Comparator.comparing(Class::getName)).forEach(e->{
            AbrePoll subObject = null;
            log.info("inscrevendo a comando {}",e.getName());
            try {
                subObject = e.getConstructor().newInstance();
                subObject.subscribeSlash(commandData,bundle);
            } catch (InstantiationException
                    |IllegalAccessException
                    |InvocationTargetException
                    |NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        });
        cua.addCommands(commandData);
    }

    public static void function(String[] args, Helper hm) throws IOException {
        log.debug("start");

        PollHelper.getPoll(args, hm, (dp) -> {
            log.debug("callback");
            if(dp.titulo() == null){
                hm.reply(Constantes.builder(hm.getBundle()),msg->{
                    hm.setMensagem(msg);
                    EventLoop.addReacao(new ConversaAbre(hm));
                });
                return;
            }
            if(dp.guild() != null){
                log.debug("poll encontrada");
                Poll poll = dp.guild().getPoll(dp.titulo());
                hm.reply(poll.makeDefaultEmbed(hm.getBundle()), message->
                    message.editMessage("poll").queue(msg->
                        PollHelper.addReaction(message,dp.poll().getOpcoes().size())
                    )
                );
                return;
            }
            if(dp.opcoes() == null){
                dp.opcoes(new LinkedList<>());
                log.debug("ops vazias? {}", dp.opcoes().isEmpty());
            }
            if(dp.opcoes().isEmpty()){
                log.debug("opcoes vazias");
                dp.opcoes().add(hm.getText("poll.yes"));
                dp.opcoes().add(hm.getText("poll.no"));
            }
            else{
                for(String s: dp.opcoes())
                    if(Pattern.matches(".*<@!?\\d+>.*",s)){
                        hm.reply(hm.getText("abre.exec.opmention"));
                        return;
                    }
            }
            if(Pattern.matches(".*<@!?\\d+>.*",dp.titulo())){
                hm.reply(hm.getText("abre.exec.timention"));
                return;
            }
            final Poll  poll = new Poll(hm.getId(),dp.titulo(),dp.opcoes());
            System.out.println(poll);
            hm.reply(poll.makeDefaultEmbed(hm.getBundle()), message ->
                    PollHelper.addReaction(message,poll.getOpcoes().size())
            );
            log.info("poll respondida");

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
    public void executeSlash(SlashCommandEvent slash, Helper.Slash hs) {
        log.info("{}??{}",slash.getName(),slash.getSubcommandName());
        if(slash.getSubcommandName() != null)
            subObjects.get(slash.getSubcommandName()).executeSlash(slash,hs);
        else
            System.out.println();
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        log.debug("start");
        function(args,hm);
    }

    public static class ConversaAbre extends MensagemReacao{
        private int pagina;
        private LinkedList<Poll> polls;
        public ConversaAbre(Helper hr) {
            super(hr, hr.getMensagem(), null, System.currentTimeMillis()+120000, null, new HashMap<>());
            pagina = 0;
            mostra(hr);
            rerender(hr.getBundle());
        }

        public void mostra(Helper hr){
            getComandos().clear();
            getComandos().put(Constantes.emote("esquerda"), h->mudaPP(h,-1));
            getComandos().put(Constantes.emote("direita"), h->mudaPP(h,1));
            polls = new LinkedList<>(Memoria
                    .guild(hr.getMensagem().getGuild().getId())
                    .getPolls()
                    .stream()
                    .filter(Poll::isOpen)
                    .toList()
            );
            var i = pagina * 10;
            var n = polls.size() - i;
            if(n >10)
                n = 10;
            var k = 1;
            for (;i<n;i++){
                var ii = i;
                var poll = polls.get(i);
                getComandos().put(Constantes.emote(""+(k++)),h->abrindoPoll(h,poll));
            }
        }

        public void abrindoPoll(Helper hr,Poll p){
            finaliza();
            getMensagem().delete().queue(q->{
                try {

                    function(new String[]{'{' + p.getTitulo() + '}'},hr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public void mudaPP(Helper hr,int n){

        }

        @Override
        public void acao() {

        }

        @Override
        public void rerender(ResourceBundle rb) {
            var eb = Constantes.builder();
            var k = 1;
            eb.setTitle("polls abertas no servidor " + getMensagem().getGuild().getName());
            for(var i=0;i<polls.size();i++){

                eb.appendDescription("\n" + Constantes.emote(""+(k++)));
                eb.appendDescription(" : **{" + polls.get(i).getTitulo() + "}**\n");
            }
            getMensagem()
                    .editMessageEmbeds(eb.build())
                    .submit()
                    .thenApply(msg->
                            msg.clearReactions()
                    )
                    .thenApply(__->{
                      getComandos().keySet().forEach(emoji->
                              getMensagem().addReaction(emoji).submit()
                      );
                      return null;
                    });
        }
    }
}
