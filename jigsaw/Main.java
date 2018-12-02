
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static javafx.stage.Screen.getPrimary;

public class Main extends Application {

    int N = 3  ;    // 默认阶数
    int MinN = 2 ;  // 最小阶数
    int MaxN = 15;  // 最大阶数
    double Width  = 70;         // 一个方块的宽,最小30
    double Height = Width;      // 一个方块的高
    double Gap = 1;             // 默认方块间隔
    double MinGap = 0;          // 方块间隔
    double MaxGap = 20;         // 方块间隔

    int[] emp =new int[2];      // 空白格位置，默认随机

    Image image = new Image("file:img\\save.jpg");  // 图片地址
    ImageView[] imgs ; // 初始化 图片数组

    Pane rootbox = new Pane();
    GridPane root = new GridPane();

    LoadButton lb = new LoadButton();   // 加载按钮

    boolean mov;        // 是否可移动
    boolean issc;       // 成功与否

    // 图像居中坐标
    double parentX;
    double parentY;

    @Override
    public void start(Stage primaryStage) {
        root.setStyle("-fx-background:transparent;");
        root.setAlignment(Pos.CENTER);

        rootbox.setStyle("-fx-background:transparent;");

        toStartShow(primaryStage);


        // 舞台，场景

        Scene scene = new Scene(rootbox);

        scene.setFill(null);    // Set scene transparent.
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setAlwaysOnTop(true);
//        primaryStage.setMaximized(true);

        // 鼠标拖动
        DragListener listener = new DragListener(primaryStage);
        listener.enableDrag(rootbox);
    }

    // 游戏开始
    public void toStartShow(Stage primaryStage){
        root.getChildren().removeAll(root.getChildren());
        rootbox.getChildren().removeAll(root);
        rootbox.getChildren().add(root);

//        root.setPadding(new Insets(20, 20, 20, 20));
        clipPic();  // 剪切图片
        loadPic();  // 加载图片

        lb.leave(primaryStage); // 加载按钮

        lb.setPicNum(primaryStage);
        lb.replay();
        lb.show();
        lb.setGapAndPicSize(primaryStage);
    }


    // 按钮类
    public class LoadButton{

        VBox vb = new VBox();
        Button leave = new Button("X");

        HBox hb = new HBox();
        Button replay = new Button("R");
        Button showOriginalPic = new Button("S");
        Button setSlider = new Button("P");
        Button setPicNum = new Button("N");

        GridPane rootshow = new GridPane();    // showPane
        GridPane rootslider = new GridPane();    // showPane
        HBox rootpicnum = new HBox();    // showPane

        Text num = new Text(String.valueOf(N));  // 图片数目

        boolean nslider = true;     // 判断是否 打开slider
        boolean showoripic = true;  // 判断是否  显示原图
        boolean showpicnum = true;  // 判断是否 打开num slider

        double cwidth = Width;      // setSlider 点击时图片的大小
        double cgap = Gap;          // setSlider 点击时间隔的大小

        LoadButton(){
            // 垂直按钮
            vb.setPadding(new Insets(5, 5, 5, 5));
            vb.setSpacing(12);  // 按钮之间间隔
            vb.setAlignment(Pos.BOTTOM_CENTER);

            // 水平按钮
            hb.setPadding(new Insets(5, 5, 5, 0));
            hb.setSpacing(12);
            hb.setAlignment(Pos.CENTER_RIGHT);
        }

        public void leave(Stage primaryStage){  // leave 按钮
            setButtonStyle(leave);
            leave.setOnAction(event-> {
                primaryStage.close();
                System.out.println("leave");
            });

            // 添加按钮进去
            vb.getChildren().removeAll(leave);
            vb.getChildren().add(leave);
            toVRoot(vb);
        }


        public void show(){                     // 显示原图按钮
            ImageView simg = new ImageView(image);

            setButtonStyle(showOriginalPic);
            showOriginalPic.setOnAction(event-> {
                if(showoripic){
                    showoripic = false;
                    simg.setFitHeight((Height+Gap+1)*N-Gap);
                    simg.setFitWidth((Width+Gap+1)*N-Gap);
                    setRootGap(0);
                    // remove setGapAndPicSize
                    rootslider.getChildren().removeAll(rootslider.getChildren());
                    rootshow.getChildren().removeAll(simg);
                    rootshow.add(simg,0,0);
                    nslider = true;

                    root.add(rootshow,0,0,N,N);
                    replay.setDisable(true);
                    setSlider.setDisable(true);
                }else {
                    setRootGap(Gap);
                    showoripic = true;
                    rootshow.getChildren().removeAll(simg);
                    root.getChildren().removeAll(rootshow);
                    replay.setDisable(false);
                    setSlider.setDisable(false);
                }

            });
            hb.getChildren().removeAll(showOriginalPic);
            hb.getChildren().add(showOriginalPic);
            toHRoot(hb);
        }

