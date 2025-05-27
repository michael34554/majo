import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizController {
    @FXML private Label questionLabel;
    @FXML private RadioButton option1, option2, option3, option4;
    @FXML private ToggleGroup optionsGroup;
    @FXML private Button nextButton;
    @FXML private Label questionNumberLabel;
    @FXML private ProgressIndicator progressIndicator;

    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<Integer> incorrectAnswers = new ArrayList<>();

    @FXML
    public void initialize() {
        optionsGroup = new ToggleGroup();
        option1.setToggleGroup(optionsGroup);
        option2.setToggleGroup(optionsGroup);
        option3.setToggleGroup(optionsGroup);
        option4.setToggleGroup(optionsGroup);
        
        loadQuestions();
        Collections.shuffle(questions);
        showQuestion();
    }

    private void loadQuestions() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("questions.txt"));
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String questionText = parts[0];
                    String[] options = parts[1].split(",");
                    int correctAnswer = Integer.parseInt(parts[2]);
                    questions.add(new Question(questionText, options, correctAnswer));
                }
            }
        } catch (IOException e) {
            // Default questions if file not found
            questions.add(new Question(
                "What is the capital of France?",
                new String[]{"London", "Paris", "Berlin", "Madrid"},
                1
            ));
            questions.add(new Question(
                "Which of these is not a Java keyword?",
                new String[]{"class", "interface", "method", "extends"},
                2
            ));
            questions.add(new Question(
                "What is the result of 5 + 3 * 2?",
                new String[]{"16", "11", "10", "13"},
                1
            ));
        }
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question current = questions.get(currentQuestionIndex);
            questionLabel.setText(current.getQuestionText());
            
            String[] options = current.getOptions();
            option1.setText(options[0]);
            option2.setText(options[1]);
            option3.setText(options[2]);
            option4.setText(options[3]);
            
            optionsGroup.selectToggle(null);
            
            questionNumberLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
            progressIndicator.setProgress((double)(currentQuestionIndex + 1) / questions.size());
        } else {
            showResults();
        }
    }

    @FXML
    private void handleNextButton() {
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        if (selected == null) {
            showAlert("Please select an answer!");
            return;
        }
        
        int selectedIndex = getSelectedIndex(selected);
        if (questions.get(currentQuestionIndex).isCorrect(selectedIndex)) {
            score++;
        } else {
            incorrectAnswers.add(currentQuestionIndex);
        }
        
        currentQuestionIndex++;
        showQuestion();
    }

    private int getSelectedIndex(RadioButton selected) {
        if (selected == option1) return 0;
        if (selected == option2) return 1;
        if (selected == option3) return 2;
        return 3;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("result-view.fxml"));
            Parent root = loader.load();
            
            ResultController controller = loader.getController();
            controller.setResults(score, questions.size(), incorrectAnswers);
            
            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Question {
    private String text;
    private String[] options;
    private int correctAnswer;

    public Question(String text, String[] options, int correctAnswer) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() { return text; }
    public String[] getOptions() { return options; }
    public boolean isCorrect(int selectedIndex) { return selectedIndex == correctAnswer; }
}