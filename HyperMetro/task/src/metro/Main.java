package metro;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static metro.BreadthFirstSearchAlgorithm.findRoute;
import static metro.DijkstrasAlgorithm.findFastestRoute;

public class Main {

    // our metro
    private static final HashMap<String, ArrayList<Station>> metro = new HashMap<>();

    public static void main(String[] args) throws IOException {

        String pathToFile = args[0];
        readFile(pathToFile);

        Scanner sc = new Scanner(System.in);
        String inputString = sc.nextLine().trim();
        String[] command = inputString.split(" ");
        while (!"/exit".equals(command[0])) {

            int firstIndex = inputString.indexOf("\"");
            String lineName;
            if (firstIndex == -1 || firstIndex - 1 != inputString.indexOf(" ")) {
                lineName = command[1];
            } else {
                lineName = inputString.substring(firstIndex + 1, inputString.indexOf("\"", firstIndex + 1));
            }

            if (metro.get(lineName) == null) {
                System.out.println("Invalid command");
            } else if ("/append".equals(command[0])) {
                String[] returnCommands = parseInput(command);
                int time = returnCommands[3] == null ? 0 : Integer.parseInt(returnCommands[3]);
                metro.get(lineName).add(new Station(lineName, returnCommands[2], new String[]{}, new String[]{}, new StationTransfer[]{}, time));
            } else if ("/remove".equals(command[0])) {
                removeStation(lineName, inputString.substring(inputString.indexOf(" ", 8) + 1));
            } else if ("/connect".equals(command[0])) {
                connectStations(inputString);
            } else if ("/route".equals(command[0])) {
                BreadthFirstSearchAlgorithm.metro = metro;
                findRoute(inputString);
            } else if ("/fastest-route".equals(command[0])) {
                DijkstrasAlgorithm.metro = metro;
                findFastestRoute(inputString);
            }

            inputString = sc.nextLine().trim();
            command = inputString.split(" ");
        }
    }

     static String[] parseInput(String[] commands) {
        int i = 0;
        String[] readyCommands = new String[4];
        StringBuilder tmp = new StringBuilder();
        for (String s : commands) {
            if (!s.contains("\"") && tmp.toString().isEmpty()) {
                readyCommands[i] = s;
                i++;
            } else if (s.startsWith("\"") && s.endsWith("\"")) {
                readyCommands[i] = s.replaceAll("\"", "");
                i++;
            } else if (!tmp.toString().isEmpty() && !s.endsWith("\"")) {
                tmp.append(" ").append(s);
            } else if (!tmp.toString().isEmpty() && s.endsWith("\"")) {
                tmp.append(" ").append(s);
                readyCommands[i] = tmp.toString().replaceAll("\"", "");
                i++;
                tmp = new StringBuilder();
            } else {
                tmp.append(s);
            }
        }
        return readyCommands; // line1 name1 line2 name2
    }

    private static void readFile(String pathToFile) throws IOException {
        Path filePath = Path.of(pathToFile);

        if (!filePath.toFile().exists()) {
            System.out.println("Error! Such a file doesn't exist!");
            return;
        }

        String fileString = Files.readString(filePath);
        Gson gson = new Gson();

        Type empMapType = new TypeToken<Map<String, ArrayList<Station>>>() {}.getType();

        Map<String, ArrayList<Station>> content = gson.fromJson(fileString, empMapType);

        // so every station will know it's line
        for (String key : content.keySet()) {
            for (Station s: content.get(key)) {
                s.line = key;
                s.totalTime = Integer.MAX_VALUE;
            }
        }

        metro.putAll(content);
    }

    private static void connectStations(String inputString) {
        String[] commands = inputString.replaceAll("/connect ", "").split(" ");

        String[] readyCommands = parseInput(commands);

        // connect one way
        connectOneWay(readyCommands[0], readyCommands[1], readyCommands[2], readyCommands[3]);
        // connect another way
        connectOneWay(readyCommands[2], readyCommands[3], readyCommands[0], readyCommands[1]);
    }

    private static void connectOneWay(String line1, String name1, String line2, String name2) {
        ArrayList<Station> line1List = metro.get(line1);

        Station current = getStationInLine(line1List, name1);

        if (current != null) {
            StationTransfer[] arr = current.transfer;
            StationTransfer[] newArr = new StationTransfer[arr.length + 1];
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            newArr[arr.length] = new StationTransfer(line2, name2);
            current.transfer = newArr;
        }
    }

     static Station getStationInLine(ArrayList<Station> lineList, String name) {

        for (Station s: lineList) {
            if (s.name.equals(name)) {
                return s;
            }
        }
        return null;
    }

    static boolean checkIfEquals(String line, String stationName, Station station) {
        return line.equals(station.line) && stationName.equals(station.name);
    }

    private static void removeStation(String lineName, String stationName) {
        ArrayList<Station> line = metro.get(lineName);

        for (Station s: line) {
            if (s.name.equals(stationName)) {
                line.remove(s);
                break;
            }
        }
    }
}
