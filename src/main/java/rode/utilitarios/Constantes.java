package rode.utilitarios;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import rode.core.Helper;
import rode.model.ConfigGuid;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;

public class Constantes {
    public static final Random rng = new Random();
    private static final Dotenv env = Dotenv.load();
    public static Gson gson = new Gson();
    private static final HashMap<String, String> EMOTES = new HashMap<>(){{
        put("check","✔");put("esquerda","⬅");put("direita","➡");
        put("br","🇧🇷");put("en","🇺🇸");put("0","0️⃣");put("1","1️⃣");
        put("2","2️⃣");put("3","3️⃣");put("4","4️⃣");put("5","5️⃣");
        put("6","6️⃣");put("7","7️⃣");put("8","8️⃣");put("9","9️⃣");
        put("10","🔟");put("branco","\u26AA");put("preto","\u26AB");
    }};
    public static final String REGEX_SAIR = "^([Ff](im|echar?)|[Ee](xit|nd)|[Cc]lose|[Ss]air?)";
    private static final HashMap<Locale,HashMap<String, EmbedBuilder>>BUILDERS = new HashMap<>();
    private static final HashMap<String, Locale> LOC = new HashMap<>();
    public final static String PREFIXO = "-";
    private static final List<String> POOL_EMOTES = new ArrayList<>(Arrays.asList("🇦", "🇧", "🇨", "🇩", "🇪", "🇫", "🇬", "🇭", "🇮", "🇯", "🇰", "🇱", "🇲", "🇳", "🇴", "🇵", "🇶", "🇷", "🇸", "🇹","✔","⬅","➡","🇧🇷","🇺🇸","0️⃣","1️⃣","2️⃣","3️⃣","4️⃣","5️⃣","6️⃣","7️⃣","8️⃣","9️⃣","🔟")) ;
    public static final List<String> LETRAS = new ArrayList<>(Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","e","r","s","t","u","v","w","z","y"));
    public static String emote(String name) {
        return EMOTES.get(name);
    }
    public static String emotePoll(int index){
        return POOL_EMOTES.get(index);
    }
    public static Locale loc(String id){
        synchronized (LOC){
            if(LOC.containsKey(id))
                return LOC.get(id);
            else{
                try{
                    var config = Memoria.config(id);
                    var l =  new Locale(config.lingua(),config.pais());
                    LOC.put(id, l);
                    return l;
                }catch (Exception e){
                    var config = new ConfigGuid(id,"pt","BR");
                    Memoria.insert(config);
                    var l = new Locale(config.lingua(),config.pais());
                    LOC.put(id,l);
                    return l;
                }
            }
        }
    }

    public static void loc(String guildId, Locale l) {
        synchronized (LOC){
            LOC.put(guildId,l);
        }
    }
    public static EmbedBuilder builder(Locale loc, String cmd){
        synchronized (BUILDERS){
            if(BUILDERS.containsKey(loc))
                return BUILDERS.get(loc).get(cmd);
            BUILDERS.put(loc,new HashMap<>());
            return null;
        }
    }
    public static void addBuilder(Locale loc, String cmd,EmbedBuilder eb){
        synchronized (BUILDERS){
            if(BUILDERS.containsKey(loc)) {
                BUILDERS.get(loc).put(cmd, eb);
                return;
            }
            BUILDERS.put(loc,new HashMap<>(){{
                put(cmd,eb);
            }});
        }
    }
    public static String env(String key){
        return env.get(key);
    }
    public static EmbedBuilder builder(ResourceBundle rb){
        return builder().setTitle(rb.getString("embed.load"));
    }
    public static EmbedBuilder builder(){
        return new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
    }
    public static BiFunction<String, EmbedBuilder, Message> reply(Helper hm){
        return (a,b)->{
            if(a != null)
                return hm.replyFull(a);
            if(b != null)
                return hm.replyFull(b);
            return null;
        };
    }

    public static String fileName(String format) {
        return "lixo/" + System.nanoTime() + "." + format;
    }

    public static boolean containsEmote(String emoji) {
        return POOL_EMOTES.contains(emoji);
    }

    public static int indexEmote(String emoji) {
        return POOL_EMOTES.indexOf(emoji);
    }

}
