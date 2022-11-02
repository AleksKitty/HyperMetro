package metro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static metro.Main.*;
import static metro.Main.checkIfEquals;

public class DijkstrasAlgorithm {

    static HashMap<String, ArrayList<Station>> metro = new HashMap<>();

    static void findFastestRoute(String inputString) {
        String[] readyCommands = parseInput(inputString.replaceAll("/fastest-route ", "").split(" "));

        ArrayList<Station> stationsList = metro.get(readyCommands[0]);

        Station startStation = getStationInLine(stationsList, readyCommands[1]);
        Station finalStation = getStationInLine(metro.get(readyCommands[2]), readyCommands[3]);

        if (startStation == null || finalStation == null) {
            return;
        }
        startStation.totalTime = 0;

        HashMap<Station, Station> route = new HashMap<>(); // key is a station of the shortest route,
                                                                                      // value is a previous station to it of the shortest route

        Station current = new Station(startStation);
        boolean isFound;

        while (current != null) {

            // check neighbors
            isFound = checkNextStations(current, null, readyCommands[2], readyCommands[3], route);

            // check transitions
            if (!isFound) {

                for (StationTransfer s : current.transfer) {
                    Station station = getStationInLine(metro.get(s.line), s.station);

                    if (station != null && !station.isVisited) {

                        station.totalTime = current.totalTime + 5;
                        isFound = checkNextStations(station, current, readyCommands[2], readyCommands[3], route);

                        if (isFound) {
                            break;
                        }
                    }
                }
            }

            current.isVisited = true;

            current = findNextStation();
        }

        printFastestRoute(route, finalStation);
    }

    static boolean checkNextStations(Station current, Station fromStation, String line, String name,
                                                   HashMap<Station, Station> route) {

        boolean isFound = false;

        for (String s: current.next) {

            Station station = getStationInLine(metro.get(current.line), s);

            if (station != null && !station.isVisited) {
                if (station.totalTime == Integer.MAX_VALUE || station.totalTime > current.totalTime + current.time) {
                    station.totalTime = current.totalTime + current.time;

                    route.put(station, Objects.requireNonNullElse(fromStation, current));
                }

                isFound = checkIfEquals(line, name, station);
            }
        }

        for (String s: current.prev) {

            Station station = getStationInLine(metro.get(current.line), s);

            if (!isFound && station != null && !station.isVisited) {

                if (station.totalTime == Integer.MAX_VALUE || station.totalTime > current.totalTime + station.time) {
                    station.totalTime = current.totalTime + station.time;

                    route.put(station, Objects.requireNonNullElse(fromStation, current));
                }

                isFound = checkIfEquals(line, name, station);
            }
        }

        return isFound;
    }

    static Station findNextStation() {
        Station minStation = null;
        int totalTime = Integer.MAX_VALUE;

        for (Map.Entry<String, ArrayList<Station>> entry : metro.entrySet()) {

            for (Station current: entry.getValue()) {
                if (!current.isVisited && current.totalTime < totalTime) {
                    minStation = current;
                    totalTime = minStation.totalTime;
                }
            }
        }

        return minStation;
    }

    private static void printFastestRoute(HashMap<Station, Station> route, Station finalStation) {

        for (Station station : route.keySet()) {
            station.isVisited = false; // set all to false
        }

        ArrayList<String> output = new ArrayList<>();
        output.add("Total: " + finalStation.totalTime + " minutes in the way");

        Station current = finalStation;

        String line = finalStation.line;
        while(current != null) {

            if (!line.equals(current.line)) {
                output.add(findTransferredStation(current, line));
                output.add("Transition to line " + line);
            }

            output.add(current.name);

            line = current.line;
            current = route.get(current);
        }

        for (int i = output.size() - 1; i >= 0; i--) {
            System.out.println(output.get(i));
        }
    }

    private static String findTransferredStation(Station current, String line) {
        for (StationTransfer s : current.transfer) {
           if (line.equals(s.line)) {
               return s.station;
           }
        }
        return ""; // should be impossible
    }
}
