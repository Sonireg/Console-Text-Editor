package editor.Converters;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.*;
import java.util.stream.*;
import java.io.*;

import com.google.gson.*;

public class XmlToJsonConverter implements FormatConverter {

    @Override
    public List<StringBuilder> convert(List<StringBuilder> content) {
        try {
            String xmlText = content.stream().map(StringBuilder::toString).collect(Collectors.joining());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(xmlText.getBytes()));

            doc.getDocumentElement().normalize();
            JsonObject jsonObject = new JsonObject();
            Element root = doc.getDocumentElement();
            jsonObject.add(root.getNodeName(), elementToJson(root));

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(jsonObject);
            return Arrays.stream(jsonOutput.split("\n")).map(StringBuilder::new).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(new StringBuilder("Ошибка преобразования XML в JSON"));
        }
    }

    private JsonElement elementToJson(Element element) {
        NodeList children = element.getChildNodes();
        if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
            return new JsonPrimitive(children.item(0).getTextContent());
        }

        JsonObject obj = new JsonObject();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElem = (Element) node;
                String tag = childElem.getTagName();
                JsonElement childJson = elementToJson(childElem);

                if (obj.has(tag)) {
                    JsonElement existing = obj.get(tag);
                    JsonArray array;
                    if (existing.isJsonArray()) {
                        array = existing.getAsJsonArray();
                    } else {
                        array = new JsonArray();
                        array.add(existing);
                    }
                    array.add(childJson);
                    obj.add(tag, array);
                } else {
                    obj.add(tag, childJson);
                }
            }
        }
        return obj;
    }
}
