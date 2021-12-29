package rode.comando.guild.domotica;

import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoDomotica;
import rode.core.Helper;
import rode.model.ComandoGuild;
import rode.model.domo.Usuario;
import rode.utilitarios.ClienteHttp;
import rode.utilitarios.Constantes;

@EComandoDomotica
public class Cadastrar extends ComandoGuild {
    private static final Logger log = LoggerFactory.getLogger(Cadastrar.class);
    public Cadastrar() {
        super("cadastro", null, "cadastro","cadastrar");
    }

    @Override
    public void execute(String[] args, Helper.Mensagem event) throws Exception {
        event.getMensagem().delete().queue();
        log.info("cadastrando");
        try{
            var user = new Usuario(event.getId(),args[1]);
            ClienteHttp.post(Constantes.env("api") + "usuario", Constantes.gson.toJson(user),(n, con)->{
                if(n >=200 && n <300){
                    event.reply("cadastrado");
                    return;
                }
                if(con.getErrorStream()!=null)
                    event.reply(n + "\n" +new String(con.getErrorStream().readAllBytes()));
            });
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
