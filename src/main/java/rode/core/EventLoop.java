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
public final class EventLoop implements Runnable{
    private static Logger log = LoggerFactory.getLogger(EventLoop.class);
    private static long count = 0;
    private static EventLoop instance;
    public static final long delay = Long.parseLong(Objects.requireNonNull(Constantes.env("delay")));
    public static final long delayS = delay/1000;
    private final static List<MensagemReacao> eventos_reacao = Collections.synchronizedList(new LinkedList<>());
    private final static List<MensagemTexto> eventos_texto = Collections.synchronizedList(new LinkedList<>());
    private final static List<Aviso> eventos_avisos = Collections.synchronizedList(new LinkedList<>());
    private final static List<Aviso> slash_cache = Collections.synchronizedList(new LinkedList<>());
    private static JDA jda;
    private EventLoop(JDA jda){
        EventLoop.jda = jda;
        Executador.poolExecutor.execute(()->
                run()
        );
        Executador.poolExecutor.execute(()->
            Memoria.eachConfig(cf->cf.avisos().forEach(EventLoop::addAviso))
        );
    }

    public static EventLoop getInstance(JDA j) {
        log.info("instancia");
        if(instance == null)
            instance = new EventLoop(j);
        return instance;
    }

    public static long geraId() {
        return ++count;
    }

    public static void reacaoGuild(Helper.Reacao hr) {
        log.info("procurando reação");
        synchronized (eventos_reacao){
            var lista = eventos_reacao.stream().filter(loop ->
                    (loop.getMensagem().getId().equals(hr.getMensagem().getId()))
                    && (loop.getMembros() == null || loop.getMembros().contains(hr.getId()))
                    && (loop.getPermissao() == null || hr.member.hasPermission(loop.getPermissao()))
            ).collect(Collectors.toList());
            for(var lop :lista) {
                log.trace("chamando ->>"+lop.getClass().getName());
                lop.run(hr);
            }
        }
    }
    public static void textoGuild(Helper.Mensagem hm){
        synchronized (eventos_texto){
            var lista = eventos_texto.stream().filter(loop->
                    (loop.getMembros() == null || loop.getMembros().contains(hm.getId()))
                    && (loop.getPermissao() == null || hm.getEvent().getMember().hasPermission(loop.getPermissao()))
            ).collect(Collectors.toList());
            for(var loop:lista){
                log.trace("chamando ->>"+loop.getClass().getName());
                loop.run(hm);
            }
        }
    }

    public static void addReacao(MensagemReacao reacaoMensagem){
        log.debug("colocando reação");
        synchronized (eventos_reacao){
            eventos_reacao.add(reacaoMensagem);
            log.debug("reação colocada");
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
        //Executador.poolExecutor.execute(()->Memoria.eachPoll(m->m.notify_(jda)));
    }

    @Override
    public void run() {
        log.info("event Loop iniciado, possuindo tick em intervalos de " + delay);
        var n = 0;
        while (true){
            try{Thread.sleep(delay);}
            catch (InterruptedException e){}
            log.debug("tick");
            checa();
            if(n++ == 10) {
                checaPolls();
                n = 0;
            }
        }
    }
}
