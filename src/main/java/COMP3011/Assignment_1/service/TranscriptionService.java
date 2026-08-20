package COMP3011.Assignment_1.service;

import org.springframework.web.multipart.MultipartFile;

import COMP3011.Assignment_1.model.TranscriptionResult;

public interface TranscriptionService {

    TranscriptionResult transcribe(MultipartFile audioFile);

}
