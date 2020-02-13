package dev.educosta.gcpfileuploader;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobField;
import com.google.cloud.storage.StorageOptions;
import dev.educosta.gcpfileuploader.storage.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1")
public class FileUploadController {

  // The name of the bucket to access
  String bucketName = "bucket-poc-upload-files";

  private final StorageService storageService;

  @Autowired
  public FileUploadController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping(value = "/hello")
  public String hello() {
    return "HELLO";
  }

  @GetMapping(value = "/file/metadata")
  public Map<BlobField, String> readMetadata(@RequestParam(value = "name") String filename) {
    return this.storageService.metadata(filename);
  }

  @GetMapping(value = "/file/image", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] readImage() throws IOException {
    return readImageStream().readAllBytes();
  }

  @GetMapping(value = "/file/video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public @ResponseBody
  byte[] readVideo() throws IOException {
    return readVideoStream().readAllBytes();
  }

  private InputStream readImageStream() {
    InputStream imageInputStream = FileUploadController.class.getResourceAsStream("/icon.jpg");
    return Objects.requireNonNull(imageInputStream);
  }

  private InputStream readVideoStream() {
    InputStream imageInputStream = FileUploadController.class.getResourceAsStream("/video.mp4");
    return Objects.requireNonNull(imageInputStream);
  }

  @DeleteMapping(value = "/file")
  public void deleteFile(@RequestParam(value = "name") String filename) {
    this.storageService.delete(filename);
  }


  @PostMapping(value = "/file")
  public void upload(@RequestParam(name = "file") MultipartFile file) {
    storageService.store(file);
  }


  public void download(String srcFilename) {
    Storage storage = StorageOptions.getDefaultInstance().getService();

    Blob blob = storage.get(BlobId.of(bucketName, srcFilename));
    blob.getContent();

  }


  public void list() {
//    Storage storage = StorageOptions.getDefaultInstance().getService();
//
//    Page<Blob> blobs = bucket.list();
//    for (Blob blob : blobs.iterateAll()) {
//      // do something with the blob
//    }
  }

}
