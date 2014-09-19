package eu.heronnet.core.model.rdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class TripleTest {

    private final BsonFactory bsonFactory = new BsonFactory();
    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerSubtypes(Triple.class, Resource.class);
    }


    @Test
    public void testTriple() {
        Triple triple = new Triple(new Resource("foo"), Predicate.HAS, new Resource("bar"));

        System.out.println(triple.toString());
    }

    @Test
    public void testNestedTriple() throws JsonProcessingException {
        Triple bugsBunny = new Triple(new Resource("Bugs Bunny"), Predicate.ISA, new Resource("rabbit"));
        Triple roadRunner = new Triple(new Resource("Roadrunner"), Predicate.ISA, new Resource("bird"));

        Triple chases = new Triple(bugsBunny, new Predicate("chases"), roadRunner);

        String string = mapper.writeValueAsString(chases);
        assertEquals(string,
                "{\"@type\":\"Triple\",\"subject\":{\"@type\":\"Triple\",\"subject\":{\"@type\":\"Resource\",\"value\":\"Bugs Bunny\"},\"predicate\":{\"predicate\":\"isa\"},\"object\":{\"@type\":\"Resource\",\"value\":\"rabbit\"}},\"predicate\":{\"predicate\":\"chases\"},\"object\":{\"@type\":\"Triple\",\"subject\":{\"@type\":\"Resource\",\"value\":\"Roadrunner\"},\"predicate\":{\"predicate\":\"isa\"},\"object\":{\"@type\":\"Resource\",\"value\":\"bird\"}}}\n");
    }

    @Test
    public void testEquality() throws IOException {
        Triple bugsBunny = new Triple(new Resource("Bugs Bunny"), Predicate.ISA, new Resource("rabbit"));
        Triple roadRunner = new Triple(new Resource("Roadrunner"), Predicate.ISA, new Resource("bird"));

        Triple chases = new Triple(bugsBunny, new Predicate("chases"), roadRunner);
        String string = mapper.writeValueAsString(chases);
        Triple back = mapper.readValue(string, Triple.class);

        assertEquals(chases, back);
    }

    @Test
    public void testComposite() {
        Triple bugsBunny = new Triple(new Resource("Bugs Bunny"), Predicate.ISA, new Resource("rabbit"));

    }
}