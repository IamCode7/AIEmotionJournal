package emotiongame.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class DiaryService {

    // < 아이디 저장할 경로 >
    private final Path dataDir = Path.of(
            System.getProperty("user.home"),
            "ai-emotion-journal",
            "user-data");

    // < 계정별 일기 폴더 경로 >
    private Path getDiaryDir(String userId) {
        if(userId == null || !userId.matches("[A-Za-z0-9_]{3,20}")) {
            // - 영어 대소문자, 숫자, 밑줄 <> 해당 문자가 3개 이상 , 20개 이하
            throw new IllegalArgumentException("로그인 한 계정이 필요합니다. ");
        }

        String folderName = HexFormat.of().formatHex(userId.getBytes(StandardCharsets.UTF_8));
        // - userId.getBytes(StandardCharsets.UTF_8 : 아이디 형식을 UTF-8 방식의 바이트 배열로 바꿈
        // - HexFormat : 바이트를 16진수로 바꾸는 객체
        // - formatHex :  바이트를 16진수로 바꿈 ex ) 아이디가 abc라면 616263 으로 바뀜 ( 대소문자를 구분하기 위해 )
        return dataDir.resolve(folderName).resolve("diaries");
        // - resolve : 경로 붙이기 사용자 홈/.ai-emotion-journal/user-data
        // - resolve(folderName) => 사용자 홈/.ai-emotion-journal/user-data/616263
        // - resolve("diaries") => 사용자 홈/.ai-emotion-journal/user-data/616263/diaries
    }

    // < 해당 계정의 일기를 계정 폴더에 저장 -> 파일 ID 호출>
    public String saveDiary(String userId, String content) throws IOException {
        Path diaryDir = getDiaryDir(userId);
        // - 계정 폴더 경로를  가지고 오기

        Files.createDirectories(diaryDir);
        // - diaryDir 경로에 폴더 만들기 --> 중간 폴더 없으면 함께 생성

        String diaryId = UUID.randomUUID().toString();
        // => 각 계정의 각 일기마다 고유한 ID를 붙임( 구분용 )
        // - 1. UUID : 자바에서 다른 값과 겹칠 가능성이 낮은 고유 아이디 생성
        // - 2. 숫자 -> 문자열 : toString();

        Path diaryFile = diaryDir.resolve(diaryId + ".txt");
        // - 저장할 파일 경로 만들기

        Files.writeString(
                diaryFile, 
                content, 
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW
        );
        // => 만든 텍스트 파일에 내용 저장
        // - 1. diaryFile : 저장할 파일 경로
        // - 2. content : 파일의 내용
        // - 3. Stand--UTF-8 : 문자열을 UTF-8 방식으로 기록
        // - 4. Stand--Create_New : 새로운 파일 만들어 저장 ( 같은 이름이면 예외 오류 )

        return diaryId;
    }

    // < Map 형식으로 파일의 이름과 파일의 내용을 저장하고 반환 >
    public Map<String, String> loadDiaries(String userId) throws IOException {
        Path diaryDir = getDiaryDir(userId);
        Map<String, String> diaries = new LinkedHashMap<>();
        // => LinkedHashMap : 데이터 넣은 순서 유지

        if(Files.notExists(diaryDir)) {
            return diaries;
        }
        // => 파일이 경로에 없으면 빈 diaries 반환

        try(DirectoryStream<Path> files = Files.newDirectoryStream(diaryDir, "*.txt")){
            for(Path file : files ) {
                String name = file.getFileName().toString();
                String diaryId = name.substring(0, name.length() - 4);

                diaries.put(diaryId, Files.readString(file, StandardCharsets.UTF_8));
            }
        }
        // - 1. DirectoryStream<Path> : 폴더 안의 파일을 하나씩 볼 수 있게 함, 이 객체는 타입이 Path임
        // - 2. Path file : 파일의 경로를 가지고 옴
        // - 3. name : 파일 이름을 가지고 옴 ( UUID 형식 )
        // - 4. diaryId : 파일 이름에서 .txt를 제외
        // - 5. diaries.put(키, 값) : 파일이름(.txt제외), 파일 내용

        return diaries;
    }
}
