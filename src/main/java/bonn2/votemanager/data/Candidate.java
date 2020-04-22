package bonn2.votemanager.data;

public class Candidate {

    private String name;
    private Election election;

    public Candidate(String name, Election election) {
        this.name = name;
        this.election = election;
    }

    public Election getElection() {
        return election;
    }

    public String getName() {
        return name;
    }
}
