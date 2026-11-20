package emotiongame.service;
import emotiongame.model.EmotionResult;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.StructuredResponseCreateParams;

public class AIEmotionAnalyzer {
    public EmotionResult analyze(String answer){

        // openAI 서버에 요청을 보낼 수 있는 클라이언트 객체 생성 
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

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
        
        // API 요청정보 만들기
        // AI 응답을 EmotionResult 형태로 받도록 요청 정보 설정
        StructuredResponseCreateParams<EmotionResult> params =
            ResponseCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .input(prompt)
                .text(EmotionResult.class)
                .build();

        // response : api 사용하기 위한 객체 가져옴
        // create : param에 적힌 정보로 AI 서버에 요청 후 결과를 가져옴
        // output() : 전체 응답에서 출력항목 가지고 옴 -> 리스트 형식 반환
        // stream : List -> Stream
        // item -> item.message() : 항목이 메세지인 경우 메시지를 옵셔널로 제공 ( item -> message )
        // .stream() : 값이 있으면 1개 없으면 0개 스트림 생성 ( 스트림은 링크드 리스트  )
        // flatMap : 옵셔널로 만들어진 맵 연결
        // 
        EmotionResult result = client.responses()
            .create(params)
            .output()
            .stream()
            .flatMap(item -> item.message().stream())
            .flatMap(message -> message.content().stream())
            .flatMap(content -> content.outputText().stream())
            .findFirst()
            .orElseThrow(() ->
                new IllegalStateException("AI 분석 결과가 없습니다.")
            );

        String emotion = result.getMainEmotion();

        if (!java.util.List.of(
                "기쁨", "슬픔", "분노", "불안", "평온"
            ).contains(emotion)) {

            throw new IllegalStateException("잘못된 감정입니다.");
        }

        return result;
    }
}
