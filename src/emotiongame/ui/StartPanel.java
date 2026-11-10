package emotiongame.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

// 메인화면 - 패널
public class StartPanel extends JPanel {
    public StartPanel(MainFrame frame){
        setLayout(null);

        // < 게임 제목 >
        JLabel title = new JLabel("AI 감정일기");
        title.setBounds(175,150,150,40);
        // JLabel 내부 가운데 정렬( 기본은 왼쪽 정렬 )
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // < 게임 시작 버튼 >
        JButton startButton = new JButton("게임 시작");
        startButton.setBounds(150, 350, 200, 50);
        add(startButton);

        // < 게임 시작 버튼 클릭 이벤트 >
        startButton.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                frame.showGame();
            }
        });
    }
}
