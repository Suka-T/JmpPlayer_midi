package jmp.lang;

import java.util.ArrayList;
import java.util.List;

public class FontRsrc {
    private List<String> lst;

    public FontRsrc(String... name) {
        lst = new ArrayList<String>();
        for (String s : name) {
            lst.add(s);
        }
    }

    public String getName(int type) {
        if (0 <= type && type < lst.size()) {
            return lst.get(type);
        }
        else {
            return lst.get(0);
        }
    }
}
