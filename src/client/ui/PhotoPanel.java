package client.ui;

import javax.swing.*;
<<<<<<< HEAD
import javax.swing.border.Border;
=======
// import javax.swing.border.Border;
>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e
import javax.swing.filechooser.FileNameExtensionFilter;

import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;
<<<<<<< HEAD
=======
import java.io.File; 
// import java.nio.file.Path;
>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e

public class PhotoPanel extends JPanel{
    JPanel upside_panel;
    JButton upload_bt;
    JLabel upside_label;
    
<<<<<<< HEAD
    JPanel downside_panel;
    JLabel img_label;

    public PhotoPanel(){
        
    	setLayout(new BorderLayout(5, 5));
        setBackground(new Color(220, 255, 255));

=======
    // 사진들을 담을 스크롤 가능한 패널
    private JPanel photoFeedPanel;
    private JScrollPane scrollPane;

    // 이미지의 최대 너비를 프레임에 맞춰 고정
    private static final int MAX_IMAGE_WIDTH = 400; 
    
    // 좋아요 버튼 사진
    private static final String LIKE_NONCLICK_PATH = "src\\client\\ui\\like_nonclick.png";
    private static final String LIKE_CLICK_PATH = "src\\client\\ui\\like_click.png";
    
    private ImageIcon likeNonClickIcon;
    private ImageIcon likeClickIcon;

    //파일이름 라벨폰트
    private static final Font FILENAME_FONT = new Font("Dialog", Font.BOLD, 20);

    public PhotoPanel(){
        
        // 아이콘 로딩 및 크기 조정
        likeNonClickIcon = new ImageIcon(LIKE_NONCLICK_PATH);
        likeClickIcon = new ImageIcon(LIKE_CLICK_PATH);

        // 아이콘 크기 조정
        likeNonClickIcon = new ImageIcon(
            likeNonClickIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)
        );
        likeClickIcon = new ImageIcon(
            likeClickIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)
        );

    	setLayout(new BorderLayout(5, 5));
        setBackground(new Color(220, 255, 255));

        // 상단 패널
>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e
        upside_panel = new JPanel();
        upside_panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        upside_panel.setPreferredSize(new Dimension(0, 35));
        upside_panel.setBackground(new Color(220, 255, 255));
        
        add(upside_panel, BorderLayout.NORTH);
        upside_panel.setLayout(new BorderLayout(5, 5));
        upload_bt = new JButton("업로드");
        upside_label = new JLabel("사진판");
        upside_label.setFont(new Font("Dialog", Font.BOLD, 20));
        upside_panel.add(upside_label, BorderLayout.CENTER);
        upside_panel.add(upload_bt, BorderLayout.EAST);
        
        upload_bt.addActionListener(new MyActionListener());

<<<<<<< HEAD
        downside_panel = new JPanel();
        downside_panel.setBackground(new Color(220, 255, 255));
        add(downside_panel, BorderLayout.CENTER);
        
        downside_panel.setLayout(new FlowLayout());

        img_label = new JLabel("이미지 파일을 선택하세요.");
        img_label.setHorizontalAlignment(SwingConstants.CENTER);
        downside_panel.add(img_label);

        // 추가예정



    }
=======
        // 사진을 쌓을 중앙 피드 패널
        photoFeedPanel = new JPanel();
        // 사진들을 수직으로 쌓기 위해 BoxLayout 사용
        photoFeedPanel.setLayout(new BoxLayout(photoFeedPanel, BoxLayout.Y_AXIS)); 
        photoFeedPanel.setBackground(new Color(220, 255, 255));
        
        // 스크롤바 추가
        scrollPane = new JScrollPane(photoFeedPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // 초기 메시지 추가
        JLabel initialLabel = new JLabel("사진을 업로드하여 피드를 만드세요.");
        initialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        initialLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        photoFeedPanel.add(initialLabel);
    }
    
    
    // 사진 이미지와 정보를 묶어 관리하는 내부 패널 클래스
    private class PhotoPostPanel extends JPanel {
        // 좋아요 상태를 저장하는 변수
        private boolean isLiked = false;
        private JButton likeButton;
        private JLabel likeCountLabel;

        private int likeCount = 0; // 테스트용 초기 좋아요 수

        public PhotoPostPanel(String fileName, ImageIcon imageIcon) {
            setLayout(new BorderLayout(5, 5));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(25, 10, 25, 10), 
                BorderFactory.createLineBorder(Color.GRAY, 1)    
            ));

            // 상단 정보
            // 폰트
            JLabel infoLabel = new JLabel(fileName);
            infoLabel.setFont(FILENAME_FONT);

            infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
            add(infoLabel, BorderLayout.NORTH);

            // 이미지 추가
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(imageLabel, BorderLayout.CENTER);
            
            // 하단 상호작용 영역
            JPanel interactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            interactionPanel.setBackground(Color.WHITE);

            // 좋아요 버튼 추가
            likeButton = new JButton(likeNonClickIcon);
            likeButton.setBorder(BorderFactory.createEmptyBorder()); // 버튼 테두리 제거
            likeButton.setContentAreaFilled(false); // 배경 투명하게
            
            // 버튼 클릭 리스너 추가
            likeButton.addActionListener(new LikeButtonListener());
            
            interactionPanel.add(likeButton);

            // 좋아요 개수 라벨
            likeCountLabel = new JLabel(likeCount + "개");
            interactionPanel.add(likeCountLabel);

            // 하단 패널 추가
            add(interactionPanel, BorderLayout.SOUTH);
            
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setMaximumSize(new Dimension(MAX_IMAGE_WIDTH + 20, getPreferredSize().height));
        }

        // 좋아요 버튼 클릭 리스너
        private class LikeButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLiked = !isLiked; // 상태 토글

                if (isLiked) {
                    likeButton.setIcon(likeClickIcon);
                    likeCount++;
                    System.out.println("[클라] 좋아요 클릭 // 현재 좋아요 수: " + likeCount);
                    // TODO: 서버에 좋아요 요청 전송 로직 구현 (Protocol.LIKE_REQUEST 등)
                } else {
                    likeButton.setIcon(likeNonClickIcon);
                    likeCount--;
                    System.out.println("[클라] 좋아요 취소 // 현재 좋아요 수: " + likeCount);
                    // TODO: 서버에 좋아요 취소 요청 전송 로직 구현
                }
                likeCountLabel.setText(likeCount + "개");
            }
        }
    }
    
    // -------------------------------------------------------------
    
