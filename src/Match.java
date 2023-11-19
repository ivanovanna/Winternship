class Match {
    private String id;
    private double rateSideA;
    private double rateSideB;
    private String result;

    public Match(String id, double rateSideA, double rateSideB, String result) {
        this.id = id;
        this.rateSideA = rateSideA;
        this.rateSideB = rateSideB;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public double getRateSideA() {
        return rateSideA;
    }

    public double getRateSideB() {
        return rateSideB;
    }

    public String getResult() {
        return result;
    }
}