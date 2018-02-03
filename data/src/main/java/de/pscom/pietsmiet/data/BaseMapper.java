package de.pscom.pietsmiet.data;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMapper<T1, T2> {
    public abstract T1 transform(T2 o2);

    public List<T1> transform(List<T2> o2List) {
        List<T1> o1List = null;
        if (o2List != null) {
            o1List = new ArrayList<>();
            T1 o1;
            for (T2 o2 : o2List) {
                o1 = transform(o2);
                o1List.add(o1);
            }
        }
        return o1List;
    }

}
