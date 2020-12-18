import java.util.Comparator;

class LongComparator implements Comparator<long[]> {

    public int compare(long[] s1, long[] s2) {
        if (s1[0] < s2[0]) {
            return -1;
        } else if (s1[0] > s2[0]) {
            return 1;
        } else {
            if (s1[1] < s2[1]) {
                return -1;
            } else if (s1[1] > s2[1]) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}