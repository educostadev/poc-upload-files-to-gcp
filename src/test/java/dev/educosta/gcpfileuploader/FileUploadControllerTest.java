package dev.educosta.gcpfileuploader;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileOutputStream;
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
        .param("name", "icon.jpg")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(resultHandler ->
            logger.debug(resultHandler.getResponse().getContentAsString())
        );

    resultActions.andExpect(status().isOk());
  }

  @Test
  void readImageAsURL() throws Exception {
    ResultActions resultActions = mockMvc.perform(get("/v1/file/image-url")
        .param("name", "icon.jpg")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(resultHandler ->
            logger.debug("Signed URL is here: " + resultHandler.getResponse().getContentAsString())
        );

    resultActions
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("http")))
    ;
  }

  @Test
  void uploadImage() throws Exception {
    String originalFileName = createFilename(".jpg");

    MockMultipartFile file = createImageFile(originalFileName);

    ResultActions resultActions = uploadFile(file);

    resultActions.andExpect(status().isOk());
  }


  @Test
  void deleteFile() throws Exception {
    String originalFileName = createFilename(".jpg");
    uploadFile(createImageFile(originalFileName)).andExpect(status().isOk());

    ResultActions resultActions = mockMvc.perform(delete("/v1/file")
        .param("name", originalFileName)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
    );

    resultActions.andExpect(status().isOk());
  }

  @Test
  void listURLs() throws Exception {
    String folder =
        FileUploadControllerTest.class.getSimpleName() + "/" + UUID.randomUUID().toString();

    String file1 = createFilename(folder, ".jpg");
    uploadFile(createImageFile(file1)).andExpect(status().isOk());

    String file2 = createFilename(folder, ".jpg");
    uploadFile(createImageFile(file2)).andExpect(status().isOk());

    ResultActions resultActions = mockMvc.perform(get("/v1/files")
        .param("directory", folder)
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(resultHandler ->
            logger.debug("Signed URLs: " + resultHandler.getResponse().getContentAsString())
        );

    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(content().string(containsString("http")))
    ;

  }

  /**
   * Read the icon.jpg from GCP. Also record the file locally for you see the result
   */
  @Test
  void readImage() throws Exception {
    ResultActions resultActions = mockMvc.perform(get("/v1/file/image-byte")
        .param("name", "icon.jpg")
        .contentType(MediaType.TEXT_PLAIN_VALUE))
        .andDo(resultHandler -> {
              String path = writeFile(resultHandler.getResponse().getContentAsByteArray(), ".jpg");
              logger.debug("Image File recorded locally at: " + path);
            }
        );

    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
    ;
  }

  @Test
  void readVideo() throws Exception {
    ResultActions resultActions = mockMvc.perform(get("/v1/file/video-byte")
        .param("name", "icon.jpg")
        .contentType(MediaType.TEXT_PLAIN_VALUE))
        .andDo(resultHandler -> {
              String path = writeFile(resultHandler.getResponse().getContentAsByteArray(), ".mp4");
              logger.debug("Video File recorded locally at: " + path);
            }
        );

    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
    ;
  }

  private String writeFile(byte[] bytes, String fileExtension) throws IOException {
    File file = File.createTempFile(UUID.randomUUID().toString(), fileExtension);
    FileOutputStream output = new FileOutputStream(file);
    output.write(bytes);
    return file.getAbsolutePath();
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
    return this.createFilename(FileUploadControllerTest.class.getSimpleName(), extension);
  }

  private String createFilename(String folder, String extension) {
    return new StringBuilder()
        .append(folder)
        .append("/")
        .append(UUID.randomUUID().toString())
        .append(extension).toString();
  }


  private InputStream readImageStream() {
    InputStream stream = FileUploadControllerTest.class.getResourceAsStream("/icon.jpg");
    assertNotNull(stream);
    return stream;
  }


}
