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

import static org.mockito.Mockito.when;
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
        mockMvc.perform(MockMvcRequestBuilders.post("/profile?accountNum=123456789&pin=1234"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }
    @Test
    void testLoginWrongUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/profile?accountNum=12345678911&pin=1234"))
                .andExpect(status().isOk())
                .andExpect(view().name("noUser"));
    }

    @Test
    void testGetWindraw() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/withdraw?accountNum=123456789&pin=1234&withdraw=123"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }

    @Test
    void testAtmNotesBalance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/atmNoteBalance"))
                .andExpect(status().isOk())
                .andExpect(view().name("notesRemainingInAtm"));

    }
}