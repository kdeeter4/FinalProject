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