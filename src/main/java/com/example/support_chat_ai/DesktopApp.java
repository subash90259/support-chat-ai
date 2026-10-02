package com.example.support_chat_ai;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import netscape.javascript.JSObject;

public class DesktopApp extends Application {

    private Stage launcherStage;
    private Stage chatStage;


    // =========================================
    // WINDOWS NATIVE CONSTANTS
    // =========================================

    private static final int GWL_EXSTYLE = -20;

    private static final int WS_EX_APPWINDOW =
            0x00040000;

    private static final int WS_EX_TOOLWINDOW =
            0x00000080;


    // =========================================
    // APPLICATION START
    // =========================================

    @Override
    public void start(Stage primaryStage) {

        createChatStage();

        createLauncherStage();

        // Show only launcher
        launcherStage.show();

        /*
         * Wait until Windows creates
         * the native launcher window.
         */
        Platform.runLater(() -> {

            hideLauncherFromTaskbar();

        });

        System.out.println(
                "Support AI launcher started"
        );
    }


    // =========================================
    // CHAT WINDOW
    // =========================================

    private void createChatStage() {

        chatStage = new Stage();

        WebView webView =
                new WebView();

        WebEngine webEngine =
                webView.getEngine();

        VoiceBridge voiceBridge =
                new VoiceBridge();


        // =====================================
        // JAVASCRIPT CONNECTION
        // =====================================

        webEngine.getLoadWorker()
                .stateProperty()
                .addListener(
                        (observable,
                         oldState,
                         newState) -> {

                    if (newState ==
                            Worker.State.SUCCEEDED) {

                        JSObject window =
                                (JSObject)
                                webEngine.executeScript(
                                        "window"
                                );


                        // VoiceBridge
                        window.setMember(
                                "voiceBridge",
                                voiceBridge
                        );


                        voiceBridge
                                .setJavascriptWindow(
                                        window
                                );


                        // JavaFX window bridge
                        window.setMember(
                                "desktopWindow",
                                this
                        );


                        System.out.println(
                                "VoiceBridge + DesktopWindow connected"
                        );
                    }
                });


        // =====================================
        // LOAD CHAT UI
        // =====================================

        webEngine.load(
                "http://localhost:8080/"
        );


        // =====================================
        // CHAT WINDOW STYLE
        // =====================================

        chatStage.initStyle(
                StageStyle.UNDECORATED
        );

        chatStage.setAlwaysOnTop(true);

        chatStage.setWidth(450);

        chatStage.setHeight(700);


        // =====================================
        // CHAT SCENE
        // =====================================

        Scene scene =
                new Scene(
                        webView,
                        450,
                        700
                );

        chatStage.setScene(scene);


        // =====================================
        // CHAT POSITION
        // =====================================

        positionChatWindow();


        // =====================================
        // HIDE CHAT AT STARTUP
        // =====================================

        chatStage.hide();
    }


    // =========================================
    // FLOATING LAUNCHER
    // =========================================

    private void createLauncherStage() {

        launcherStage =
                new Stage();


        // Unique Windows title
        launcherStage.setTitle(
                "SupportAI_Launcher"
        );


        // Transparent window
        launcherStage.initStyle(
                StageStyle.TRANSPARENT
        );


        // Always on top
        launcherStage.setAlwaysOnTop(true);


        // Prevent resizing
        launcherStage.setResizable(false);


        // =====================================
        // AI BUTTON
        // =====================================

        Button aiButton =
                new Button("🤖");


        aiButton.setStyle("""
                -fx-background-color: #202123;
                -fx-text-fill: white;
                -fx-font-size: 24px;
                -fx-background-radius: 50;
                -fx-border-radius: 50;
                -fx-cursor: hand;
                """);


        aiButton.setPrefSize(
                60,
                60
        );


        // =====================================
        // BUTTON CLICK
        // =====================================

        aiButton.setOnAction(event -> {

            if (chatStage.isShowing()) {

                // Hide chat
                chatStage.hide();

            } else {

                // Open chat
                positionChatWindow();

                chatStage.show();

                chatStage.toFront();

                chatStage.requestFocus();
            }
        });


        // =====================================
        // ROOT
        // =====================================

        StackPane root =
                new StackPane(
                        aiButton
                );


        root.setAlignment(
                Pos.CENTER
        );


        root.setStyle(
                "-fx-background-color: transparent;"
        );


        // =====================================
        // SCENE
        // =====================================

        Scene scene =
                new Scene(
                        root,
                        60,
                        60
                );


        scene.setFill(null);


        launcherStage.setScene(
                scene
        );


        launcherStage.setWidth(60);

        launcherStage.setHeight(60);


        // =====================================
        // POSITION
        // =====================================

        positionLauncher();
    }


    // =========================================
    // POSITION LAUNCHER
    // =========================================

    private void positionLauncher() {

        Rectangle2D screen =
                Screen.getPrimary()
                        .getVisualBounds();


        double x =
                screen.getMaxX()
                - 80;


        double y =
                screen.getMaxY()
                - 100;


        launcherStage.setX(x);

        launcherStage.setY(y);
    }


    // =========================================
    // HIDE LAUNCHER FROM TASKBAR
    // =========================================

    private void hideLauncherFromTaskbar() {

    HWND hwnd =
            User32.INSTANCE.FindWindow(
                    null,
                    "SupportAI_Launcher"
            );

    if (hwnd == null) {

        System.out.println(
                "Launcher window not found"
        );

        return;
    }


    // =====================================
    // GET CURRENT EXTENDED WINDOW STYLE
    // =====================================

    LONG_PTR currentStyle =
            User32.INSTANCE.GetWindowLongPtr(
                    hwnd,
                    GWL_EXSTYLE
            );


    long style =
            currentStyle.longValue();


    // =====================================
    // REMOVE APP WINDOW STYLE
    // =====================================

    style =
            style & ~WS_EX_APPWINDOW;


    // =====================================
    // ADD TOOL WINDOW STYLE
    // =====================================

    style =
            style | WS_EX_TOOLWINDOW;


    // =====================================
    // APPLY NEW STYLE
    // =====================================

    User32.INSTANCE.SetWindowLongPtr(
            hwnd,
            GWL_EXSTYLE,
            Pointer.createConstant(style)
    );


    // =====================================
    // REFRESH WINDOW
    // =====================================

    User32.INSTANCE.SetWindowPos(
            hwnd,
            null,
            0,
            0,
            0,
            0,
            WinUser.SWP_NOMOVE
                    | WinUser.SWP_NOSIZE
                    | WinUser.SWP_NOZORDER
                    | WinUser.SWP_FRAMECHANGED
    );


    System.out.println(
            "Launcher removed from taskbar"
    );
}
    // =========================================
    // POSITION CHAT WINDOW
    // =========================================

    private void positionChatWindow() {

        Rectangle2D screen =
                Screen.getPrimary()
                        .getVisualBounds();


        double width = 450;

        double height = 700;


        double x =
                screen.getMaxX()
                - width
                - 20;


        double y =
                screen.getMaxY()
                - height
                - 20;


        chatStage.setX(x);

        chatStage.setY(y);
    }


    // =========================================
    // MINIMIZE CHAT
    // =========================================

    public void minimizeWindow() {

        chatStage.hide();
    }


    // =========================================
    // CLOSE CHAT
    // =========================================

    public void closeWindow() {

        chatStage.hide();
    }
}