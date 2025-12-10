package client.network;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import javax.swing.SwingUtilities;

import common.Protocol;

import javax.swing.JOptionPane;

public class ClientNetwork {
    // 1. 서버 접속 정보 
    // 172.16.54.175
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 8080;

    //172.20.10.4

    private boolean loggedIn = false;
    private String loggedInId = null;
    private String loggedInName = null;

    public boolean isLoggedIn() {
    return loggedIn;
    }
    public String getLoggedInId() {
        return loggedInId;
    }  
    public String getLoggedInName() {
        return loggedInName;
    }

    // 싱글톤 패턴 (ClientNetwork 객체를 하나만 유지)
    private static ClientNetwork instance = new ClientNetwork();
    public static ClientNetwork getInstance() {
        return instance;
    }
    private ClientNetwork() {}
    


    // 1. 메시지 구조 (System, User)
    private static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        public Message() {}
    }

    // 2. API 요청 구조
    private static class ChatRequest {
        public String model;
        public Message[] messages;
        public ChatRequest() {}
    }

    // 3. API 응답 구조 (간소화)
    private static class ChatResponse {
        public Choice[] choices;
        public ChatResponse() {}
        private static class Choice {
            public Message message;
            public Choice() {}
        }
    }
    

    /*
     * 로그인 요청을 서버에 전송하고 응답을 처리합니다.
     */
    // ✅ 로그인: 성공이면 true, 실패면 false 리턴
    public boolean requestLogin(String id, String pw) {
        System.out.println("[클라] requestLogin 호출: " + id + "/" + pw);
        try (
            Socket socket = new Socket(SERVER_IP, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"))
        ) {
            // 서버에 로그인 정보 전송
            out.println(Protocol.LOGIN_REQUEST + id + ":" + pw);

            // 서버 응답 수신
            String serverResponse = in.readLine();
            System.out.println("[클라] 로그인 응답: " + serverResponse);

            if (serverResponse != null && serverResponse.startsWith(Protocol.SUCCESS_RESPONSE)) {
                // 응답 형식: SUCCESS:id:name
                String data = serverResponse.substring(Protocol.SUCCESS_RESPONSE.length());
                String[] parts = data.split(":");
                
                if (parts.length == 2) { 
                    loggedInId = parts[0]; 
                    loggedInName = parts[1]; 
                    loggedIn = true;
                    return true; 
                } else {
                    // 응답 형식 오류
                    return false;
                }
            } else {
                return false; // 로그인 실패
            }

        } catch (IOException e) {
            System.out.println("[클라] 로그인 중 오류: " + e.getMessage());
            JOptionPane.showMessageDialog(
                null,
                "서버 연결에 실패했습니다. 서버가 실행 중인지 확인하세요.",
                "연결 오류",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    public void requestJoin(String joinData) {
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT); // 서버 접속 정보 재사용
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                // 1. 서버에 회원가입 정보 전송 (형식: JOIN:ID:PW:NAME:…)
                out.println(Protocol.JOIN_REQUEST + joinData); 
                
                // 2. 서버 응답 수신
                String serverResponse = in.readLine();
                
                // 3. 응답 처리 (UI 스레드에서 실행)
                SwingUtilities.invokeLater(() -> {
                    if (serverResponse != null && serverResponse.startsWith(Protocol.SUCCESS_RESPONSE)) {
                        JOptionPane.showMessageDialog(null, "🎉 회원가입 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                        // 성공 후 로그인 화면으로 돌아가는 로직 추가 가능 (RegisterFrame 닫기)
                        
                    } else if (serverResponse != null && serverResponse.startsWith(Protocol.FAIL_RESPONSE)) {
                        String failReason = serverResponse.substring(Protocol.FAIL_RESPONSE.length());
                        JOptionPane.showMessageDialog(null, "회원가입 실패: " + failReason, "실패", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "서버 응답 오류 발생.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (IOException e) {
                 SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "서버 연결에 실패했습니다. 서버가 실행 중인지 확인하세요.", "연결 오류", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    public void requestLogout() {
    new Thread(() -> {
        try (
            Socket socket = new Socket(SERVER_IP, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            out.println(Protocol.LOGOUT_REQUEST);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "로그아웃 실패");
        }
    }).start();
    }

    private BufferedReader listenerInput;
    private Thread listenerThread;

    public void startListener() {
        new Thread(() -> {
            try {
                Socket socket = new Socket(SERVER_IP, PORT);
                listenerInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                listenerThread = new Thread(() -> {
                    try {
                        String msg;
                        while ((msg = listenerInput.readLine()) != null) {
                            System.out.println("[수신] " + msg);
                        }
                    } catch (IOException e) {}
                });

                listenerThread.start();

            } catch (IOException e) {
                System.out.println("[Listen 연결 실패]");
            }
        }).start();
    }

    private void sendSimple(String msg) {
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println(msg);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "서버 연결 실패");
            }
        }).start();
    }

    public void requestDeleteUser(String id, String pw) {
        sendSimple(Protocol.DELETE_USER_REQUEST + id + ":" + pw);
    }

    // 메세지 송신
    public void sendChat(String chatData) {
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) {
                if (out != null) {
                    out.println(Protocol.CHAT_MESSAGE_SEND + chatData);
                }   
            }
            catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, 
                        "채팅 전송 실패: 서버 연결 오류", "오류", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    public void requestUpdateUser(String id, String pw, String name, String email, String phone) {
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                // 패킷 형식: UPDATE_USER:id:pw:name:email:phone
                String packet = Protocol.UPDATE_USER_REQUEST
                            + id + ":" + pw + ":" + name + ":" + email + ":" + phone;

                System.out.println("[클라] UPDATE 패킷 = " + packet);

                out.println(packet);

                String response = in.readLine();
                System.out.println("[클라] UPDATE 응답 = " + response);

                SwingUtilities.invokeLater(() -> {
                    if (response != null && response.startsWith(Protocol.SUCCESS_RESPONSE)) {
                        JOptionPane.showMessageDialog(null, "회원 정보 수정 완료!", "성공", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "수정 실패: 서버 오류 또는 형식 오류", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버 연결 실패.", "연결 오류", JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }
    public void sendPhoto(File file) {
        if (loggedInId == null) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, 
                    "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE));
            return;
        }
        
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT);

                // 메타데이터 전송 String
                // 파일 데이터 전송 
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                FileInputStream fis = new FileInputStream(file);
                
            ) {
                // 형식: PHOTO_UPLOAD:userId:fileName:fileLength\n
                String metaData = Protocol.PHOTO_UPLOAD_REQUEST 
                                + loggedInId + ":" + file.getName() + ":" + file.length() + "\n";
                
                // 문자열을 바이트로 변환하여 전송
                dos.write(metaData.getBytes("UTF-8"));
                dos.flush();
                
                System.out.println("[클라] 사진 업로드 메타데이터 전송: " + metaData.trim());
                
                // 사진 파일 데이터 전송
                byte[] buffer = new byte[4096];
                int read;
                while ((read = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, read);
                }
                dos.flush();
                
                System.out.println("[클라] 사진 파일 전송 완료: " + file.getName());

                // TODO: 서버로부터의 응답

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, 
                        "사진 업로드 실패: 서버 연결 또는 파일 전송 오류", "오류", JOptionPane.ERROR_MESSAGE));
                e.printStackTrace();
            }
        }).start();
    }

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    private static final String API_KEY = "";
    /**
     * GPT-4o mini 모델을 사용하여 텍스트의 맞춤법을 교정합니다.
     * @param originalText 교정할 원본 텍스트
     * @param callback 교정 결과를 비동기적으로 처리할 콜백 함수
     */
    public void getSpellCorrection(String originalText, Consumer<String> callback) {
    if (API_KEY == null || API_KEY.isEmpty()) {
        callback.accept("[교정 오류] API 키가 설정되지 않았습니다.");
        return;
    }

    new Thread(() -> {
        try {
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            Message systemMsg = new Message("system", 
                "당신은 한국어 맞춤법, 띄어쓰기, 문법을 교정하고 교정된 문장만 출력하는 전문 교정기입니다. 교정 결과 외의 다른 설명은 절대 추가하지 마세요.");
            Message userMsg = new Message("user", originalText);

            ChatRequest requestBody = new ChatRequest();
            requestBody.model = "gpt-4o-mini";
            requestBody.messages = new Message[]{systemMsg, userMsg};

            String requestJson = objectMapper.writeValueAsString(requestBody);

            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY) 
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                
                ChatResponse chatResponse = objectMapper.readValue(response.body(), ChatResponse.class);
                String correctedText = chatResponse.choices[0].message.content.trim();
                
                
                SwingUtilities.invokeLater(() -> callback.accept(correctedText));
            } else {
                SwingUtilities.invokeLater(() -> callback.accept(
                    "[교정 오류] API 호출 실패: 상태 코드 " + response.statusCode() + " | " + response.body())
                );
            }

        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> callback.accept("[교정 오류] 예외 발생: " + e.getMessage()));
        }
    }).start();
}
}

