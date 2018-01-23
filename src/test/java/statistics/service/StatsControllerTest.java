package statistics.service;

import statistics.core.Stats;
import statistics.core.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;


@WebAppConfiguration
@RunWith(SpringRunner.class)
@WebMvcTest(value = StatsController.class, secure = false)
public class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SyncStatsCollector collector;

    @Test
    public void testPostTransaction() throws Exception {

        String txnJson = "{\"amount\":12.3,\"timestamp\":1478192204000}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                "/transactions").
                accept(MediaType.APPLICATION_JSON).content(txnJson).
                contentType(MediaType.APPLICATION_JSON);

        // Tests http code if collector rejected the transaction
        Mockito.when(collector.observe(Mockito.any(Transaction.class))).thenReturn(false);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        // Tests http code if collector accepted the transaction
        Mockito.when(collector.observe(Mockito.any(Transaction.class))).thenReturn(true);

        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testStatistics() throws Exception {

        Stats notEmpty = new Stats();
        notEmpty.observe(1);

        Stats[] collectorResults = new Stats[]{new Stats(), notEmpty};
        String[] expectedResponses = new String[]{
                "{\"count\":0}",
                "{\"count\":1,\"sum\":1.0,\"max\":1.0,\"min\":1.0,\"avg\":1.0}"};

        for (int i = 0; i < collectorResults.length; i++) {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                    "/statistics").
                    contentType(MediaType.APPLICATION_JSON);

            Mockito.when(collector.getAggregated()).thenReturn(collectorResults[i]);

            MvcResult result = mockMvc.perform(requestBuilder).andReturn();
            MockHttpServletResponse response = result.getResponse();
            assertEquals(HttpStatus.OK.value(), response.getStatus());
            assertEquals(expectedResponses[i], response.getContentAsString());
        }
    }
}