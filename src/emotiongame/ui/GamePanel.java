package emotiongame.ui;

import emotiongame.service.DiaryService;
import java.io.IOException;
import emotiongame.model.EmotionResult;
import emotiongame.service.AIEmotionAnalyzer;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

// 게임 화면 - 패널
public class GamePanel extends JPanel {

    private Random random = new Random();

    private String[] questions = new String[3];

    private int qListNumber = 0;

    private ArrayList<String> answers = new ArrayList<String>();

    private final DiaryService diaryService = new DiaryService();

    private JLabel imageLabel;

    private ArrayList<String> emotions = new ArrayList<String>();

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
        title.setBounds(150, 30, 200, 20);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // < 기본 알 이미지 >
        imageLabel = new JLabel();
        imageLabel.setBounds(100,10,300,300);
        add(imageLabel);
        changeImage("/images/basicEgg/egg.png");

        // < 질문 >
        // - 각 파트 별 랜덤 질문 뽑기
        selectRandomQuestions();
        JLabel question = new JLabel(questions[qListNumber]);
        question.setBounds(50, 300, 400, 30);
        add(question);

        // < 답변 입력 창 >
        JTextArea answerArea = new JTextArea();
        answerArea.setBounds(50, 350, 400, 150);
        answerArea.setLineWrap(true);
        // - 가로폭 넘으면 자동 줄바꿈
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
        submitButton.setBounds(150, 525, 200, 50);
        add(submitButton);

        // < 답변 제출 버튼 이벤트 > -> 답변 확인 팝업
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = frame.getCurId();

                if(userId == null){
                    JOptionPane.showMessageDialog(GamePanel.this, "먼저 로그인해 주세요.");
                    return;
                }
                String answer = answerArea.getText().trim();
                if (answer.isEmpty()) {
                    JOptionPane.showMessageDialog(GamePanel.this, "답변을 입력해 주세요");
                    return;
                }
                // showConfirmDialog 매개변수 : 팝업 위치, 내용, 팝업 title, 확인 취소 버튼
                // int result = JOptionPane.showConfirmDialog(GamePanel.this, "작성한 일기 : " +
                // answer + "\n\n제출 하시겠습니까?",
                // "답변 확인", JOptionPane.OK_CANCEL_OPTION);
                // 확인을 누를 시 다음 질문으로 넘어감

                // < 사용자의 답변을 출력하는 텍스트 창 >
                JTextArea previewArea = new JTextArea(answer);
                previewArea.setEditable(false);
                previewArea.setLineWrap(true);
                previewArea.setWrapStyleWord(true);
                // - 내용 수정 막음, 자동 줄바꿈 ON, 단어 단위 줄바꿈 ON

                // < 사용자 답변 출력 텍스트 창에 스크롤을 붙임 >
                JScrollPane preBar = new JScrollPane(
                        previewArea,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        // - 가로 스크롤 바 X , 세로 스크롤 바 O
                
                // < 텍스트 창 크기 설정 >
                preBar.setPreferredSize(new Dimension(360, 180));
                
                // < 사용자 답변 확인용 확인 팝업 >
                int result = JOptionPane.showConfirmDialog(GamePanel.this, new Object[] {
                        "작성한 일기:",
                        preBar,
                        "제출하시겠습니까?"
                },
                // - 스크롤이 보이도록 preBar 삽입
                // - 배열 삽입 시 자동으로 위에서 부터 읽음
                        "답변 확인",
                        JOptionPane.OK_CANCEL_OPTION);
                        // - " 답변 확인" : 팝업의 제목 표시줄
                        // OK_CANCEL_OPTION : 확인 취소 버튼 표시 

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

                            
                            return analyzer.analyze(answer);
                            // => 사용자 답변을 AI에 전달하고 분석 결과 반환
                            // - 1. analyze : service/AIEmotionAnalyzer
                        }

