package jmp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jlib.plugin.IPlugin;
import jmp.file.CommonRegisterINI;

public class InvokeArgs {

    public InvokeArgs() {
        creg = new ArrayList<CommonRegisterINI>();
    }

    public IPlugin stdPlugin = null;
    public boolean doReturn = false;
    public File loadFile = null;
    public List<CommonRegisterINI> creg;
}
