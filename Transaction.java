public class Transaction {
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