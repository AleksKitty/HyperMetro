package metro;

public class StationTransfer {
    String line;
    String station;

    public StationTransfer(String line, String station) {
        this.line = line;
        this.station = station;
    }

    @Override
    public String toString() {
        return "StationTransfer{" +
                "line='" + line + '\'' +
                ", station='" + station + '\'' +
                '}';
    }
}
