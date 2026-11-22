package emotiongame.ui;

import javax.swing.*;
import java.awt.*;

/*
< 사용자 관리 경로 >
1. 사용자 개인 정보 관리 : C:\Users\Admin\.ai-emotion-journal\ users-plain.properties
2. 계정 별 사용자 일기 관리 : C:\Users\Admin\ai-emotion-journal\ user-data
*/

// < 메인화면 >
public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    // == mainPanel.setLayout(cardLayout);
    private String curId;

    public MainFrame(){
        setTitle("AI 감정일기");
        setSize(500, 700);
        setResizable(false);
        // 메인 패널에 로그인 화면, 홈 화면, 게임 화면 추가
        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(new StartPanel(this), "START");
        mainPanel.add(new GamePanel(this),"GAME");
        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // < 게임화면 보여주기 >
    public void showGame() {
        cardLayout.show(mainPanel, "GAME");
    }

    // < 홈 화면 보여주기 >
    public void showStart() {
        cardLayout.show(mainPanel, "START");
    }

    // < 로그인 성공시 아이디 저장 & 홈 화면 이동 >
    public void loginSuccess(String id){
        this.curId = id;
        showStart();
    }

    // < 현재 아이디 가지고 오기 >
    public String getCurId() {
        return curId;
    }
    public static void main(String[] args){
        new MainFrame();
    }
}
