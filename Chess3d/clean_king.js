const { execSync } = require('child_process');
const fs = require('fs');

const width = 1024, height = 1024;
const rawInput = '/tmp/king_inspect.raw';
const buffer = fs.readFileSync(rawInput);
const outputBuffer = Buffer.from(buffer);

const visited = new Uint8Array(width * height);
const queue = [];

function isBg(r, g, b) {
    const maxVal = Math.max(r, g, b);
    const minVal = Math.min(r, g, b);
    const sat = maxVal - minVal;
    
    // Background in king image is checkerboard / grey / neutral / violet-gray
    // The wooden piece has warm ivory tone:
    const isWarmWood = (r - b > 20 && r - g > -10 && r > 70);
    const isBrightIvoryHighlight = (r > 190 && g > 170 && r > b + 12);
    const isDarkWoodCrevice = (r > 35 && r - b > 12 && maxVal < 90);
    
    if (isWarmWood || isBrightIvoryHighlight || isDarkWoodCrevice) {
        return false; // Piece
    }
    return true; // Background
}

// Seed all border pixels
for (let x = 0; x < width; x++) {
    queue.push([x, 0], [x, height - 1]);
}
for (let y = 0; y < height; y++) {
    queue.push([0, y], [width - 1, y]);
}

let head = 0;
while (head < queue.length) {
    const [x, y] = queue[head++];
    if (x < 0 || x >= width || y < 0 || y >= height) continue;
    const pIndex = y * width + x;
    if (visited[pIndex]) continue;
    visited[pIndex] = 1;

    const idx = pIndex * 4;
    const r = buffer[idx], g = buffer[idx + 1], b = buffer[idx + 2];
    
    if (isBg(r, g, b)) {
        outputBuffer[idx + 3] = 0; // Transparent

        if (x > 0 && !visited[y * width + (x - 1)]) queue.push([x - 1, y]);
        if (x < width - 1 && !visited[y * width + (x + 1)]) queue.push([x + 1, y]);
        if (y > 0 && !visited[(y - 1) * width + x]) queue.push([x, y - 1]);
        if (y < height - 1 && !visited[(y + 1) * width + x]) queue.push([x, y + 1]);
    }
}

// Anti-aliasing / soft feathering along transparent border edges
for (let y = 1; y < height - 1; y++) {
    for (let x = 1; x < width - 1; x++) {
        const idx = (y * width + x) * 4;
        if (outputBuffer[idx + 3] !== 0) {
            const n1 = (y * width + (x - 1)) * 4 + 3;
            const n2 = (y * width + (x + 1)) * 4 + 3;
            const n3 = ((y - 1) * width + x) * 4 + 3;
            const n4 = ((y + 1) * width + x) * 4 + 3;
            const transparentNeighbors = (outputBuffer[n1] === 0 ? 1 : 0) +
                                         (outputBuffer[n2] === 0 ? 1 : 0) +
                                         (outputBuffer[n3] === 0 ? 1 : 0) +
                                         (outputBuffer[n4] === 0 ? 1 : 0);
            if (transparentNeighbors > 0) {
                const r = outputBuffer[idx], g = outputBuffer[idx + 1], b = outputBuffer[idx + 2];
                const maxVal = Math.max(r, g, b);
                const minVal = Math.min(r, g, b);
                const sat = maxVal - minVal;
                if (sat < 30) {
                    outputBuffer[idx + 3] = Math.max(0, Math.round(255 * (sat / 30)));
                }
            }
        }
    }
}

const rawOut = '/tmp/king_clean.raw';
fs.writeFileSync(rawOut, outputBuffer);
execSync(`convert -size 1024x1024 -depth 8 rgba:${rawOut} app/src/main/res/drawable/img_piece_white_king.png`);

console.log('Saved clean white king PNG successfully!');
