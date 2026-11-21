package emotiongame.ui;

import emotiongame.model.EmotionResult;
import emotiongame.service.AIEmotionAnalyzer;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// 게임 화면 - 패널
public class GamePanel extends JPanel {

    private Random random = new Random();

    private String[] questions = new String[3];

    private int qListNumber = 0;

    private ArrayList<String> answers = new ArrayList<String>();

    // < 질문 리스트 > ( 매일 같은 질문을 받지 않도록 다양성을 줌 )
    private String[] questionsPart01 = {
            "오늘 하루를 한 문장으로 표현한다면?",
            "오늘은 평소와 비교했을 때 어떤 하루였나요?",
            "오늘 하루의 시작과 끝은 어떻게 달랐나요?"
    };

    private String[] questionsPart02 = {
            "오늘 가장 기억에 남는 일은 무엇인가요?",
            "오늘 누군가와 나눈 대화 중 인상 깊었던 것이 있나요?",
            "오늘 예상하지 못했던 일이 있었나요?"
    };

    private String[] questionsPart03 = {
            "지금 마음속에서 가장 크게 느껴지는 감정은 무엇인가요?",
            "오늘 가장 오래 지속된 감정은 무엇인가요?",
            "오늘 표현하지 못하고 마음속에 남겨둔 감정이 있나요?"
    };

    public GamePanel(MainFrame frame) {
        setLayout(null);

        // < 게임 화면 제목 >
        JLabel title = new JLabel("게임 화면 입니다.");
        title.setBounds(150, 50, 200, 40);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // < 질문 >
        // - 각 파트 별 랜덤 질문 뽑기
        selectRandomQuestions();
        JLabel question = new JLabel(questions[qListNumber]);
        question.setBounds(50, 150, 400, 30);
        add(question);

        // < 답변 입력 창 >
        JTextArea answerArea = new JTextArea();
        answerArea.setBounds(50, 200, 400, 150);
        add(answerArea);

        // < 홈으로 이동 버튼 >
        JButton homeButton = new JButton("홈으로 이동");
        homeButton.setBounds(10, 10, 100, 40);
        add(homeButton);

        // < 홈으로 이동 버튼 클릭 이벤트 > -> 홈화면 보여주기
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.showStart();
            }
        });

        // < 답변 제출 버튼 >
        JButton submitButton = new JButton("답변 제출");
        submitButton.setBounds(150, 400, 200, 50);
        add(submitButton);

        // < 답변 제출 버튼 이벤트 > -> 답변 확인 팝업
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String answer = answerArea.getText().trim();
                if (answer.isEmpty()) {
                    JOptionPane.showMessageDialog(GamePanel.this, "답변을 입력해 주세요");
                    return;
                }
                // showConfirmDialog 매개변수 : 팝업 위치, 내용, 팝업 title, 확인 취소 버튼
                int result = JOptionPane.showConfirmDialog(GamePanel.this, "작성한 일기 : " + answer + "\n\n제출 하시겠습니까?",
                        "답변 확인", JOptionPane.OK_CANCEL_OPTION);
                // 확인을 누를 시 다음 질문으로 넘어감

                // 사용자의 답변 확인(확인 눌렀을 시에 조건문 작동) -> 정숫값 반환
                if (result == JOptionPane.OK_OPTION) {
                    
                    // 답변 제출 버튼 비활성화
                    submitButton.setEnabled(false);

                    // 백그라운드 작업 정의 
                    SwingWorker<EmotionResult, Void> worker = new SwingWorker<EmotionResult, Void>() {
                        // SwingWorker<T,M> : T -> 결과를 반환할 타입, M -> 작업 중간에 publish()에 보낼 타입( void는 중간결과를 보내지 않음)

                        // 별도 스레드에서 ai 작업 호출 
                        @Override
                        protected EmotionResult doInBackground() throws Exception {
                        // 직접 호출 불가 worker.excute로 호출, throws : 예외를 저장했다가 나중에 get으로 호출

                            // AI 분석 객체 생성
                            AIEmotionAnalyzer analyzer = new AIEmotionAnalyzer();

                            // 사용자 답변을 AI에 전달하고 분석 결과 반환
                            return analyzer.analyze(answer);
                        }

                        // 분석 후 swing 화면에서 결과 실행
                        @Override
                        protected void done() {
                        // 백그라운드 작업 끝나고 실행 
                            
                            // 제출 버튼 활성화
                            submitButton.setEnabled(true);

                            try {
                                // AI 분석 결과 가져오기
                                EmotionResult aiResult = get();
                                // EmotionResult타입이 아니면 예외 처리 오류

                                // AI가 분석한 감정 출력
                                JOptionPane.showMessageDialog(
                                        GamePanel.this,
                                        "AI가 분석한 감정은 " + aiResult.getMainEmotion() + "입니다!",
                                        "AI 감정 분석 결과",
                                        JOptionPane.INFORMATION_MESSAGE);

                                // 답변 저장
                                answers.add(answer);

                                // 다음 질문으로 이동
                                qListNumber++;

                                if (qListNumber < questions.length) {
                                    question.setText(questions[qListNumber]);
                                    answerArea.setText("");
                                } else {
                                    JOptionPane.showMessageDialog(
                                            GamePanel.this,
                                            "모든 질문을 완료했어요!");
                                }

                            } catch (Exception ex) {
                                Throwable cause = ex.getCause() != null
                                        ? ex.getCause()
                                        : ex;

                                JOptionPane.showMessageDialog(
                                        GamePanel.this,
                                        "AI 분석에 실패했습니다.\n" + cause.getMessage(),
                                        "오류",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    };
                    // 백그라운드 작업 실행 
                    worker.execute();
                }

            }
        });
    }

    // < 각 파트별 랜덤으로 질문 가져오기 >
    private void selectRandomQuestions() {
        questions[0] = questionsPart01[random.nextInt(questionsPart01.length)];
        questions[1] = questionsPart02[random.nextInt(questionsPart02.length)];
        questions[2] = questionsPart03[random.nextInt(questionsPart03.length)];
    }
}
