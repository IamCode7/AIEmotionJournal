package emotiongame.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;

public class UserService {
    // < 계정 정보 파일을 어디에 저장할지 정함(경로 저장) >
    private final Path userFile = Path.of(
            System.getProperty("user.home"),
            ".ai-emotion-journal",
            "users-plain.properties");
    // - Path : 자바에서 파일을 저장할 때 쓰는 자료형

    // < 회원 가입 >
    public boolean register(String id, char[] password)
            throws IOException {
        validate(id, password);
        Properties users = loadUser();
        if (users.containsKey(id)) {
            return false;
        }
        users.setProperty(id, new String(password));
        saveUsers(users);
        return true;
    }
    // - validate : 아이디 비번이 조건에 맞는지 검사 -> 조건에 안맞으면 예외
    // - localUser : 기존 파일의 아이디와 비번 짝을 users에 담아 반환
    // - 아이디가 이미 존재하면 return false( 파일의 정보와 대비 )
    // - saveUsers : 파일에 정보 저장
    // - IOException : 파일 읽기 쓰기 작업에 생기는 오류 -> 실행 종료
    // - Properties : 키 값을 함께 저장하는 클래스

    // < 로그인 >
    public boolean login(String id, char[] password) throws IOException {
        validate(id, password);

        Properties users = loadUser();
        String savedPassword = users.getProperty(id);
        if (savedPassword == null)
            return false;
        return savedPassword.equals(new String(password));
    }
    // - 아이디 비번 조건 검사 -> 파일에서 계정 정보 가지고 오기 -> 파일에 저장된 계정 정보에서 맞는 아이디의 비번 가지고 옴 -> 해당
    // - 아이디가 없는 아이디면(저장 X) return false -> 있다면 사용자가 입력한 비번과 저장된 비번이 맞는지 확인후 return
    // - throw는 예외를 던지고 throws는 메서드에서 어떤 예외가 나올 수 있는지 선언

    // < 아이디 비번 형식 검사 >
    private void validate(String id, char[] password) {
        if (id == null || !id.matches("[A-Za-z0-9_]{3,20}")) {
            throw new IllegalArgumentException("아이디는 영문, 숫자, _로 3~20자입니다.");
        }

        if (password == null || password.length < 8 || password.length > 128) {
            throw new IllegalArgumentException("비밀번호는 8~128자로 입력하세요.");
        }
    }
    // - 영문 소문자, 대문자, 숫자, 밑줄, 3~20 글자가 없으면 예외 객체를 만듬!

    // < 계정 파일을 읽고 Properties를 반환 >
    private Properties loadUser() throws IOException {
        Properties users = new Properties();

        try (Reader reader = Files.newBufferedReader(
                userFile, StandardCharsets.UTF_8)) {

            users.load(reader);

        } catch (NoSuchFileException e) {

        }

        return users;
    }
    // - 파일을 읽다가 IOException이 나오면 여기서 처리하지 않고 호출한 쪽으로 전달
    // - newBufferedReader : 파일을 열고 읽을 수 있는 객체로 만듬. ( 파일 경로, 읽는 방식 )
    // - load : reader을 통해 파일을 읽고, users에 채움
    // - 파일이 없을때 예외처리

    // < 회원가입 정보를 다시 파일에 저장 >
    private void saveUsers(Properties users) throws IOException {

        Files.createDirectories(userFile.getParent());

        try (Writer writer = Files.newBufferedWriter(
                userFile, StandardCharsets.UTF_8)) {

            users.store(writer, "Local user accounts");
        }
    }
    // - createDirectories : 매개변수의 경로에 폴더가 없으면 만듬 있으면 그대로 사용 
    // - userFile.getParent() : 자기자신을 제외한 부모 경로 
    // - 파일 명 : "users-plain.properties"
    // - writer : 문서를 쓰기용으로 열음
    // - 위에서 저장된 users를 가져와 저장 "Local user accounts"는 ( 파일 전체에 한번만 붙는 주석 )
    // + store 자체가 파일에 시간 저장

}
