package editor.Converters;

import com.google.gson.*;
import java.util.*;
import java.util.stream.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

public class JsonToXmlConverter implements FormatConverter {

    @Override
    public List<StringBuilder> convert(List<StringBuilder> content) {
        String jsonText = content.stream().map(StringBuilder::toString).collect(Collectors.joining());
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonText);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("root");
            doc.appendChild(root);
            buildXml(jsonElement, doc, root);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return Arrays.stream(writer.toString().split("\n")).map(StringBuilder::new).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(new StringBuilder("Ошибка преобразования JSON в XML"));
        }
    }

    private void buildXml(JsonElement json, Document doc, Element parent) {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                Element child = doc.createElement(entry.getKey());
                buildXml(entry.getValue(), doc, child);
                parent.appendChild(child);
            }
        } else if (json.isJsonArray()) {
            JsonArray arr = json.getAsJsonArray();
            for (JsonElement elem : arr) {
                Element item = doc.createElement("item");
                buildXml(elem, doc, item);
                parent.appendChild(item);
            }
        } else if (json.isJsonPrimitive()) {
            parent.appendChild(doc.createTextNode(json.getAsString()));
        } else if (json.isJsonNull()) {
            parent.appendChild(doc.createTextNode("null"));
        }
    }
}
