package rode.model;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.utilitarios.Constantes;
import rode.utilitarios.Regex;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
public class Poll implements Serializable{
    private static Logger log = LoggerFactory.getLogger(Poll.class);
    private String criadorId;
    private String titulo;
    private List<String> opcoes;
    private List<Integer> valores;
    private HashMap<String,Integer> usuariosId;


    private LocalDateTime dataLimite;
    private LocalDateTime dataCriacao;

    public MessageEmbed visualiza(int i,ResourceBundle rb) {
        top t = calculaTop();
        EmbedBuilder eb = Constantes.builder();
        eb.setTitle(titulo);
        if(Regex.isLink(opcoes.get(i)))
           eb.setImage(opcoes.get(i));
        else
            eb.appendDescription(opcoes.get(i));
        float numero = (t.total==0)? 0 : (((float)valores.get(i)/t.total)*100);
        return eb.addField(rb.getString("poll.op"), Constantes.LETRAS.get(i),true)
                .setFooter(String.format("%1$d/%2$d                %3$d %5$s [%4$.2f %%]", i,(opcoes.size()-1), valores.get(i), numero,rb.getString("poll.vote"))
         ).build();


    }



    public Poll(String titulo, LinkedList<String> opcoes, GuildMessageReceivedEvent event) {
        this.criadorId = event.getAuthor().getId();
        this.titulo = titulo;
        this.opcoes = opcoes;
        this.valores = new LinkedList<>();
        this.usuariosId = new HashMap<>();
        this.dataCriacao = LocalDateTime.now();
        opcoes.forEach(e->valores.add(0));
    }

    public Poll(String criadorId, String titulo, LinkedList<String> opcoes, LinkedList<Integer> valores, HashMap<String, Integer> usuariosId, LocalDateTime dataCriacao, LocalDateTime dataLimite) {
        this.criadorId = criadorId;
        this.titulo = titulo;
        this.opcoes = opcoes;
        this.valores = valores;
        this.usuariosId = usuariosId;
        this.dataCriacao = dataCriacao;
        this.dataLimite = dataLimite;
    }

    public Poll(String criadorId, String titulo, LinkedList<String> opcoes) {
        this.criadorId = criadorId;
        this.titulo = titulo;
        this.opcoes = opcoes;
        this.usuariosId = new HashMap<>();
        this.valores = new LinkedList<>();
        this.dataCriacao = LocalDateTime.now();
    }
    public Poll(){}


    public static Poll fromMongo(Document d) {
        HashMap<String, Integer> ids = new HashMap<>();
        ((List<Document>)d.get("usuariosId")).forEach(doc->ids.put(doc.getString("usuario"),doc.getInteger("escolha")));
        return new Poll(d.getString("criadorId"),
                        d.getString("titulo"),
                        (new LinkedList<>((List<String>)d.get("opcoes"))),
                        (new LinkedList<>((List<Integer>)d.get("valores"))),
                        ids,
                d.getString("dataCriacao") == null? null:LocalDateTime.parse(d.getString("dataCriacao")),
                d.getString("dataLimite") == null? null:LocalDateTime.parse(d.getString("dataLimite"))
                    );
    }
    public Document toMongo() {
        return new Document()
                .append("criadorId",criadorId)
                .append("titulo",titulo)
                .append("opcoes",opcoes)
                .append("dataCriacao", dataCriacao==null? null:dataCriacao.toString())
                .append("dataLimite", dataLimite==null?null:dataLimite.toString())
                .append("valores",valores)
                .append("usuariosId",usuariosId.entrySet().stream().map(u->
                        new Document()
                                .append("usuario",u.getKey())
                                .append("escolha",u.getValue())
                ).collect(Collectors.toList()));
    }

