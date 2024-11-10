import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Transaction {
    private String voterId;
    private String candidate;

    public Transaction(String voterId, String candidate) {
        this.voterId = voterId;
        this.candidate = candidate;
    }

    public String getVoterId() {
        return voterId;
    }

    public String getCandidate() {
        return candidate;
    }
}

class Block {
    private int index;
    private String previousHash;
    private String hash;
    private List<Transaction> transactions;

    public Block(int index, String previousHash) {
        this.index = index;
        this.previousHash = previousHash;
        this.transactions = new ArrayList<>();
        this.hash = calculateHash();
    }

    public int getIndex() {
        return index;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        this.hash = calculateHash(); // Recalculate hash when a new transaction is added
    }

    private String calculateHash() {
        StringBuilder data = new StringBuilder();
        data.append(index).append(previousHash);
        for (Transaction transaction : transactions) {
            data.append(transaction.getVoterId()).append(transaction.getCandidate());
        }
        return applySHA256(data.toString());
    }

    private String applySHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

class Blockchain {
    private List<Block> chain;

    public Blockchain() {
        chain = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        if (chain.isEmpty()) {
            createBlock("0"); // Create the first block if chain is empty
        }
        chain.get(chain.size() - 1).addTransaction(transaction);
    }

    public void createBlock(String previousHash) {
        Block newBlock = new Block(chain.size(), previousHash);
        chain.add(newBlock);
    }

    public List<Block> getChain() {
        return chain;
    }
}

public class VotingSystem {
    private Blockchain blockchain;
    private List<String> candidates;
    private Map<String, Integer> voteCount;
    private List<String> registeredVoters;

    public VotingSystem() {
        blockchain = new Blockchain();
        candidates = new ArrayList<>();
        voteCount = new HashMap<>();
        registeredVoters = new ArrayList<>();
    }

    public void registerCandidate(String candidate) {
        candidates.add(candidate);
        voteCount.put(candidate, 0);
        System.out.println("Candidate " + candidate + " registered successfully.");
    }

    public void registerVoter(String voterId) {
        if (registeredVoters.contains(voterId)) {
            System.out.println("Voter ID " + voterId + " is already registered.");
        } else {
            registeredVoters.add(voterId);
            System.out.println("Voter ID " + voterId + " registered successfully.");
        }
    }

    public void vote(String voterId, String candidate) {
        if (!registeredVoters.contains(voterId)) {
            System.out.println("Voter ID not registered. Please register first.");
            return;
        }
        if (!candidates.contains(candidate)) {
            System.out.println("Candidate not found. Please vote for a registered candidate.");
            return;
        }
        // Check if the voter has already voted
        for (Block block : blockchain.getChain()) {
            for (Transaction transaction : block.getTransactions()) {
                if (transaction.getVoterId().equals(voterId)) {
                    System.out.println("Voter ID " + voterId + " has already voted.");
                    return;
                }
            }
 }
        Transaction transaction = new Transaction(voterId, candidate);
        blockchain.addTransaction(transaction);
        blockchain.createBlock(blockchain.getChain().get(blockchain.getChain().size() - 1).getHash());
        voteCount.put(candidate, voteCount.get(candidate) + 1);
        System.out.println("Vote recorded for " + candidate + "!");
    }

    public void displayVotes() {
        if (blockchain.getChain().isEmpty()) {
            System.out.println("No votes recorded yet.");
            return;
        }

        for (Block block : blockchain.getChain()) {
            System.out.println("Block #" + block.getIndex() + " Hash: " + block.getHash());
            for (Transaction transaction : block.getTransactions()) {
                System.out.println("Voter: " + transaction.getVoterId() + " voted for " + transaction.getCandidate());
            }
        }
    }

    public void displayResults() {
        String winner = null;
        int maxVotes = -1;

        for (String candidate : candidates) {
            int votes = voteCount.get(candidate);
            System.out.println(candidate + " received " + votes + " votes.");
            if (votes > maxVotes) {
                maxVotes = votes;
                winner = candidate;
            }
        }

        if (winner != null) {
            System.out.println("The winner is " + winner + " with " + maxVotes + " votes!");
        } else {
            System.out.println("No votes were cast.");
        }
    }

    public void displayCandidates() {
        if (candidates.isEmpty()) {
            System.out.println("No candidates registered yet.");
        } else {
            System.out.println("Registered Candidates:");
            for (String candidate : candidates) {
                System.out.println("- " + candidate);
            }
        }
    }

    public void showMenu() {
        System.out.println("\n--- Voting System Menu ---");
        System.out.println("1. Register a Candidate");
        System.out.println("2. Register a Voter");
        System.out.println("3. Cast a Vote");
        System.out.println("4. Display Votes");
        System.out.println("5. Display Candidates");
        System.out.println("6. Exit and Show Results");
        System.out.print("Choose an option: ");
    }

    public static void main(String[] args) {
        VotingSystem votingSystem = new VotingSystem();
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            votingSystem.showMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter candidate name: ");
                    String candidate = scanner.nextLine();
                    votingSystem.registerCandidate(candidate);
                    break;
                case 2:
                    System.out.print("Enter voter ID: ");
                    String voterId = scanner.nextLine();
                    votingSystem.registerVoter(voterId);
                    break;
                case 3:
                    votingSystem.displayCandidates();
                    System.out.print("Enter voter ID: ");
                    voterId = scanner.nextLine();
                    System.out.print("Enter candidate name: ");
                    candidate = scanner.nextLine();
                    votingSystem.vote(voterId, candidate);
                    break;
                case 4:
                    votingSystem.displayVotes();
                    break;
                case 5:
                    votingSystem.displayCandidates();
                    break;
                case 6:
                    System.out.println("Exiting the Voting System. Here are the results:");
                    votingSystem.displayResults();
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}