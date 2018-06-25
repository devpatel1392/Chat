
package chat;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 *
 * @author Bruce
 */
public class Chat extends Application {
    
    private boolean isServer = false;
    private TextArea messages = new TextArea();
    private NetworkConn conn = isServer ? createServer() : createClient();
    
    private Parent createContent() {
        messages.setPrefHeight(550);
        TextField input = new TextField();
        input.setOnAction(event -> {
            String message = isServer ? "Server: " : "Client: ";
            message += input.getText();
            input.clear();
            
            messages.appendText(message + "\n");
            
            try {
                conn.send(message);
            } catch (Exception ex) {
                messages.appendText("Failed to send");
            }
        });
        
        VBox vb = new VBox(20, messages, input);
        vb.setPrefSize(600, 600);
        return vb;
    }
    
    @Override 
    public void init() throws Exception {
        conn.start();
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        conn.close();
    }

    private Server createServer() {
        return new Server(55555, data -> {
           Platform.runLater(() -> {
               messages.appendText(data.toString() + "\n");
           });
        });
    }
    
    private Client createClient() {
        return new Client("127.0.0.1", 55555, data -> {
           Platform.runLater(() -> {
               messages.appendText(data.toString() + "\n");
           });
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