        public void replay(){                    // replay 按钮
            setButtonStyle(replay);
            replay.setOnAction(event-> loadPic());

            hb.getChildren().removeAll(replay);
            hb.getChildren().add(replay);
            toHRoot(hb);
        }

        public void setGapAndPicSize(Stage primaryStage){                   // Slider 按钮

            Slider gapslider = new Slider(MinGap,MaxGap,Gap);
            gapslider.setStyle("-fx-font-size:10;" +
                    "-fx-control-inner-background: #6af78d;");

            gapslider.valueProperty().addListener(ov->{
                // 调整窗口大小
                if(Gap > cgap){
                    primaryStage.setWidth((Width+Gap)*N+200);
                    primaryStage.setHeight((Height+Gap)*N+45);
                }
                Gap = gapslider.getValue();
                root.setVgap(Gap);
                root.setHgap(Gap);
            });

            Slider picSizeslider = new Slider(40,200,Width);
            picSizeslider.setStyle("-fx-font-size:10;" +
                    "-fx-control-inner-background: #8ef7a8;");

            picSizeslider.valueProperty().addListener(ov->{
                if(Width > cwidth){
                    primaryStage.setWidth((Width+Gap)*N+200);
                    primaryStage.setHeight((Height+Gap)*N+45);
                }

                Width = picSizeslider.getValue();
                Height = picSizeslider.getValue();

                for (int i = 0; i < imgs.length; i++) {
                    imgs[i].setFitWidth(Width);
                    imgs[i].setFitHeight(Height);
                }
            });

//            rootslider.setStyle("-fx-background:#8ef7d9;");
            rootslider.setVgap(5);
            rootslider.setAlignment(Pos.CENTER);

            setButtonStyle(setSlider);
            setSlider.setOnAction(event-> {
                if(nslider){
                    nslider = false;
                    cgap = Gap;
                    cwidth = Width;

                    rootbox.getChildren().removeAll(rootslider);
                    rootslider.getChildren().removeAll(rootslider.getChildren());
                    rootslider.add(gapslider,0,0);
                    rootslider.add(picSizeslider,0,1);

                    rootbox.getChildren().add(rootslider);
                    rootslider.relocate((Width+Gap)*N+45,setSlider.getBoundsInParent().getMaxY()-30);

                    primaryStage.setWidth((Width+Gap)*N+200);
                    primaryStage.setHeight((Height+Gap)*N+45);
                }else {
                    nslider = true;
                    rootbox.getChildren().removeAll(rootslider);

                    primaryStage.setWidth((Width+Gap)*N+45);
                    primaryStage.setHeight((Height+Gap)*N+45);
                }
            });

            vb.getChildren().removeAll(setSlider);
            vb.getChildren().add(setSlider);
            toHRoot(hb);
        }

        public void setPicNum(Stage primaryStage){
            Slider setpicnum = new Slider(MinN,MaxN,N);
            setpicnum.setBlockIncrement(5);
            setpicnum.setMinorTickCount(5);
            setpicnum.setMajorTickUnit(5);

            setpicnum.setStyle("-fx-control-inner-background: #2ce0de");
            rootpicnum.setSpacing(4);
            rootpicnum.setStyle("-fx-background:transparent;");

            setpicnum.valueProperty().addListener(ov->{
                num.setText(String.valueOf((int)setpicnum.getValue()));
                N = (int)setpicnum.getValue();
            });

            setButtonStyle(setPicNum);
            setPicNum.setOnAction(event-> {
                    if(showpicnum){
                        showpicnum = false;

                            primaryStage.setHeight((Height+Gap)*N+60);
                            primaryStage.setWidth(primaryStage.getWidth()+60);

                        // 滑条样式
                        rootpicnum.relocate(hb.getBoundsInParent().getWidth()-110,(Width+Gap)*N+45);
                        rootpicnum.getChildren().removeAll(setpicnum,num);
                        rootpicnum.getChildren().addAll(setpicnum,num);

                        rootbox.getChildren().removeAll(rootpicnum);
                        rootbox.getChildren().add(rootpicnum);

                        rootslider.getChildren().removeAll(rootslider.getChildren());
                        replay.setDisable(true);
                        setSlider.setDisable(true);
                        showOriginalPic.setDisable(true);
                    }else {
                        showpicnum = true;
                        rootpicnum.getChildren().removeAll(setpicnum,num);
                        rootbox.getChildren().removeAll(rootpicnum);
                        replay.setDisable(false);
                        setSlider.setDisable(false);
                        showOriginalPic.setDisable(false);

                        rootslider.getChildren().removeAll(rootslider.getChildren());
                        rootshow.getChildren().removeAll(rootshow.getChildren());
                        // 重新
                         nslider = true;
                         showoripic = true;
                         mov = true;
                        issc = false;
                        toStartShow(primaryStage);
                        primaryStage.setWidth((Width+Gap)*N+45);
                        primaryStage.setHeight((Height+Gap)*N+45);
                    }
            });

            // 添加按钮进去
            hb.getChildren().removeAll(setPicNum);
            hb.getChildren().add(setPicNum);
            toHRoot(hb);
        }

