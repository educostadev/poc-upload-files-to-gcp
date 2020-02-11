package dev.educosta.gcpfileuploader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
    ""
})
class FileUploadControllerTest {

  @Autowired
  MockMvc mockMvc;

  static final Logger logger = LoggerFactory.getLogger(FileUploadControllerTest.class);

  @BeforeAll
  static void init() {
    assertNotNull(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
  }


  @Test
  void readMetadata() throws Exception {

    ResultActions resultActions = mockMvc.perform(get("/v1/file/readMetadata")
        .param("name", "dashboard.png")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(resultHandler ->
            logger.debug(resultHandler.getResponse().getContentAsString())
        );

    resultActions.andExpect(status().isOk());
  }

  @Test
  void uploadImage() throws Exception {

    String fileName = "/icon.png";
    InputStream inputStream = FileUploadControllerTest.class.getResourceAsStream(fileName);
    String MEDIA_TYPE_VALUE = MediaType.IMAGE_PNG_VALUE;

    MockMultipartFile file = new MockMultipartFile(fileName, "", MEDIA_TYPE_VALUE,
        inputStream.readAllBytes());
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders.multipart("/v1/file/upload")
            .file("file", file.getBytes()));
    //.characterEncoding("UTF-8"));

    resultActions.andExpect(status().isOk());
  }


}
