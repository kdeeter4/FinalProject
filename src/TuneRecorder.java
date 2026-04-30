import java.util.ArrayList;
import java.util.List;

public class TuneRecorder {
    private List<Tune.NoteEvent> recorded = new ArrayList<>();
    private long lastHitTime = -1;

    public void recordHit(Note note, int durationMs) {
        int gapMs = 0;
        if (lastHitTime >= 0) {
            gapMs = (int)(System.currentTimeMillis() - lastHitTime);
        }
        recorded.add(new Tune.NoteEvent(note, durationMs, gapMs));
        lastHitTime = System.currentTimeMillis();
    }

    public Tune buildTune() {
        return new Tune(recorded.toArray(new Tune.NoteEvent[0]));
    }

    public void reset() {
        recorded.clear();
        lastHitTime = -1;
    }
}