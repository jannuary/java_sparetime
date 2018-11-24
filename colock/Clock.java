import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Screen;
import java.util.Date;
import java.text.SimpleDateFormat;
import javafx.scene.paint.Color;
import javafx.application.Platform; // UI Thread 
import java.util.Timer; // 时间线程
import java.util.TimerTask;
import javafx.geometry.Rectangle2D;

public class Clock extends Application {
    Text text;
    
    @Override
    public void start(Stage stage) {
        text = new Text(clock());
        text.setFont(Font.loadFont("file:Agency.ttf.woff", 35));
        text.setFill(Color.GREEN);

        HBox root = new HBox();
        root.getChildren().add(text);

        final int width = 130;
        final int height = 40;
        final Scene scene = new Scene(root,width, height);

        scene.setFill(null);    // Set scene transparent.
        stage.initStyle(StageStyle.TRANSPARENT);    // Set stage transparent. 
        // set right top
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getWidth() - width);      
        stage.setY(10); 

        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        
        stage.show();
        System.out.println("Time start.");
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(clock());
                    }
                });
            }
        }, 0, 500);

        stage.setOnCloseRequest(event->{    // Close
            System.out.println("Stage has been closed."); 
            timer.cancel();     // Close TimerTask
        });
    }

    public static String clock(){   // Time string
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

