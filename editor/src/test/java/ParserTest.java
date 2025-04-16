import org.junit.jupiter.api.Test;

import editor.Parsers.JSONParser;
import editor.Parsers.MDParser;
import editor.Parsers.TXTPArser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class ParserTest {
    
    @Test
    void testTXTParser() {
        TXTPArser parser = new TXTPArser();
        List<StringBuilder> input = Arrays.asList(
            new StringBuilder("Hello World"),
            new StringBuilder("Another line")
        );
        
        List<StringBuilder> result = parser.parse(input, 5);
        
        assertEquals(6, result.size());
        assertEquals("Hello", result.get(0).toString());
        assertEquals(" Worl", result.get(1).toString());
        assertEquals("d", result.get(2).toString());
        assertEquals("Anoth", result.get(3).toString());
    }
    
    @Test
    void testMDParserBold() {
        MDParser parser = new MDParser();
        List<StringBuilder> input = List.of(new StringBuilder("**bold**"));
        
        List<StringBuilder> result = parser.parse(input, 100);
        
        assertTrue(result.get(0).toString().contains("\u001B[1m"));
    }
    
    @Test
    void testJSONParserInvalid() {
        JSONParser parser = new JSONParser();
        List<StringBuilder> input = List.of(new StringBuilder("{invalid}"));
        
        List<StringBuilder> result = parser.parse(input, 100);
        
        assertEquals("Invalid JSON format", result.get(0).toString());
    }
}