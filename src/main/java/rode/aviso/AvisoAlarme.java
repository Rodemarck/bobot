package rode.aviso;

import net.dv8tion.jda.api.JDA;
import org.bson.Document;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.time.LocalDateTime;

public class AvisoAlarme extends Aviso{
    private long espasamento;
    private int repeticao;

    public AvisoAlarme(String canal, LocalDateTime horario,String guildId, String id,String titulo, String mensagem, long espasamento, int repeticao) {
        super(guildId,id,canal,horario,mensagem,titulo);
        this.espasamento = espasamento;
        this.repeticao = repeticao;
    }

    public static AvisoAlarme fromMongo(Document d) {
        System.out.println(d);
        return new AvisoAlarme(d.getString("canal"),
                d.getString("horario")!= null ?LocalDateTime.parse(d.getString("horario")) : null,
                d.getString("guildId"),
                d.getString("id"),
                d.getString("titulo"),
                d.getString("mensagem"),
                d.getLong("espasamento"),
                d.getInteger("repeticao")
        );
    }
    public Document toMongo(){
        return super.toMongo()
                .append("espasamento",espasamento)
                .append("repeticao",repeticao);
    }

    @Override
    public boolean acao(JDA jda) {
        var eb = Constantes.builder()
                .setTitle(getTitulo())
                .addField("criado por","<@" + getId() + ">",true)
                .appendDescription(getMensagem());
        jda.getTextChannelById(getCanal()).sendMessageEmbeds(eb.build()).queue();
        setHorario(getHorario().plusSeconds(espasamento));
        --repeticao;
        Memoria.usandoConfig(getGuildId(), conf->{
            var n = conf.avisos().size();
            for(int i=0;i<n;i++){
                var a =conf.avisos().get(i);
                if(a.getTitulo().equals(getTitulo())){
                    conf.avisos().set(i,this);
                }
            }
        });
        return repeticao > 0;
    }
}
