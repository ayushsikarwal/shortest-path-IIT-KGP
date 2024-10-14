import java.io.*;
import java.util.*;

public class ShortestPath {
    public static void openFile(Scanner inFile, String fileName) throws FileNotFoundException {
        inFile = new Scanner(new File(fileName));
    }

    public static void processFile(Scanner inFileGraph, Scanner inFileNodes, double[][] graph, List<String> nodesName, Map<String, Integer> mpp, String pair, String lineGraph, String lineNodes, int i) {
        nodesName.add("");
        while (inFileGraph.hasNextLine() && inFileNodes.hasNextLine()) {
            lineGraph = inFileGraph.nextLine();
            lineNodes = inFileNodes.nextLine();
            nodesName.add(lineNodes);
            mpp.put(lineNodes, i);
            Scanner iss = new Scanner(lineGraph);
            while (iss.hasNext()) {
                pair = iss.next();
                String[] parts = pair.split(",");
                int numV = Integer.parseInt(parts[0]);
                double numWt = Double.parseDouble(parts[1]);
                graph[i][numV] = numWt;
            }
            i++;
            iss.close();
        }
    }

    public static void makeDistanceParentMatrix(int n, double[][] graph, double[][] dist, int[][] parent) {
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n; j++) {
                if (graph[i][j] == -1) {
                    dist[i][j] = 1e9;
                } else if (graph[i][j] != 0) {
                    parent[i][j] = i;
                    dist[i][j] = graph[i][j];
                }
            }
        }
    }

    public static void updateDistanceParentMatrix(int n, double[][] dist, int[][] parent) {
        for (int via = 1; via < n; via++) {
            for (int i = 1; i < n; i++) {
                for (int j = 1; j < n; j++) {
                    if (dist[i][via] + dist[via][j] < dist[i][j]) {
                        dist[i][j] = dist[i][via] + dist[via][j];
                        parent[i][j] = via;
                    }
                }
            }
        }
    }

    public static String makeChoice(Scanner scanner) {
        System.out.println("1. Search Path");
        System.out.println("2. Exit");
        System.out.print("Enter your choice(1 or 2) : ");
        String choice = scanner.nextLine();
        if (choice.isEmpty() || choice.length() > 1) {
            System.out.println("Invalid input! Choose again");
            return makeChoice(scanner);
        }
        char ch = choice.charAt(0);
        if (Character.isDigit(ch)) {
            if (ch == '2' || ch == '1') return choice;
            else {
                System.out.println("Invalid input! Choose again");
                return makeChoice(scanner);
            }
        } else {
            System.out.println("Invalid input! Choose again");
            return makeChoice(scanner);
        }
    }

    public static void chooseSrcDest(double[][] dist, int[][] parent, Map<String, Integer> mpp, String[] nodes) {
        Scanner scanner = new Scanner(System.in);
        for (String node : mpp.keySet()) {
            System.out.println(node);
        }
        System.out.println();
        while (true) {
            System.out.print("Enter source: ");
            nodes[0] = scanner.nextLine().toUpperCase();
            System.out.print("Enter destination: ");
            nodes[1] = scanner.nextLine().toUpperCase();
            if (!mpp.containsKey(nodes[0]) || !mpp.containsKey(nodes[1])) {
                System.out.println("Invalid! Please choose from the List\n");
            } else if (nodes[0].equals(nodes[1])) {
                System.out.println("Source and destination cannot be the same. Please choose again \n");
            } else {
                break;
            }
        }
    }

    public static double findPath(String sourceNode, String destinationNode, double[][] dist, int[][] parent, Map<String, Integer> mpp, List<String> nodesName, List<String> path) {
        int srcNodeNum = mpp.get(sourceNode);
        int destNodeNum = mpp.get(destinationNode);
        Stack<Integer> st = new Stack<>();
        int node = destNodeNum;
        st.push(node);
        while (parent[srcNodeNum][node] != srcNodeNum) {
            node = parent[srcNodeNum][node];
            st.push(node);
        }
        st.push(srcNodeNum);
        while (!st.empty()) {
            int index = st.pop();
            path.add(nodesName.get(index));
        }
        return dist[srcNodeNum][destNodeNum];
    }

    public static void main(String[] args) throws FileNotFoundException {
        int n = 31;
        double[][] graph = new double[n][n];
        double[][] dist = new double[n][n];
        int[][] parent = new int[n][n];
        List<String> nodesName = new ArrayList<>();
        String[] nodes = new String[2];
        List<String> path = new ArrayList<>();
        Map<String, Integer> mpp = new HashMap<>();
        String pair = "", lineGraph = "", lineNodes = "";
        int i = 1;

        for (double[] row : graph) {
            Arrays.fill(row, -1);
        }

        Scanner inFileGraph = new Scanner(new File("file_graph.txt"));
        Scanner inFileNodes = new Scanner(new File("file_nodes_name.txt"));

        processFile(inFileGraph, inFileNodes, graph, nodesName, mpp, pair, lineGraph, lineNodes, i);
        makeDistanceParentMatrix(n, graph, dist, parent);
        updateDistanceParentMatrix(n, dist, parent);

        Scanner scanner = new Scanner(System.in);
        String choice = makeChoice(scanner);
        if (choice.equals("2")) {
            System.out.println("THANK YOU!");
            System.exit(0);
        }

        chooseSrcDest(dist, parent, mpp, nodes);
        String sourceNode = nodes[0];
        String destinationNode = nodes[1];

        double minDist = findPath(sourceNode, destinationNode, dist, parent, mpp, nodesName, path);

        System.out.println("\nMinimum distance from " + sourceNode + " to " + destinationNode + " is " + minDist + " km \n");
        System.out.println("Best Path is: ");
        for (int j = 0; j < path.size(); j++) {
            if (j != path.size() - 1) System.out.print(path.get(j) + " -> ");
            else System.out.println(path.get(j) + "\n");
        }

        scanner.close();
        inFileGraph.close();
        inFileNodes.close();
    }
}