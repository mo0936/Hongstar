package client.ui;

import javax.swing.*;

import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

public class ChatPanel extends JPanel{

    // JLabel information = new JLabel("상대방 이름(chatPanel)");
    
    JPanel header_panel = new JPanel(); // 상단 패널
    JLabel chat_oppo_name_label = new JLabel("상대방 이름"); // 채팅방 이름 or 상대방 이름
    JButton spell_check_button = new JButton("맞춤법 ON"); // 맞춤법 on/off 버튼
    private boolean isSpellCheckOn = false; //상태 
    
    JTextArea text_area = new JTextArea();
    JPanel input_panel = new JPanel();
    JTextField input_field = new JTextField();
    JButton input_button = new JButton("전송");
    private JScrollPane scrollPane;
    public ChatPanel(){
        
    	setLayout(new BorderLayout(10, 10));
        
        // 상단 패널
        header_panel.setLayout(new BorderLayout()); // 중앙(라벨)과 오른쪽(버튼) 분할
        header_panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        header_panel.setPreferredSize(new Dimension(0, 40)); // 높이 조정

        chat_oppo_name_label.setHorizontalAlignment(SwingConstants.CENTER);
        header_panel.add(chat_oppo_name_label, BorderLayout.CENTER);

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

        
        // 입력 패널
        input_panel.setPreferredSize(new Dimension(0, 100));
        input_panel.setLayout(new BorderLayout(5, 5));
        add(input_panel, BorderLayout.SOUTH);

        //"전송" 버튼
        input_button.setPreferredSize(new Dimension(100, 0));
        input_button.addActionListener(new MyActionListener()); // 액션리스너 enter입력가능
        
        input_panel.add(input_field, BorderLayout.CENTER);
        input_panel.add(input_button, BorderLayout.EAST);
        
        input_field.addActionListener(new MyActionListener()); // 액션리스너 "전송" 버튼 클릭


        setBackground(new Color(210, 245, 255));
        input_panel.setBackground(new Color(210, 245, 255));

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

    private void sendMessage(String finalMsg, String myName) {
        text_area.append("[" + myName + "]: " + finalMsg + "\n");
        ClientNetwork.getInstance().sendChat(finalMsg);
    }

    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            String msg = input_field.getText();
            if(msg.trim().isEmpty()) return;

            String myName = ClientNetwork.getInstance().getLoggedInName(); 
            if (myName == null) {
                myName = "내 이름";
            }
            
            final String finalMyName = myName; 
            
            if (isSpellCheckOn) { 
                
                text_area.append("[시스템] 맞춤법 검사 중...\n");
                
                ClientNetwork.getInstance().getSpellCorrection(msg, (correctedMsg) -> {
                    
                    if (correctedMsg.startsWith("[교정 오류]")) {
                        text_area.append(correctedMsg + "\n");
                        
                        sendMessage(msg, finalMyName);
                    } else {
                        int result = JOptionPane.showConfirmDialog(
                            ChatPanel.this, 
                            "교정된 메시지를 전송할까요?\n\n[원본] " + msg + "\n[교정] " + correctedMsg, 
                            "맞춤법 교정", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.QUESTION_MESSAGE
                        );
                        
                        if (result == JOptionPane.YES_OPTION) {
                            sendMessage(correctedMsg, finalMyName);
                        } else {
                            sendMessage(msg, finalMyName);
                        }
                    }
                });

            } else {
                sendMessage(msg, myName);
            }

            input_field.setText("");
        }
    }


    
}
