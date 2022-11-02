package metro;

import java.util.Arrays;

public class Station {
    String line;
    String name;
    String[] prev;
    String[] next;
    boolean isVisited;
    StationTransfer[] transfer;
    int time;
    int totalTime;

    // add new Station
    public Station (String line, String name, String[] prev, String[] next, StationTransfer[] transfer, int time) {
        this.line = line;
        this.name = name;
        this.prev = prev;
        this.next = next;
        this.transfer = transfer;
        this.time = time;
        this.totalTime = Integer.MAX_VALUE;
    }

    // for copying
    public Station(Station another) {
        this.line = another.line;
        this.name = another.name;
        this.prev = another.prev;
        this.next = another.next;
        this.isVisited = another.isVisited;
        this.transfer = another.transfer;
        this.time = another.time;
        this.totalTime = another.totalTime;
    }

    @Override
    public String toString() {
        return "Station{" +
                "line='" + line + '\'' +
                ", name='" + name + '\'' +
                ", prev=" + Arrays.toString(prev) +
                ", next=" + Arrays.toString(next) +
                ", transfer=" + Arrays.toString(transfer) +
                ", time=" + time +
                ", totalTime=" + totalTime +
                '}';
    }
}
