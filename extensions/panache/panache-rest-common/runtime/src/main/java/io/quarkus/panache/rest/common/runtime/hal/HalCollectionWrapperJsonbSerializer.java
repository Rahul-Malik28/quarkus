package io.quarkus.panache.rest.common.runtime.hal;

import java.util.Map;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class HalCollectionWrapperJsonbSerializer implements JsonbSerializer<HalCollectionWrapper> {

    private final HalLinksProvider linksExtractor;

    public HalCollectionWrapperJsonbSerializer() {
        this.linksExtractor = new RestEasyHalLinksProvider();
    }

    HalCollectionWrapperJsonbSerializer(HalLinksProvider linksExtractor) {
        this.linksExtractor = linksExtractor;
    }

    @Override
    public void serialize(HalCollectionWrapper wrapper, JsonGenerator generator, SerializationContext context) {
        generator.writeStartObject();
        writeEmbedded(wrapper, generator, context);
        writeLinks(wrapper.getElementType(), generator, context);
        generator.writeEnd();
    }

    private void writeEmbedded(HalCollectionWrapper wrapper, JsonGenerator generator, SerializationContext context) {
        generator.writeKey("_embedded");
        generator.writeStartObject();
        generator.writeKey(wrapper.getCollectionName());
        generator.writeStartArray();
        for (Object entity : wrapper.getCollection()) {
            context.serialize(new HalEntityWrapper(entity), generator);
        }
        generator.writeEnd();
        generator.writeEnd();
    }

    private void writeLinks(Class<?> type, JsonGenerator generator, SerializationContext context) {
        Map<String, HalLink> links = linksExtractor.getLinks(type);
        context.serialize("_links", links, generator);
    }
}