    public boolean hasUser(String id){
        boolean b = this.usuariosId.containsKey(id);
        log.debug("has user({}) = {}", id, b);
        return b;

    }
    public int votouPara(String id){
        return this.usuariosId.get(id);
    }
    public void add(int index, String userId){
        if(!hasUser(userId)) {
            valores.set(index, valores.get(index) + 1);
            usuariosId.put(userId,index);
        }
    }
    public void rem(int index, String userId){
        if (hasUser(userId) && usuariosId.get(userId) == index) {
            valores.set(index, (valores.get(index) > 0) ? (valores.get(index) - 1) : 0);
            usuariosId.remove(userId);
        }
    }

    public void addOpcoes(LinkedList<String> opcoes) {
        for(String s: opcoes)
            if (this.opcoes.contains(s))
                opcoes.remove(s);
        for(String s: opcoes){
            this.opcoes.add(s);
            this.valores.add(0);
        }
    }
    public void remOpcoes(LinkedList<String> opcoes) {
        log.debug("remOp");
        synchronized (this) {
            this.usuariosId.entrySet().forEach(u -> {
                log.debug("{} -> {}", u.getKey(), u.getValue());
            });
            var userClone = ((HashMap<String, Integer>) usuariosId.clone());
            for (var s : opcoes){
                if (this.opcoes.contains(s)) {
                    var index = this.opcoes.indexOf(s);
                    for (var id : this.usuariosId.entrySet()) {
                        log.debug("procurando por {}", id.getKey());
                        if (id.getValue() == index) {
                            log.debug("remova {}", id.getValue());
                            userClone.remove(id.getKey());
                        } else if (id.getValue() > index)
                            userClone.put(id.getKey(), id.getValue() - 1);
                    }
                    this.opcoes.remove(index);
                    this.valores.remove(index);
                }
            }
            usuariosId.entrySet().clear();
            userClone.entrySet().forEach(u->
                usuariosId.put(u.getKey(), u.getValue())
            );
            this.usuariosId.entrySet().forEach(u->{
                log.debug("{} -> {}", u.getKey(), u.getValue());
            });
        }
    }

    public boolean aberto(){
        return dataLimite == null || LocalDateTime.now().isBefore(dataLimite);
    }

    public void fecha(){
        dataLimite = LocalDateTime.now().minusMinutes(1);
    }

    private top calculaTop(){
        int pri=0,sec=0,total=0,priPos=0,secPos=0,num;
        int n = opcoes.size();
        for(int i=0; i<n;i++){
            num = valores.get(i);
            if(num >= pri){
                sec = pri;
                pri = num;
                secPos = priPos;
                priPos = i;
            }else if(num > sec)
                sec = num;
            total += num;
        }
        return new top(pri, priPos, sec,secPos, total);
    }
    public MessageEmbed me(ResourceBundle rb){
        top t = calculaTop();
        int n = opcoes.size();
        log.debug("me, total = {}",t.total);
        EmbedBuilder eb = Constantes.builder();
        eb.setTitle(titulo);
        for(int i=0; i<n;i++){
            int numero = (t.total==0)? 0 : Math.round(((float)valores.get(i)/t.total)*100);
            eb.appendDescription(Constantes.emotePoll(i) + ": " + ((i==t.priPos)? ("**" + opcoes.get(i) + "**"):opcoes.get(i) )+ "\t[" + (numero) + "%]\n\n");
        }
        eb.appendDescription(aberto()? rb.getString("poll.state") : rb.getString("poll.end"));
        if(t.pri == t.sec)
            eb.appendDescription(rb.getString("poll.vote.drawn"));
        else
            eb.appendDescription(String.format(rb.getString("poll.vote.win"),opcoes.get(t.priPos),(t.pri-t.sec)));
        return eb.build();
    }

