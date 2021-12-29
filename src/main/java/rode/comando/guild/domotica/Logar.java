package rode.comando.guild.domotica;

import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.core.Anotacoes.EComandoDomotica;
import rode.core.Helper;
import rode.model.ComandoGuild;
import rode.model.domo.Usuario;
import rode.utilitarios.ClienteHttp;
import rode.utilitarios.Constantes;

@EComandoDomotica
public class Logar extends ComandoGuild {
    private static final Logger log = LoggerFactory.getLogger(Logar.class);
    public Logar() {
        super("logar", null, "login","logar");
    }


    @Override
    public void execute(String[] args, Helper.Mensagem event) throws Exception {
        event.getMensagem().delete().queue();
        log.info("logando");
        try{
            var user = new Usuario(event.getId(),args[1]);
            ClienteHttp.post(Constantes.env("api") + "usuario/logar", Constantes.gson.toJson(user),(n,con)->{
                if(n >=200 && n <300){
                    event.reply("logado");
                }
                System.out.println(n);
                if(con.getErrorStream() != null)
                    event.reply(new String(con.getErrorStream().readAllBytes()));
            });
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
