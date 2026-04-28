import javax.sound.sampled.*;

public class Note {

    // Base frequencies for octave 4, C is lowest
    public final double[] FREQUENCIES = {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88};
    String notes = "CDEFGAB";

    String name; // e.g. "A5"
    char letter; // e.g. 'A'
    int octave;  // e.g. 5
    double freq;

    public Note(String name) {
        if (name.length() != 2) {
            throw new IllegalArgumentException("Note must be in format like 'A5' or 'C4'");
        }

        this.name   = name;
        this.letter = name.charAt(0);
        this.octave = Character.getNumericValue(name.charAt(1));

        double baseFreq = FREQUENCIES[notes.indexOf(letter)];
        // For each octave you double the frequency so this doubles the frequency the necessary amount of times
        this.freq = baseFreq * Math.pow(2, octave - 4);
    }

    // Gets the name of the note
    public String getName() {
        return name;
    }

    // Plays the note
    public void playNote(int durationMs) {
        // Creates seperate thread to prevent the game from freezing
        Thread audioThread = new Thread(() -> {
            try {
                // Splits the wanted sound up into samples where each sample plays for a certain number of seconds
                float sampleRate = 44100f;
                int numSamples = (int) (sampleRate * durationMs / 1000);
                byte[] buffer = new byte[numSamples * 2];

                // Creates a sin wave by performing a sin() computation based on where the sample shows up
                for (int i = 0; i < numSamples; i++) {
                    double angle = 2.0 * Math.PI * i * freq / sampleRate;
                    short sample = (short) (Short.MAX_VALUE * Math.sin(angle));
                    buffer[i * 2]     = (byte) (sample & 0xFF);
                    buffer[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
                }

                // Creates an audioforamt and dataline object to play the sound to the user
                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

                // Plays the sound through the open line
                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();

            } catch (LineUnavailableException e) {
                System.err.println("Audio playback failed: " + e.getMessage());
            }
        });

        // Required functions to make the main thread function normally
        audioThread.setDaemon(true);
        audioThread.start();
    }
}