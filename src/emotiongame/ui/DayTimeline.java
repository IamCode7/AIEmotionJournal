package emotiongame.ui;

import javax.swing.*;
import java.util.*;

// < 최종 결과 발표 전 타임라인 모달 팝업 >
public class DayTimeline extends JDialog {

    private String[] questions;
    private List<String> answers;
    
    public DayTimeline(JFrame parent, String[] questions, List<String> answers) {
        // 매개변수 : parent(창을 소유하는 부모 창), " "(팝업창의 제목), true(부모창 조작 막음 : 모달 팝업)
        super(parent, "오늘의 감정 기록", true);

        this.questions = questions.clone();
        this.answers = List.copyOf(answers);

        setSize(400, 500);
        // 팝업창을 부모창의 가운데로 정렬
        setLocationRelativeTo(parent); 
        setLayout(null);

        // <모달 팝업 제목>
        JLabel title = new JLabel("오늘 일기");
        title.setBounds(100,30,200,40);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // <모달 팝업 페이지 번호>
        JLabel pageNum = new JLabel(" 1 / 4 페이지");
        pageNum.setBounds(100,75,200,30);
        pageNum.setHorizontalAlignment(SwingConstants.CENTER);
        add(pageNum);
    }
}
