import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Database;
import model.UsuarioRepository;
import view.UsuarioView;
import controller.UsuarioController;

/** Ponto de entrada: conecta Model, View e Controller. */
public class CampusHubInterface extends Application {
    private Database database;

    @Override
    public void start(Stage stage) {
        try {
            database = new Database(System.getProperty("campushub.db", "campushub.db"));
            UsuarioRepository repo = new UsuarioRepository(database);
            UsuarioView view = new UsuarioView();
            UsuarioController controller = new UsuarioController(repo, view);
            stage.setTitle("Campus Hub - Gerenciamento de Usuários");
            stage.setScene(new Scene(view.getRaiz(), 860, 560));
            stage.setMinWidth(780);
            stage.setMinHeight(520);
            stage.show();
            controller.carregarTodos();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                "Não foi possível abrir o Campus Hub: " + e.getMessage()).showAndWait();
            if (database != null) database.close();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        if (database != null) database.close();
    }

    public static void main(String[] args) { launch(args); }
}