    public MessageEmbed config(ResourceBundle rb) {
        top t = calculaTop();
        EmbedBuilder eb = Constantes.builder();
        eb.setTitle(titulo);
        eb.appendDescription(rb.getString("poll.creator"));
        String dara = "";

        log.debug("no fim minha dara foi [{}]", dara);
        if(dataCriacao != null){
            eb.appendDescription(String.format("%s"+rb.getString("poll.date.format")+'\n',rb.getString("poll.create"),dataCriacao.format(DateTimeFormatter.ofPattern("dd")),dataCriacao.format(DateTimeFormatter.ofPattern("MMMM")),dataCriacao.format(DateTimeFormatter.ofPattern("YYYY")), dataCriacao.format(DateTimeFormatter.ofPattern("HH:mm"))));
        }

        eb.appendDescription(rb.getString("poll.open"));

        if(dataLimite == null)
            eb.appendDescription("**"+rb.getString("poll.yes")+"**");
        else{
            if(LocalDateTime.now().isBefore(dataLimite)){
                eb.appendDescription("**"+rb.getString("poll.yes")+"**");
                eb.appendDescription(rb.getString("poll.date.limit"));
            }
            else{
                eb.appendDescription("**"+rb.getString("poll.no")+"**");
                eb.appendDescription(rb.getString("poll.date.close"));
            }
            eb.appendDescription(String.format(rb.getString("poll.date.format"),dataLimite.format(DateTimeFormatter.ofPattern("dd")), dataLimite.format(DateTimeFormatter.ofPattern("MMMM")),dataLimite.format(DateTimeFormatter.ofPattern("YYYY")),dataLimite.format(DateTimeFormatter.ofPattern("HH:mm"))));
        }
        eb.appendDescription(rb.getString("poll.vote.resultado"));

        if (t.pri == t.sec)
            eb.appendDescription(rb.getString("poll.vote.drawn"));
        else
            eb.appendDescription(String.format(rb.getString("poll.vote.win"), opcoes.get(t.priPos) ,(t.pri - t.sec)));
        return eb.build();
    }


    public String titulo() {
        return titulo;
    }

    public List<String> opcoes() {
        return opcoes;
    }

    public List<Integer> valores() {
        return valores;
    }

    public String criadorId() {
        return criadorId;
    }


    @Override
    public String toString() {
        return "Poll{ criadorId=" + criadorId +
                ", titulo='" + titulo + '\'' +
                ", opcoes=" + opcoes +
                ", valores=" + valores +
                ", usuariosId=" + usuariosId +
                '}';
    }

    public HashMap<String, Integer> getNumeroVotos(){
        HashMap<String, Integer> votos = new HashMap<>();
        int n = opcoes.size();
        for (int i=0; i<n; i++){
            if(valores.get(i) !=0 )
                votos.put(opcoes.get(i), valores.get(i));
        }
        return votos;
    }
    public void getVotos(EmbedBuilder eb, ResourceBundle rb) {
        for(Map.Entry<String, Integer> u :usuariosId.entrySet()){
            System.out.println(opcoes);
            eb.appendDescription(String.format(rb.getString("poll.vote.to"),u.getKey(), opcoes.get(u.getValue())));
        }
    }

    public HashMap<String, Integer> usuariosId() {
        return usuariosId;
    }

    public LocalDateTime dataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDateTime dataLimite) {
        this.dataLimite = dataLimite;
    }

    public LocalDateTime dataCriacao() {
        return dataCriacao;
    }

    public void dataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    private class top{
        private int pri;
        private int priPos;
        private int sec;
        private int secPos;
        private int total;

        public top(int pri, int priPos, int sec, int secPos, int total) {
            this.pri = pri;
            this.priPos = priPos;
            this.sec = sec;
            this.secPos = secPos;
            this.total = total;
        }

        public int pri() {
            return pri;
        }

        public void pri(int pri) {
            this.pri = pri;
        }

        public int priPos() {
            return priPos;
        }

        public void priPos(int priPos) {
            this.priPos = priPos;
        }

        public int sec() {
            return sec;
        }

        public void sec(int sec) {
            this.sec = sec;
        }

        public int secPos() {
            return secPos;
        }

        public void secPos(int secPos) {
            this.secPos = secPos;
        }

        public int total() {
            return total;
        }

        public void total(int total) {
            this.total = total;
        }
    }
}
