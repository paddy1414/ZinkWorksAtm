package org.patricknorton.zinkworks.ZinkWorksAtm;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class AtmResourceTest {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private MockMvc mockMvc;


    @Test
    void testLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/resetAccounts"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile?accountNum=123456789&pin=1234"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("{\"accountNum\":\"123456789\",\"pin\":\"1234\",\"overDraft\":200,\"openingBalance\":800}"));
        ;
        //    .andExpect(view().name("profile"));
    }

    @Test
    void testLoginWrongUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/resetAccounts"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile?accountNum=12345678911&pin=1234"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("{\"page\":\"noUser\"}"));
    }

    @Test
    void testGetWindraw() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/resetAccounts"));

        String expected = "{\"result\":\"Update successful\\nNew Balance is: 680 \\n50 euro notes: 2 \\n20 euro notes: 1 \\n\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/withdraw?accountNum=123456789&pin=1234&withdraw=123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(expected));
    }

    @Test
    void testAtmNotesBalance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/resetAccounts"));

        mockMvc.perform(MockMvcRequestBuilders.get("/web/atmNoteBalance"))
                .andExpect(status().isOk())
                .andExpect(view().name("notesRemainingInAtm"));

    }
}