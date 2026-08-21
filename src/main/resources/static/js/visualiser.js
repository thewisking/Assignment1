let audioContext;
let analyser;
let frequencyData;
let barValues;
let peakValues;
let animationFrameId;

const barCount = 32;
const barGap = 3;
const frequencyScale = 2.1;

const canvas = document.getElementById("audio-canvas");
const canvasContext = canvas ? canvas.getContext("2d") : null;

const recordingIndicator =
    document.getElementById("recording-indicator");

export function setupSoundVisualiser(stream) {
    const AudioContextClass = window.AudioContext || window.webkitAudioContext;

    if (!AudioContextClass || !canvasContext) {
        return;
    }

    audioContext = new AudioContextClass();

    const source = audioContext.createMediaStreamSource(stream);

    analyser = audioContext.createAnalyser();
    analyser.fftSize = 512;
    analyser.smoothingTimeConstant = 0.75;

    source.connect(analyser);

    frequencyData =
        new Uint8Array(analyser.frequencyBinCount);

    barValues =
        new Float32Array(barCount);

    peakValues =
        new Float32Array(barCount);

    if (recordingIndicator) {
        recordingIndicator.textContent = "REC // LIVE";
    }

    updateSoundVisualiser();
}

function updateSoundVisualiser() {
    if (!analyser || !canvasContext) {
        return;
    }

    analyser.getByteFrequencyData(frequencyData);

    updateBars();

    drawGrid();
    drawSpectrum();

    animationFrameId =
        requestAnimationFrame(updateSoundVisualiser);
}

function updateBars() {
    for (let index = 0; index < barCount; index++) {
        const targetValue =
            getFrequencyValue(index);

        const responseSpeed =
            targetValue > barValues[index]
                ? 0.35
                : 0.14;

        barValues[index] +=
            (targetValue - barValues[index])
            * responseSpeed;

        peakValues[index] =
            Math.max(
                barValues[index],
                peakValues[index] - 0.012
            );
    }
}

function getFrequencyValue(index) {
    const position =
        index / (barCount - 1);

    const frequencyIndex =
        Math.floor(
            Math.pow(position, frequencyScale)
            * (frequencyData.length - 1)
        );

    return frequencyData[frequencyIndex] / 255;
}

function drawGrid() {
    const width = canvas.width;
    const height = canvas.height;

    canvasContext.clearRect(0, 0, width, height);

    canvasContext.strokeStyle =
        "rgba(63, 220, 255, 0.10)";

    canvasContext.lineWidth = 1;

    for (let x = 0; x <= width; x += 45) {
        canvasContext.beginPath();
        canvasContext.moveTo(x, 0);
        canvasContext.lineTo(x, height);
        canvasContext.stroke();
    }

    for (let y = 0; y <= height; y += 32) {
        canvasContext.beginPath();
        canvasContext.moveTo(0, y);
        canvasContext.lineTo(width, y);
        canvasContext.stroke();
    }

    canvasContext.strokeStyle =
        "rgba(99, 236, 255, 0.3)";

    canvasContext.beginPath();
    canvasContext.moveTo(0, height / 2);
    canvasContext.lineTo(width, height / 2);
    canvasContext.stroke();
}

function drawSpectrum() {
    const width = canvas.width;
    const height = canvas.height;
    const centreX = width / 2;
    const centreY = height / 2;
    const maximumHeight = height * 0.42;
    const availableWidth = width / 2;
    const totalGapWidth = barGap * (barCount - 1);
    const barWidth = (availableWidth - totalGapWidth) / barCount;

    canvasContext.save();

    for (let index = 0; index < barCount; index++) {
        const distanceFromCentre = index * (barWidth + barGap);
        const leftX = centreX - barWidth - distanceFromCentre;
        const rightX = centreX + distanceFromCentre;
        const barHeight = Math.max(3, barValues[index] * maximumHeight);
        const peakHeight = peakValues[index] * maximumHeight;
        const colourProgress = index / (barCount - 1);
        const lightness = 1000 + colourProgress * 20;
        const colour = `hsl(0 100% ${lightness}%)`;

        drawSpectrumBar(leftX, centreY, barWidth, barHeight, peakHeight, colour);
        drawSpectrumBar(rightX, centreY, barWidth, barHeight, peakHeight, colour);
    }

    canvasContext.restore();
}

function drawSpectrumBar(x, centreY, barWidth, barHeight, peakHeight, colour) {
    canvasContext.fillStyle = colour;
    canvasContext.shadowBlur = 9;
    canvasContext.shadowColor = colour;
    canvasContext.globalAlpha = 0.9;

    drawRoundedRectangle(
        x,
        centreY - barHeight,
        barWidth,
        barHeight,
        3
    );

    canvasContext.globalAlpha = 0.18;
    canvasContext.shadowBlur = 0;

    drawRoundedRectangle(
        x,
        centreY,
        barWidth,
        barHeight * 0.45,
        3
    );

    canvasContext.globalAlpha = 0.8;
    canvasContext.fillStyle = "#003cff";
    canvasContext.shadowBlur = 6;
    canvasContext.shadowColor = "#ffffff";

    drawRoundedRectangle(
        x,
        centreY - peakHeight - 2,
        barWidth,
        2,
        1
    );
}

function drawRoundedRectangle(x, y, width, height, radius) {
    const limitedRadius =
        Math.min(
            radius,
            width / 2,
            height / 2
        );

    canvasContext.beginPath();

    canvasContext.moveTo(
        x + limitedRadius,
        y
    );

    canvasContext.lineTo(
        x + width - limitedRadius,
        y
    );

    canvasContext.quadraticCurveTo(
        x + width,
        y,
        x + width,
        y + limitedRadius
    );

    canvasContext.lineTo(
        x + width,
        y + height - limitedRadius
    );

    canvasContext.quadraticCurveTo(
        x + width,
        y + height,
        x + width - limitedRadius,
        y + height
    );

    canvasContext.lineTo(
        x + limitedRadius,
        y + height
    );

    canvasContext.quadraticCurveTo(
        x,
        y + height,
        x,
        y + height - limitedRadius
    );

    canvasContext.lineTo(
        x,
        y + limitedRadius
    );

    canvasContext.quadraticCurveTo(
        x,
        y,
        x + limitedRadius,
        y
    );

    canvasContext.fill();
}

export function stopSoundVisualiser() {
    if (animationFrameId) {
        cancelAnimationFrame(animationFrameId);
    }

    animationFrameId = null;

    if (audioContext) {
        audioContext.close();
    }

    audioContext = null;
    analyser = null;
    frequencyData = null;
    barValues = null;
    peakValues = null;

    if (recordingIndicator) {
        recordingIndicator.textContent = "STANDBY";
    }

    drawIdleState();
}

function drawIdleState() {
    if (!canvasContext) {
        return;
    }

    drawGrid();

    canvasContext.strokeStyle =
        "rgba(99, 236, 255, 0.45)";

    canvasContext.lineWidth = 2;

    canvasContext.beginPath();
    canvasContext.moveTo(
        0,
        canvas.height / 2
    );

    canvasContext.lineTo(
        canvas.width,
        canvas.height / 2
    );

    canvasContext.stroke();
}

drawIdleState();