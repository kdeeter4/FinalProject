import javax.sound.sampled.*;

public class Tune {

    // Bundles a note with its duration and the gap after it. Static so as to make a NoteEvent independent of Tune
    public static class NoteEvent {
        public final Note note;
        public final int durationMs;  // how long the note plays
        public final int gapMs;       // silence after the note before the next one

        public NoteEvent(Note note, int durationMs, int gapMs) {
            this.note = note;
            this.durationMs = durationMs;
            this.gapMs = gapMs;
        }

        // Convenience constructor if you don't need a gap
        public NoteEvent(Note note, int durationMs) {
            this(note, durationMs, 0);
        }
    }

    // Instance Variables
    private NoteEvent[] events;

    // Constructor
    public Tune(NoteEvent[] events) {
        this.events = events;
    }

    // Gets the events
    public NoteEvent[] getEvents() {
        return events;
    }

    // Plays the tune
    public void playTune() {
        // Run on a background thread so the game doesn't freeze
        Thread tuneThread = new Thread(() -> {
            for (NoteEvent event : events) {
                event.note.playNote(event.durationMs);

                // Wait for the note + gap to finish before playing the next one
                try {
                    Thread.sleep(event.durationMs + event.gapMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return; // stop playing if interrupted
                }
            }
        });

        tuneThread.setDaemon(true);
        tuneThread.start();
    }

    // Scores another tune based on how close the tunes are in note events
    public int score(Tune other) {
        NoteEvent[] a = this.events;
        NoteEvent[] b = other.getEvents();

        // Checks if there are no notes played
        if (a.length == 0 && b.length == 0) return 100;
        if (a.length == 0 || b.length == 0) return 0;

        int minLen = Math.min(a.length, b.length);
        int maxLen = Math.max(a.length, b.length);

        // We convert each note to a semitone index so that nearby notes score better than distant ones.
        double pitchTotal = 0;
        for (int i = 0; i < minLen; i++) {
            int semiA = Tune.toSemitone(a[i].note);
            int semiB = Tune.toSemitone(b[i].note);
            int diff = Math.abs(semiA - semiB);
            // Max meaningful diff: 1 octave (12 semitones) = score 0; same = score 1
            double noteSimilarity = Math.max(0, 1.0 - diff / 12.0);
            pitchTotal += noteSimilarity;
        }
        double pitchScore = (pitchTotal / maxLen) * 50;  // penalise length mismatches too

        // Compare how close each note's duration is, as a ratio.
        double durTotal = 0;
        for (int i = 0; i < minLen; i++) {
            int dA = a[i].durationMs;
            int dB = b[i].durationMs;
            double ratio = dA >= dB
                    ? (dB == 0 ? 0 : (double) dB / dA)
                    : (dA == 0 ? 0 : (double) dA / dB);
            durTotal += ratio;  // 1.0 = identical, approaches 0 as they diverge
        }
        double durScore = (durTotal / maxLen) * 30;

        // Score based on how longs gaps are
        double gapTotal = 0;
        for (int i = 0; i < minLen; i++) {
            int gA = a[i].gapMs;
            int gB = b[i].gapMs;
            if (gA == 0 && gB == 0) {
                gapTotal += 1.0;  // both have no gap: perfect
            } else {
                int bigger  = Math.max(gA, gB);
                int smaller = Math.min(gA, gB);
                double ratio = bigger == 0 ? 0 : (double) smaller / bigger;
                gapTotal += ratio;
            }
        }
        double gapScore = (gapTotal / maxLen) * 20;

        return Math.min(100, (int) Math.round(pitchScore + durScore + gapScore));
    }

    // Converts a Note to an absolute semitone number for easy distance comparison.
    // C4 = 0, D4 = 2, ..., B4 = 11, C5 = 12, etc.
    public static int toSemitone(Note note) {
        int[] semitones = {0, 2, 4, 5, 7, 9, 11}; // C D E F G A B
        String noteOrder = "CDEFGAB";
        int letterIndex = noteOrder.indexOf(note.letter);
        return (note.octave - 4) * 12 + semitones[letterIndex];
    }
}