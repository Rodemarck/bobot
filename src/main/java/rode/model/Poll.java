package rode.model;

import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Helper;
import rode.utilitarios.Constantes;

import java.io.*;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Data
public class Poll implements Serializable{
    private static Logger log = LoggerFactory.getLogger(Poll.class);
    private String criadorId;
    private String titulo;
    private List<String> opcoes;
    private List<Integer> valores;
    private HashMap<String,Integer> usuariosId;

    private LocalDateTime dataLimite;
    private LocalDateTime dataCriacao;

    private class top {
        int pri;
        int priPos;
        int sec;
        int secPos;
        int total;

        public top(int pri, int priPos, int sec, int secPos, int total) {
            this.pri = pri;
            this.priPos = priPos;
            this.sec = sec;
            this.secPos = secPos;
            this.total = total;
        }
    };

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
                .append("dataCriacao", dataCriacao.toString())
                .append("dataLimite", dataLimite.toString())
                .append("valores",valores)
                .append("usuariosId",usuariosId.entrySet().stream().map(u->
                        new Document()
                                .append("usuario",u.getKey())
                                .append("escolha",u.getValue())
                ).collect(Collectors.toList()));
    }

    public boolean hasUser(String id){
        boolean b = this.usuariosId.containsKey(id);
        log.info("has user({}) = {}", id, b);
        return b;

    }
    public int getOriginal(String id){
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
        for(String s:opcoes){
            if(this.opcoes.contains(s)){
                final int index = this.opcoes.indexOf(s);
                this.opcoes.remove(s);
                this.valores.remove(index);
                for(Map.Entry<String,Integer> usuario : usuariosId.entrySet()){
                    if(usuario.getValue() == index){
                        usuariosId.remove(usuario.getKey());
                    }
                }
            }
        }
    }

    public boolean isAberto(){
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
    public MessageEmbed me(){
        top t = calculaTop();
        int n = opcoes.size();
        log.info("me, total = {}",t.total);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(titulo);
        for(int i=0; i<n;i++){
            int numero = (t.total==0)? 0 : Math.round(((float)valores.get(i)/t.total)*100);
            eb.appendDescription(Constantes.POOL_EMOTES.get(i) + ": " + ((i==t.priPos)? ("**" + opcoes.get(i) + "**"):opcoes.get(i) )+ "\t[" + (numero) + "%]\n\n");
        }
        eb.appendDescription(isAberto()? "situação: " : "teminou em: ");
        if(t.pri == t.sec)
            eb.appendDescription("**empate**");
        else
            eb.appendDescription("**" + opcoes.get(t.priPos) + "** ganhando por " + (t.pri-t.sec) +" votos");
        return eb.build();
    }

    public MessageEmbed config() {
        top t = calculaTop();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(titulo);
        eb.appendDescription("criador : <@" + criadorId + "> \n");
        if(dataCriacao != null)
            eb.appendDescription("criado no dia : " + dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + '\n');
        eb.appendDescription("aberta: ");
        if (isAberto()) {
            eb.appendDescription("**Sim.**\n");
            if (dataLimite != null) {
                long tempo = ChronoUnit.MINUTES.between(LocalDateTime.now(), dataLimite);
                double dias = Math.floor(tempo / (24 * 60));
                tempo -= (dias * 24 * 60);
                double horas = Math.floor(tempo / 60);
                tempo -= horas;
                eb.appendDescription("tempo restante **" + dias + " dias, " + horas + " horas, " + tempo + " minutos**\n");
            }
        } else
            eb.appendDescription("**Não.**\n");
        eb.appendDescription("resultado: ");
        if (t.pri == t.sec)
            eb.appendDescription("**empate**\n");
        else
            eb.appendDescription("**" + opcoes.get(t.priPos) + "** ganhando por " + (t.pri - t.sec) + " votos\n");
        return eb.build();
    }


    public String getTitulo() {
        return titulo;
    }

    public List<String> getOpcoes() {
        return opcoes;
    }

    public List<Integer> getValores() {
        return valores;
    }

    public String getCriadorId() {
        return criadorId;
    }


    @Override
    public String toString() {
        return "Poll{ criadorId=" + criadorId +
                ", titulo='" + titulo + '\'' +
                ", opcoes=" + opcoes +
                ", valores=" + valores +
                ", usuariosId=" + usuariosId +
                //", testes="+ testes+
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
    public void getVotos(EmbedBuilder eb, JDA jda) {
        for(Map.Entry<String, Integer> u :usuariosId.entrySet()){
            eb.appendDescription("**<@" +u.getKey()+ ">** votou para **" + opcoes.get(u.getValue()) + "**\n\n");
        }
    }

    public HashMap<String, Integer> getUsuariosId() {
        return usuariosId;
    }


}
