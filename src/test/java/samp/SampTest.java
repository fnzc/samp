package samp;

import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class SampTest {

    @Test
    public void testNoHeader() throws IOException {
        final String message = "SAMP/1.0 EVENT /order/532534/items\n\nbananas";
        assertEquals("/order/532534/items",
                     Samp.parse(message).action());
        Map<String, String> headers = Samp.parse(message).headers();
        assertEquals(0, headers.size());
        assertEquals("bananas\n",
                     new String(Samp.parse(message).body()));
    }

    @Test
    public void testSingleHeader() throws IOException {
        final String message = "SAMP/1.0 EVENT /order/532534/items\nContent-Type: text/csv\n\n\"foo\",234,1,\"Wholesale\",88.99,,,,2,";
        assertEquals("/order/532534/items",
                     Samp.parse(message).action());
        Map<String, String> headers = Samp.parse(message).headers();
        assertEquals("text/csv", headers.get(Samp.ContentType));
        assertEquals(null, headers.get(Samp.CorrelationId));
        assertEquals(1, headers.size());
        assertEquals("\"foo\",234,1,\"Wholesale\",88.99,,,,2,\n",
                     new String(Samp.parse(message).body()));
    }

    @Test
    public void testAllHeaders() throws IOException {
        final String message = "SAMP/1.0 EVENT /make/lunch\nFrom: bob@someplace.com\nCorrelation-Id: a54d3200-d8c5-4ef2-8514-0e3f9e0533e9\nDate: 2016-05-12T04:03:39.668Z\nContent-Type: application/json\nTrace: api-gateway...menud...order-placement\n\n{\"orderNumber\":\"542523\",\"placed\":true,\"product\":\"burger\",\"quantity\":1}";
        assertEquals("/make/lunch",
                     Samp.parse(message).action());
        Map<String, String> headers = Samp.parse(message).headers();
        assertEquals("application/json", headers.get(Samp.ContentType));
        assertEquals("a54d3200-d8c5-4ef2-8514-0e3f9e0533e9", headers.get(Samp.CorrelationId));
        assertEquals(5, headers.size());
        assertEquals("{\"orderNumber\":\"542523\",\"placed\":true,\"product\":\"burger\",\"quantity\":1}\n",
                     new String(Samp.parse(message).body()));
    }

    @Test
    public void testMultilineBody() throws IOException {
        final String message = "SAMP/1.0 FAILURE /whinge\n\nTHIS\nIS\nCRAP\n";
        assertEquals("/whinge",
                     Samp.parse(message).action());
        Map<String, String> headers = Samp.parse(message).headers();
        assertEquals(0, headers.size());
        assertEquals("THIS\nIS\nCRAP\n",
                     new String(Samp.parse(message).body()));
    }

}