>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e
    private class MyActionListener implements ActionListener {
        private JFileChooser chooser;
        public MyActionListener() {
            chooser = new JFileChooser();
        }
        public void actionPerformed(ActionEvent e){
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "JPG & GIF & PNG Images", 
            "JPG", "GIF", "PNG");
            chooser.setFileFilter(filter);
            int ret = chooser.showOpenDialog(null);
<<<<<<< HEAD
=======
            
>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e
            if (ret != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null,
                     "파일을 선택하지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
<<<<<<< HEAD
            String filePath = chooser.getSelectedFile().getPath();

            ImageIcon originalIcon = new ImageIcon(filePath);
        
            // 이미지를 표시할 공간 (downside_panel)의 크기를 가져옵니다
            int panelWidth = downside_panel.getWidth() > 0 ? downside_panel.getWidth() : 600; 
            int panelHeight = downside_panel.getHeight() > 0 ? downside_panel.getHeight() : 400;

            // 이미지 크기를 패널 크기에 맞게 조정합니다
            Image originalImage = originalIcon.getImage();
            
            // ImageUtil 같은 별도 클래스 없이 간단하게 조정합니다
            Image scaledImage = originalImage.getScaledInstance(
                panelWidth, 
                panelHeight, 
                Image.SCALE_SMOOTH // 부드러운 스케일링
            );
            
            // 조정된 이미지로 새로운 아이콘을 생성하여 라벨에 설정합니다.
            img_label.setIcon(new ImageIcon(scaledImage));
            img_label.setText(null); // 이미지가 있을 때는 텍스트를 숨깁니다.
            
            // 변경된 컴포넌트가 화면에 즉시 반영되도록 부모 컨테이너를 갱신합니다.
            downside_panel.revalidate();
            downside_panel.repaint();

            // img_label.setIcon(new ImageIcon(filePath));
        }
    }
}
=======
            
            File selectedFile = chooser.getSelectedFile();
            String filePath = selectedFile.getPath();
            // Path를 사용해 파일 이름만 추출
            String fileName = selectedFile.toPath().getFileName().toString(); 

            // 이미지 로드 및 크기 조정
            ImageIcon originalIcon = new ImageIcon(filePath);
            Image originalImage = originalIcon.getImage();
            
            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);
            
            if (originalWidth < 0 || originalHeight < 0) {
                 JOptionPane.showMessageDialog(null,
                     "유효하지 않은 이미지 파일입니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 비율을 유지하며 최대 너비에 맞게 크기 조정
            double ratio = (double)MAX_IMAGE_WIDTH / originalWidth;
            int scaledWidth = MAX_IMAGE_WIDTH;
            int scaledHeight = (int) (originalImage.getHeight(null) * ratio);
            
            Image scaledImage = originalImage.getScaledInstance(
                scaledWidth, 
                scaledHeight, 
                Image.SCALE_SMOOTH 
            );
            
            // 새로운 PhotoPostPanel 생성 
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            PhotoPostPanel newPost = new PhotoPostPanel(fileName, scaledIcon);
            
            // 피드 패널에 추가
            if (photoFeedPanel.getComponentCount() > 0 && photoFeedPanel.getComponent(0) instanceof JLabel) {
                 JLabel firstLabel = (JLabel) photoFeedPanel.getComponent(0);
                 if (firstLabel.getText() != null && firstLabel.getText().contains("피드를 만드세요")) {
                    photoFeedPanel.remove(0);
                 }
            }
            
            // 가장 위에 최신 게시물이 오도록 0번 인덱스에 추가
            photoFeedPanel.add(newPost, 0); 
            
            // 서버 사진 보내기 요청
            // ClientNetwork network = new ClientNetwork();
            ClientNetwork.getInstance().sendPhoto(selectedFile); // 실제 업로드 로직 주석 처리
            System.out.println("[PhotoPanel][클라이언트] 사진 업로드 요청 (테스트): " + selectedFile.getName());


            // 화면 갱신 및 스크롤바 조정
            photoFeedPanel.revalidate();
            photoFeedPanel.repaint();
            
            // 최신 이미지가 보이도록 스크롤을 맨 위로 이동 (0번 인덱스에 추가했으므로)
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(0);
            });
        }
    }
}
>>>>>>> a89c944b510581c184008a22ce544e9e661bc85e
