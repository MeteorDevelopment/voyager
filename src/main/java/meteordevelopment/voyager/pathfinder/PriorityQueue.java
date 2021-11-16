package meteordevelopment.voyager.pathfinder;

import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;

import java.util.Comparator;

public class PriorityQueue<T> extends ObjectHeapPriorityQueue<T> {
    public PriorityQueue(Comparator<? super T> c) {
        super(c);
    }

    public boolean contains(T v) {
        for (int i = 0; i < size; i++) {
            if (heap[i] == v) return true;
        }

        return false;
    }
}
