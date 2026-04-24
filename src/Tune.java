import javax.sound.sampled.*;

public class Tune {

    // Bundles a note with its duration and the gap after it
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

    private NoteEvent[] events;

    public Tune(NoteEvent[] events) {
        this.events = events;
    }

    public NoteEvent[] getEvents() {
        return events;
    }

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

    public int score(Tune other) {
        NoteEvent[] a = this.events;
        NoteEvent[] b = other.getEvents();

        // Checks if there are no notes played
        if (a.length == 0 && b.length == 0) return 100;
        if (a.length == 0 || b.length == 0) return 0;

        int minLen = Math.min(a.length, b.length);
        int maxLen = Math.max(a.length, b.length);

        // --- 1. Note pitch score (50% weight) ---
        // Notes are named "C4", "D5", etc. We convert each to a semitone index
        // so that nearby notes score better than distant ones.
        double pitchTotal = 0;
        for (int i = 0; i < minLen; i++) {
            int semiA = toSemitone(a[i].note);
            int semiB = toSemitone(b[i].note);
            int diff = Math.abs(semiA - semiB);
            // Max meaningful diff: 1 octave (12 semitones) = score 0; same = score 1
            double noteSimilarity = Math.max(0, 1.0 - diff / 12.0);
            pitchTotal += noteSimilarity;
        }
        double pitchScore = (pitchTotal / maxLen) * 50;  // penalise length mismatches too

        // --- 2. Duration score (30% weight) ---
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

        // --- 3. Gap score (20% weight) ---
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
    private int toSemitone(Note note) {
        int[] semitones = {0, 2, 4, 5, 7, 9, 11}; // C D E F G A B
        String noteOrder = "CDEFGAB";
        int letterIndex = noteOrder.indexOf(note.letter);
        return note.octave * 12 + semitones[letterIndex];
    }
}


/*
Example tunes:

int q = 400;  // quarter note (ms)
int h = 800;  // half note
int g = 50;   // gap between notes

Tune twinkle = new Tune(new Tune.NoteEvent[] {
        // "Twin-kle twin-kle"
        new Tune.NoteEvent(new Note("C4"), q, g),
        new Tune.NoteEvent(new Note("C4"), q, g),
        new Tune.NoteEvent(new Note("G4"), q, g),
        new Tune.NoteEvent(new Note("G4"), q, g),

        // "lit-tle star"
        new Tune.NoteEvent(new Note("A4"), q, g),
        new Tune.NoteEvent(new Note("A4"), q, g),
        new Tune.NoteEvent(new Note("G4"), h, g),

        // "How I won-der"
        new Tune.NoteEvent(new Note("F4"), q, g),
        new Tune.NoteEvent(new Note("F4"), q, g),
        new Tune.NoteEvent(new Note("E4"), q, g),
        new Tune.NoteEvent(new Note("E4"), q, g),

        // "what you are"
        new Tune.NoteEvent(new Note("D4"), q, g),
        new Tune.NoteEvent(new Note("D4"), q, g),
        new Tune.NoteEvent(new Note("C4"), h, g),

        // "Up a-bove the"
        new Tune.NoteEvent(new Note("G4"), q, g),
        new Tune.NoteEvent(new Note("G4"), q, g),
        new Tune.NoteEvent(new Note("F4"), q, g),
        new Tune.NoteEvent(new Note("F4"), q, g),

        // "world so high"
        new Tune.NoteEvent(new Note("E4"), q, g),
        new Tune.NoteEvent(new Note("E4"), q, g),
        new Tune.NoteEvent(new Note("D4"), h, g),

        // "Like a dia-mond"
        new Tune.NoteEvent(new Note("G4"), q, g),
        new Tune.NoteEvent(new Note("G4"), q, g),
        new Tune.NoteEvent(new Note("F4"), q, g),
        new Tune.NoteEvent(new Note("F4"), q, g),

        // "in the sky"
        new Tune.NoteEvent(new Note("E4"), q, g),
        new Tune.NoteEvent(new Note("E4"), q, g),
        new Tune.NoteEvent(new Note("D4"), h, g),

        // "Twin-kle twin-kle"
        new Tune.NoteEvent(new Note("C4"), q, g),
        new Tune.NoteEvent(new Note("C4"), q, g),
        new Tune.NoteEvent(new Note("G4"), q, g),
        new Tune.NoteEvent(new Note("G4"), q, g),

        // "lit-tle star"
        new Tune.NoteEvent(new Note("A4"), q, g),
        new Tune.NoteEvent(new Note("A4"), q, g),
        new Tune.NoteEvent(new Note("G4"), h, g),

        // "How I won-der"
        new Tune.NoteEvent(new Note("F4"), q, g),
        new Tune.NoteEvent(new Note("F4"), q, g),
        new Tune.NoteEvent(new Note("E4"), q, g),
        new Tune.NoteEvent(new Note("E4"), q, g),

        // "what you are"
        new Tune.NoteEvent(new Note("D4"), q, g),
        new Tune.NoteEvent(new Note("D4"), q, g),
        new Tune.NoteEvent(new Note("C4"), h, g),
});

twinkle.playTune();
 */