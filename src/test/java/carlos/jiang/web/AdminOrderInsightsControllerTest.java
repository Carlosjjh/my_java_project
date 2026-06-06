package carlos.jiang.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AdminOrderInsightsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void analyzeReturnsAggregatedOrderMetrics() throws Exception {
        MockHttpSession session = registerAndPrepareProfile("insight_user_1");
        createOrder(session, 1, 2);
        createOrder(session, 2, 1);

        mockMvc.perform(get("/api/admin/order-insights")
                        .session(session)
                        .param("topLimit", "2")
                        .param("minAmount", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCount").value(2))
                .andExpect(jsonPath("$.totalSales").value(357.0))
                .andExpect(jsonPath("$.averageOrderValue").value(178.5))
                .andExpect(jsonPath("$.topProducts", hasSize(2)))
                .andExpect(jsonPath("$.topProducts[0].productId").value(2))
                .andExpect(jsonPath("$.topProducts[0].sales").value(199.0))
                .andExpect(jsonPath("$.recentOrders", hasSize(2)));
    }

    @Test
    void analyzeRejectsInvalidDateRange() throws Exception {
        MockHttpSession session = registerAndPrepareProfile("insight_user_2");

        mockMvc.perform(get("/api/admin/order-insights")
                        .session(session)
                        .param("from", "2026-05-02")
                        .param("to", "2026-05-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("from must be before or equal to to"));
    }

    private MockHttpSession registerAndPrepareProfile(String account) throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Insight User",
                                  "account": "%s",
                                  "password": "123456"
                                }
                                """.formatted(account)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) registerResult.getRequest().getSession(false);
        mockMvc.perform(put("/api/auth/profile")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipientName": "Zhang San",
                                  "phone": "13800138000",
                                  "address": "Shanghai"
                                }
                                """))
                .andExpect(status().isOk());
        return session;
    }

    private void createOrder(MockHttpSession session, long productId, int quantity) throws Exception {
        mockMvc.perform(post("/api/orders")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    {
                                      "productId": %d,
                                      "quantity": %d
                                    }
                                  ]
                                }
                                """.formatted(productId, quantity)))
                .andExpect(status().isOk());
    }
}
