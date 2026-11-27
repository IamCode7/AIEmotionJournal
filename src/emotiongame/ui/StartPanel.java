package emotiongame.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.URL;

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

        // < 이미지 파일 가지고 오기 >
        URL imageUrl = getClass().getResource("/images/nest/test.png");
        // - getClass() : 현재 객체의 클래스 정보를 가지고 옴

        // < 클래스 패스 확인 코드 >
        System.out.println("클래스 패스 : " + System.getProperty("java.class.path"));

        // < 이미지 파일 확인 >
        if(imageUrl == null) {
            throw new IllegalStateException("이미지 파일을 찾을 수 없어요ㅜㅜ");
        }
        // - 객체나 프로그램이 현재 작업을 정상적으로 실행 할 수 없을 때 ( 이미지를 불러오지 못했을 때)

        // < 파일에서 이미지 읽고, 이미지 수정하고, 이미지를 패널에 추가 >
        ImageIcon imageIcon = new ImageIcon(imageUrl);
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(360, 180, Image.SCALE_REPLICATE);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setBounds(70, 195, 360,180);
        add(imageLabel);
        // - ImageIcon : 자바 스윙에서 이미지를 읽고 화면에 표시하기 위함
        // - Image : 이미지를 수정하기위한 클래스
        // - getScaledInstance : 이미지 크기 조절( 가로 , 세로 , 경계 )
        // - setBounds : JLabel의 위치와 크기 ( 이안에 이미지를 붙임 )

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
