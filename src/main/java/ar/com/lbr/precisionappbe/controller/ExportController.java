package ar.com.lbr.precisionappbe.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<byte[]> export(
            @RequestParam String content,
            @RequestParam String filename,
            @RequestParam(required = false, defaultValue = "UTF-8") String charset,
            @RequestParam(name = "mime", required = false, defaultValue = "application/octet-stream") String mime
    ) {
        byte[] data = java.util.Base64.getDecoder().decode(content);

        String encoded = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE,
                mime + (mime.startsWith("text/") && !mime.toLowerCase().contains("charset") ? ";charset=" + charset : ""));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename.replace("\"","") + "\"; filename*=UTF-8''" + encoded);
        headers.setCacheControl("no-store, must-revalidate");
        headers.setPragma("no-cache");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
