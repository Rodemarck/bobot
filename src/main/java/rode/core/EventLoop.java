package rode.core;

import rode.model.ModelMensagem;
import rode.utilitarios.Constantes;

import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Objects;

public class EventLoop implements Runnable{
    private static volatile Hashtable<Long, ModelMensagem> mensagens = new Hashtable<>();
    public static final long delay = Long.parseLong(Objects.requireNonNull(Constantes.env.get("delay")));
    public static final long delayS = delay/1000;
    private static EventLoop instance;

    private EventLoop(){
        Executador.poolExecutor.execute(this);
    }
    public static EventLoop getInstance() {
        if(instance == null)
            instance = new EventLoop();
        return instance;
    }

    private void tempo(){
        synchronized (this){
            var agora = LocalDateTime.now();
            mensagens.entrySet().removeIf(m -> m.getValue().tick(agora));
        }
    }
    private ModelMensagem getMensagem(long id){
        synchronized (this){
            if(mensagens.containsKey(id)) {
                var m = mensagens.get(id);
                m.atualiza();
                return m;
            }
            return null;
        }
    }
    public static ModelMensagem mensagem(long id){
        return getInstance().getMensagem(id);
    }
    private void setMensagem(long id, ModelMensagem mensagem){
        synchronized (this){
            if(!mensagens.containsKey(id))
                mensagens.put(id,mensagem);
        }
    }
    public static void mensagem(long id, ModelMensagem mensagem){
        getInstance().setMensagem(id,mensagem);
    }
    private void deletaMensagem(long id){
        synchronized (this){
            if(mensagens.containsKey(id))
                mensagens.remove(id);
        }
    }
    public static void deleta(long id){
        getInstance().deletaMensagem(id);
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
