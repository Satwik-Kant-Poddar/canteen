# QR Studio - User Guide

Welcome to **QR Studio**, a premium, self-hosted web application that allows you to design and generate high-quality custom QR codes. QR Studio generates standard-compliant codes that are instantly readable by any standard iOS, Android, or industrial QR scanner.

---

## Key Features

- **Custom Text & URLs**: Encode any string, web address, Wi-Fi parameters, contact info, or email text (up to 2000 characters).
- **Custom Color Swatches**: Pick custom hexadecimal colors for the QR code pixels (foreground) and the card background.
- **Predefined Design Themes**: Quickly toggle between classic black-and-white, Ocean Blue, Vibrant Pink, Emerald, and Dark Velvet presets.
- **Precision (Error Correction Level)**: Choose the recovery rating of the code (7%, 15%, 25%, or 30%). Higher levels allow the QR code to remain readable even if it is partially dirty, scuffed, or damaged.
- **Resolution Control**: Generate codes in sizes ranging from 200x200px to 600x600px.
- **Format Flexibility**: Output files in lossless PNG or compressed JPEG formats.
- **Interactive Live Preview**: Instant rendering and feedback display.
- **Instant Actions**:
  - **Download Image**: Save the generated image directly to your local computer.
  - **Copy Link**: Copies the exact API endpoint URL to your clipboard for hot-linking.
- **Session History Tracker**: Retains your last 10 generated QR codes in the sidebar so you can reload, compare, or re-download them in a single click.

---

## How to Run QR Studio

### Prerequisites
- **Java**: Java Development Kit (JDK) 17 or higher.
- **Maven**: Apache Maven installed on your local path.

### Steps to Run

1. **Start the Application**
   In your terminal, navigate to the project directory and run the Maven Spring Boot plugin command:
   ```bash
   mvn spring-boot:run
   ```

2. **Access the Web Interface**
   Once the console shows that the application has started, open your web browser and navigate to:
   [http://localhost:8080](http://localhost:8080)

3. **Stop the Application**
   Press `Ctrl + C` in the running terminal window to stop the server.

---

## Customizing Your QR Code

### 1. Input Data
Write or paste your URL (e.g. `https://google.com`) or text block into the textarea. The live character count in the corner monitors your entry length.

### 2. Sizing Recommendations
- **200x200 px**: Best for simple short URLs, text messages, or business cards.
- **300x300 px**: The standard option for general digital screens, newsletters, and emails.
- **400x400 px / 600x600 px**: Recommended for print material (flyers, banners) or dense information.

### 3. Precision (Error Correction Level)
- **L (Low - 7%)**: Best for clean digital screens with low-density modules.
- **M (Medium - 15%)**: Recommended default. Good balance of resolution density and safety.
- **Q (Quarter - 25%)**: Higher safety margin. Good for outdoor screens or high-traffic layouts.
- **H (High - 30%)**: Maximum safety margin. Good for physical prints that might get wet, scratched, or bent.

### 4. Selecting Custom Colors
- Always ensure there is **adequate contrast** between your foreground and background colors.
- If the contrast is too low (e.g. yellow pixels on a white background), camera scanners might struggle to isolate the markers.
- Classic dark pixels on a light background scan fastest.
