public class Contact {
    private String username;
    private String label;

    public Contact(String username, String label) {
        this.username = username;
        this.label = label;
    }

    public String getUsername() {
        return username;
    }

    public String getLabel() {
        return label;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
