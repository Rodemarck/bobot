package rode.utilitarios;

import io.github.cdimascio.dotenv.Dotenv;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Constantes {
    public static final Dotenv env = Dotenv.load();
    public static final HashMap<String, String> EMOTES = new HashMap<>(){{
        put("check","✔");
        put("esquerda","⬅");
        put("direita","➡");
    }};
    public final static String PREFIXO = "-";
    public final static List<Long> EXCLUDE_CHAT = new ArrayList<>(Arrays.asList(484909251710550027l));
    public static final List<String> POOL_EMOTES = new ArrayList<>(Arrays.asList("🇦", "🇧", "🇨", "🇩", "🇪", "🇫", "🇬", "🇭", "🇮", "🇯", "🇰", "🇱", "🇲", "🇳", "🇴", "🇵", "🇶", "🇷", "🇸", "🇹","✔","⬅","➡")) ;
    public static final List<String> POOL_votos = new ArrayList<>(Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","e","r","s","t","u","v","w","z","y"));
    public static final Color[][] cores = new Color[][]{
            new Color[]{Color.decode("#FF0000") },
            new Color[]{Color.decode("#FF0000"), Color.decode("#00FFFF")},
            new Color[]{Color.decode("#FF0000"), Color.decode("#00FF00"), Color.decode("#0000FF") },
            new Color[]{Color.decode("#FF0000"), Color.decode("#80FF00"), Color.decode("#00FFFF"), Color.decode("#8000FF") },
            new Color[]{Color.decode("#FF0000"), Color.decode("#CCFF00"), Color.decode("#00FF66"), Color.decode("#0066FF"), Color.decode("#CC00FF") },
            new Color[]{Color.decode("#FF0000"), Color.decode("#FFFF00"), Color.decode("#00FF00"), Color.decode("#00FFFF"), Color.decode("#0000FF"), Color.decode("#FF00FF") },
            new Color[]{Color.decode("#FF0000"), Color.decode("#FFDB00"), Color.decode("#49FF00"), Color.decode("#00FF92"), Color.decode("#0092FF"), Color.decode("#4900FF"), Color.decode("#FF00DB") },
            new Color[]{Color.decode("#FF0000"), Color.decode("#FFBF00"), Color.decode("#80FF00"), Color.decode("#00FF40"), Color.decode("#00FFFF"), Color.decode("#0040FF"), Color.decode("#8000FF"), Color.decode("#FF00BF") },
            new Color[]{Color.decode("#FF0000"), Color.decode("#FFAA00"), Color.decode("#AAFF00"), Color.decode("#00FF00"), Color.decode("#00FFAA"), Color.decode("#00AAFF"), Color.decode("#0000FF"), Color.decode("#AA00FF"), Color.decode("#FF00AA") },
            new Color[]{Color.decode("#FF0000"), Color.decode("#FF9900"), Color.decode("#CCFF00"), Color.decode("#33FF00"), Color.decode("#00FF66"), Color.decode("#00FFFF"), Color.decode("#0066FF"), Color.decode("#3300FF"), Color.decode("#CC00FF"), Color.decode("#FF0099")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF8B00"),Color.decode("#E8FF00"),Color.decode("#5DFF00"),Color.decode("#00FF2E"),Color.decode("#00FFB9"),Color.decode("#00B9FF"),Color.decode("#002EFF"),Color.decode("#5D00FF"),Color.decode("#E800FF"),Color.decode("#FF008B")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF8000"),Color.decode("#FFFF00"),Color.decode("#80FF00"),Color.decode("#00FF00"),Color.decode("#00FF80"),Color.decode("#00FFFF"),Color.decode("#0080FF"),Color.decode("#0000FF"),Color.decode("#8000FF"),Color.decode("#FF00FF"),Color.decode("#FF0080")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF7600"),Color.decode("#FFEB00"),Color.decode("#9DFF00"),Color.decode("#27FF00"),Color.decode("#00FF4E"),Color.decode("#00FFC4"),Color.decode("#00C4FF"),Color.decode("#004EFF"),Color.decode("#2700FF"),Color.decode("#9D00FF"),Color.decode("#FF00EB"),Color.decode("#FF0076")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF6D00"),Color.decode("#FFDB00"),Color.decode("#B6FF00"),Color.decode("#49FF00"),Color.decode("#00FF24"),Color.decode("#00FF92"),Color.decode("#00FFFF"),Color.decode("#0092FF"),Color.decode("#0024FF"),Color.decode("#4900FF"),Color.decode("#B600FF"),Color.decode("#FF00DB"),Color.decode("#FF006D")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF6600"),Color.decode("#FFCC00"),Color.decode("#CCFF00"),Color.decode("#66FF00"),Color.decode("#00FF00"),Color.decode("#00FF66"),Color.decode("#00FFCC"),Color.decode("#00CCFF"),Color.decode("#0066FF"),Color.decode("#0000FF"),Color.decode("#6600FF"),Color.decode("#CC00FF"),Color.decode("#FF00CC"),Color.decode("#FF0066")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF6000"),Color.decode("#FFBF00"),Color.decode("#DFFF00"),Color.decode("#80FF00"),Color.decode("#20FF00"),Color.decode("#00FF40"),Color.decode("#00FF9F"),Color.decode("#00FFFF"),Color.decode("#009FFF"),Color.decode("#0040FF"),Color.decode("#2000FF"),Color.decode("#8000FF"),Color.decode("#DF00FF"),Color.decode("#FF00BF"),Color.decode("#FF0060")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF5A00"),Color.decode("#FFB400"),Color.decode("#F0FF00"),Color.decode("#96FF00"),Color.decode("#3CFF00"),Color.decode("#00FF1E"),Color.decode("#00FF78"),Color.decode("#00FFD2"),Color.decode("#00D2FF"),Color.decode("#0078FF"),Color.decode("#001EFF"),Color.decode("#3C00FF"),Color.decode("#9600FF"),Color.decode("#F000FF"),Color.decode("#FF00B4"),Color.decode("#FF005A")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF5500"),Color.decode("#FFAA00"),Color.decode("#FFFF00"),Color.decode("#AAFF00"),Color.decode("#55FF00"),Color.decode("#00FF00"),Color.decode("#00FF55"),Color.decode("#00FFAA"),Color.decode("#00FFFF"),Color.decode("#00AAFF"),Color.decode("#0055FF"),Color.decode("#0000FF"),Color.decode("#5500FF"),Color.decode("#AA00FF"),Color.decode("#FF00FF"),Color.decode("#FF00AA"),Color.decode("#FF0055")},
            new Color[]{Color.decode("#FF0000"),Color.decode("#FF5100"),Color.decode("#FFA100"),Color.decode("#FFF200"),Color.decode("#BCFF00"),Color.decode("#6BFF00"),Color.decode("#1BFF00"),Color.decode("#00FF36"),Color.decode("#00FF86"),Color.decode("#00FFD7"),Color.decode("#00D7FF"),Color.decode("#0086FF"),Color.decode("#0036FF"),Color.decode("#1B00FF"),Color.decode("#6B00FF"),Color.decode("#BC00FF"),Color.decode("#FF00F2"),Color.decode("#FF00A1"),Color.decode("#FF0051")}
    };

    public static String emote(String name) {
        return EMOTES.get(name);
    }
    public static String emotePoll(int index){
        return POOL_EMOTES.get(index);
    }
}
