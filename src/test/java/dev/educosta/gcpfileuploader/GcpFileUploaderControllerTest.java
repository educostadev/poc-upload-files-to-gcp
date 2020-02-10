package dev.educosta.gcpfileuploader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@SpringBootTest
class GcpFileUploaderControllerTest {

  @Autowired
  MockMvc mockMvc;


  @Test
  void fileUploadAndRead() throws Exception {
    upload();
    read();
  }

  private void read() throws Exception {
    ResultActions resultActions = mockMvc.perform(get("/v1/file/read")
        .contentType(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk());
  }

  private void upload() throws Exception {
    ResultActions resultActions = mockMvc.perform(post("/v1/file/upload")
        .contentType(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk());
  }


}
