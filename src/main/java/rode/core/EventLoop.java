package rode.core;

import rode.model.ModelMensagem;
import rode.utilitarios.Constantes;

import java.time.LocalDateTime;
import java.util.Hashtable;

public class EventLoop implements Runnable{
    private static volatile Hashtable<Long, ModelMensagem> mensagens = new Hashtable<>();
    public static final long delay = Long.parseLong(Constantes.env.get("delay"));
    private static EventLoop instance;

    private EventLoop(){
        run();
    }
    public static EventLoop getInstance() {
        if(instance == null)
            instance = new EventLoop();
        return instance;
    }

    private void tempo(){
        synchronized (this){
            var agora = LocalDateTime.now();
            mensagens.entrySet().removeIf(item->item.getValue().tempoLimite().isBefore(agora));
        }
    }
    public ModelMensagem mensagem(long id){
        synchronized (this){
            if(mensagens.containsKey(id)) {
                var m = mensagens.get(id);
                m.atualiza();
                return m;
            }
            return null;
        }
    }
    public void mensagem(long id, ModelMensagem mensagem){
        synchronized (this){
            if(!mensagens.containsKey(id))
                mensagens.put(id,mensagem);
        }
    }

    @Override
    public void run() {
        while (true){
            try{
                Thread.sleep(delay);
                tempo();
            }catch (Throwable t){
                t.printStackTrace();
            }
        }
    }
}
