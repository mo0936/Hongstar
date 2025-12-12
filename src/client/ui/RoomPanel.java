package client.ui;

import javax.swing.*;
<<<<<<< HEAD
=======

>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e
import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

<<<<<<< HEAD
public class RoomPanel extends JPanel {

    JLabel information = new JLabel("information");
    TextArea text_area = new TextArea();
    JPanel input_panel = new JPanel();
    JTextField input_field = new JTextField();
    JButton input_button = new JButton("전송");

    public RoomPanel() {

        setLayout(new BorderLayout(10, 10));

        // 정보 창
        information.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        information.setPreferredSize(new Dimension(0, 70));
        add(information, BorderLayout.NORTH);

        // 채팅 화면
        text_area.setEditable(false);
        add(text_area, BorderLayout.CENTER);

=======
public class RoomPanel extends JPanel{

    // JLabel information = new JLabel("상대방 이름(chatPanel)");
    
    JPanel header_panel = new JPanel(); // 상단 패널
    JLabel room_name_label = new JLabel("채팅방 이름"); // 채팅방 이름 or 상대방 이름
    JButton spell_check_button = new JButton("맞춤법 ON"); // 맞춤법 on/off 버튼
    private boolean isSpellCheckOn = false; //상태 
    
    JTextArea text_area = new JTextArea();
    JPanel input_panel = new JPanel();
    JTextField input_field = new JTextField();
    JButton input_button = new JButton("전송");
    private JScrollPane scrollPane;
    public RoomPanel(){
        
    	setLayout(new BorderLayout(10, 10));
        
        // 상단 패널
        header_panel.setLayout(new BorderLayout()); // 중앙(라벨)과 오른쪽(버튼) 분할
        header_panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        header_panel.setPreferredSize(new Dimension(0, 40)); // 높이 조정

        room_name_label.setHorizontalAlignment(SwingConstants.CENTER);
        header_panel.add(room_name_label, BorderLayout.CENTER);

        // 맞춤법 버튼 추가
        spell_check_button.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        spell_check_button.addActionListener(new SpellCheckButtonListener());
        
        // 버튼을 담을 작은 패널을 만들어 오른쪽 정렬
        JPanel button_wrap_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        button_wrap_panel.add(spell_check_button);
        button_wrap_panel.setOpaque(false); // 배경 투명 처리
        
        header_panel.add(button_wrap_panel, BorderLayout.EAST);
        
        add(header_panel, BorderLayout.NORTH);

        // 채팅 화면
        text_area.setEditable(false);

        // 스크롤바 추가
        scrollPane = new JScrollPane(text_area);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
        add(scrollPane, BorderLayout.CENTER);

        
>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e
        // 입력 패널
        input_panel.setPreferredSize(new Dimension(0, 100));
        input_panel.setLayout(new BorderLayout(5, 5));
        add(input_panel, BorderLayout.SOUTH);

<<<<<<< HEAD
        input_button.setPreferredSize(new Dimension(100, 0));
        input_panel.add(input_field, BorderLayout.CENTER);
        input_panel.add(input_button, BorderLayout.EAST);

        // 액션 리스너 등록 (엔터 / 버튼 공통)
        MyActionListener listener = new MyActionListener();
        input_button.addActionListener(listener);
        input_field.addActionListener(listener);

        setBackground(new Color(210, 245, 255));
        input_panel.setBackground(new Color(210, 245, 255));

        // 🔥 서버에서 오는 채팅 메시지 수신해서 화면에 출력
        //   서버에서 보내는 형식: "CHAT:아이디:내용"
        //   ClientNetwork.startListener()에서 "CHAT:" 떼고 "아이디:내용"만 넘겨줬다고 가정
        ClientNetwork.getInstance().onChatReceived(fullMsg -> {
            // fullMsg 형식: "아이디:내용"
            String line = fullMsg;

            String[] parts = fullMsg.split(":", 2); // 앞에서 한 번만 자르기
            if (parts.length == 2) {
                String sender = parts[0];
                String text   = parts[1];
                line = "[" + sender + "] " + text;
            }

            text_area.append(line + "\n");
        });
    }

    // ===== 입력 필드 / 버튼 액션 =====
    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String text = input_field.getText().trim();
            if (text.isEmpty()) return;

            // 🔥 서버로 메시지 전송
            // 실제 패킷은 ClientNetwork.sendChat() 내부에서
            // "CHAT_SEND:로그인아이디:내용" 형식으로 만들어서 보냄
            ClientNetwork.getInstance().sendChat(text);

            // 입력창 비우기 (내가 보낸 메시지는 서버 브로드캐스트로 다시 돌아와서 위에서 append 됨)
            input_field.setText("");
        }
    }
}
=======
        //"전송" 버튼
        input_button.setPreferredSize(new Dimension(100, 0));
        input_button.addActionListener(new MyActionListener()); // 액션리스너 enter입력가능
        
        input_panel.add(input_field, BorderLayout.CENTER);
        input_panel.add(input_button, BorderLayout.EAST);
        
        input_field.addActionListener(new MyActionListener()); // 액션리스너 "전송" 버튼 클릭


        setBackground(new Color(200, 235, 255));
        input_panel.setBackground(new Color(200, 235, 255));

    }

    private class SpellCheckButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            isSpellCheckOn = !isSpellCheckOn; 
            
            if (isSpellCheckOn) {
                spell_check_button.setText("맞춤법 OFF");
                System.out.println("[시스템] 맞춤법 검사 기능 활성화");
            } else {
                spell_check_button.setText("맞춤법 ON");
                System.out.println("[시스템] 맞춤법 검사 기능 비활성화");
            }
        }
    }

    private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e){
        String msg = input_field.getText();
        if(msg.trim().isEmpty()) return;

        text_area.append("[내 이름]: " + msg + "\n");
        ClientNetwork.getInstance().sendChat(msg);

        input_field.setText("");
    }
}


    
}
>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e
