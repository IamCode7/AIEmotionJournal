package emotiongame.ui;

import emotiongame.service.UserService;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    // <login이나 register 호출>
    private final UserService userService = new UserService();
    // <아이디 입력>
    private final JTextField idField = new JTextField();
    // <비밀번호 입력>
    private final JPasswordField passwordField = new JPasswordField();


    public LoginPanel(MainFrame frame) {
        setLayout(null);

        JLabel title = new JLabel("AI 감정일기 로그인", SwingConstants.CENTER);
        title.setBounds(100, 100, 280, 40);
        add(title);

        // < 아이디 라벨 ( 글자 ) 붙이기 >
        JLabel idLabel = new JLabel("아이디");
        idLabel.setBounds(100, 165, 280, 25);
        add(idLabel);

        // < 아이디 필드 ( 입력칸 ) 붙이기 >
        idField.setBounds(100, 195, 280, 35);
        add(idField);

        JLabel passwordLabel = new JLabel("비밀번호");
        passwordLabel.setBounds(100, 245, 280, 25);
        add(passwordLabel);

        passwordField.setBounds(100, 275, 280, 35);
        add(passwordField);

        JButton loginBtn = new JButton("로그인");
        loginBtn.setBounds(100, 335, 130, 40);
        add(loginBtn);

        JButton registerButton = new JButton("회원가입");
        registerButton.setBounds(250, 335, 130, 40);
        add(registerButton);

        // < 로그인 버튼 & 회원가입 버튼 눌렀을 때 이벤트 >
        loginBtn.addActionListener(e -> submit(frame, false));
        registerButton.addActionListener(e -> submit(frame, true));
    }

    // < 회원가입, 로그인 이벤트 처리 >
    private void submit(MainFrame frame, boolean registering) {
        String id = idField.getText().trim();
        char[] password = passwordField.getPassword();
        // - getPassword() : 입력된 비밀번호를 char로 가지고 옴

        try {
            if (registering) {
                // < 아이디 중복이 아니면 true 저장 >
                boolean registered = userService.register(id, password);
                // < 중복 -> 이미 사용 중인 아이디, 중복X -> 가입 완료! >
                if (registered) {
                    JOptionPane.showMessageDialog(this, "가입 완료! 다시 로그인해 주세요.");
                } else {
                    JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
                }
                return;
            }

            // < 저장된 비번이 있으면 true > 
            boolean loggedIn = userService.login(id, password);

            // < 저장된 비번이 있다면 메인화면 보여주기 >
            if (loggedIn) {
                frame.loginSuccess(id);
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.");
            }
            // < 아이디가 조건에 맞지 않을 때 >
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            // - service 클래스의 예외 처리 실행 파일

            // < 파일처리에 오류가 생겼을 때 >
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "계정 파일을 읽거나 저장하지 못했습니다.");
            

        } finally {
            // < 비밀 번호가 틀리거나 맞았을때 칸에서 지우는 역할 >
            passwordField.setText("");
        }
    }
}
