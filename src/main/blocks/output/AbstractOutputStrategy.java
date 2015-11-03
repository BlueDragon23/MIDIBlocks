package blocks.output;

import MIDIBlocks.Note;

public abstract class AbstractOutputStrategy {
    
    public abstract void setParams(String param);
    
    public abstract String getParams();
    
    public abstract String getParamNames();
    
    public abstract void output(Note note);
    
    public abstract String getName();
}
