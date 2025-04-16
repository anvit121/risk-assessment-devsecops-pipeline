public class VulnerableJava {
    public static void main(String[] args) {
        String password = "bad-password"; // Hardcoded secret
        String query = "SELECT * FROM users WHERE username = '" + args[0] + "'"; // SQL Injection
        System.out.println("Unsecured query: " + query);
    }

    public void unusedMethod() {
        // sample dead code for Sonar
    }
}
