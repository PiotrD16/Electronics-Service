public class MongoResults {
    private int userId;
    private int repairId;
    private String description;
    private byte[] imageBytes;

    public MongoResults(int userId, int repairId, String description, byte[] imageBytes) {
        this.userId = userId;
        this.repairId = repairId;
        this.description = description;
        this.imageBytes = imageBytes;
    }

    // Gettery
    public int getUserId() {
        return userId;
    }

    public int getRepairId() {
        return repairId;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }
}