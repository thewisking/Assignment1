import {
    setupSoundVisualiser,
    stopSoundVisualiser
} from "./visualiser.js";

const recordButton = document.getElementById("record-button");
const statusText = document.getElementById("recording-status");
const transcriptionOutput = document.getElementById("transcription-output");

let mediaRecorder;
let microphoneStream;
let audioChunks = [];

recordButton.addEventListener("click", () => {

    if (mediaRecorder && mediaRecorder.state === "recording") {
        stopRecording();
    } else {
        startRecording();
    }

});

async function startRecording() {
    try {
        audioChunks = [];

        transcriptionOutput.textContent = "";
        statusText.textContent = "Requesting microphone permission...";

        microphoneStream = await navigator.mediaDevices.getUserMedia({ audio: true });

        const mimeType = getSupportedMimeType();

        if (mimeType) {
            mediaRecorder = new MediaRecorder( microphoneStream, { mimeType: mimeType } );
        } else {
            mediaRecorder = new MediaRecorder(microphoneStream);
        }

        mediaRecorder.addEventListener("dataavailable", event => {

            if (event.data.size > 0) {
                audioChunks.push(event.data);
            }

        });

        mediaRecorder.addEventListener("stop", uploadRecording);

        setupSoundVisualiser(microphoneStream);

        mediaRecorder.start();

        recordButton.disabled = false;
        recordButton.textContent = "Stop recording";
        statusText.textContent = "Recording...";

    } catch (error) {
        recordButton.disabled = false;
        recordButton.textContent = "Start recording";

        statusText.textContent = "Microphone access was denied or is unavailable.";

        console.error(error);
    }
}

function stopRecording() {
    if (!mediaRecorder || mediaRecorder.state === "inactive") {
        return;
    }

    mediaRecorder.stop();

    stopSoundVisualiser();

    if (microphoneStream) {
        microphoneStream.getTracks().forEach(track => track.stop());
    }

    recordButton.disabled = true;
    recordButton.textContent = "Transcribing...";
    statusText.textContent = "Preparing transcription...";
}

async function uploadRecording() {

    const recordingType =   mediaRecorder.mimeType || "audio/webm";
    const recordingBlob =   new Blob(audioChunks, { type: recordingType });
    const formData =        new FormData();

    formData.append( "audio", recordingBlob, getRecordingFilename(recordingType) );
    statusText.textContent = "Transcribing...";

    try {
        const response = await fetch(
            "/api/v1/transcribe",
            {
                method: "POST",
                body: formData
            }
        );

        if (!response.ok) {
            throw new Error(
                `Transcription request failed: ${response.status}`
            );
        }

        const transcription = await response.text();

        transcriptionOutput.textContent = transcription || "No transcription was returned.";
        statusText.textContent = "Finished. Ready to record again.";

    } catch (error) {
        transcriptionOutput.textContent = "The transcription request failed.";
        statusText.textContent = "Ready to record again.";

        console.error(error);

    } finally {
        recordButton.disabled = false;
        recordButton.textContent = "Start recording";
        mediaRecorder = null;
    }
}

function getSupportedMimeType() {
    const mimeTypes = [
        "audio/webm;codecs=opus",
        "audio/webm",
        "audio/ogg;codecs=opus"
    ];

    return mimeTypes.find(
        mimeType => MediaRecorder.isTypeSupported(mimeType)
    );
}

function getRecordingFilename(recordingType) {
    if (recordingType.includes("ogg")) {
        return "recording.ogg";
    }

    return "recording.webm";
}