import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private List<Block> chain;
    private List<Transaction> currentTransactions;

    public Blockchain() {
        chain = new ArrayList<>();
        currentTransactions = new ArrayList<>();
        // Create the genesis block
        createBlock("0");
    }

    public Block createBlock(String previousHash) {
        Block block = new Block(chain.size(), System.currentTimeMillis(), currentTransactions, previousHash);
        chain.add(block);
        currentTransactions = new ArrayList<>(); // Clear the transaction pool
        return block;
    }

    public void addTransaction(Transaction transaction) {
        currentTransactions.add(transaction);
    }

    public List<Block> getChain() {
        return chain;
    }
}