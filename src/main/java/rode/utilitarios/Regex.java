package rode.utilitarios;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Regex {
    private static Charset UTF8 = StandardCharsets.UTF_8;


    public static LinkedList<String> extractInside(String pattern, String input){
        LinkedList<String> strs = new LinkedList<>(extract(pattern, input)
                .stream()
                .map(s -> s.substring(1, s.length()-1))
                .toList()
        );
        return strs;
    }

    public static LinkedList<String> extract(String pattern, String input){
        LinkedList<String> strs = new LinkedList<>();
        Matcher m = Pattern
                .compile(pattern)
                .matcher(input);

        while (m.find())
            strs.add( m.group());
        return strs;
    }

    public static boolean isLink(String input){
        Matcher m = Pattern
                .compile("(?:http(s)?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]+")
                .matcher(input);
        return m.find();
    }
}
