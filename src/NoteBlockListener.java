// Listener interface for handling noteblock interactions (like mouselistener)
// Classes that implement it (like game) define what happens when noteblock is hit
public interface NoteBlockListener {
    // Called when a noteblock is hit
    void onNoteBlockHit(NoteBlock block);
}