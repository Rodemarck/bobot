package rode.utilitarios;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    private static Charset UTF8 = StandardCharsets.UTF_8;


    public static LinkedList<String> extract(String pattern, String input){
        LinkedList<String> strs = new LinkedList<>();
        Matcher m = Pattern
                .compile(pattern)
                .matcher(input);

        String str;
        while (m.find()){
            str = m.group();
            strs.add(str.substring(1,str.length()-1));
        }
        return strs;
    }
}
