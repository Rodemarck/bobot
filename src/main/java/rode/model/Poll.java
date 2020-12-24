package rode.model;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Helper;
import rode.utilitarios.Constantes;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
public class Poll implements Serializable{
    private static Logger log = LoggerFactory.getLogger(Poll.class);
    private String criadorId;
    private String titulo;
    private List<String> opcoes;
    private List<Integer> valores;
    private HashMap<String,Integer> usuariosId;

    public Poll(String titulo, LinkedList<String> opcoes, GuildMessageReceivedEvent event) {
        this.criadorId = event.getAuthor().getId();
        this.titulo = titulo;
        this.opcoes = opcoes;
        this.valores = new LinkedList<>();
        this.usuariosId = new HashMap<>();
        opcoes.forEach(e->valores.add(0));
    }

    public Poll(String criadorId, String titulo, LinkedList<String> opcoes, LinkedList<Integer> valores, HashMap<String, Integer> usuariosId) {
        this.criadorId = criadorId;
        this.titulo = titulo;
        this.opcoes = opcoes;
        this.valores = valores;
        this.usuariosId = usuariosId;
    }

    public Poll(String criadorId, String titulo, LinkedList<String> opcoes) {
        this.criadorId = criadorId;
        this.titulo = titulo;
        this.opcoes = opcoes;
        this.usuariosId = new HashMap<>();
        this.valores = new LinkedList<>();
    }
    public Poll(){}


    public static Poll fromMongo(Document d) {

        HashMap<String, Integer> ids = new HashMap<>();
        ((List<Document>)d.get("usuariosId")).forEach(doc->ids.put(doc.getString("usuario"),doc.getInteger("escolha")));
        return new Poll(d.getString("criadorId"),
                        d.getString("titulo"),
                        (new LinkedList<>((List<String>)d.get("opcoes"))),
                        (new LinkedList<>((List<Integer>)d.get("valores"))),
                ids);
    }
    public Document toMongo() {
        return new Document()
                .append("criadorId",criadorId)
                .append("titulo",titulo)
                .append("opcoes",opcoes)
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

    public MessageEmbed me(){
        int pri=0,sec=0,total=0,pos=0,num;
        int n = opcoes.size();
        for(int i=0; i<n;i++){
            num = valores.get(i);
            if(num >= pri){
                sec = pri;
                pos = i;
                pri = num;
            }else if(num > sec)
                sec = num;
            total += num;
        }
        log.info("me, total = {}",total);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(titulo);
        for(int i=0; i<n;i++){
            int numero = (total==0)? 0 : Math.round(((float)valores.get(i)/total)*100);
            eb.appendDescription(Constantes.POOL_EMOTES.get(i) + ": " + ((i==pos)? ("**" + opcoes.get(i) + "**"):opcoes.get(i) )+ "\t[" + (numero) + "%]\n\n");
        }
        if(pri == sec)
            eb.appendDescription("situação: **empatado** ");
        else
            eb.appendDescription("situação: **" + opcoes.get(pos) + "** ganhando por " + (pri-sec) +" votos");
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
