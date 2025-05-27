import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class ResultController {
    @FXML private Label scoreLabel;
    @FXML private Label percentageLabel;
    @FXML private Label messageLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button closeButton;

    @FXML
    public void initialize() {
        // Initialization if needed
    }

    public void setResults(int score, int total, List<Integer> incorrect) {
        double percentage = (double)score / total * 100;
        
        scoreLabel.setText(score + " / " + total);
        percentageLabel.setText(String.format("%.1f%%", percentage));
        progressBar.setProgress(percentage / 100);
        
        if (percentage >= 80) {
            messageLabel.setText("Excellent! You know your stuff!");
        } else if (percentage >= 60) {
            messageLabel.setText("Good job! You passed!");
        } else {
            messageLabel.setText("Keep practicing! You'll do better next time.");
        }
    }

    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}