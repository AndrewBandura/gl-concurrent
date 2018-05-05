package com.andy.concurrent.array;

import java.util.*;

public class ThreadSafeArrayList<E> extends ArrayList<E> {

    public static void main(String args[]) {

        List<String> synchronizedarraylist =
                Collections.synchronizedList(new ArrayList<>());

        synchronizedarraylist.add("First");
        synchronizedarraylist.add("Second");
        synchronizedarraylist.add("Third");

        synchronized(synchronizedarraylist) {
            Iterator<String> iterator = synchronizedarraylist.iterator();
            while (iterator.hasNext())
                System.out.println(iterator.next());
        }
    }

}
