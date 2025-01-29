package shogi;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static javax.swing.JOptionPane.*;



public class Shogi{
    public static void main(String[] args) {
        JFrame frame = new JFrame("将棋");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 625);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(new Panel());
        frame.setVisible(true);
    }
}



class Panel extends JPanel implements MouseListener{
	//初期設定
	static final ImageIcon[][] GYOKU_ICON = new ImageIcon[3][2];{
		GYOKU_ICON[1][0] = new ImageIcon("玉(先手).png");
		GYOKU_ICON[2][0] = new ImageIcon("王(後手).png");
	}
	static final ImageIcon[][] KIN_ICON = new ImageIcon[3][2];{
		KIN_ICON[1][0] = new ImageIcon("金(先手).png");
		KIN_ICON[2][0] = new ImageIcon("金(後手).png");
	}
	static final ImageIcon[][] GIN_ICON = new ImageIcon[3][2];{
		GIN_ICON[1][0] = new ImageIcon("銀(先手).png");
		GIN_ICON[2][0] = new ImageIcon("銀(後手).png");
		GIN_ICON[1][1] = new ImageIcon("成銀(先手).png");
		GIN_ICON[2][1] = new ImageIcon("成銀(後手).png");
	}
	static final ImageIcon[][] KEI_ICON = new ImageIcon[3][2];{
		KEI_ICON[1][0] = new ImageIcon("桂(先手).png");
		KEI_ICON[2][0] = new ImageIcon("桂(後手).png");
		KEI_ICON[1][1] = new ImageIcon("成桂(先手).png");
		KEI_ICON[2][1] = new ImageIcon("成桂(後手).png");
	}
	static final ImageIcon[][] KYO_ICON = new ImageIcon[3][2];{
		KYO_ICON[1][0] = new ImageIcon("香(先手).png");
		KYO_ICON[2][0] = new ImageIcon("香(後手).png");
		KYO_ICON[1][1] = new ImageIcon("成香(先手).png");
		KYO_ICON[2][1] = new ImageIcon("成香(後手).png");
	}
	static final ImageIcon[][] HISYA_ICON = new ImageIcon[3][2];{
		HISYA_ICON[1][0] = new ImageIcon("飛車(先手).png");
		HISYA_ICON[2][0] = new ImageIcon("飛車(後手).png");
		HISYA_ICON[1][1] = new ImageIcon("龍(先手).png");
		HISYA_ICON[2][1] = new ImageIcon("龍(後手).png");
	}
	static final ImageIcon[][] KAKU_ICON = new ImageIcon[3][2];{
		KAKU_ICON[1][0] = new ImageIcon("角(先手).png");
		KAKU_ICON[2][0] = new ImageIcon("角(後手).png");
		KAKU_ICON[1][1] = new ImageIcon("馬(先手).png");
		KAKU_ICON[2][1] = new ImageIcon("馬(後手).png");
	}
	static final ImageIcon[][] FU_ICON = new ImageIcon[3][2];{
		FU_ICON[1][0] = new ImageIcon("歩(先手).png");
		FU_ICON[2][0] = new ImageIcon("歩(後手).png");
		FU_ICON[1][1] = new ImageIcon("と(先手).png");
		FU_ICON[2][1] = new ImageIcon("と(後手).png");
	}
	
	/*
	初期駒配置と格納変数
	piece[2]  piece[4]  piece[6]  piece[8]  piece[10] piece[12] piece[14] piece[16] piece[18]
			  piece[20]										              piece[22]
	piece[24] piece[26] piece[28] piece[30] piece[32] piece[34] piece[36] piece[38] piece[40]
	
	
	
	piece[23] piece[25] piece[27] piece[29] piece[31] piece[33] piece[35] piece[37] piece[39]
			  piece[19]										              piece[21]
	piece[1]  piece[3]  piece[5]  piece[7]  piece[9]  piece[11] piece[13] piece[15] piece[17]
	*/
	
	static final int PIECE_ONE = 1;//駒の総計
	static final int PIECE_SUM = 40;//駒の総計
	static final int FIELD_MIN_SIZE = 1;//盤面サイズ
	static final int FIELD_MAX_SIZE = 9;//盤面サイズ
	static final int HOLD_MIN_SIZE = 1;//手持ちサイズ
	static final int HOLD_MAX_SIZE = 7;//手持ちサイズ
	static final int NO_PIECE = 0;//駒なし位置
	static final int FIRST_TURN = 1;//先手手番コード
	static final int LAST_TURN = 2;//後手手番コード
	static final int REVERSE_FALSE = 0;//成が発生していない
	static final int REVERSE_TRUE = 1;//成が発生している
	static final int HOLD = 0;//手持ち位置
	static final List<Integer> GYOKU_LIST = Arrays.asList(9,10);
	static final List<Integer> KIN_LIST = Arrays.asList(7,8,11,12);
	static final List<Integer> GIN_LIST = Arrays.asList(5,6,13,14);
	static final List<Integer> KEI_LIST = Arrays.asList(3,4,15,16);
	static final List<Integer> KYO_LIST = Arrays.asList(1,2,17,18);
	static final List<Integer> HISYA_LIST = Arrays.asList(20,21);
	static final List<Integer> KAKU_LIST = Arrays.asList(19,22);
	static final List<Integer> FU_LIST = IntStream.range(23,PIECE_SUM + 1).boxed().collect(Collectors.toList());
	
