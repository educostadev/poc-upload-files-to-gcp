package dev.educosta.gcpfileuploader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
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
    ResultActions resultActions = mockMvc.perform(get("/v1/file/metadata")
        .param("name", "dashboard.png")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(resultHandler ->
            logger.debug(resultHandler.getResponse().getContentAsString())
        );

    resultActions.andExpect(status().isOk());
  }

  @Test
  void uploadImage() throws Exception {
    String originalFileName = createFilename(".png");

    MockMultipartFile file = createImageFile(originalFileName);

    ResultActions resultActions = uploadFile(file);

    resultActions.andExpect(status().isOk());
  }


  @Test
  void deleteFile() throws Exception {
    String originalFileName = createFilename(".png");
    uploadFile(createImageFile(originalFileName)).andExpect(status().isOk());

    ResultActions resultActions = mockMvc.perform(delete("/v1/file")
        .param("name", originalFileName)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
    );

    resultActions.andExpect(status().isOk());
  }

  private MockMultipartFile createImageFile(String originalFileName) throws IOException {
    return new MockMultipartFile(
        "file",
        originalFileName,
        MediaType.IMAGE_PNG_VALUE,
        readImageStream().readAllBytes()
    );
  }

  private ResultActions uploadFile(MockMultipartFile file) throws Exception {
    return mockMvc.perform(
        MockMvcRequestBuilders.multipart("/v1/file")
            .file(file)
            .contentType(Objects.requireNonNull(file.getContentType()))
    );
  }

  private String createFilename(String extension) {
    return new StringBuilder()
        .append(FileUploadControllerTest.class.getSimpleName())
        .append("/")
        .append(UUID.randomUUID().toString())
        .append(extension).toString();
  }


  void uploadImageAntigo() throws Exception {

    String fileName = "/icon.png";
    InputStream inputStream = FileUploadControllerTest.class.getResourceAsStream(fileName);
    String MEDIA_TYPE_VALUE = MediaType.IMAGE_PNG_VALUE;

    MockMultipartFile file = new MockMultipartFile(fileName, "", MEDIA_TYPE_VALUE,
        inputStream.readAllBytes());
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders.multipart("/v1/file")
            .file("file", file.getBytes()));
    //.characterEncoding("UTF-8"));

    resultActions.andExpect(status().isOk());
  }

  private InputStream readImageStream() {
    InputStream imageInputStream = FileUploadControllerTest.class.getResourceAsStream("/icon.png");
    assertNotNull(imageInputStream);
    return imageInputStream;
  }
}
