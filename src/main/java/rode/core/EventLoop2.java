package rode.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.model.maker.MensagemReacao;
import rode.model.maker.MensagemTexto;
import rode.utilitarios.Constantes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EventLoop2{
    private static Logger log = LoggerFactory.getLogger(EventLoop2.class);
    private static long count = 0;
    private static EventLoop2 instance;
    public static final long delay = Long.parseLong(Objects.requireNonNull(Constantes.env.get("delay")));
    public static final long delayS = delay/1000;
    private final static List<MensagemReacao> eventos_reacao = Collections.synchronizedList(new LinkedList<>());
    private final static List<MensagemTexto> eventos_texto = Collections.synchronizedList(new LinkedList<>());

    private EventLoop2(){

    }

    public static EventLoop2 getInstance() {
        if(instance == null)
            instance = new EventLoop2();
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

    public static void checa() {
        synchronized (eventos_reacao){
            eventos_reacao.removeIf(MensagemReacao::expirado);
        }
        synchronized (eventos_texto){
            eventos_texto.removeIf(MensagemTexto::expirado);
        }
    }
}
