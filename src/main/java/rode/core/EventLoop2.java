package rode.core;

import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.aviso.Aviso;
import rode.model.maker.MensagemReacao;
import rode.model.maker.MensagemTexto;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EventLoop2 implements Runnable{
    private static Logger log = LoggerFactory.getLogger(EventLoop2.class);
    private static long count = 0;
    private static EventLoop2 instance;
    public static final long delay = Long.parseLong(Objects.requireNonNull(Constantes.env.get("delay")));
    public static final long delayS = delay/1000;
    private final static List<MensagemReacao> eventos_reacao = Collections.synchronizedList(new LinkedList<>());
    private final static List<MensagemTexto> eventos_texto = Collections.synchronizedList(new LinkedList<>());
    private final static List<Aviso> eventos_avisos = Collections.synchronizedList(new LinkedList<>());
    private static JDA jda;
    private EventLoop2(JDA jda){
        EventLoop2.jda = jda;
        run();
        Executador.poolExecutor.submit(()->{
            Memoria.eachConfig(cf->cf.avisos().forEach(EventLoop2::addAviso));
        });
    }

    public static EventLoop2 getInstance(JDA j) {
        log.info("instancia");
        if(instance == null)
            instance = new EventLoop2(j);
        return instance;
    }

    public static long geraId() {
        return ++count;
    }

    public static void reacaoGuild(Helper.Reacao hr) {
        log.debug("procurando reação");
        synchronized (eventos_reacao){
            var lista = eventos_reacao.stream().filter(loop ->
                    (loop.mensagem().getId().equals(hr.mensagem().getId()))
                    && (loop.membro() == null || loop.membro().equals(hr.id()))
                    && (loop.permissao() == null || hr.getEvent().getMember().hasPermission(loop.permissao()))
            ).collect(Collectors.toList());
            for(var lop :lista) {
                log.trace("chamando ->>"+lop.getClass().getName());
                lop.run(hr);
            }
        }
    }
    public static void textoGuild(Helper.Mensagem hm){
        log.debug("procurando texto");
        synchronized (eventos_texto){
            var lista = eventos_texto.stream().filter(loop->
                    (loop.membro() == null || loop.membro().equals(hm.id()))
                    && (loop.permissao() == null || hm.getEvent().getMember().hasPermission(loop.permissao()))
            ).collect(Collectors.toList());
            for(var loop:lista){
                log.trace("chamando ->>"+loop.getClass().getName());
                loop.run(hm);
            }
        }
    }

    public static void addReacao(MensagemReacao reacaoMensagem){
        synchronized (eventos_reacao){
            eventos_reacao.add(reacaoMensagem);
        }
    }
    public static void addTexto(MensagemTexto mensagemTexto){
        synchronized (eventos_texto){
            eventos_texto.add(mensagemTexto);
        }
    }
    public static void addAviso(Aviso aviso){
        synchronized (eventos_avisos){
            eventos_avisos.add(aviso);
        }
    }

    public static void checa() {
        synchronized (eventos_reacao){
            log.debug("eventos_reacao = " + eventos_reacao.size());
            eventos_reacao.removeIf(MensagemReacao::expirado);
        }
        synchronized (eventos_texto){
            log.debug("eventos_texto = " + eventos_texto.size());
            eventos_texto.removeIf(MensagemTexto::expirado);
        }
        synchronized (eventos_avisos){
            log.debug("eventos_avisos = " + eventos_avisos.size());
            //eventos_avisos.removeIf(Aviso::expirado);
        }
    }
    public static void checaPolls(){
        Executador.poolExecutor.submit(()->Memoria.eachPoll(m->m.notifica(jda)));
    }

    @Override
    public void run() {
        log.info("event Loop iniciado");
        var b = false;
        while (true){
            try{Thread.sleep(delay);}
            catch (InterruptedException e){}
            log.debug("tick");
            checa();
            if(b)
                checaPolls();
            b = !b;
        }
    }
}