        private void toVRoot(VBox vbox){
            root.getChildren().removeAll(vbox);
            root.add(vbox,N,0,1,N);
        }

        private void toHRoot(HBox hbox){
            root.getChildren().removeAll(hbox);
            root.add(hbox,0,N,N+1,1);
        }

        private void removeButton(HBox box, Button button){
            box.getChildren().removeAll(button);
        }

        // 设置按钮样式
        private void setButtonStyle(Button button){
            button.setStyle(
                    "-fx-background-color: linear-gradient(to right,#00fffc,#fff600);" +
                            "-fx-background-radius: 90;"
            );
            button.setOnMouseEntered(ev->{
                button.setScaleX(1.1);
                button.setScaleY(1.1);
            });
            button.setOnMouseExited(ev->{
                button.setScaleX(1);
                button.setScaleY(1);
            });
        }

    }


    // 图片加载
    public void loadPic(){
        mov = true;
        issc = false;
        root.getChildren().removeAll(imgs); // 清空网格

        // 横向加入图片
        List<Integer> ns = rand();
        for (int i = 0,k=0; i < N; i++) {
            for (int j = 0; j < N; j++,k++) {
                if(ns.get(k)==N*N-1) {
                    continue;
                }
                root.add(imgs[ns.get(k)], j, i);
            }
        }

        // 加载按钮
//        root.setGridLinesVisible(true);
        setRootGap(Gap);
        lb.showOriginalPic.setDisable(false);
    }

    // 切割图
    public void clipPic(){
        imgs = new ImageView[N*N];
        double eg = image.getWidth()/N;

        for(int i = 0, k = 0; i < N; ++i) {
            for(int j = 0; j < N; ++j, ++k) {
                imgs[k] = new ImageView(image);        //初始化数组
                imgs[k].setOnMouseClicked(new moveEven());
                imgs[k].setViewport(new Rectangle2D(eg * j, eg * i, eg, eg));             //切割图片
                imgs[k].setFitHeight(Height);
                imgs[k].setFitWidth(Width);
            }
        }
    }


    public static void main(String[] args) throws IOException{
        launch(args);
//        Img img = new Img("D:\\test\\java-workspace\\ReImage\\src\\sample\\img\\fengjing.jpg");
//        System.out.println(img.getWidth());
//        System.out.println(img.getHeight());
//        img.clip();
    }

