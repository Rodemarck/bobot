package rode.comando.guild.domotica;

import net.dv8tion.jda.api.Permission;
import rode.core.Anotacoes.EComandoDomotica;
import rode.core.Helper;
import rode.model.ComandoGuild;
import rode.model.domo.Dispositivo;
import rode.model.domo.Rede;
import rode.model.domo.Usuario;
import rode.utilitarios.ClienteHttp;
import rode.utilitarios.Constantes;

import java.util.Arrays;
import java.util.LinkedList;

@EComandoDomotica
public class CadastraRede extends ComandoGuild {
    public CadastraRede() {
        super("rede", null, "rede");
    }

    @Override
    public void execute(String[] args, Helper.Mensagem event) throws Exception {
        try{
            var user = new Rede(args[1],System.currentTimeMillis()+"",event.getId(), Arrays.asList());
            ClienteHttp.post(Constantes.env("api") + "rede/" + event.getId(), Constantes.gson.toJson(user),(n, con)->{
                if(n >=200 && n <300){
                    event.reply("Rede criada!!");
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
