package dev.educosta.gcpfileuploader;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class GcpFileUploaderController {

  @GetMapping(value = "/file/read")
  public String read() {
    return "OK";
  }

  @PostMapping(value = "/file/upload")
  public boolean write() {
    return true;
  }
}
