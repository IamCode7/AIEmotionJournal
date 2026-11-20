package emotiongame.ui;

import javax.swing.*;
import java.awt.*;

// 메인화면
public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    // == mainPanel.setLayout(cardLayout);

    public MainFrame(){
        setTitle("AI 감정일기");
        setSize(500, 700);
        // 메인 패널에 홈 화면, 게임 화면 추가
        mainPanel.add(new StartPanel(this), "START");
        mainPanel.add(new GamePanel(this),"GAME");
        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // 게임화면 보여주기
    public void showGame() {
        cardLayout.show(mainPanel, "GAME");
    }

    // 홈 화면 보여주기
    public void showStart() {
        cardLayout.show(mainPanel, "START");
    }
    public static void main(String[] args){
        new MainFrame();
    }
}
