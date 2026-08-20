package COMP3011.Assignment_1.service;

import org.springframework.web.multipart.MultipartFile;

public interface TranscriptionService {

    String transcribe(MultipartFile audioFile);

}
