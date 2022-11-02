package metro;

import java.util.ArrayList;
import java.util.HashMap;

import static metro.Main.*;

public class BreadthFirstSearchAlgorithm {

    static HashMap<String, ArrayList<Station>> metro;

    static void findRoute(String inputString) {
        String[] commands = inputString.replaceAll("/route ", "").split(" ");
        String[] readyCommands = parseInput(commands);

        ArrayList<Station> doublyLinkedList = metro.get(readyCommands[0]);

        Station startStation = getStationInLine(doublyLinkedList, readyCommands[1]);
        Station finalStation = getStationInLine(metro.get(readyCommands[2]), readyCommands[3]);

        if (startStation == null || finalStation == null) {
            return;
        }

        ArrayList<ArrayList<Station>> brFirstSearch = new ArrayList<>();

        boolean isFound = false;

        ArrayList<Station> currentList = new ArrayList<>();
        currentList.add(startStation);
        startStation.isVisited = true;
        brFirstSearch.add(currentList);

        ArrayList<Station> nextList;
        while (!isFound) {

            nextList = new ArrayList<>();

            for (Station current : currentList) {

                isFound = checkNextStations(current, nextList, isFound, readyCommands[2], readyCommands[3]);

                if (!isFound) {

                    for (StationTransfer s : current.transfer) {
                        Station station = getStationInLine(metro.get(s.line), s.station);

                        if (station != null && !station.isVisited) {

                            isFound = checkNextStations(station, nextList, false, readyCommands[2], readyCommands[3]);

                            if (isFound) {
                                break;
                            }
                        }
                    }
                }
            }

            brFirstSearch.add(nextList);
            currentList = new ArrayList<>(nextList);
        }

        // print what we found
        printRoute(brFirstSearch, startStation, finalStation);
    }

    static boolean checkNextStations(Station current, ArrayList<Station> nextList,
                                     boolean isFound, String line, String name) {

        //changed to arrays
        for (String s : current.next) {
            Station station = getStationInLine(metro.get(current.line), s);
            isFound = doCheck(station, nextList, isFound, line, name);
        }


        for (String s : current.prev) {
            Station station = getStationInLine(metro.get(current.line), s);
            isFound = doCheck(station, nextList, isFound, line, name);
        }

        return isFound;
    }

    private static boolean doCheck(Station station, ArrayList<Station> nextList,
                                   boolean isFound, String line, String name) {

        if (!isFound && station != null && !station.isVisited) {
            nextList.add(station);
            station.isVisited = true;
            isFound = checkIfEquals(line, name, station);
        }

        return isFound;
    }

    private static void printRoute(ArrayList<ArrayList<Station>> brFirstSearch, Station startStation, Station finalStation) {
        finalStation.isVisited = false;
        startStation.isVisited = false;

        Station current = new Station(finalStation);

        ArrayList<String> output = new ArrayList<>();
        output.add(finalStation.name);

        Station tmp = new Station(current);
        for (int i = brFirstSearch.size() - 1; i > 0; i--) {

            current = null;
            for (Station station : brFirstSearch.get(i - 1)) {
                station.isVisited = false;

                if (current == null) {
                    current = checkForOutput(new Station(tmp), station, output, true);
                }

                if (current == null) {
                    current = checkForOutput(new Station(tmp), station, output, false);
                }

                // transactions for current
                if (current == null) {
                    for (StationTransfer stationTransfer : new Station(tmp).transfer) {

                        Station s = getStationInLine(metro.get(tmp.line), stationTransfer.station);

                        if (s != null) {
                            current = nextTransactions(s, station, output);

                            if (current != null) {
                                break;
                            }
                        }
                    }
                }

                // transactions for next
                if (current == null) {
                    for (String name : new Station(tmp).next) {

                        Station s = getStationInLine(metro.get(tmp.line), name);

                        if (s != null) {
                            current = nextTransactions(s, station, output);

                            if (current != null) {
                                break;
                            }
                        }
                    }
                }

                // transactions for prev
                if (current == null) {
                    for (String name : new Station(tmp).prev) {

                        Station s = getStationInLine(metro.get(tmp.line), name);

                        if (s != null) {
                            current = nextTransactions(s, station, output);

                            if (current != null) {
                                break;
                            }
                        }
                    }
                }

                if (current != null) {
                    tmp = new Station(current);
                }
            }
        }

        for (int i = output.size() - 1; i >= 0; i--) {
            System.out.println(output.get(i));
        }
    }

    // check next and prev arrays
    private static Station checkForOutput(Station current, Station station, ArrayList<String> output,
                                          boolean isNext) {
        String[] names;
        if (isNext) {
            names = current.next;
        } else {
            names = current.prev;
        }

        boolean isFound = false;
        for (String name : names) {
            Station s = getStationInLine(metro.get(current.line), name);

            if (!isFound && s != null && checkIfEquals(current.line, name, station)) {
                output.add(name);
                current = s;
                isFound = true;
            }
        }

        return isFound ? current : null;
    }

    private static Station nextTransactions(Station current, Station station, ArrayList<String> output) {

        boolean isFound = false;
        for (StationTransfer t : current.transfer) {
            Station tmp = getStationInLine(metro.get(t.line), t.station); // to set false

            if (!isFound && tmp != null && checkIfEquals(tmp.line, tmp.name, station)) {
                output.add(tmp.name);
                output.add("Transition to line " + current.line);
                output.add(current.name);
                current = tmp;
                isFound = true;
            }
        }
        return isFound ? current : null;
    }
}
