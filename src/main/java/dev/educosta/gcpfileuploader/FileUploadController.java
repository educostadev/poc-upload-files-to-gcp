package dev.educosta.gcpfileuploader;

import com.google.cloud.storage.Storage.BlobField;
import dev.educosta.gcpfileuploader.storage.StorageService;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

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

  @GetMapping(value = "/file/image-byte", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] readImage(@RequestParam(value = "name") String filename) {
    return this.storageService.load(filename);
  }

  @GetMapping(value = "/file/image-url", produces = MediaType.APPLICATION_JSON_VALUE)
  public URL readImageAsURL(@RequestParam(value = "name") String filename) {
    return this.storageService.loadAsURL(filename);
  }

  @GetMapping(value = "/file/image-url-redirect", produces = MediaType.APPLICATION_JSON_VALUE)
  public RedirectView readImageAsRedirectedURL(@RequestParam(value = "name") String filename) {
    return new RedirectView(this.storageService.loadAsURL(filename).toString());
  }

  @GetMapping(value = "/files", produces = MediaType.APPLICATION_JSON_VALUE)
  public Set<URL> readURLs(@RequestParam(value = "directory") String directory) {
    return this.storageService.loadAllURL(directory);
  }

  @GetMapping(value = "/file/video-byte", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] readVideo(@RequestParam(value = "name") String filename) {
    return this.storageService.load(filename);
  }


  @DeleteMapping(value = "/file")
  public void deleteFile(@RequestParam(value = "name") String filename) {
    this.storageService.delete(filename);
  }


  @PostMapping(value = "/file")
  public void upload(@RequestParam(name = "file") MultipartFile file) {
    storageService.store(file);
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