    // 点击事件
    class moveEven implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
//                    System.out.println("Double clicked");
                }else if (event.getClickCount() == 1) {
//                    System.out.println("Single clicked");
                }
            }

            ImageView img = (ImageView) event.getSource();

            // 移动判断
            int[] temp = new int[]{GridPane.getRowIndex(img),GridPane.getColumnIndex(img)};
            if(((temp[0]==emp[0] && Math.abs(temp[1]-emp[1])==1) || (temp[1]==emp[1] && Math.abs(temp[0]-emp[0])==1)) && mov ) {
                        GridPane.setConstraints(img,emp[1],emp[0]);
                    emp = temp.clone();

                // 当空白格出现在最右下时，判断是否成功
                // 成功则显示最后一张
                if(emp[0]+emp[1]==N+N-2 && iss()){
                    root.add(imgs[N*N-1],emp[0],emp[1]);

                    setRootGap(0);

                    lb.showOriginalPic.setDisable(true);

//                    // 按钮淡出
//                    FadeTransition ft = new FadeTransition(Duration.millis(1500), replay);
//                    ft.setFromValue(0);
//                    ft.setToValue(1);
//                    ft.play();
//                    replay.setStyle(
//                            "-fx-background-color: linear-gradient(to right,#00fffc,#fff600);" +
//                            "-fx-background-radius: 25;"
//                    );
//                    root.add(replay, 0, 0,N,N);

                    mov = false;
                    issc = true;
                }
            }
        }

        // 成功与否
        public  boolean iss(){
            int x;
            for (int i = 0; i < N*N-1; ++i) {
                x= (N*GridPane.getRowIndex(imgs[i]) + GridPane.getColumnIndex(imgs[i]));
                if(i!=x){
                    return false;
                }
            }
            return true;
        }
    }


    // 随机数
    public List<Integer> rand(){

        boolean re; // 是否重新生成
        List<Integer> ns = new ArrayList<>();

        do{
            re = false;
            ns.clear();
            // 随机生成顺序
            while (ns.size() < N*N-1) {
                int num = (int)(Math.random() * N*N-1);
                if(!ns.contains(num)) ns.add(num);
            }

            // 计算逆序数
            int inum = 0;
            for (int i = 0; i < ns.size()-1; i++) {
                for (int j = i; j < ns.size()-1; j++) {
                    inum = ns.get(i)>ns.get(j+1) ? inum+1 : inum;
                }
            }

            // 空白格在最后：inum 如果是奇数则转为偶数
            if(inum%2==1){
                boolean conti=true;
                for (int i = 0 ; i < ns.size()-1 && conti; i++) {
                    for (int j = i; j < ns.size()-1; j++) {
                        if(ns.get(i)>ns.get(j+1)){
                            int x = ns.get(i);
                            ns.set(i,ns.get(j+1));
                            ns.set(j+1,x);
                            conti=false;
                            break;
                        }
                    }

                }
            }

            // 判断顺序是否已经打乱，没有则重新
            for (int i = 0; i < ns.size() ; i++) {
                if(i!=ns.get(i)) break;
                if(i==ns.size()-2){
                    re = true;
                    System.out.println("reRandNs");
                }
            }
        }while (re);

        // 随机空白格
        emp[0] = (int)(Math.random() * N);  // 空白格随机
        emp[1] = (int)(Math.random() * N);
        ns.add(N*N-1);
        int[][] ans = new int[N][N];
        for (int i = 0,k=0; i < N ; i++) {  // 转为二维
            for (int j = 0; j < N; j++,k++) {
                ans[i][j] = ns.get(k);
            }
        }

        // 空白格行间移动
        for (int i = N-1; !(i == emp[1]); i--) {
            int font = ans[N-1][i];
            ans[N-1][i] = ans[N-1][i-1];
            ans[N-1][i-1] = font;
        }

        // 空白格列间移动
        for (int i = N-1; !(i == emp[0]); i--) {
            int font = ans[i][emp[1]];
            ans[i][emp[1]] = ans[i-1][emp[1]];
            ans[i-1][emp[1]] = font;
        }

        // 转为一维
        for (int i = 0,k=0; i < N ; i++) {
            for (int j = 0; j < N; j++,k++) {
                ns.set(k,ans[i][j]);
            }
        }

        return  ns;
    }

    // 鼠标拖动
    public class DragListener implements EventHandler<MouseEvent> {

        private double xOffset = 0;
        private double yOffset = 0;
        private final Stage stage;

        public DragListener(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(MouseEvent event) {
            event.consume();
            if (event.getClickCount() == 2) {   // 双击才能移动
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
//                    if(event.getScreenY() - yOffset < 0) {
//                        stage.setY(0);
//                    }else {
//                        stage.setY(event.getScreenY() - yOffset);
//                    }
                }
            }
        }

        public void enableDrag(Node node) {
            node.setOnMousePressed(this);
            node.setOnMouseDragged(this);
        }
    }


    // 设置方块间的间距
    public void setRootGap(double gaps){
        root.setVgap(gaps);
        root.setHgap(gaps);
    }

}




class Img{
    int width;
    int height;
    BufferedImage img;
    Img(String path) throws IOException{
        this.img = ImageIO.read(new File(path));
    }

    public int getWidth(){
        return img.getWidth();
    }

    public int getHeight(){
        return img.getHeight();
    }

    public void clip() throws IOException {
        int ar = 3;
        int min = img.getWidth();
        int max = img.getHeight();
        if(min>max){
            min=img.getHeight();
            max=img.getWidth();
        }

        int width = min/ar;
        int height = min/ar;

        int[] pixels = new int[width*height];
        BufferedImage x = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for (int i = 0,k=0; i <ar; i++) {
            for (int j = 0; j <ar; j++,k++) {
                img.getRGB((max - min) / 2 + i * width, j*height, width, height, pixels, 0, width);
                x.setRGB(0, 0, width, height, pixels, 0, width);

                File outputfile = new File("img\\"+k+".jpg");
                ImageIO.write(x, "jpg", outputfile);
            }
        }

    }
}