                        // < AI 분석 후 결과 보여주고, 답변 3개 모이면 일기 저장 > 
                        @Override
                        protected void done() {
                            // => done은 백그라운드 작업이 끝나면 자동 호출
                            Icon presIcon = imageLabel.getIcon();

                            try {
                                EmotionResult aiResult = get();
                                // => SwingWorker의 백그라운드 작업 결과물을 가지고 옴

                                String emotion = aiResult.getMainEmotion();
                                
                                evolveCharacter(emotion);

                                JOptionPane.showMessageDialog(
                                        GamePanel.this,
                                        "AI가 분석한 감정은 "
                                                + aiResult.getMainEmotion() + "입니다!",
                                        "AI 감정 분석 결과",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                // => 분석된 감정 팝업으로 보여주기
                                // - 1. getMainEmotion() : model/EmotionResult.java  

                                
                                ArrayList<String> nextAnswers = new ArrayList<>(answers);
                                nextAnswers.add(answer);
                                // => 기존 답변에 이번 답변까지 포함한 임시 목록 만들기
                                // - nextAnswers : 임시 리스트 ( 답변 추가 용 )

                                
                                if (nextAnswers.size() == questions.length) {
                                    StringBuilder content = new StringBuilder();
                                    for (int i = 0; i < questions.length; i++) {
                                        content.append(questions[i]).append("\n")
                                                .append(nextAnswers.get(i)).append("\n\n");
                                    }
                                    // 로그인한 계정의 폴더에 일기 하나 저장
                                    diaryService.saveDiary(userId, content.toString());
                                }
                                // => 질문 3개에 모두 답했다면 파일에 저장
                                // - 1. StringBuilder : 문자열 이어 붙이는 객체

                                answers.add(answer);
                                emotions.add(emotion);
                                System.out.println("누적 감정 : " + emotions);
                                qListNumber++;

                                if (qListNumber < questions.length) {
                                    // 다음 질문 표시
                                    question.setText(questions[qListNumber]);
                                    answerArea.setText("");

                                } else {
                                    JOptionPane.showMessageDialog(
                                            GamePanel.this,
                                            "일기를 저장했어요!"
                                    );

                                    
                                    answers.clear();
                                    // - 기존 답변 초기화
                                    emotions.clear();
                                    // - 기존 감정 초기화
                                    changeImage("/images/basicEgg/egg.png");
                                    // - 기본 알 이미지로 변경
                                    qListNumber = 0;
                                    // - 질문 번호 초기화
                                    selectRandomQuestions();
                                    // => 다음 일기를 작성할 수 있도록 초기화
                                    // - selectRandomQuestions : 질문 랜덤 뽑기

                                    question.setText(questions[0]);
                                    answerArea.setText("");
                                }

                            } catch (IOException e) {
                                // 파일 저장 실패 시 현재 질문과 답변을 유지
                                JOptionPane.showMessageDialog(
                                        GamePanel.this,
                                        "일기 저장에 실패했습니다. 다시 제출해 주세요."
                                );

                            } catch (Exception ex) {
                                Throwable cause = ex.getCause() != null
                                        ? ex.getCause()
                                        : ex;

                                JOptionPane.showMessageDialog(
                                        GamePanel.this,
                                        "AI 분석에 실패했습니다.\n" + cause.getMessage(),
                                        "오류",
                                        JOptionPane.ERROR_MESSAGE
                                );

                            } finally {
                                // 성공하거나 실패해도 제출 버튼 다시 활성화
                                submitButton.setEnabled(true);
                            }
                        }
                    };
                    // 백그라운드 작업 실행
                    worker.execute();
                }

            }
        });
    }
    // <흐름>
    /*
    AI가 {"emotion": "기쁨"} 형태로 응답
    ↓
    EmotionResult 객체의 emotion에 "기쁨"이 담김
    ↓
    analyze()가 그 객체를 반환
    ↓
    doInBackground()가 그 객체를 반환
    ↓
    done()에서 get()으로 객체를 가져옴
    */

    // < 각 파트별 랜덤으로 질문 가져오기 >
    private void selectRandomQuestions() {
        questions[0] = questionsPart01[random.nextInt(questionsPart01.length)];
        questions[1] = questionsPart02[random.nextInt(questionsPart02.length)];
        questions[2] = questionsPart03[random.nextInt(questionsPart03.length)];
    }

    // < 이미지 경로 받아서 화면에 표시 >
    // - 사용자에 답변에 따른 캐릭터 변화 
    private void changeImage(String Path){

        URL imageUrl = getClass().getResource(Path);
        if(imageUrl == null) {
            throw new IllegalStateException("이미지 파일을 찾을 수 없어요 ㅜㅜ" + Path);
        }
        ImageIcon imageIcon = new ImageIcon(imageUrl);
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_REPLICATE);
        imageLabel.setIcon(new ImageIcon(scaledImage));
    }
    // - setIcon : 현재 이미지를 바꾸는 기능

    private void evolveCharacter(String emotion){
        if(!emotion.equals("기쁨")){
            return;
        }
        int stage = emotions.size() + 1;
        if (stage == 1) {
        changeImage("/images/baby/testBaby.png");
    } else if (stage == 2) {
        changeImage("/images/growth/testGrowth.png");
    } else if (stage == 3) {
        changeImage("/images/adult/testAdult.png");
    }

    }
}