	int[] playerCode = new int[PIECE_SUM + 1];{//各駒の所属プレイヤー　1:先手　2:後手
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
			playerCode[i] = (i%2 == 0)? LAST_TURN: FIRST_TURN;
		}
	}
	int[][] piecePlace = new int[10][10];{//盤面上の駒配置確認　0:駒なし　1:先手　2:後手
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			piecePlace[i][1] = piecePlace[i][3] = LAST_TURN;
			piecePlace[i][7] = piecePlace[i][9] = FIRST_TURN;
		}
		piecePlace[2][2] = piecePlace[8][2] = LAST_TURN;
		piecePlace[2][8] = piecePlace[8][8] = FIRST_TURN;
	}
	JLabel[] piece = new JLabel[PIECE_SUM + 1];{//標示する駒
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
			piece[i] = new JLabel(FU_ICON[1][0]);
		}
	}
	int[] positionX = new int[PIECE_SUM + 1];{//各駒のx座標
		for(int i = PIECE_ONE; i < 19; i+= 2) {
			positionX[i] = positionX[i + 1] = (i + 1) / 2;
		}
		positionX[19] = positionX[20] = 2;
		positionX[21] = positionX[22] = 8;
		for(int i = 23; i <= PIECE_SUM; i+= 2) {
			positionX[i] = positionX[i + 1] = (i - 21) / 2;
		}
	}
	int[] positionY = new int[PIECE_SUM + 1];{//各駒のy座標
		for(int i = PIECE_ONE; i < 19; i++) {
			positionY[i] = (i % 2 == 0)? 1: 9;
		}
		for(int i = 19; i < 23; i++) {
			positionY[i] = (i % 2 == 0)? 2: 8;
		}
		for(int i = 23; i <= PIECE_SUM; i++) {
			positionY[i] = (i % 2 == 0)? 3: 7;
		}
	}
	int placeX;//手持ち駒のx座標
	int placeY;//手持ち駒のy座標
	int[][] number = new int[3][8];//手持ち枚数
	
	int turnMyself = 1;//現在の手番コード
	int turnOpponent = 2;//現在の相手手番コード
	int[] reverseCode = new int[PIECE_SUM + 1];//各駒の成状況　0:通常　1:成
	
	ValueRange searchRange;//クリックされた座標特定用
	int pointX;//クリックされたx座標
	int pointY;//クリックされたy座標
	boolean canHold;//手持ちがクリックされた時
	int holdTurnCode;//クリックされた手持ち駒の手番
	int holdPieceCode;//クリックされた手持ち駒番号
	int activePiece;//クリックされた駒番号
	
	int[] defaultMotionRange = new int[8];//駒の可動域設定 0を12方向とし、以下時計回りに番号を振る
	boolean existsKeima;//駒の可動域設定 桂馬専用
	int[] direction = new int[4];//駒の8方向移動時の格納変数
	boolean[] existsDoubleFu = new boolean[10];//二歩確認用
	boolean[][] existsPieceRange = new boolean[10][10];//選択中の駒用の可動域描写
	
	int[][] provisionalPiecePlace = new int[10][10];//仮に選択駒を打った盤面
	int[] provisionalPositionX = new int[PIECE_SUM + 1];//仮に選択駒を打った際の各駒のx座標
	int[] provisionalPositionY = new int[PIECE_SUM + 1];//仮に選択駒を打った際の各駒のy座標
	boolean[][] existsProvisionalPieceRange = new boolean[10][10];//仮盤面の玉の可動域
	int provisionalGyokuPositionX;//仮盤面の玉のx座標
	int provisionalGyokuPositionY;//仮盤面の玉のy座標
	boolean[][] existsProvisionalAllPieceRange = new boolean[10][10];//仮盤面の全自駒可動域
	boolean[][] existsProvisionalAllPieceRangeSecond = new boolean[10][10];//仮盤面の全自駒可動域2つ目
	
	boolean existsFuCheckmate;//打ち歩詰め通知
	boolean existsCheckmate;//詰み通知
	
	int dialogResult;//成込み確認
	int[] onlyReverse = new int[2];//強制的に成が発生するマス
	boolean extstsNowReverse;//この手番で成が発生した通知
	boolean extstsNoReverse;//この手番で不成が発生した通知
	ImageIcon[] reverseIcon = new ImageIcon[2];//成込み確認用アイコン
	
	int moveNumber;//手数
	String turnRecord;//棋譜に記載する手番
	int positionXRecord;//棋譜に記載するx座標
	String positionYRecord;//棋譜に記載するy座標
	String pieceNameRecord;//棋譜に記載する駒名
	Map<Integer, String> FIELD_NUMBER_MAP = new HashMap<Integer, String>();{//y座標変換用
		FIELD_NUMBER_MAP.put(1,"一");
		FIELD_NUMBER_MAP.put(2,"二");
		FIELD_NUMBER_MAP.put(3,"三");
		FIELD_NUMBER_MAP.put(4,"四");
		FIELD_NUMBER_MAP.put(5,"五");
		FIELD_NUMBER_MAP.put(6,"六");
		FIELD_NUMBER_MAP.put(7,"七");
		FIELD_NUMBER_MAP.put(8,"八");
		FIELD_NUMBER_MAP.put(9,"九");
	}
	String alreadyReverseRecord;//棋譜に記載する成状況(既に成済)
	String nowReverseRecord;//棋譜に記載する成状況(この手番で成)
	int previousPositionX;//移動する駒の前の配置場所 x座標入力用
	String previousPosition;//移動する駒の前の配置場所
	int oneStepBackPositionXRecord;//1手前の棋譜x座標
	String oneStepBackPositionYRecord;//1手前の棋譜y座標
	String gameRecord = new String();//表示する棋譜
	
	JFrame reverseFlame = new JFrame();
	JLabel turnLabel = new JLabel();
	JLabel recordLabel = new JLabel();
	JButton waitButton=new JButton();
	DefaultListModel<String> gameRecordmodel = new DefaultListModel<String>();
	JList<String> gameRecordList = new JList<String>(gameRecordmodel);
	JScrollPane gameRecordScroll = new JScrollPane();
	ArrayList<Integer> allPieceRecodeList = new ArrayList<>(24000);
	ArrayList<Integer> oneStepBackPositionXRecordList = new ArrayList<>(150);
	ArrayList<String> oneStepBackPositionYRecordList = new ArrayList<>(150);
	
	
	
	//画面の設定
    protected Panel() {
    	setBackground(new Color(240, 170, 80));
    	addMouseListener(this);
    	add(waitButton);
    	wiatButtonAction();
    	add(turnLabel);
    	add(recordLabel);
    	gameRecordScroll.getViewport().setView(gameRecordList);
    	add(gameRecordScroll);
    }
	
    
    
    //待ったボタンの処理
    private void wiatButtonAction(){
		waitButton.addActionListener(e->{
			if(0 < allPieceRecodeList.size()) {
				//1手前のデータを呼び出す
				for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
					for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
						piecePlace[i][j] = 0;
					}
				}
				for(int i = PIECE_SUM; PIECE_ONE <= i; i--) {
					positionX[i] = allPieceRecodeList.get(allPieceRecodeList.size() - 4 - (40 - i) * 4);
					positionY[i] = allPieceRecodeList.get(allPieceRecodeList.size() - 3 - (40 - i) * 4);
					playerCode[i] = allPieceRecodeList.get(allPieceRecodeList.size() - 2 - (40 - i) * 4);
					reverseCode[i] = allPieceRecodeList.get(allPieceRecodeList.size() - 1 - (40 - i) * 4);
					piecePlace[positionX[i]][positionY[i]] = playerCode[i];
				}
				oneStepBackPositionXRecord = oneStepBackPositionXRecordList.get(oneStepBackPositionXRecordList.size() - 1);
				oneStepBackPositionYRecord = oneStepBackPositionYRecordList.get(oneStepBackPositionYRecordList.size() - 1);
				
				//リセット
				allPieceRecodeList.subList(allPieceRecodeList.size() - 160, allPieceRecodeList.size()).clear();
				gameRecordmodel.remove(gameRecordmodel.size() - 1);
				oneStepBackPositionXRecordList.remove(oneStepBackPositionXRecordList.size() - 1);
				oneStepBackPositionYRecordList.remove(oneStepBackPositionYRecordList.size() - 1);
				moveNumber--;
				nextTurn();
				resetMotionRange();
				pointX = pointY = holdTurnCode = holdPieceCode = 0;
				existsCheckmate = false;
				repaint();
			}
		});
	}
    
    
    
    //画面の描写
    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		motionRangeDraw(g);
		fieldDraw(g);
		piecesDraw(g);
		turnAndCheckmateDraw();
		setLableAndButton();
	}
	
	
	
	//操作処理
	@Override
	public void mouseClicked(MouseEvent e) {
		Point point = e.getPoint();
		pieceControl(point);
		repaint();
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	
	
	//駒の可動域描写
	private void motionRangeDraw(Graphics g) {
		g.setColor(new Color(255, 100, 100));
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
				if(existsPieceRange[i][j]) {//駒の可動域であれば表示
					g.fillRect(40 + i * 60, -40 + j * 60, 60, 60);
				}
			} 
		}
		g.setColor(new Color(255, 50, 50));
		if(!(pointX == HOLD || pointY == HOLD)){//フィード内選択時にはその駒を表示
			g.fillRect(40 + pointX * 60,-40 + pointY * 60, 60, 60);
		}else if(!(holdTurnCode == NO_PIECE||holdPieceCode == NO_PIECE)) {//手持ち選択時にはその駒を表示
			switch(holdTurnCode) {
			case 1:
				g.fillRect(650, 60 + holdPieceCode * 60, 60, 60);
				break;
			case 2:
				g.fillRect(20, 460 - holdPieceCode * 60, 60, 60);
				break;
			default:
				break;
			}
		}
	}
	
	
	
	//盤描写
	private void fieldDraw(Graphics g) {
		g.setColor(new Color(255, 50, 50));
		if(turnMyself == FIRST_TURN) {
			g.fillRect(100, 560, 540, 20);
		}else {
			g.fillRect(100, 0, 540, 20);
		}
		g.setColor(Color.BLACK);
		g.drawLine(20, 20, 20, 475);
		g.drawLine(20, 20, 90, 20);
		g.drawLine(20, 475, 90, 475);
		g.drawLine(90, 20, 90, 475);
		for(int i = 0; i <= FIELD_MAX_SIZE; i++) {
			g.drawLine(100, 20 + i * 60, 640, 20 + i * 60);
		}
		for(int i = 0; i <= FIELD_MAX_SIZE; i++) {
			g.drawLine(100 + i * 60, 20,100 + i * 60, 560);
		}
		g.drawLine(650, 560, 650, 105);
		g.drawLine(650, 560, 720, 560);
		g.drawLine(650, 105, 720, 105);
		g.drawLine(720, 560, 720, 105);
		
		
	}
	
	
	
	//駒描写
	private void piecesDraw(Graphics g) {
		//駒の画像読み込み
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
			if(KIN_LIST.contains(i)) {
				piece[i].setIcon(KIN_ICON[playerCode[i]][reverseCode[i]]);
			}else if(GIN_LIST.contains(i)) {
				piece[i].setIcon(GIN_ICON[playerCode[i]][reverseCode[i]]);
			}else if(KEI_LIST.contains(i)) {
				piece[i].setIcon(KEI_ICON[playerCode[i]][reverseCode[i]]);
			}else if(KYO_LIST.contains(i)) {
				piece[i].setIcon(KYO_ICON[playerCode[i]][reverseCode[i]]);
			}else if(GYOKU_LIST.contains(i)) {
				piece[i].setIcon(GYOKU_ICON[playerCode[i]][reverseCode[i]]);
			}else if(HISYA_LIST.contains(i)) {
				piece[i].setIcon(HISYA_ICON[playerCode[i]][reverseCode[i]]);
			}else if(KAKU_LIST.contains(i)) {
				piece[i].setIcon(KAKU_ICON[playerCode[i]][reverseCode[i]]);
			}else if(FU_LIST.contains(i)) {
				piece[i].setIcon(FU_ICON[playerCode[i]][reverseCode[i]]);
			}
		}
		
		//駒の表示
		for(int i = 0; i < 3; i++) {
			for(int j = HOLD_MIN_SIZE; j <= HOLD_MAX_SIZE; j++) {
				number[i][j] = 0;
			}
		}
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
			piece[i].setOpaque(false);
			if(positionX[i] == HOLD) {
				holdPieceNumber(i);
				piece[i].setBounds(placeX, placeY, 60, 60);
			}else {
				piece[i].setBounds(40 + positionX[i] * 60, -40 + positionY[i] * 60, 60, 60);
			}
			add(piece[i]);
		}
		
		//手持ち駒の枚数表示
		g.setFont(new Font("Arial", Font.BOLD, 25));
		for(int i = HOLD_MIN_SIZE; i <= HOLD_MAX_SIZE; i++) {
			if(1 < number[1][i]) {
				g.drawString(String.valueOf(number[1][i]), 700, 110 + i * 60);
			}
			if(1 < number[2][i]) {
				g.drawString(String.valueOf(number[2][i]), 70, 510 - i * 60);
			}
		}
	}
 	
	
	
	//手持ちの駒位置と枚数
	private void holdPieceNumber(int pieceCode) {
		placeX = (playerCode[pieceCode] == FIRST_TURN)? 650: 20;
		if(KIN_LIST.contains(pieceCode)) {
			number[playerCode[pieceCode]][3]++;
			placeY = (playerCode[pieceCode] == FIRST_TURN)? 240: 280;
		}else if(GIN_LIST.contains(pieceCode)) {
			number[playerCode[pieceCode]][4]++;
			placeY = (playerCode[pieceCode] == FIRST_TURN)? 300: 220;
		}else if(KEI_LIST.contains(pieceCode)) {
			number[playerCode[pieceCode]][5]++;
			placeY = (playerCode[pieceCode] == FIRST_TURN)? 360: 160;
		}else if(KYO_LIST.contains(pieceCode)) {
			number[playerCode[pieceCode]][6]++;
			placeY = (playerCode[pieceCode] == FIRST_TURN)? 420: 100;
		}else if(HISYA_LIST.contains(pieceCode)) {
			number[playerCode[pieceCode]][1]++;
			placeY = (playerCode[pieceCode] == FIRST_TURN)? 120: 400;
		}else if(KAKU_LIST.contains(pieceCode)) {
			number[playerCode[pieceCode]][2]++;
			placeY = (playerCode[pieceCode] == FIRST_TURN)? 180: 340;
		}else if(FU_LIST.contains(pieceCode)){
			number[playerCode[pieceCode]][7]++;
			placeY = (playerCode[pieceCode] == FIRST_TURN)? 480: 40;
		}
	}
	
	
	
    //先後の表示
    private void turnAndCheckmateDraw(){
    	if(existsCheckmate) {
    		turnLabel.setText("詰み");
    	}else {
    		switch(turnMyself) {
        	case 1:
        		turnLabel.setText("先手");
        		break;
        	case 2:
        		turnLabel.setText("後手");
        		break;
    		default:
    			break;
        	}
    	}
    	turnLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 40));
    	turnLabel.setBounds(650, 40, 100, 40);
    }
    
    
    
    //ラベルとボタンの設定
    private void setLableAndButton(){
    	//待ったボタンの設定
    	waitButton.setText("待った");
    	waitButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 19));
    	waitButton.setBounds(2,500,95,50);
    	waitButton.setBorderPainted(false);
    	waitButton.setFocusable(false);
    	
    	//棋譜ラベルの設定
    	recordLabel.setText("棋譜");
    	recordLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
    	recordLabel.setBounds(750,20,50,20);
    	
    	//棋譜の設定
    	gameRecordList.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
    	gameRecordScroll.setPreferredSize(new Dimension(170, 500));
    	gameRecordScroll.setBounds(750,50,170,500);
	}
    
    
    
    //駒操作
    private void pieceControl(Point point) {
		//クリックされた座標の特定
		canHold = false;
		pointX = pointY = 0;
		detectFieldPosition(point);
		if(pointX == NO_PIECE||pointY == NO_PIECE) {
			detectHoldPosition(point);
		}
		
		//自分の駒が選択されれば、その駒の可動域を特定
		if(canHold) {//手持ち駒選択
			resetMotionRange();
			detectholdPiece();
			holdMotionRange(holdPieceCode);
		}else if(!(pointX == HOLD) && !(pointY == HOLD) && piecePlace[pointX][pointY] == turnMyself) {//フィールド駒選択
			resetMotionRange();
			detectFieldPiece();
			fieldMotionRnage(activePiece);
			
		//駒の可動域内が選択されれば、駒の移動
		}else if(existsPieceRange[pointX][pointY]) {
			//盤面の記録
			allGameRecord();
			
			//移動マスに相手の駒があれば取る
			if(!(piecePlace[pointX][pointY] == NO_PIECE)) {
				pieceGet();
			}
			
			//成条件を満たしていれば成判定
			if(reverseCode[activePiece] == REVERSE_FALSE && !(positionY[activePiece] == HOLD)) {
				if((playerCode[activePiece] == FIRST_TURN  &&  positionY[activePiece] < 4)
				    ||  (playerCode[activePiece] == FIRST_TURN && pointY < 4)
				      ||  (playerCode[activePiece] == LAST_TURN && 6 < positionY[activePiece])
				        ||  (playerCode[activePiece] == LAST_TURN && 6 < pointY)) {
					reverseCheck();
				}
			}
			
			//駒の移動
			gameRecordDraw();
			pieceMove();
			
			//次の番へ
			nextTurn();
			resetMotionRange();
			pointX = pointY = holdTurnCode = holdPieceCode = 0;
			searchCheckmate();
			
		//自分の駒もしくは可動域内がクリックされなければ、リセット
		}else {
			resetMotionRange();
			pointX = pointY = holdTurnCode = holdPieceCode = 0;
		}
	}
	
	
	
	//クリック位置の特定(手持ち)
	private void detectHoldPosition(Point point) {
		switch(turnMyself) {
		case 1:
			searchRange  =  ValueRange.of(650, 710);
		    if(searchRange.isValidIntValue(point.x)){
		    	holdTurnCode = FIRST_TURN;
		    }
		    for(int i = HOLD_MIN_SIZE; i <= HOLD_MAX_SIZE; i++) {
		    	searchRange = ValueRange.of(60 + i * 60, 120 + i * 60);
		    	if(searchRange.isValidIntValue(point.y)){
		    		holdPieceCode = i;
			    	break;
			    }
		    }
			break;
		case 2:
	    	searchRange = ValueRange.of(20, 80);
		    if(searchRange.isValidIntValue(point.x)){
		    	holdTurnCode = LAST_TURN;
		    }
		    for(int i = HOLD_MIN_SIZE; i <= HOLD_MAX_SIZE; i++) {
		    	searchRange = ValueRange.of(460 - i * 60, 520 - i * 60);
		    	if(searchRange.isValidIntValue(point.y)){
		    		holdPieceCode = i;
			    	break;
			    }
		    }
		    break;
		default:
			break;
		}
	    if(!(holdTurnCode == NO_PIECE) && !(holdPieceCode == NO_PIECE) && 0 < number[holdTurnCode][holdPieceCode]) {
	    	canHold = true;
	    }
	}
	
	
	
	//クリック位置の特定(盤面)
	private void detectFieldPosition(Point point) {
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			searchRange = ValueRange.of(40 + 60 * i, 100 + 60 * i);
		    if(searchRange.isValidIntValue(point.x)){
		    	pointX = i;
		    	break;
		    }
		}
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			searchRange = ValueRange.of( - 40 + 60 * i, 20 + 60 * i);
		    if(searchRange.isValidIntValue(point.y)){
		    	pointY = i;
		    	break;
		    }
		}
	}
	
	
	
	//駒の特定(手持ち)
	private void detectholdPiece() {
		switch(holdPieceCode) {
		case 1:
			for(int i: HISYA_LIST) {
				if(positionX[i] == HOLD && playerCode[i] == holdTurnCode) {
					activePiece = i;
					break;
				}
			}
			break;
		case 2:
			for(int i: KAKU_LIST) {
				if(positionX[i] == HOLD && playerCode[i] == holdTurnCode) {
					activePiece = i;
					break;
				}
			}
			break;
		case 3:
			for(int i: KIN_LIST) {
				if(positionX[i] == HOLD && playerCode[i] == holdTurnCode) {
					activePiece = i;
					break;
				}
			}
			break;
		case 4:
			for(int i: GIN_LIST) {
				if(positionX[i] == HOLD && playerCode[i] == holdTurnCode) {
					activePiece = i;
					break;
				}
			}
			break;
		case 5:
			for(int i: KEI_LIST) {
				if(positionX[i] == HOLD && playerCode[i] == holdTurnCode) {
					activePiece = i;
					break;
				}
			}
			break;
		case 6:
			for(int i: KYO_LIST) {
				if(positionX[i] == HOLD && playerCode[i] == holdTurnCode) {
					activePiece = i;
					break;
				}
			}
			break;
		case 7:
			for(int i: FU_LIST) {
				if(positionX[i] == HOLD && playerCode[i] == holdTurnCode) {
					activePiece = i;
					break;
				}
			}
			break;
		default:
			break;
		}
	}
	
	
	
	//駒の特定(盤面)
	private void detectFieldPiece() {
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
			if(pointX == positionX[i] && pointY == positionY[i]) {
				activePiece = i;
				break;
			}
		}
	}
	
	
	
	//手持ち駒の移動範囲
	private void holdMotionRange(int pieceCode) {
		//二歩の確認
		if(pieceCode == 7) {
			for(int i: FU_LIST) {
				if(!(positionX[i] == HOLD) && playerCode[i] == turnMyself && reverseCode[i] == REVERSE_FALSE) {
					existsDoubleFu[positionX[i]] = true;
				}
			}
		}
		
		//駒を置ける範囲の特定
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			if(!(existsDoubleFu[i])) {
				for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
					if(pieceCode == 5 && turnMyself == FIRST_TURN && j < 3) {//桂馬は後方2行へ置けない
						continue;
					}else if((pieceCode == 6 || pieceCode == 7) && turnMyself == FIRST_TURN && j < 2) {//香車と歩は後方1行へ置けない
						continue;
					}else if(pieceCode == 5 && turnMyself == LAST_TURN && 7 < j) {//桂馬は後方2行へ置けない
						continue;
					}else if((pieceCode == 6 || pieceCode == 7) && turnMyself == LAST_TURN && 8 < j) {//香車と歩は後方1行へ置けない
						continue;
					}else if(piecePlace[i][j] == NO_PIECE) {//上記以外で駒未配置位置には移動可能
						existsPieceRange[i][j] = true;
					}
				}
			}
		}
		
		//打ち歩詰め確認(歩が選択され、相手玉の前に置けるなら)
		if(pieceCode == 7 && existsPieceRange[positionX[11 - turnMyself]][positionY[11 - turnMyself] + 1 - (turnMyself - 1) * 2]) {
			fuCheckmate();
		}
		
		//配置により王手が解除されるか
		checkLock(0);
	}
	
	
	
	//盤面駒の移動範囲
	private void fieldMotionRnage(int pieceNumber){
		searchMotionRange(pieceNumber, positionX[pieceNumber], positionY[pieceNumber], playerCode[pieceNumber]
				,existsPieceRange, piecePlace);
		
		//移動により王手が解除されるか
		checkLock(pieceNumber);
	}
	
	
	
	//打ち歩詰め禁止
	private void fuCheckmate(){
		resetProvisionalBoard();
		
		//仮に歩を打った際の盤面
		provisionalPiecePlace[positionX[11 - turnMyself]][positionY[11 - turnMyself] + 1 - (turnMyself - 1) * 2] = turnMyself;
		nextTurn();
		
		//相手玉の可動域
		searchMotionRange(11 - turnMyself, positionX[11 - turnMyself], positionY[11 - turnMyself], playerCode[11 - turnMyself]
						, existsProvisionalPieceRange, provisionalPiecePlace);
		
		//全自駒の可動域
		for(int all = PIECE_ONE; all <= PIECE_SUM; all++) {
			if(!(positionX[all] == HOLD) && !(positionY[all] == HOLD) && playerCode[all] == turnMyself) {
				searchMotionRange(all, positionX[all], positionY[all], turnMyself
								, existsProvisionalAllPieceRange, provisionalPiecePlace);
			}
		}
		
		//玉は相手駒が効いてるマスへ移動不可
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
				if(existsProvisionalAllPieceRange[i][j] && existsProvisionalPieceRange[i][j]) {
					existsProvisionalPieceRange[i][j] = false;
				}
			}
		}
		
		//相手玉の可動域があるか確認
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
				if(existsProvisionalPieceRange[i][j]) {
					existsFuCheckmate = false;
					break;
				}
			}
		}
		
		//歩を玉以外の駒で取れるか
		nextTurn();
		for(int all = PIECE_ONE; all <= PIECE_SUM; all++) {
			if(!(positionX[all] == HOLD) && !(positionY[all] == HOLD) && playerCode[all] == turnOpponent && !(all == 11 - turnMyself)) {
				searchMotionRange(all, positionX[all], positionY[all], turnOpponent
								, existsProvisionalAllPieceRangeSecond, provisionalPiecePlace);
			}
		}
		if(existsProvisionalAllPieceRangeSecond[positionX[11 - turnMyself]][positionY[11 - turnMyself] + 1 - (turnMyself - 1) * 2]) {
			existsFuCheckmate = false;
		}
		
		//相手玉の可動域がないなら打ち歩詰め禁止
		if(existsFuCheckmate) {
			existsPieceRange[positionX[11 - turnMyself]][positionY[11 - turnMyself] + 1 - (turnMyself - 1) * 2] = false;
		}
	}
	
	
	
	//選択駒を使って王手解除可能か確認
	private void checkLock(int pieceNumber){
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
				if(existsPieceRange[i][j]) {
					resetProvisionalBoard();
					
					//仮に選択駒を打った際の盤面
					provisionalPiecePlace[i][j] = turnMyself;
					if(!(positionX[pieceNumber] == HOLD)) {
						provisionalPiecePlace[positionX[pieceNumber]][positionY[pieceNumber]] = NO_PIECE;
						for(int k = PIECE_ONE; k <= PIECE_SUM; k++) {
							if(i == positionX[k] && j == positionY[k]) {
								provisionalPositionX[k] = provisionalPositionY[k] = HOLD;
							}
						}
					}
					if(GYOKU_LIST.contains(pieceNumber)) {
						provisionalGyokuPositionX = i;
						provisionalGyokuPositionY = j;
					}
					
					//相手の全フィード駒の可動域調査
					for(int all = PIECE_ONE; all <= PIECE_SUM; all++) {
						if(!(provisionalPositionX[all] == HOLD) && !(provisionalPositionY[all] == HOLD) && playerCode[all] == turnOpponent) {
							searchMotionRange(all, provisionalPositionX[all], provisionalPositionY[all], turnOpponent
											, existsProvisionalAllPieceRange, provisionalPiecePlace);
						}
					}
					
					//自玉が相手駒の可動域内にいるか確認
					if(existsProvisionalAllPieceRange[provisionalGyokuPositionX][provisionalGyokuPositionY]) {
						existsPieceRange[i][j] = false;
					}
				}
			}
		}
	}
	
	
	
	//クリックした駒や王手調査時の駒の可動域
	private void searchMotionRange(int pieceNumber, int provisionalX, int provisionalY, int turn
								  , boolean[][] existsRange, int[][] cell){
		defaultPieceMotionRange(pieceNumber, turn);
		
		//周囲8方向への可動域
		for(int i = 0; i < 8; i++) {
			if(!(defaultMotionRange[i] == 0)) {//周囲8方向へ移動可能かどうか
				for(int j = 1; j <= defaultMotionRange[i]; j++) {
					switch(i){
					case 0:
						direction[0] = provisionalX;
						direction[1] = provisionalY - j;
						break;
					case 1:
						direction[0] = provisionalX + j;
						direction[1] = provisionalY - j;
						break;
					case 2:
						direction[0] = provisionalX + j;
						direction[1] = provisionalY;
						break;
					case 3:
						direction[0] = provisionalX + j;
						direction[1] = provisionalY + j;
						break;
					case 4:
						direction[0] = provisionalX;
						direction[1] = provisionalY + j;
						break;
					case 5:
						direction[0] = provisionalX - j;
						direction[1] = provisionalY + j;
						break;
					case 6:
						direction[0] = provisionalX - j;
						direction[1] = provisionalY;
						break;
					case 7:
						direction[0] = provisionalX - j;
						direction[1] = provisionalY - j;
						break;
					default:
						break;
					}
					if(FIELD_MIN_SIZE <= direction[0] && direction[0] <= FIELD_MAX_SIZE 
							&& FIELD_MIN_SIZE <= direction[1] && direction[1] <= FIELD_MAX_SIZE) {//フィード内であるか
						if(cell[direction[0]][direction[1]] == NO_PIECE) {//移動先に駒がない
							existsRange[direction[0]][direction[1]] = true;
						}else if(cell[direction[0]][direction[1]] == turn){//移動先に自駒がある
							if(!(turn == turnMyself)) {//自駒移動時は移動不可、相手駒可動域調査時は移動可能
								existsRange[direction[0]][direction[1]] = true;
							}
							break;
						}else {//移動先に相手駒がある
							existsRange[direction[0]][direction[1]] = true;
							break;
						}
					}else {
						break;
					}
				}
			}
		}
		
		//桂馬の可動域
		if(existsKeima) {
			switch(turn) {
			case 1:
				direction[0] = provisionalX - 1;
				direction[1] = provisionalY - 2;
				direction[2] = provisionalX + 1;
				direction[3] = provisionalY - 2;
				break;
			case 2:
				direction[0] = provisionalX - 1;
				direction[1] = provisionalY + 2;
				direction[2] = provisionalX + 1;
				direction[3] = provisionalY + 2;
				break;
			default:
				break;
			}
			for(int i = 0; i < 4; i+= 2) {
				if(FIELD_MIN_SIZE <= direction[i] && direction[i] <= FIELD_MAX_SIZE 
						&& FIELD_MIN_SIZE <= direction[i + 1] && direction[i + 1] <= FIELD_MAX_SIZE) {//フィード内であるか
					if(!(cell[direction[i]][direction[i + 1]] == turn)) {//移動先に自駒がない
						existsRange[direction[i]][direction[i + 1]] = true;
					}else if(!(turn == turnMyself)) {//相手駒可動域調査時は移動可能
						existsRange[direction[i]][direction[i + 1]] = true;
					}
				}
			}
		}
	}
	
	
	
	//全盤面の記録
	private void allGameRecord(){
		oneStepBackPositionXRecordList.add(oneStepBackPositionXRecord);
		oneStepBackPositionYRecordList.add(oneStepBackPositionYRecord);
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
			allPieceRecodeList.add(positionX[i]);
			allPieceRecodeList.add(positionY[i]);
			allPieceRecodeList.add(playerCode[i]);
			allPieceRecodeList.add(reverseCode[i]);
		}
	}
	
	
	
	//駒を取る
	private void pieceGet() {
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
			if(pointX == positionX[i] && pointY == positionY[i]) {
				positionX[i] = positionY[i] = HOLD;
				playerCode[i] = turnMyself;
				reverseCode[i] = REVERSE_FALSE;
				break;
			}
		}
	}
	
	
	
	//成込み確認
	private void reverseCheck() {
		//強制成込み条件確認
		onlyReverse[0] = 0;
		onlyReverse[1] = 0;
		if(KEI_LIST.contains(activePiece)) {
			onlyReverse[0] = (turnMyself == FIRST_TURN)? 1: 9;
			onlyReverse[1] = (turnMyself == FIRST_TURN)? 2: 8;
		}else if(KYO_LIST.contains(activePiece) || FU_LIST.contains(activePiece)) {
			onlyReverse[0] = (turnMyself == FIRST_TURN)? 1: 9;
		}
		
		//成込み処理(玉 金はエラーを起こすことで、成込み確認を無効にしている)
		if(onlyReverse[0] == pointY || onlyReverse[1] == pointY) {
			reverseCode[activePiece] = REVERSE_TRUE;
			extstsNowReverse = true;
		}else{
			try {
				defaultReverseIcon();
				dialogResult = showOptionDialog(reverseFlame,"駒を成りますか","成込み確認",
						DEFAULT_OPTION,PLAIN_MESSAGE,null,reverseIcon,null);
				reverseCode[activePiece] = (dialogResult <= 0)? REVERSE_FALSE: REVERSE_TRUE;
				extstsNowReverse = (dialogResult <= 0)? false: true;
				extstsNoReverse = (dialogResult <= 0)? true: false;
			}catch(Exception e) {
			}
		}
	}
	
	
	
	//棋譜の作成
	private void gameRecordDraw(){
		//前処理
		moveNumber++;
		alreadyReverseRecord = "";
		
		//表示する棋譜の作成
		turnRecord = (turnMyself == FIRST_TURN)? "▲": "△";
		positionXRecord = 10 - pointX;
		positionYRecord = FIELD_NUMBER_MAP.get(pointY);
		defaultPieceName();
		if(extstsNowReverse) {
			nowReverseRecord = "成";
		}else if(extstsNoReverse) {
			nowReverseRecord = "不成";
		}else {
			nowReverseRecord = "";
		}
		previousPositionX = 10 - positionX[activePiece];
		if(previousPositionX == 10 && positionY[activePiece] == HOLD) {
			previousPosition = "打";
		}else {
			previousPosition = "(" + previousPositionX + positionY[activePiece] + ")";
		}
		if(oneStepBackPositionXRecord == positionXRecord && oneStepBackPositionYRecord.equals(positionYRecord)) {
			gameRecord = moveNumber + ": 同" + alreadyReverseRecord + pieceNameRecord + nowReverseRecord + previousPosition;
		}else {
			gameRecord = moveNumber + ": " + turnRecord + positionXRecord + positionYRecord
					+ alreadyReverseRecord + pieceNameRecord + nowReverseRecord + previousPosition;
		}
		gameRecordmodel.addElement(new String(gameRecord));
		
		//後処理
		oneStepBackPositionXRecord = positionXRecord;
		oneStepBackPositionYRecord = positionYRecord;
		extstsNowReverse = false;
		extstsNoReverse =false;
		gameRecordList.ensureIndexIsVisible(moveNumber - 1);
	}
	
	
	
	//駒の移動
	private void pieceMove() {
		piecePlace[pointX][pointY] = playerCode[activePiece];
		piecePlace[positionX[activePiece]][positionY[activePiece]] = NO_PIECE;
		positionX[activePiece] = pointX;
		positionY[activePiece] = pointY;
	}
	
	
	
	//詰み確認
    private void searchCheckmate(){
    	existsCheckmate = true;
		//自分の全フィールド駒の可能域を調べ、1つでも可動域をもつ駒があれば詰まない
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
    		if(playerCode[i] == turnMyself && !(positionX[i] == HOLD) && !(positionY[i] == HOLD)) {
    			fieldMotionRnage(i);
    			searchHaveOneRange();
    			resetMotionRange();
    		}
    		if(!existsCheckmate) {
				break;
			}
    	}
		
		//自分の全手持ち駒を調べ、1つでも可動域をもつ駒があれば詰まない
		if(existsCheckmate) {
			for(int i = HOLD_MIN_SIZE; i <= HOLD_MAX_SIZE; i++) {
				if(!(number[turnMyself][i] == 0)) {
					holdMotionRange(i);
					searchHaveOneRange();
	    			resetMotionRange();
				}
				if(!existsCheckmate) {
					break;
				}
			}
		}
    }
    
    
    
    //1つでも可動域を持っているか確認
    private void searchHaveOneRange(){
    	for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
				if(existsPieceRange[i][j]) {
					existsCheckmate = false;
					break;
				}
			}
			if(!existsCheckmate) {
				break;
			}
		}
    }
    
    
    
	//駒の可動域リセット
	private void resetMotionRange() {
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
				existsPieceRange[i][j] = false;
			}
			existsDoubleFu[i] = false;
		}
	}
	
	
	
	//仮盤面のリセット
	private void resetProvisionalBoard(){
		existsFuCheckmate = true;
		provisionalGyokuPositionX = positionX[8 + turnMyself];
		provisionalGyokuPositionY = positionY[8 + turnMyself];
		for(int i = FIELD_MIN_SIZE; i <= FIELD_MAX_SIZE; i++) {
			for(int j = FIELD_MIN_SIZE; j <= FIELD_MAX_SIZE; j++) {
				provisionalPiecePlace[i][j] = piecePlace[i][j];
				existsProvisionalPieceRange[i][j] = false;
				existsProvisionalAllPieceRange[i][j] = false;
				existsProvisionalAllPieceRangeSecond[i][j] = false;
			}
		}
		for(int i = PIECE_ONE; i <= PIECE_SUM; i++) {
			provisionalPositionX[i] = positionX[i];
			provisionalPositionY[i] = positionY[i];
		}
	}
	
	
	
	//次の手番へ
	private void nextTurn(){
		turnMyself = (turnMyself == FIRST_TURN)? LAST_TURN: FIRST_TURN;
		turnOpponent = (turnOpponent == FIRST_TURN)? LAST_TURN: FIRST_TURN;
	}
	
	
	
	//駒の可動域設定(defaultMotionRange[0]を12時方向とし、以下時計回りに番号を振る)
	private void defaultPieceMotionRange(int pieceNumber, int turn){
		existsKeima = false;
		if(GYOKU_LIST.contains(pieceNumber)) {
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 1; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 1;							; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 1; defaultMotionRange[3] = 1;
		}else if(KIN_LIST.contains(pieceNumber)) {
			if(turn == FIRST_TURN) {
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 1; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 1; defaultMotionRange[3] = 0;
			}else{
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 1; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 1; defaultMotionRange[3] = 1;
			}
		}else if(GIN_LIST.contains(pieceNumber)) {
			if(turn == FIRST_TURN && reverseCode[pieceNumber] == REVERSE_FALSE) {
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 1; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 0; defaultMotionRange[3] = 1;
			}else if(turn == LAST_TURN && reverseCode[pieceNumber] == REVERSE_FALSE) {
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 0; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 1; defaultMotionRange[3] = 1;
			}else if(turn == FIRST_TURN && reverseCode[pieceNumber] == REVERSE_TRUE) {
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 1; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 1; defaultMotionRange[3] = 0;
			}else{
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 1; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 1; defaultMotionRange[3] = 1;
			}
		}else if(KEI_LIST.contains(pieceNumber)) {
			if(turn == FIRST_TURN && reverseCode[pieceNumber] == REVERSE_FALSE) {
				existsKeima = true;
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 0; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 0; defaultMotionRange[3] = 0;
			}else if(turn == LAST_TURN && reverseCode[pieceNumber] == REVERSE_FALSE) {
				existsKeima = true;
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 0; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 0; defaultMotionRange[3] = 0;
			}else if(turn == FIRST_TURN && reverseCode[pieceNumber] == REVERSE_TRUE) {
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 1; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 1; defaultMotionRange[3] = 0;
			}else{
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 1; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 1; defaultMotionRange[3] = 1;
			}
		}else if(KYO_LIST.contains(pieceNumber)) {
			if(turn == FIRST_TURN && reverseCode[pieceNumber] == REVERSE_FALSE) {
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 8; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 0; defaultMotionRange[3] = 0;
			}else if(turn == LAST_TURN && reverseCode[pieceNumber] == REVERSE_FALSE) {
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 0; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 8; defaultMotionRange[3] = 0;
			}else if(turn == FIRST_TURN && reverseCode[pieceNumber] == REVERSE_TRUE) {
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 1; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 1; defaultMotionRange[3] = 0;
			}else{
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 1; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 1; defaultMotionRange[3] = 1;
			}
		}else if(HISYA_LIST.contains(pieceNumber)) {
			if(reverseCode[pieceNumber] == REVERSE_FALSE) {
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 8; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 8;						    ; defaultMotionRange[2] = 8;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 8; defaultMotionRange[3] = 0;
			}else{
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 8; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 8;						    ; defaultMotionRange[2] = 8;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 8; defaultMotionRange[3] = 1;
			}
		}else if(KAKU_LIST.contains(pieceNumber)) {
			if(reverseCode[pieceNumber] == REVERSE_FALSE) {
				defaultMotionRange[7] = 8; defaultMotionRange[0] = 0; defaultMotionRange[1] = 8;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 8; defaultMotionRange[4] = 0; defaultMotionRange[3] = 8;
			}else{
				defaultMotionRange[7] = 8; defaultMotionRange[0] = 1; defaultMotionRange[1] = 8;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 8; defaultMotionRange[4] = 1; defaultMotionRange[3] = 8;
			}
		}else if(FU_LIST.contains(pieceNumber)){
			if(turn == FIRST_TURN && reverseCode[pieceNumber] == REVERSE_FALSE) {
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 1; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 0; defaultMotionRange[3] = 0;
			}else if(turn == LAST_TURN && reverseCode[pieceNumber] == REVERSE_FALSE) {
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 0; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 0;						    ; defaultMotionRange[2] = 0;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 1; defaultMotionRange[3] = 0;
			}else if(turn == FIRST_TURN && reverseCode[pieceNumber] == REVERSE_TRUE) {
				defaultMotionRange[7] = 1; defaultMotionRange[0] = 1; defaultMotionRange[1] = 1;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 0; defaultMotionRange[4] = 1; defaultMotionRange[3] = 0;
			}else{
				defaultMotionRange[7] = 0; defaultMotionRange[0] = 1; defaultMotionRange[1] = 0;
				defaultMotionRange[6] = 1;						    ; defaultMotionRange[2] = 1;
				defaultMotionRange[5] = 1; defaultMotionRange[4] = 1; defaultMotionRange[3] = 1;
			}
		}
	}
	
	
	
	//成込み時のアイコン
	private void defaultReverseIcon() {
		if(GYOKU_LIST.contains(activePiece) || KIN_LIST.contains(activePiece)) {
			reverseIcon[0] = KIN_ICON[0][0];
			reverseIcon[1] = KIN_ICON[0][0];
		}else if(GIN_LIST.contains(activePiece)) {
			reverseIcon[0] = GIN_ICON[1][0];
			reverseIcon[1] = GIN_ICON[1][1];
		}else if(KEI_LIST.contains(activePiece)) {
			reverseIcon[0] = KEI_ICON[1][0];
			reverseIcon[1] = KEI_ICON[1][1];
		}else if(KYO_LIST.contains(activePiece)) {
			reverseIcon[0] = KYO_ICON[1][0];
			reverseIcon[1] = KYO_ICON[1][1];
		}else if(HISYA_LIST.contains(activePiece)) {
			reverseIcon[0] = HISYA_ICON[1][0];
			reverseIcon[1] = HISYA_ICON[1][1];
		}else if(KAKU_LIST.contains(activePiece)) {
			reverseIcon[0] = KAKU_ICON[1][0];
			reverseIcon[1] = KAKU_ICON[1][1];
		}else if(FU_LIST.contains(activePiece)){
			reverseIcon[0] = FU_ICON[1][0];
			reverseIcon[1] = FU_ICON[1][1];
		}
	}
	
	
	
	//駒の名前
	private void defaultPieceName() {
		if(GYOKU_LIST.contains(activePiece)) {
			pieceNameRecord = "玉";
		}else if(KIN_LIST.contains(activePiece)) {
			pieceNameRecord = "金";
		}else if(GIN_LIST.contains(activePiece)) {
			pieceNameRecord = "銀";
			if(!extstsNowReverse && reverseCode[activePiece] == REVERSE_TRUE) {
				alreadyReverseRecord = "成";
			}
		}else if(KEI_LIST.contains(activePiece)) {
			pieceNameRecord = "桂";
			if(!extstsNowReverse && reverseCode[activePiece] == REVERSE_TRUE) {
				alreadyReverseRecord = "成";
			}
		}else if(KYO_LIST.contains(activePiece)) {
			pieceNameRecord = "香";
			if(!extstsNowReverse && reverseCode[activePiece] == REVERSE_TRUE) {
				alreadyReverseRecord = "成";
			}
		}else if(HISYA_LIST.contains(activePiece)) {
			pieceNameRecord = "飛";
			if(!extstsNowReverse && reverseCode[activePiece] == REVERSE_TRUE) {
				pieceNameRecord = "龍";
			}
		}else if(KAKU_LIST.contains(activePiece)) {
			pieceNameRecord = "角";
			if(!extstsNowReverse && reverseCode[activePiece] == REVERSE_TRUE) {
				pieceNameRecord = "馬";
			}
		}else if(FU_LIST.contains(activePiece)) {
			pieceNameRecord = "歩";
			if(!extstsNowReverse && reverseCode[activePiece] == REVERSE_TRUE) {
				pieceNameRecord = "と";
			}
		}
	}
}