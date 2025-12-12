package client.ui;

import javax.swing.*;
import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

public class DMPanel extends JPanel{

    private JTextField targetIdField = new JTextField(); // DM 대상 ID 입력 필드
    private JButton connectBtn = new JButton("연결"); // 연결 버튼

    // JPanel header_panel = new JPanel();
    JButton spell_check_button = new JButton("맞춤법 ON");
    private boolean isSpellCheckOn = false;
    
    private JPanel messageContainer = new JPanel();
    private JScrollPane scrollPane;

    JPanel input_panel = new JPanel();
    private JTextArea inputField = new JTextArea(3, 20);
    JButton input_button = new JButton("전송");

    private static final Font BASE_FONT = new Font("맑은 고딕", Font.PLAIN, 13);
    private static final Font MESSAGE_FONT = new Font("맑은 고딕", Font.PLAIN, 14);
    private static final Font SENDER_FONT = new Font("맑은 고딕", Font.PLAIN, 11);
    
    private String targetId = null;
    /*
    * 아래로 옮김 
    private void sendFinalDM(String msgToSend) {
        String myId = ClientNetwork.getInstance().loggedInId;
        appendMessage(myId, msgToSend, true);
        ClientNetwork.getInstance().sendDirectMessage(targetId, msgToSend);
    }
    */
    public DMPanel(){
        
        try {
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof Font) {
                    UIManager.put(key, BASE_FONT);
                }
            }
            UIManager.put("TabbedPane.font", new Font("맑은 고딕", Font.BOLD, 14));
        } catch (Exception e) {

        }

        
    	setLayout(new BorderLayout(5, 5));

        // --- 🔴 상단 패널 재구성 (ID 입력 + 연결 + 맞춤법 ON) ---
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        topPanel.setPreferredSize(new Dimension(0, 40));
        topPanel.setBackground(new Color(230, 245, 255)); // 헤더 배경색
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5); // 여백 설정

        // 1. "상대 ID:" 라벨
        JLabel idLabel = new JLabel("상대 ID:");
        idLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        gbc.gridx = 0; gbc.weightx = 0; // 최소 너비
        topPanel.add(idLabel, gbc);

        // 2. ID 입력 필드
        gbc.gridx = 1; gbc.weightx = 1.0; // 🔴 남은 공간을 모두 차지하도록 비율 설정
        targetIdField.setFont(BASE_FONT);
        topPanel.add(targetIdField, gbc);

        // 3. 연결 버튼
        gbc.gridx = 2; gbc.weightx = 0; // 최소 너비
        connectBtn.setFont(BASE_FONT);
        connectBtn.addActionListener(e -> connectTarget());
        topPanel.add(connectBtn, gbc);
        
        // 4. 맞춤법 ON/OFF 버튼
        gbc.gridx = 3; gbc.weightx = 0; // 최소 너비
        spell_check_button.setFont(BASE_FONT);
        spell_check_button.addActionListener(new SpellCheckButtonListener());
        topPanel.add(spell_check_button, gbc);
        
        add(topPanel, BorderLayout.NORTH);
        // --- 🔴 상단 패널 재구성 끝 ---

        
        // 채팅 메시지 컨테이너 설정
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS)); // 수직으로 쌓기
        messageContainer.setBackground(new Color(250, 250, 250)); 
        
        // 스크롤바 추가
        scrollPane = new JScrollPane(messageContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 부드러운 스크롤링
        add(scrollPane, BorderLayout.CENTER);

        
        // 입력 패널
        input_panel.setLayout(new BorderLayout(5, 5));
        input_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(input_panel, BorderLayout.SOUTH);

        // "전송" 버튼
        input_button.setPreferredSize(new Dimension(80, 0));
        input_button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        input_button.addActionListener(new MyActionListener()); 
        
        // 입력 영역
        inputField.setLineWrap(true);
        inputField.setFont(MESSAGE_FONT); 
        JScrollPane inputScrollPane = new JScrollPane(inputField);
        inputScrollPane.setPreferredSize(new Dimension(0, 60));
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        input_panel.add(inputScrollPane, BorderLayout.CENTER);
        input_panel.add(input_button, BorderLayout.EAST);
        
        // Enter 키 입력 시 전송, Shift+Enter는 개행
        inputField.addKeyListener(new KeyAdapter() {
             @Override
             public void keyPressed(KeyEvent e) {
                 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                     e.consume();
                     if (e.isShiftDown()) {
                         inputField.append("\n");
                     } else {
                         new MyActionListener().actionPerformed(null);
                     }
                 }
             }
         });


        setBackground(new Color(250, 250, 250));
        input_panel.setBackground(new Color(240, 240, 240));
        
        ClientNetwork.getInstance().onDirectMessageReceived((toId, fromId, msg) -> {
            String myId = ClientNetwork.getInstance().loggedInId;
            
            // 현재 대화 상대와의 메시지만 처리 (혹은 나에게 온 메시지)
            if (fromId.equals(targetId) || toId.equals(myId)) {
                
                boolean isMyMessage = fromId.equals(myId);
                appendMessage(fromId, msg, isMyMessage);
            }
        });
    }

    private class BubblePanel extends JPanel {
        public BubblePanel(String bgColor) {
            setLayout(new BorderLayout(0, 3));
            setBackground(Color.decode(bgColor));

            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            setCursor(new Cursor(Cursor.TEXT_CURSOR));

            setBorder(BorderFactory.createLineBorder(Color.decode(bgColor).darker().darker(), 1));
        }
    }
    
    private class MessageBubblePanel extends JPanel {
        
        public MessageBubblePanel(String sender, String message, boolean isMe) {
            
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); 
            setOpaque(false);

            setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0)); 
            
            String bgColor = isMe ? "#DCEBFF" : "#FFFFFF"; 
           
            JPanel bubble = new BubblePanel(bgColor); 
        
            JLabel senderLabel = new JLabel(sender);
            senderLabel.setFont(SENDER_FONT);
            senderLabel.setForeground(new Color(85, 85, 85));
            bubble.add(senderLabel, BorderLayout.NORTH);

            String messageHtml = "<html><body style='font-family: \"맑은 고딕\"; font-size: 14px; margin: 0; padding: 0; background: " + bgColor + ";'>" + message.replace("\n", "<br>") + "</body></html>";
            JEditorPane messagePane = new JEditorPane("text/html", messageHtml);
            messagePane.setEditable(false);
            messagePane.setOpaque(false);
            
            Dimension prefSize = messagePane.getPreferredSize();
            int maxWidth = (int)(DMPanel.this.getWidth() * 0.7);

            int width = Math.min(prefSize.width, maxWidth);
            
            messagePane.setPreferredSize(new Dimension(width, prefSize.height));
            
            bubble.add(messagePane, BorderLayout.CENTER);

            add(bubble);
            
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height)); 
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }
    }
    
    public void appendMessage(String sender, String message, boolean isMe) {
        
        MessageBubblePanel bubble = new MessageBubblePanel(sender, message, isMe);
        
        messageContainer.add(bubble);
        
    
        messageContainer.revalidate();
        messageContainer.repaint();
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
    }
    
    public void appendSystemMessage(String message) {
        JLabel sysLabel = new JLabel(message);
        sysLabel.setFont(new Font("맑은 고딕", Font.ITALIC, 12));
        sysLabel.setForeground(new Color(136, 136, 136));
        sysLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        sysLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); 

        
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);
        wrapper.add(sysLabel);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        messageContainer.add(wrapper);
        
        messageContainer.revalidate();
        messageContainer.repaint();
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
    }


    private class SpellCheckButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            isSpellCheckOn = !isSpellCheckOn; 
            
            if (isSpellCheckOn) {
                spell_check_button.setText("맞춤법 OFF");
                appendSystemMessage("맞춤법 검사 기능 활성화");
            } else {
                spell_check_button.setText("맞춤법 ON");
                appendSystemMessage("맞춤법 검사 기능 비활성화");
            }
        }
    }

    private void sendMessage(String finalMsg, String myId) {
        sendFinalDM(finalMsg);
    }

    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            if (targetId == null || targetId.isEmpty()) {
                JOptionPane.showMessageDialog(DMPanel.this, "먼저 상대 ID를 입력하고 연결하세요", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String msg = inputField.getText().trim(); 
            if(msg.isEmpty()) {
                inputField.setText(""); 
                return;
            }

            String myId = ClientNetwork.getInstance().loggedInId;
            final String finalMyId = myId;

            if (isSpellCheckOn) { 
                
                appendSystemMessage("맞춤법 검사 중...");
                
                // 🔴 맞춤법 검사 요청
                // ClientNetwork에 getSpellCorrection이 정의되어 있다고 가정하고 사용
                ClientNetwork.getInstance().getSpellCorrection(msg, (correctedMsg) -> {
                    
                    if (correctedMsg.startsWith("[교정 오류]")) {
                        // 서버 오류 등
                        appendSystemMessage(correctedMsg);
                        sendFinalDM(msg); // 원본 전송
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            int result = JOptionPane.showConfirmDialog(
                                DMPanel.this, 
                                "교정된 메시지를 전송할까요?\n\n[원본] " + msg.replace("\n", " ") + "\n[교정] " + correctedMsg.replace("\n", " "), 
                                "맞춤법 교정", 
                                JOptionPane.YES_NO_OPTION, 
                                JOptionPane.QUESTION_MESSAGE
                            );
                            
                            if (result == JOptionPane.YES_OPTION) {
                                sendFinalDM(correctedMsg);
                            } else {
                                sendFinalDM(msg);
                            }
                        });
                    }
                });

            } else {
                sendFinalDM(msg); // 맞춤법 OFF: 원본 전송
            }

            inputField.setText("");
        }
    }

    // ===== 상대 ID 설정 =====
    private void connectTarget() {
        String id = targetIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "상대 ID를 입력하세요");
            return;
        }

        targetId = id;
        // chatArea.append(">>> [" + id + "] 님과의 1:1 채팅 시작\n");
        appendSystemMessage(">>> [" + id + "] 님과의 1:1 채팅 시작");
    }

    // ===== DM 전송 =====
    private void sendFinalDM(String msgToSend) {
        String myId = ClientNetwork.getInstance().loggedInId;
        appendMessage(myId, msgToSend, true);
        ClientNetwork.getInstance().sendDirectMessage(targetId, msgToSend);
    }
    
}