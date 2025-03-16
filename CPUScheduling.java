import java.util.*;
import java.util.InputMismatchException;
class Process {
    String id;
    int arrivalTime, burstTime, remainingTime, completionTime, waitingTime, turnaroundTime;

    Process(String id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

public class CPUScheduling {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

            while (true) {
                System.out.println("\nCPU Scheduling Algorithms");
                System.out.println("===========================");
                System.out.println("1. First-Come, First-Served (FCFS)");
                System.out.println("2. Shortest-Job-First (SJF)");
                System.out.println("3. Shortest-Remaining-Time (SRT)");
                System.out.println("4. Round Robin (RR)");
                System.out.println("5. Exit\n");
                int choice = 0;
                while(true){
                    try {
                        System.out.print("Select an option: ");
                        choice = scanner.nextInt();
                        scanner.nextLine();
                        if(choice < 1 || choice > 5) {
                            System.out.println("Wrong input!!!");
                            continue;
                        }        
                    } catch (Exception e) {
                        System.out.println("Please enter the Integer.");
                        scanner.nextLine();
                        continue;
                    }
                    break;
                }


                if (choice == 5) {
                    System.out.println("Thank You :)");
                    break;
                }
    
                List<Process> processes = getProcesses();
                switch (choice) {
                    case 1: fcfs(processes); break;
                    case 2: sjf(processes); break;
                    case 3: srt(processes); break;
                    case 4: roundRobin(processes); break;
                    default: System.out.println("Invalid choice. Try again.\n");
                }
            }
        }
        
        private static List<Process> getProcesses() {
        int n = 0;
        int arrivalTime = 0;
        int burstTime = 0;

        while (true) {
            try {

                System.out.print("Enter number of processes: ");
                n = scanner.nextInt();
                scanner.nextLine();
                if (n <= 0) {
                    System.out.println("Number of process must be more than 0.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Please enter integer numbers.");
                scanner.nextLine();
                continue;
            }
            break;
        }
        List<Process> processes = new ArrayList<>();
        
        for (int i = 1; i <= n; i++) {

            System.out.print("Enter Process ID: ");
            String id = scanner.next();
            while (true) {                
                try{
                    System.out.print("Enter Arrival Time: ");
                    arrivalTime = scanner.nextInt();
                    scanner.nextLine();
                    if (arrivalTime < 0) {
                        System.out.println("Arrival time must be positive integer.");
                        continue;
                    }
    
                } catch (InputMismatchException e) {
                    System.out.println("Please enter integer numbers.");
                    scanner.nextLine();
                    continue;
                }
                break;
            }


            while (true) {
                try {
                    System.out.print("Enter Burst Time: ");
                    burstTime = scanner.nextInt();
                    scanner.nextLine();
                    if (burstTime <= 0) {
                        System.out.println("Burst Time must be more than 0.");
                        continue;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Please enter the integer numbers.");
                    scanner.nextLine();
                    continue;
                }
                break;
            }

            processes.add(new Process(id, arrivalTime, burstTime));
        }
        return processes;
    }
    private static void fcfs(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        for (Process p : processes) {
            currentTime = Math.max(currentTime, p.arrivalTime);
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            currentTime += p.burstTime;
        }
        printResults(processes);
    }

    private static void sjf(List<Process> processes) {
        List<Process> readyQueue = new ArrayList<>();
        List<Process> sortedProcesses = new ArrayList<>(processes);
        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        while (!sortedProcesses.isEmpty() || !readyQueue.isEmpty()) {
            while (!sortedProcesses.isEmpty() && sortedProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.add(sortedProcesses.remove(0));
            }
            if (!readyQueue.isEmpty()) {
                readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
                Process p = readyQueue.remove(0);
                currentTime = Math.max(currentTime, p.arrivalTime) + p.burstTime;
                p.completionTime = currentTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
            } else {
                currentTime++;
            }
        }
        printResults(processes);
    }

    private static void srt(List<Process> processes) {
        int time = 0;
        int completed = 0;
        List<Process> readyQueue = new ArrayList<>();
        while (completed < processes.size()) {
            for (Process p : processes) {
                if (p.arrivalTime == time) {
                    readyQueue.add(p);
                }
            }
            readyQueue.sort(Comparator.comparingInt(p -> p.remainingTime));
            if (!readyQueue.isEmpty()) {
                Process p = readyQueue.get(0);
                p.remainingTime--;
                if (p.remainingTime == 0) {
                    p.completionTime = time + 1;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - p.burstTime;
                    readyQueue.remove(p);
                    completed++;
                }
            }
            time++;
        }
        printResults(processes);
    }


    private static void roundRobin(List<Process> processes) {
        System.out.print("Enter Time Quantum: ");
        int quantum = scanner.nextInt();
        Queue<Process> queue = new LinkedList<>(processes);
        int time = 0;
        while (!queue.isEmpty()) {
            Process p = queue.poll();
            if (p.remainingTime > quantum) {
                time += quantum;
                p.remainingTime -= quantum;
                queue.add(p);
            } else {
                time += p.remainingTime;
                p.remainingTime = 0;
                p.completionTime = time;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
            }
        }
        printResults(processes);
    }

    private static void printResults(List<Process> processes) {
        /*
         * P: Process ID
         * AT: Arrival Time
         * BT: Burst Time
         * CT: Completion Time
         * WT: Waiting Time
         * TAT: Turn Around Time
         */
        int numberOfProcess = 0;
        System.out.println("\nP  AT  BT  CT  WT  TAT");
        System.out.println("===================================");   
        double totalWaiting = 0, totalTurnaround = 0;
        for (Process p : processes) {
            System.out.printf("%s\t%d\t%d\t%d\t%d\t%d\n", p.id, p.arrivalTime, p.burstTime, p.completionTime, p.waitingTime, p.turnaroundTime);
            totalWaiting += p.waitingTime;
            totalTurnaround += p.turnaroundTime;
            numberOfProcess ++;
        }
        System.out.println("___________________________________");
        System.out.print("Number of process(es): " + numberOfProcess);
        System.out.printf("\nAverage Waiting Time: %.2f", totalWaiting / processes.size());
        System.out.print(" ms");
        System.out.printf("\nAverage Turnaround Time: %.2f", totalTurnaround / processes.size());
        System.out.print(" ms");
        System.out.println("\n___________________________________");
    }
}
