import java.util.ArrayList;
import java.util.List;
// Records a sequence of notes and timing information and makes a tune object from it
public class TuneRecorder {
    //Stores recorded note events in order
    private List<Tune.NoteEvent> recorded = new ArrayList<>();
    // Timestamp to determning timing spacing between notes
    private long lastHitTime = -1;

    // Records a note being played
    public void recordHit(Note note, int durationMs) {
        int gapMs = 0;
        // Calculate gap from last note hit
        if (lastHitTime >= 0) {
            gapMs = (int)(System.currentTimeMillis() - lastHitTime);
        }
        // Store note information
        recorded.add(new Tune.NoteEvent(note, durationMs, gapMs));
        // update timing system
        lastHitTime = System.currentTimeMillis();
    }
    // Builds a tune out of list of notes
    public Tune buildTune() {
        return new Tune(recorded.toArray(new Tune.NoteEvent[0]));
    }
    // clears all recorded data and timing.
    public void reset() {
        recorded.clear();
        lastHitTime = -1;
    }
}