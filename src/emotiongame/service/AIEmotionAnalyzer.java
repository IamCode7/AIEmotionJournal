package emotiongame.service;

import emotiongame.model.EmotionResult;
import java.util.List;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.StructuredResponseCreateParams;

public class AIEmotionAnalyzer {

        // 사용자에게 하루 일기에 대한 답변을 받아서 AI 호출
        public EmotionResult analyze(String answer) {

        // openAI 서버에 요청을 보낼 수 있는 클라이언트 객체 생성
        //OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        // AI 에게 보내 지시문
        String prompt = """
                당신은 감정일기 분석 AI입니다.

                사용자의 일기를 분석해서 가장 대표적인 감정을
                다음 5가지 중 하나만 선택하세요.

                기쁨, 슬픔, 분노, 불안, 평온

                규칙:
                1. 반드시 5가지 감정 중 하나만 선택하세요.
                2. 감정의 비율이나 숫자는 반환하지 마세요.
                3. emotion에는 선택한 감정의 한국어 이름을 넣으세요.
                4. 일기에 나타난 감정을 기준으로 판단하세요.

                사용자 일기:
                """ + answer;

        String API_KEY = "API_KEY";

        // AI 클라이언트 설정
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(API_KEY)
                .build();
        // 누가 어떤 정보로 인증 요청?
        // builder : 객체를 만드는 다른 방법 = new

        // API 요청정보 만들기
        // AI 응답을 EmotionResult 형태로 받도록 요청 정보 설정
        StructuredResponseCreateParams<EmotionResult> params = ResponseCreateParams.builder()
                .model("gpt-6-luna")
                .input(prompt)
                .text(EmotionResult.class)
                .build();
        // 어떤 모델에 무엇을 요청할껀가

        // AI 응답의 결과를 형식에 맞게 저장
        EmotionResult result = client.responses()
                .create(params)
                .output()
                .stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("AI 분석 결과가 없습니다."));
        // response : api 사용하기 위한 객체 가져옴
        // create : param에 적힌 정보로 AI 서버에 요청 후 결과를 가져옴
        // output() : 전체 응답에서 출력항목 가지고 옴 -> 리스트 형식 반환 item -> message -> content
        // 1. stream[item1, item2]
        // 2. stream[message1, message2]
        // 3. stream[content1, content2] -> flatMap 이 하나로 연결
        // 4. stream[EL("기쁨")], stream[EL("슬픔")]
        // 5. .findFirst : 첫번째 값 ex) 기쁨 선택
        // 6. 옵셔널 empty의 경우 분석결과가 없다고 result에 저장 -> 예외 발생
        // ** 옵셔널로 만들어지기 때문에 값이 없으면 요소 0개 스트림

        // 감정 불러오기 -> model>EmotionResult
        String emotion = result.getMainEmotion();

        List<String> emotions = List.of("기쁨", "슬픔", "분노", "불안", "평온");

        // emotion이 감정인지 확인
        if (!emotions.contains(emotion)) {

            throw new IllegalStateException("잘못된 감정입니다.");
        }
        // java.util.List : 리스트 만들기
        // IllegalStateException : 예상치 못한 상태
        // catch 가 없으면 스레드 중단

        return result;
    }
}
