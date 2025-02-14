package towerdefense;

import static javax.swing.JOptionPane.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

//♦ゲームスタート画面♦

//ステージ選択画面
public class TowerDefense{
	public static void main(String[] args) {
        JFrame mainFrame = new JFrame("タワーディフェンス");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 200);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.add(new MainPanel());
        mainFrame.setVisible(true);
	}
}

class MainPanel extends JPanel{
	JLabel selectCommentLabel = new JLabel();
	JButton stageSelectButton1 = new JButton();
	JButton stageSelectButton2 = new JButton();
	JButton stageSelectButton3 = new JButton();
	Font font = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	
	//画面の設定
    protected MainPanel() {
    	setBackground(new Color(240, 170, 80));
    	add(selectCommentLabel);
    	add(stageSelectButton1);
    	add(stageSelectButton2);
    	add(stageSelectButton3);
    	buttonAction();
     }
	
    //画面の描写
    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		comment();
		buttonSet();
    }
    
    //ステージ選択
    private void buttonAction() {
    	stageSelectButton1.addActionListener(e->{
    		new Stage1Data().importData();
    		new StageFrame();
    	});
    	stageSelectButton2.addActionListener(e->{
    		showMessageDialog(null,"現在調整中");
    	});
    	stageSelectButton3.addActionListener(e->{
    		showMessageDialog(null,"現在調整中");
    	});
    }
    
    //表示コメント
    private void comment() {
    	selectCommentLabel.setText("ステージを選択してください");
    	selectCommentLabel.setFont(font);
    	selectCommentLabel.setBounds(20,20,300,20);
    }
    
    //ボタン設定
    private void buttonSet() {
    	stageSelectButton1.setText("Stage1");
    	stageSelectButton1.setFont(font);
    	stageSelectButton1.setBounds(20,50,100,100);
    	stageSelectButton1.setFocusable(false);
    	
    	stageSelectButton2.setText("Stage2");
    	stageSelectButton2.setFont(font);
    	stageSelectButton2.setBounds(140,50,100,100);
    	stageSelectButton2.setFocusable(false);
    	
    	stageSelectButton3.setText("Stage3");
    	stageSelectButton3.setFont(font);
    	stageSelectButton3.setBounds(260,50,100,100);
    	stageSelectButton3.setFocusable(false);
    }
}



//♦ゲーム処理♦

//ゲーム画面表示
class StageFrame {
	protected StageFrame(){
		JFrame gameFrame = new JFrame("Stage1");
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setSize(1235, 600);
		gameFrame.setResizable(false);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.add(new StagePanel());
		gameFrame.setVisible(true);
	}
}

class StagePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener{
	AttackJudgment AttackJudgment = new AttackJudgment();
	UnitOperation UnitOperation = new UnitOperation();
	CorrectionStatus CorrectionStatus = new CorrectionStatus();
	JLabel categoryLabel[] = new JLabel[8];
	JLabel actionCommentLabel = new JLabel();
	JLabel residueEnemyLabel = new JLabel();
	JButton rangeDrawButton = new JButton();
	JButton pauseButton = new JButton();
	
	Timer timer;
	long gameTime;
	List<Boolean> existsEnemyMotionTimerList = new ArrayList<>();
	List<Boolean> existsEnemyMoveTimerList = new ArrayList<>();
	static List<Boolean> existsSoldierMotionTimerList = new ArrayList<>();
	
	List<List<Integer>> mixNearUnitPlacementList = new ArrayList<>();
	List<List<Integer>> mixFarUnitPlacementList = new ArrayList<>();
	List<List<Integer>> mixAllUnitPlacementList = new ArrayList<>();
	
	static List<Boolean> existsActiveEnemyList = new ArrayList<>();
	static List<BufferedImage> enemyImageList = new ArrayList<>();
	static List<List<Integer>> enemyStatusList = new ArrayList<>();
	static List<List<Integer>> enemyPlacementList = new ArrayList<>();
	static List<EnemyMotion> EnemyMotionList = new ArrayList<>();
	static List<EnemyMove> EnemyMoveList = new ArrayList<>();
	static List<Integer> blockList = new ArrayList<>();
	static int residueEnemy;
	
	static List<Boolean> existsActiveSoldierList = new ArrayList<>();
	static List<BufferedImage> soldierImageList = new ArrayList<>();
	static List<List<Integer>> soldierStatusList = new ArrayList<>();
	static List<List<Integer>> initialSoldierStatusList = new ArrayList<>();
	static List<List<Integer>> correctionSoldierStatusList = new ArrayList<>();
	static List<List<Integer>> soldierPlacementList = new ArrayList<>();
	static List<SoldierMotion> SoldierMotionList = new ArrayList<>();
	
	final static int UNIT_SIZE = 60;
	final static int CORRECTION_POSITION = 15;
	
	static List<Hit> HitList = new ArrayList<>();
	static int hitNumber;
	static double soldierMorale = 100;
	static double enemyMorale = 100;
	double moraleDifference;
	
	int mouseX;
	int mouseY;
	int unitNumber;
	int target;
	
	boolean existsRangeDisplay = true;
	boolean canPause;
	boolean canSelect;
	boolean existsGameOver;
	
	//初期設定取り込み
    protected StagePanel() {
    	timer = new Timer(10, this);
		timer.start();
    	addMouseListener(this);
    	addMouseMotionListener(this);
    	setBackground(new Color(240, 170, 80));
    	for(int i = 0; i < categoryLabel.length; i++) {
    		categoryLabel[i] = new JLabel();
    		add(categoryLabel[i]);
    	}
    	add(residueEnemyLabel);
    	add(actionCommentLabel);
    	add(rangeDrawButton);
    	rangeDrawButton.addActionListener(e->{
    		existsRangeDisplay = (existsRangeDisplay)? false: true;
    	});
    	add(pauseButton);
    	pauseButton.addActionListener(e->{
    		canPause = true;
    		pause();
    		showMessageDialog(null,"一時停止中");
    		canPause = false;
    		restart();
    	});
		mixNearUnitPlacementList = Stream.concat(StageData.nearUnitPlacementList.stream(), StageData.allUnitPlacementList.stream())
				.collect(Collectors.toList());
		mixFarUnitPlacementList = Stream.concat(StageData.farUnitPlacementList.stream(), StageData.allUnitPlacementList.stream())
				.collect(Collectors.toList());
		mixAllUnitPlacementList = Stream.concat(mixNearUnitPlacementList.stream(), StageData.farUnitPlacementList.stream())
				.collect(Collectors.toList());
		residueEnemy = StageData.enemyList.size();
		for(int i = 0; i < StageData.enemyList.size(); i ++) {
			existsActiveEnemyList.add(false);
			enemyImageList.add(EnemyData.ENEMY_IMAGE_LIST.get(StageData.enemyList.get(i).get(0) * 2));
			enemyStatusList.add(new ArrayList<>(EnemyData.ENEMY_STATUS_LIST.get(StageData.enemyList.get(i).get(0))));
			enemyPlacementList.add(new ArrayList<>(StageData.moveList.get(StageData.enemyList.get(i).get(1)).get(0)));
			EnemyMoveList.add(new EnemyMove(i));
			EnemyMotionList.add(new EnemyMotion(i));
			blockList.add(-1);
		}
		for(int i = 0; i < StageData.facilityPlacementList.size(); i++) {
			existsActiveSoldierList.add(true);
			if(i == 0) {
				soldierImageList.add(SoldierData.CASTLE_IMAGE.get(0));
				soldierStatusList.add(new ArrayList<>(SoldierData.CASTLE_STATUS));
				initialSoldierStatusList.add(new ArrayList<>(SoldierData.CASTLE_STATUS));
			}else {
				soldierImageList.add(SoldierData.GATE_IMAGE.get(0));
				soldierStatusList.add(new ArrayList<>(SoldierData.GATE_STATUS));
				initialSoldierStatusList.add(new ArrayList<>(SoldierData.GATE_STATUS));
			}
			correctionSoldierStatusList.add(Arrays.asList(0, 0, 0, 0, 0, 0, 0));
			soldierPlacementList.add(new ArrayList<>(StageData.facilityPlacementList.get(i)));
			SoldierMotionList.add(new SoldierMotion(i, 0));
		}
		for(int i = 0; i < 100; i++) {
			HitList.add(new Hit());
		}
    }
    
	//画面の描写
    protected void paintComponent(Graphics g) {
    	if(!canPause) {
    		CorrectionStatus.correction();
    		atack();
    	}
    	super.paintComponent(g);
    	initialDraw(g);
		enemyDraw(g);
		soldierDraw(g);
		hitDraw(g);
		placementDraw(g);
		progressDataDraw(g);
		gameEnd(g);
    }
    
    //ユニット操作
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		for(int i = 0; i < SoldierData.UNIT_PLACEMENT_LIST.size(); i++) {
			if(SoldierData.UNIT_PLACEMENT_LIST.get(i).get(0).isValidIntValue(mouseX)
					&& SoldierData.UNIT_PLACEMENT_LIST.get(i).get(1).isValidIntValue(mouseY)){
				unitNumber = i;
				canSelect = true;
	    		break;
	    	}
		}
		exit: while (true) {
			if(!canSelect){
				for(int i = 0; i < existsActiveSoldierList.size(); i++) {
					if(existsActiveSoldierList.get(i)) {
						if(ValueRange.of(soldierPlacementList.get(i).get(0), soldierPlacementList.get(i).get(0) + UNIT_SIZE).isValidIntValue(mouseX)
								&& ValueRange.of(soldierPlacementList.get(i).get(1), soldierPlacementList.get(i).get(1) + UNIT_SIZE).isValidIntValue(mouseY)) {
							canPause = true;
				    		pause();
				    		CorrectionStatus.unitEnhancement(i, UnitOperation.operation(i, true));
				    		canPause = false;
				    		restart();
				    		break exit;
						}
					}
				}
				for(int i = 0; i < existsActiveEnemyList.size(); i++) {
					if(existsActiveEnemyList.get(i)) {
						if(ValueRange.of(enemyPlacementList.get(i).get(0), enemyPlacementList.get(i).get(0) + UNIT_SIZE).isValidIntValue(mouseX)
								&& ValueRange.of(enemyPlacementList.get(i).get(1), enemyPlacementList.get(i).get(1) + UNIT_SIZE).isValidIntValue(mouseY)) {
							canPause = true;
				    		pause();
				    		UnitOperation.operation(i, false);
				    		canPause = false;
				    		restart();
				    		break exit;
						}
					}
				}
			}
			break exit;
		}
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(canSelect) {
			mouseX = e.getX();
			mouseY = e.getY();
			if(unitNumber <= 2) {
				for(List<Integer> i : mixNearUnitPlacementList) {
					if(placementDetermination(i)) {
						break;
					}
				}
			}else if(3 <= unitNumber && unitNumber <= 5){
				for(List<Integer> i : mixFarUnitPlacementList) {
					if(placementDetermination(i)) {
						break;
					}
				}
			}else if(6 <= unitNumber) {
				for(List<Integer> i : mixAllUnitPlacementList) {
					if(placementDetermination(i)) {
						break;
					}
				}
			}
			canSelect = false;
			repaint();
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(canSelect) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaint();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
	//時計
	@Override
	public void actionPerformed(ActionEvent e) {
		gameTime++;
		repaint();
	}
	
	//初期フィード表示
	private void initialDraw(Graphics g) {
		g.setColor(new Color(255, 220, 220));
		g.fillRect(1010, 50, 200, 100);
		g.fillRect(1010, 150, 100, 100);
		g.fillRect(1010, 350, 50, 100);
		g.fillRect(1110, 350, 50, 100);
		g.setColor(new Color(220, 220, 255));
		g.fillRect(1110, 150, 100, 100);
		g.fillRect(1010, 250, 200, 100);
		g.fillRect(1060, 350, 50, 100);
		g.fillRect(1160, 350, 50, 100);
		g.setColor(Color.BLACK);
		g.drawLine(1010, 50, 1010, 450);
		g.drawLine(1110, 50, 1110, 450);
		g.drawLine(1210, 50, 1210, 450);
		g.drawLine(1010, 50, 1210, 50);
		g.drawLine(1010, 150, 1210, 150);
		g.drawLine(1010, 250, 1210, 250);
		g.drawLine(1010, 350, 1210, 350);
		g.drawLine(1010, 450, 1210, 450);
		for(int i = 0; i < categoryLabel.length; i++) {
			if(i < 3) {
				categoryLabel[i].setText("近攻");
			}else if(i < 6) {
				categoryLabel[i].setText("遠攻");
			}else if(i == 6){
				categoryLabel[i].setText("回復");
			}else {
				categoryLabel[i].setText("支援");
			}
			categoryLabel[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, 18));
			categoryLabel[i].setBounds(1065 + i % 2 * 100, 115 + i / 2 * 100, 100, 40);
			categoryLabel[i].setForeground(Color.GRAY);
    	}
		actionCommentLabel.setText("↓ユニットを配置してください↓");
		actionCommentLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 13));
		actionCommentLabel.setBounds(1010, 15, 300, 20);
		rangeDrawButton.setText("射程表示");
		rangeDrawButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
		rangeDrawButton.setBounds(1010, 475, 100, 40);
		rangeDrawButton.setFocusable(false);
		pauseButton.setText("一時停止");
		pauseButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
		pauseButton.setBounds(1110, 475, 100, 40);
		pauseButton.setFocusable(false);
		exit: for(int i = 0; i < StageData.existsfieldConditionsList.size(); i++) {
			for(int j = 0; j < StageData.existsfieldConditionsList.get(i).size(); j++) {
				if(!(StageData.existsfieldConditionsList.get(i).get(j) == existsActiveSoldierList.get(j + 1))) {
					break;
				}
				if(j == StageData.existsfieldConditionsList.get(i).size() - 1) {
					g.drawImage(StageData.fieldImageList.get(i), 0, 0, this);
					break exit;
				}
			}
		}
		for(int i = 0; i < SoldierData.SOLDIER_IMAGE_LIST.size(); i += 2) {
			g.drawImage(SoldierData.SOLDIER_IMAGE_LIST.get(i), 1015 + i % 4 * 50, 55 + i / 4 * 100, this);
		}
		for(List<Integer> i : StageData.nearUnitPlacementList) {
			g.drawImage(PlacementData.PLACEMENT_IMAGE_LIST.get(0), i.get(0), i.get(1), this);
		}
		for(List<Integer> i : StageData.farUnitPlacementList) {
			g.drawImage(PlacementData.PLACEMENT_IMAGE_LIST.get(1), i.get(0), i.get(1), this);
		}
		for(List<Integer> i : StageData.allUnitPlacementList) {
			g.drawImage(PlacementData.PLACEMENT_IMAGE_LIST.get(2), i.get(0), i.get(1), this);
		}
	}
	
	//敵移動
	private void enemyDraw(Graphics g) {
		for(int i = 0; i < StageData.enemyList.size(); i++) {
			if(StageData.enemyList.get(i).get(2) == gameTime) {
				existsActiveEnemyList.set(i, true);
				EnemyMoveList.get(i).moveStart();
			}else if(existsActiveEnemyList.get(i)) {
				if(existsRangeDisplay) {
					rangeDraw(g, enemyPlacementList.get(i), enemyStatusList.get(i));
				}
				HPDraw(g, enemyStatusList.get(i), enemyPlacementList.get(i));
				g.drawImage(enemyImageList.get(i), enemyPlacementList.get(i).get(0), enemyPlacementList.get(i).get(1), this);
			}
		}
	}
	
	//配置ユニット
	private void soldierDraw (Graphics g) {
		for(int i = 0; i < soldierImageList.size(); i++) {
			if(existsActiveSoldierList.get(i)) {
				if(0 < soldierStatusList.get(i).get(4)) {
					if(existsRangeDisplay) {
						rangeDraw(g, soldierPlacementList.get(i), soldierStatusList.get(i));
					}
					g.drawImage(soldierImageList.get(i), soldierPlacementList.get(i).get(0), soldierPlacementList.get(i).get(1), this);
				}
				HPDraw(g, soldierStatusList.get(i), soldierPlacementList.get(i));
			}
		}
	}
	
	//攻撃描写
	private void hitDraw(Graphics g) {
		for(int i = 0; i < 100; i++) {
			HitList.get(i).hitDraw(g);
		}
	}
	
	//ユニット移動
	private void placementDraw(Graphics g) {
		if(canSelect) {
			g.drawImage(SoldierData.SOLDIER_IMAGE_LIST.get(unitNumber * 2), mouseX - 50, mouseY - 50, this);
		}
	}
	
	//ゲーム状況
	private void progressDataDraw(Graphics g) {
		residueEnemyLabel.setText("残敵: " + residueEnemy);
		residueEnemyLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		residueEnemyLabel.setBounds(850,525,100,30);
		residueEnemyLabel.setBackground(Color.WHITE);
		residueEnemyLabel.setOpaque(true);
		residueEnemyLabel.setHorizontalAlignment(JLabel.CENTER);
		g.setColor(Color.BLUE);
		g.fillRect(25, 525, 800, 30);
		moraleDifference = (double) 2200 * enemyMorale / (soldierMorale + enemyMorale) - 700;
		if(moraleDifference <= 5) {
			moraleDifference = 5;
		}else if(795 <= moraleDifference) {
			moraleDifference = 795;
		}
		g.setColor(Color.RED);
		g.fillRect(25, 525, (int) moraleDifference, 30);
		g.setColor(Color.BLACK);
		g.fillPolygon(new int[] {415,425,435}, new int[] {500,525,500}, 3);
		g.setColor(Color.WHITE);
		g.drawRect(25, 525, 800, 30);
		g.drawPolygon(new int[] {415,425,435}, new int[] {500,525,500}, 3);
	}
	
	//ゲーム終了
	private void gameEnd(Graphics g) {
		if(soldierStatusList.get(0).get(1) <= 0) {
			existsGameOver = true;
		}
		if(existsGameOver || residueEnemy <= 0) {
	    	Graphics2D g2d = (Graphics2D) g;
	    	Font endFont = new Font("Aria", Font.BOLD|Font.ITALIC, 150);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setFont(endFont);
			if(existsGameOver) {
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(10));
				g2d.draw(endFont.createGlyphVector(g2d.getFontRenderContext(),"GAME").getOutline(400, 250));
				g2d.draw(endFont.createGlyphVector(g2d.getFontRenderContext(),"OVER").getOutline(400, 400));
				g2d.setColor(Color.RED);
				g2d.drawString("GAME",400,250);
				g2d.drawString("OVER",400,400);
			}else {
				g2d.setColor(Color.RED);
				g2d.setStroke(new BasicStroke(10));
				g2d.draw(endFont.createGlyphVector(g2d.getFontRenderContext(),"CLEAR").getOutline(400, 300));
				g2d.setColor(Color.YELLOW);
				g2d.drawString("CLEAR",400,300);
			}
			pause();
		}
	}
	
	//射程表示
	private void rangeDraw(Graphics g, List<Integer> placementList, List<Integer> statusList) {
		g.setColor(new Color(255, 0, 0, 20));
		g.fillOval(placementList.get(0) + CORRECTION_POSITION - statusList.get(4),
				placementList.get(1) + CORRECTION_POSITION - statusList.get(4),
				statusList.get(4) * 2 + UNIT_SIZE,
				statusList.get(4) * 2 + UNIT_SIZE);
	}
	
	//HP表示
	private void HPDraw(Graphics g, List<Integer> unitStatusList, List<Integer> unitPlacementList) {
		g.setColor(Color.BLACK);
		g.fillRect(unitPlacementList.get(0) + CORRECTION_POSITION, unitPlacementList.get(1) + 70, UNIT_SIZE, 10);
		g.setColor(new Color(150, 200, 100));
		g.fillRect(unitPlacementList.get(0) + CORRECTION_POSITION, unitPlacementList.get(1) + 70, UNIT_SIZE * unitStatusList.get(1) / unitStatusList.get(0), 10);
		g.setColor(Color.WHITE);
		g.drawRect(unitPlacementList.get(0) + CORRECTION_POSITION, unitPlacementList.get(1) + 70, UNIT_SIZE, 10);
	}
	
	//配置許可
	private boolean placementDetermination(List<Integer> placementList) {
		if(ValueRange.of(placementList.get(0), placementList.get(0) + UNIT_SIZE).isValidIntValue(mouseX)
				&& ValueRange.of(placementList.get(1), placementList.get(1) + UNIT_SIZE).isValidIntValue(mouseY)) {
			for(int i = 0; i < soldierPlacementList.size(); i++) {
				if(existsActiveSoldierList.get(i)) {
					if(soldierPlacementList.get(i).get(0) == placementList.get(0) - CORRECTION_POSITION && soldierPlacementList.get(i).get(1) == placementList.get(1) - CORRECTION_POSITION){
						return false;
					}
				}
			}
			existsActiveSoldierList.add(true);
			soldierImageList.add(SoldierData.SOLDIER_IMAGE_LIST.get(unitNumber * 2));
			soldierStatusList.add(new ArrayList<>(SoldierData.SOLDIER_STATUS_LIST.get(unitNumber)));
			initialSoldierStatusList.add(new ArrayList<>(SoldierData.SOLDIER_STATUS_LIST.get(unitNumber)));
			correctionSoldierStatusList.add(Arrays.asList(0, 0, 0, 0, 0, 0, 0));
			soldierPlacementList.add(Arrays.asList(placementList.get(0) - CORRECTION_POSITION, placementList.get(1) - CORRECTION_POSITION));
			SoldierMotionList.add(new SoldierMotion(soldierStatusList.size() - 1, unitNumber));
			return true;
		}
		return false;
	}
	
	//攻撃動作
	private void atack() {
		for(int i = 0; i< correctionSoldierStatusList.size(); i++) {
			for(int j = 0; j < correctionSoldierStatusList.get(i).size(); j++) {
				correctionSoldierStatusList.get(i).set(j, 0);
			}
		}
		for(int i = 0; i < existsActiveSoldierList.size(); i++) {
			if(existsActiveSoldierList.get(i) && 0 < soldierStatusList.get(i).get(4)) {
				if(0 < soldierStatusList.get(i).get(2)) {
					target = AttackJudgment.judgmentNear(soldierStatusList.get(i).get(4), soldierPlacementList.get(i), enemyPlacementList, existsActiveEnemyList);
				}else if(soldierStatusList.get(i).get(2) < 0){
					target = AttackJudgment.judgmentRatio(soldierPlacementList, existsActiveSoldierList, soldierStatusList, i);
				}else {
					target = AttackJudgment.judgmentAll(i);
				}
				if(0 <= target) {
					SoldierMotionList.get(i).motionStart(target);
				}else {
					SoldierMotionList.get(i).motionStop();
				}
			}
		}
		for(int i = 0; i < existsActiveEnemyList.size(); i++) {
			if(existsActiveEnemyList.get(i)) {
				if(0 < enemyStatusList.get(i).get(2)) {
					target = AttackJudgment.judgmentNear(enemyStatusList.get(i).get(4), enemyPlacementList.get(i), soldierPlacementList, existsActiveSoldierList);
				}else if(enemyStatusList.get(i).get(2) < 0){
					target = AttackJudgment.judgmentRatio(enemyPlacementList, existsActiveEnemyList, enemyStatusList, i);
				}else {
					target = -1;
				}
				if(0 <= target) {
					EnemyMotionList.get(i).motionStart(target);
				}else {
					EnemyMotionList.get(i).motionStop();
				}
			}
		}
	}
	
	//一時停止
	private void pause() {
		existsSoldierMotionTimerList.clear();
		existsEnemyMotionTimerList.clear();
		existsEnemyMoveTimerList.clear();
		for(int i = 0; i < SoldierMotionList.size(); i ++) {
			if(0 < soldierStatusList.get(i).get(4)) {
				existsSoldierMotionTimerList.add(SoldierMotionList.get(i).timerStop());
			}else {
				existsSoldierMotionTimerList.add(false);
			}
		}
		for(int i = 0; i < EnemyMotionList.size(); i ++) {
			existsEnemyMotionTimerList.add(EnemyMotionList.get(i).timerStop());
			existsEnemyMoveTimerList.add(EnemyMoveList.get(i).timerStop());
		}
		for(Hit i: HitList) {
			i.timerStop();
		}
		timer.stop();
		removeMouseListener(this);
    	removeMouseMotionListener(this);
    	remove(rangeDrawButton);
    	remove(pauseButton);
	}
	
	//再開
	private void restart() {
		for(int i = 0; i < existsSoldierMotionTimerList.size(); i ++) {
			if(existsSoldierMotionTimerList.get(i)) {
				SoldierMotionList.get(i).timerRestart();
			}
		}
		for(int i = 0; i < existsEnemyMotionTimerList.size(); i ++) {
			if(existsEnemyMotionTimerList.get(i)) {
				EnemyMotionList.get(i).timerRestart();
			}
			if(existsEnemyMoveTimerList.get(i)){
				EnemyMoveList.get(i).moveStart();
			}
		}
		timer.restart();
		addMouseListener(this);
    	addMouseMotionListener(this);
		add(rangeDrawButton);
		add(pauseButton);
	}
}

//共通基本行動
class Motion{
	Calculation Calculation = new Calculation();
	Timer timer;
	List<BufferedImage> imageList;
	BufferedImage defaultNormalImage;
	BufferedImage defaultActionImage;
	List<List<Integer>> statusList;
	List<List<Integer>> anotherStatusList;
	List<Boolean> existsActiveAnotherList;
	List<EnemyMove> EnemyMoveList;
	int target;
	int number;
	long actionTime;
	long delayTime;
	
	protected void motion(int number) {
		this.number = number;
		EnemyMoveList = StagePanel.EnemyMoveList;
	}
}

//自軍行動
class SoldierMotion extends Motion implements ActionListener{
	List<EnemyMotion> AnotherMotionList;
	
	protected SoldierMotion(int number, int unitNumber) {
		super.motion(number);
		imageList = StagePanel.soldierImageList;
		defaultNormalImage = SoldierData.SOLDIER_IMAGE_LIST.get(unitNumber * 2);
		defaultActionImage = SoldierData.SOLDIER_IMAGE_LIST.get(unitNumber * 2 + 1);
		statusList = StagePanel.soldierStatusList;
		anotherStatusList = StagePanel.enemyStatusList;
		existsActiveAnotherList = StagePanel.existsActiveEnemyList;
		AnotherMotionList = StagePanel.EnemyMotionList;
		timer = new Timer(statusList.get(number).get(5),this);
		timer.setInitialDelay(0);
	}
	
	protected void motionStart(int target) {
		if(!timer.isRunning()) {
			timer.setRepeats(true);
			timer.restart();
		}
		this.target = target;
	}
	
	protected void motionStop() {
		if(imageList.get(number) == defaultActionImage) {
			timer.setRepeats(false);
		}else {
			timer.stop();
		}
		timer.setInitialDelay(0);
	}
	
	protected void timerRestart() {
		timer.restart();
	}
	
	protected boolean timerStop() {
		if(timer.isRunning()) {
			timer.stop();
			delayTime = System.currentTimeMillis() - actionTime;
			try {
				timer.setInitialDelay(statusList.get(number).get(5) - (int) delayTime);
			}catch(Exception ignore) {
			}
			return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(imageList.get(number) == defaultActionImage) {
			imageList.set(number, defaultNormalImage);
		}else {
			imageList.set(number, defaultActionImage);
			StagePanel.hitNumber++;
			if(0 < statusList.get(number).get(2)) {
				atack();
			}else {
				heal();
			}
		}
		actionTime = System.currentTimeMillis();
	}
	
	private void atack() {
		anotherStatusList.get(target).set(1, Calculation.damage(statusList.get(number).get(2), anotherStatusList.get(target), true));
		StagePanel.HitList.get(StagePanel.hitNumber % 100).timerStart(target, false, true);
		if(anotherStatusList.get(target).get(1) <= 0) {
			existsActiveAnotherList.set(target, false);
			new DeleteBlock().deleteOne(target);
			StagePanel.residueEnemy -= 1;
			StagePanel.soldierMorale += (double) 100 / anotherStatusList.size();
			AnotherMotionList.get(target).timerStop();
			EnemyMoveList.get(target).timerStop();
		}
	}
	
	private void heal() {
		statusList.get(target).set(1, Calculation.heal(statusList.get(number).get(2), statusList.get(target), true));
		StagePanel.HitList.get(StagePanel.hitNumber % 100).timerStart(target, false, false);
	}
}

//敵軍行動
class EnemyMotion extends Motion implements ActionListener{
	List<SoldierMotion> AnotherMotionList;
	
	protected EnemyMotion(int number) {
		super.motion(number);
		imageList = StagePanel.enemyImageList;
		defaultNormalImage = EnemyData.ENEMY_IMAGE_LIST.get(StageData.enemyList.get(number).get(0) * 2);
		defaultActionImage = EnemyData.ENEMY_IMAGE_LIST.get(StageData.enemyList.get(number).get(0) * 2 + 1);
		statusList = StagePanel.enemyStatusList;
		anotherStatusList = StagePanel.soldierStatusList;
		existsActiveAnotherList = StagePanel.existsActiveSoldierList;
		AnotherMotionList = StagePanel.SoldierMotionList;
		timer = new Timer(statusList.get(number).get(5),this);
		timer.setInitialDelay(0);
	}
	
	protected void motionStart(int target) {
		if(!timer.isRunning()) {
			timer.setRepeats(true);
			timer.restart();
		}
		this.target = target;
	}
	
	protected void motionStop() {
		if(imageList.get(number) == defaultActionImage) {
			timer.setRepeats(false);
		}else {
			timer.stop();
		}
		timer.setInitialDelay(0);
	}
	
	protected void timerRestart() {
		timer.restart();
	}
	
	protected boolean timerStop() {
		if(timer.isRunning()) {
			timer.stop();
			delayTime = System.currentTimeMillis() - actionTime;
			try {
				timer.setInitialDelay(statusList.get(number).get(5) - (int) delayTime);
			}catch(Exception ignore) {
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(imageList.get(number) == defaultActionImage) {
			imageList.set(number, defaultNormalImage);
			EnemyMoveList.get(number).moveStart();
		}else {
			imageList.set(number, defaultActionImage);
			EnemyMoveList.get(number).timerStop();
			StagePanel.hitNumber++;
			if(0 < statusList.get(number).get(2)) {
				atack();
			}else {
				heal();
			}
		}
		actionTime = System.currentTimeMillis();
	}
	
	private void atack() {
		anotherStatusList.get(target).set(1, Calculation.damage(statusList.get(number).get(2), anotherStatusList.get(target), false));
		StagePanel.HitList.get(StagePanel.hitNumber % 100).timerStart(target, true, true);
		if(anotherStatusList.get(target).get(1) <= 0) {
			existsActiveAnotherList.set(target, false);
			new DeleteBlock().deleteAll(target);
			StagePanel.enemyMorale += (double) StagePanel.enemyMorale / 2 - 30;
			AnotherMotionList.get(target).timerStop();
		}
	}
	
	private void heal() {
		statusList.get(target).set(1, Calculation.heal(statusList.get(number).get(2), statusList.get(target), false));
		StagePanel.HitList.get(StagePanel.hitNumber % 100).timerStart(target, true, false);
	}
}

class EnemyMove implements ActionListener{
	Timer timer;
	int number;
	List<List<Integer>> moveList;
	List<Integer> placementList;
	int x;
	int y;
	int route = 1;
	int blockNumber;
	double distance;
	
	protected EnemyMove(int number) {
		this.number = number;
		moveList = StageData.moveList.get(StageData.enemyList.get(number).get(1));
		placementList = StagePanel.enemyPlacementList.get(number);
		timer = new Timer(StagePanel.enemyStatusList.get(number).get(6), this);
		timer.setInitialDelay(0);
	}
	
	protected void moveStart() {
		if(!timer.isRunning()) {
			timer.restart();
		}
	}
	
	protected boolean timerStop() {
		if(timer.isRunning()) {
			timer.stop();
			return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(moveJudgment()) {
			try {
				moveDistance();
				placementList.set(0, placementList.get(0) + x);
				placementList.set(1, placementList.get(1) + y);
				if(Math.abs(moveList.get(route).get(0)	- placementList.get(0) + x) <= 5
						&& Math.abs(moveList.get(route).get(1)	- placementList.get(1) + y) <= 5) {
					route++;
				}
			}catch(Exception ignore) {
			}
		}
	}
	
	private boolean moveJudgment() {
		distance = 0;
		if(0 <= StagePanel.blockList.get(number)) {
			return false;
		}
		for(int i = 0; i < StagePanel.existsActiveSoldierList.size(); i++) {
			if(StagePanel.existsActiveSoldierList.get(i) && 0 < StagePanel.soldierStatusList.get(i).get(6)) {
				distance = (double) Math.sqrt(Math.pow(placementList.get(0) - StagePanel.soldierPlacementList.get(i).get(0), 2)
						+ Math.pow(placementList.get(1) - StagePanel.soldierPlacementList.get(i).get(1), 2));
				if(distance <= StagePanel.UNIT_SIZE) {
					blockNumber = 0;
					for(int j: StagePanel.blockList) {
						if(j == i) {
							blockNumber++;
						}
					}
					if(blockNumber < StagePanel.soldierStatusList.get(i).get(6)) {
						StagePanel.blockList.set(number, i);
						x = 0;
						y = 0;
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private void moveDistance() {
		try {
			switch(moveList.get(route).get(2)){
			case 1:
				x = 0;
				y = -5;
				break;
			case 2:
				x = 5;
				y = -5;
				break;
			case 3:
				x = 5;
				y = 0;
				break;
			case 4:
				x = 5;
				y = 5;
				break;
			case 5:
				x = 0;
				y = 5;
				break;
			case 6:
				x = -5;
				y = 5;
				break;
			case 7:
				x = -5;
				y = 0;
				break;
			case 8:
				x = -5;
				y = -5;
				break;
			default:
				x = 0;
				y = 0;
				break;
			}
		}catch(Exception reachCastle) {
			x = 0;
			y = 0;
		}
	}
}

//攻撃可否
class AttackJudgment{
	double distance;
	double minDistance;
	double ratio;
	double minRatio;
	int target;
	
	protected int judgmentNear(int atackRange, List<Integer> placement, List<List<Integer>> anotherPlacementList, List<Boolean> activeAnotherList) {
		minDistance = 0;
		for(int i = 0; i < anotherPlacementList.size(); i++) {
			if(activeAnotherList.get(i)) {
				distance = (double) Math.sqrt(Math.pow(placement.get(0) - anotherPlacementList.get(i).get(0), 2)
						+ Math.pow(placement.get(1) - anotherPlacementList.get(i).get(1), 2));
				if(minDistance == 0) {
					minDistance = distance;
					target = i;
				}else {
					if(distance < minDistance) {
						minDistance = distance;
						target = i;
					}
				}
			}
		}
		if(minDistance == 0) {
			return -1;
		}else {
			return (minDistance <= atackRange + StagePanel.UNIT_SIZE)? target: -1;
		}
	}
	
	protected int judgmentRatio(List<List<Integer>> placementList, List<Boolean> activeList, List<List<Integer>> statusList, int number) {
		minRatio = 1;
		for(int i = 0; i < placementList.size(); i++) {
			if(activeList.get(i)) {
				distance = (double) Math.sqrt(Math.pow(placementList.get(number).get(0) - placementList.get(i).get(0), 2)
						+ Math.pow(placementList.get(number).get(1) - placementList.get(i).get(1), 2));
				if(distance <= statusList.get(number).get(4) + StagePanel.UNIT_SIZE) {
					if(statusList.get(number).get(2) == 0) {
						return i;
					}
					ratio = (double) statusList.get(i).get(1) / statusList.get(i).get(0);
					if(ratio < minRatio) {
						minRatio = ratio;
						target = i;
					}
				}
			}
		}
		if(minRatio == 1) {
			return -1;
		}else {
			return target;
		}
	}
	
	protected int judgmentAll(int number) {
		target = -1;
		for(int i = 0; i < StagePanel.soldierPlacementList.size(); i++) {
			if(StagePanel.existsActiveSoldierList.get(i) && !(number == i)) {
				distance = (double) Math.sqrt(Math.pow(StagePanel.soldierPlacementList.get(number).get(0) - StagePanel.soldierPlacementList.get(i).get(0), 2)
						+ Math.pow(StagePanel.soldierPlacementList.get(number).get(1) - StagePanel.soldierPlacementList.get(i).get(1), 2));
				if(distance <= StagePanel.soldierStatusList.get(number).get(4) + StagePanel.UNIT_SIZE) {
					new CorrectionStatus().fanBuff(i);
					target = i;
				}
			}
		}
		return target;
	}
}

//ダメージ計算
class Calculation{
	double moraleCorrection;
	double value;
	int HP;
	
	protected int damage(int atack, List<Integer> status, boolean existsWhich) {
		moraleCorrection = (existsWhich)? StagePanel.soldierMorale / StagePanel.enemyMorale: StagePanel.enemyMorale / StagePanel.soldierMorale;
		value = (double) 100 * atack / status.get(3) * moraleCorrection;
		HP = status.get(1) - (int) value;
		return HP;
	}
	
	protected int heal(int atack, List<Integer> status, boolean existsWhich) {
		moraleCorrection = (existsWhich)? StagePanel.soldierMorale / StagePanel.enemyMorale: StagePanel.enemyMorale / StagePanel.soldierMorale;
		value = (double) atack * 1.3 * moraleCorrection;
		HP = status.get(1) - (int) value;
		if(status.get(0) < HP) {
			return status.get(0);
		}else {
			return HP;
		}
	}
}

//攻撃描写
class Hit implements ActionListener{
	Timer timer;
	int target;
	boolean existsWhich;
	boolean existsAtack;
	boolean canDraw;
	List<Integer> placementList;
	Random random = new Random();
	int hitPointX;
	int hitPointY;
	BufferedImage image;
	
	protected Hit() {
		timer = new Timer(200, this);
		timer.setInitialDelay(0);
	}
	
	protected void timerStart(int target, boolean existsWhich, boolean existsAtack) {
		this.target = target;
		this.existsWhich = existsWhich;
		this.existsAtack = existsAtack;
		canDraw = false;
		timer.setRepeats(true);
		timer.start();
	}
	
	protected void timerStop() {
		if(timer.isRunning()) {
			timer.stop();
		}
	}
	
	protected void hitDraw(Graphics g) {
		if(canDraw) {
			draw(g);
			draw(g);
			timer.setRepeats(false);
		}
	}
	
	private void draw(Graphics g) {
		placementList = (existsWhich == existsAtack)? StagePanel.soldierPlacementList.get(target): StagePanel.enemyPlacementList.get(target);
		hitPointX = random.nextInt(StagePanel.UNIT_SIZE) + placementList.get(0);
		hitPointY = random.nextInt(StagePanel.UNIT_SIZE) + placementList.get(1);
		image = (existsAtack)? AtackData.HIT_IMAGE.get(0):AtackData.HEAL_IMAGE.get(0);
		g.drawImage(image, hitPointX, hitPointY, null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		canDraw = (canDraw)? false: true;
	}
}

//ユニット操作
class UnitOperation{
	List<Integer> statusList;
	ImageIcon icon;
	double ratio;
	String atack;
	String comment;
	String block;
	int operation;
	String selectMenu[] = {"退却", "HP UP", "攻撃 UP", "防御 UP", "射程 UP"};
	
	protected int operation(int number, boolean existsWhich) {
		if(existsWhich) {
			icon = new ImageIcon(StagePanel.soldierImageList.get(number));
			statusList = StagePanel.soldierStatusList.get(number);
		}else {
			icon = new ImageIcon(StagePanel.enemyImageList.get(number));
			statusList = StagePanel.enemyStatusList.get(number);
		}
		ratio = (double) statusList.get(1) / statusList.get(0) * 100;
		if(0 <= statusList.get(2)) {
			atack = "攻撃力";
		}else {
			atack = "回復力";
		}
		comment ="【ステータス】\n"
				+ "HP: " + statusList.get(0) + " / " + statusList.get(1) + " (" + String.format("%.1f", ratio) + "%)\n"
				+ atack + ": " + Math.abs(statusList.get(2)) + "\n"
				+ "防御力: " + statusList.get(3) + "\n"
				+ "射程: " + statusList.get(4) + "\n"
				+ "攻撃速度: " + statusList.get(5) + " ms";
		if(existsWhich) {
			if(1000 < statusList.get(6)) {
				block = "∞";
			}else {
				block = "" + statusList.get(6);
			}
			comment += "\n" + "足止め数: " + block;
			if(statusList.get(4) == 0) {
				operation = -1;
				showOptionDialog(null, comment, "設備情報", OK_CANCEL_OPTION, PLAIN_MESSAGE, icon, null, null);
			}else {
				operation = showOptionDialog(null, comment, "ユニット操作", OK_CANCEL_OPTION, PLAIN_MESSAGE, icon, selectMenu, selectMenu[0]);
			}
		}else {
			comment += "\n" + "移動速度: " + statusList.get(6) + " ms";
			showOptionDialog(null, comment, "敵兵情報", OK_CANCEL_OPTION, PLAIN_MESSAGE, icon, null, null);
		}
		return operation;
	}
}

//能力値補正
class CorrectionStatus{
	double value;
	
	protected void correction() {
		for(int i = 0; i < StagePanel.soldierStatusList.size(); i++) {
			if(StagePanel.existsActiveSoldierList.get(i)) {
				for(int j = 0; j < StagePanel.soldierStatusList.get(i).size(); j++) {
					if(!(j == 1)) {
						StagePanel.soldierStatusList.get(i).set(j, StagePanel.initialSoldierStatusList.get(i).get(j) + StagePanel.correctionSoldierStatusList.get(i).get(j));
					}
				}
			}
		}
	}
	
	protected void unitEnhancement(int number, int operation) {
		switch(operation){
		case 0:
			StagePanel.existsActiveSoldierList.set(number, false);
			StagePanel.existsSoldierMotionTimerList.set(number, false);
			StagePanel.SoldierMotionList.get(number).timerStop();
			new DeleteBlock().deleteAll(number);
			break;
		case 1:
			StagePanel.initialSoldierStatusList.get(number).set(0, calculation(StagePanel.initialSoldierStatusList.get(number).get(0), 1.4));
			break;
		case 2:
			if(StagePanel.initialSoldierStatusList.get(number).get(2) == 0) {
				showMessageDialog(null,"このユニットの攻撃力は上がりません");
			}else {
				StagePanel.initialSoldierStatusList.get(number).set(2, calculation(StagePanel.initialSoldierStatusList.get(number).get(2), 1.2));
			}
			break;
		case 3:
			StagePanel.initialSoldierStatusList.get(number).set(3, calculation(StagePanel.initialSoldierStatusList.get(number).get(3), 1.2));
			break;
		case 4:
			StagePanel.initialSoldierStatusList.get(number).set(4, StagePanel.initialSoldierStatusList.get(number).get(4) + 30);
			break;
		default:
			break;
		}
	}
	
	private int calculation(int status, double magnification) {
		value = (double) status * magnification;
		return (int) value;
	}
	
	protected void fanBuff(int number) {
		if(0 < StagePanel.initialSoldierStatusList.get(number).get(2)) {
			StagePanel.correctionSoldierStatusList.get(number).set(2, StagePanel.correctionSoldierStatusList.get(number).get(2) + 10);
		}else if(StagePanel.initialSoldierStatusList.get(number).get(2) < 0) {
			StagePanel.correctionSoldierStatusList.get(number).set(2, StagePanel.correctionSoldierStatusList.get(number).get(2) - 10);
		}
		StagePanel.correctionSoldierStatusList.get(number).set(3, StagePanel.correctionSoldierStatusList.get(number).get(3) + 10);
	}
}

//足止め消去
class DeleteBlock{
	protected void deleteAll(int number) {
		if(0 < StagePanel.soldierStatusList.get(number).get(6)) {
			for(int i = 0; i < StagePanel.blockList.size(); i++) {
				if(StagePanel.blockList.get(i) == number) {
					StagePanel.blockList.set(i, -1);
				}
			}
		}
	}
	
	protected void deleteOne(int number) {
		StagePanel.blockList.set(number, -1);
	}
}



//♦共通基本データ♦

//画像取り込み
class InputImage{
	BufferedImage image;
	List<BufferedImage> imageList = new ArrayList<>();
	int resizeWidth;
	int resizeHeight;
	BufferedImage resizeImage;
	BufferedImage finalImage;
	
	protected List<BufferedImage> input(List<String> imageNameList){
		try{
			for(String i : imageNameList) {
				image = editImage(ImageIO.read(new File(i)));
				imageList.add(image);
			}
		}catch(Exception input) {
		}
		return imageList;
	}
	
	//画像処理
	private BufferedImage editImage(BufferedImage originalImage) {
		resizeWidth = originalImage.getWidth() / 2;
		resizeHeight = originalImage.getHeight() / 2;
		resizeImage = new BufferedImage(resizeWidth, resizeHeight, BufferedImage.TYPE_3BYTE_BGR);
		resizeImage.createGraphics().drawImage(
	    	originalImage.getScaledInstance(resizeWidth, resizeHeight, Image.SCALE_AREA_AVERAGING),
	        0, 0, resizeWidth, resizeHeight, null);
		
		finalImage = new BufferedImage(resizeWidth, resizeHeight, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < resizeHeight; y++) {
			for (int x = 0; x < resizeWidth; x++) {
				if (resizeImage.getRGB(x, y) == new Color(254, 254 ,254).getRGB()) {
					finalImage.setRGB(x, y, 0);
				}else {
					finalImage.setRGB(x, y, resizeImage.getRGB(x, y));
                }
			}
		}
		return finalImage;
	}
}

//自軍データ
class SoldierData{
	final static List<String> SOLDIER_NAME_LIST = Arrays.asList(
			"image/soldier/sord normal.png",
			"image/soldier/sord action.png",
			"image/soldier/spear normal.png",
			"image/soldier/spear action.png",
			"image/soldier/shield normal.png",
			"image/soldier/shield action.png",
			"image/soldier/dart normal.png",
			"image/soldier/dart action.png",
			"image/soldier/bow normal.png",
			"image/soldier/bow action.png",
			"image/soldier/gun normal.png",
			"image/soldier/gun action.png",
			"image/soldier/hammer normal.png",
			"image/soldier/hammer action.png",
			"image/soldier/fan normal.png",
			"image/soldier/fan action.png");
	final static List<BufferedImage> SOLDIER_IMAGE_LIST = new InputImage().input(SOLDIER_NAME_LIST);
	final static List<List<Integer>> SOLDIER_STATUS_LIST = Arrays.asList(
			Arrays.asList(1000, 1000, 100, 100, 30, 700, 1),//sord
			Arrays.asList(700, 700, 70, 70, 60, 1000, 1),//spear
			Arrays.asList(3000, 3000, 30, 150, 30, 1500, 3),//shield
			Arrays.asList(1000, 1000, 100, 100, 100, 500, 0),//dart
			Arrays.asList(500, 500, 70, 70, 150, 1000, 0),//bow
			Arrays.asList(800, 800, 150, 70, 200, 2000, 0),//gun
			Arrays.asList(1000, 1000, -100, 100, 100, 1000, 0),//hammer
			Arrays.asList(1000, 1000, 0, 100, 100, 1000, 0));//fan
	final static List<List<ValueRange>> UNIT_PLACEMENT_LIST = Arrays.asList(
			Arrays.asList(ValueRange.of(1040, 1085), ValueRange.of(80, 125)),//sord
			Arrays.asList(ValueRange.of(1140, 1185), ValueRange.of(80, 125)),//spear
			Arrays.asList(ValueRange.of(1040, 1080), ValueRange.of(175, 225)),//shield
			Arrays.asList(ValueRange.of(1135, 1185), ValueRange.of(175, 225)),//dart
			Arrays.asList(ValueRange.of(1045, 1085), ValueRange.of(280, 320)),//bow
			Arrays.asList(ValueRange.of(1140, 1180), ValueRange.of(280, 320)),//gun
			Arrays.asList(ValueRange.of(1035, 1085), ValueRange.of(375, 425)),//hammer
			Arrays.asList(ValueRange.of(1135, 1190), ValueRange.of(380, 420)));//fan
	final static List<String> CASTLE_NAME = Arrays.asList("image/soldier/castle.png");
	final static List<BufferedImage> CASTLE_IMAGE = new InputImage().input(CASTLE_NAME);
	final static List<Integer> CASTLE_STATUS = Arrays.asList(20000, 10000, 0, 50, 0, 0, 100000);
	final static List<String> GATE_NAME = Arrays.asList("image/soldier/castle gate.png");
	final static List<BufferedImage> GATE_IMAGE = new InputImage().input(GATE_NAME);
	final static List<Integer> GATE_STATUS = Arrays.asList(10000, 10000, 0, 50, 0, 0, 100000);
}

//配置位置データ
class PlacementData{
	final static List<String> PLACEMENT_NAME_LIST = Arrays.asList(
			"image/soldier/near placement.png",
			"image/soldier/far placement.png",
			"image/soldier/all placement.png");
	final static List<BufferedImage> PLACEMENT_IMAGE_LIST = new InputImage().input(PLACEMENT_NAME_LIST);
}

//攻撃画像
class AtackData{
	final static List<String> HIT_NAME = Arrays.asList("image/soldier/hit.png");
	final static List<BufferedImage> HIT_IMAGE = new InputImage().input(HIT_NAME);
	final static List<String> HEAL_NAME = Arrays.asList("image/soldier/heal.png");
	final static List<BufferedImage> HEAL_IMAGE = new InputImage().input(HEAL_NAME);
}

//敵軍データ
class EnemyData{
	final static List<String> ENEMY_NAME_LIST = Arrays.asList(
			"image/enemy/blue slime normal.png",
			"image/enemy/blue slime action.png",
			"image/enemy/green slime normal.png",
			"image/enemy/green slime action.png",
			"image/enemy/red slime normal.png",
			"image/enemy/red slime action.png",
			"image/enemy/yellow slime normal.png",
			"image/enemy/yellow slime action.png");
	final static List<BufferedImage> ENEMY_IMAGE_LIST = new InputImage().input(ENEMY_NAME_LIST);
	final static List<List<Integer>> ENEMY_STATUS_LIST = Arrays.asList(
			Arrays.asList(1000, 1000, 30, 30, 20, 1000, 100),//0: blue slime
			Arrays.asList(2000, 2000, 30, 30, 20, 1000, 100),//1: green slime
			Arrays.asList(1000, 1000, 60, 30, 20, 1000, 100),//2: red slime
			Arrays.asList(1000, 1000, 30, 30, 20, 1000, 50));//3: yellow slime
}

//選択ステージデータ
class StageData{
	static List<BufferedImage> fieldImageList;
	static List<List<Boolean>> existsfieldConditionsList;
	static List<List<Integer>> facilityPlacementList;
	static List<List<Integer>> nearUnitPlacementList;
	static List<List<Integer>> farUnitPlacementList;
	static List<List<Integer>> allUnitPlacementList;
	static List<List<List<Integer>>> moveList;
	static List<List<Integer>> enemyList;
}



//♦各ステージ基本データ♦

//ステージデータ入力用 新ステージ実装時にextendsしてね
abstract class Stage{
	abstract protected List<BufferedImage> fieldImageList();
	abstract protected List<List<Boolean>> existsfieldConditionsList();
	abstract protected List<List<Integer>> facilityPlacementList();
	abstract protected List<List<Integer>> nearUnitPlacementList();
	abstract protected List<List<Integer>> farUnitPlacementList();
	abstract protected List<List<Integer>> allUnitPlacementList();
	abstract protected List<List<List<Integer>>> moveList();
	abstract protected List<List<Integer>> enemyList();
	
	protected void importData() {
		StageData.fieldImageList = fieldImageList();
		StageData.existsfieldConditionsList = existsfieldConditionsList();
		StageData.facilityPlacementList = facilityPlacementList();
		StageData.nearUnitPlacementList = nearUnitPlacementList();
		StageData.farUnitPlacementList = farUnitPlacementList();
		StageData.allUnitPlacementList = allUnitPlacementList();
		StageData.moveList = moveList();
		StageData.enemyList = enemyList();
	}
}

//Stage1データ
class Stage1Data extends Stage{
	List<String> fieldNameList = Arrays.asList(
			"image/field/stage1-1.png",
			"image/field/stage1-2.png",
			"image/field/stage1-3.png");
	List<BufferedImage> fieldImageList = new InputImage().input(fieldNameList);
	List<List<Boolean>> existsfieldConditionsList = Arrays.asList(
			Arrays.asList(true, true),
			Arrays.asList(false, true),
			Arrays.asList(false, false));
	List<List<Integer>> facilityPlacementList = Arrays.asList(
			Arrays.asList(875, 53),
			Arrays.asList(555, 260),
			Arrays.asList(790, 428));
	List<List<Integer>> nearUnitPlacementList = Arrays.asList(
			Arrays.asList(150, 284),
			Arrays.asList(267, 284),
			Arrays.asList(384, 284),
			Arrays.asList(501, 284),
			Arrays.asList(682, 401),
			Arrays.asList(917, 284),
			Arrays.asList(917, 168));
	List<List<Integer>> farUnitPlacementList = Arrays.asList(
			Arrays.asList(208, 225),
			Arrays.asList(326, 225),
			Arrays.asList(444, 225),
			Arrays.asList(267, 344),
			Arrays.asList(386, 344),
			Arrays.asList(502, 344),
			Arrays.asList(563, 461),
			Arrays.asList(563, 166),
			Arrays.asList(682, 166),
			Arrays.asList(799, 166),
			Arrays.asList(799, 284));
	List<List<Integer>> allUnitPlacementList = Arrays.asList(
			Arrays.asList(682, 284),
			Arrays.asList(917, 401));
	List<List<List<Integer>>> moveList = Arrays.asList(
			Arrays.asList(Arrays.asList(0, 269, 0),//経路: 0
					Arrays.asList(667, 269, 3),
					Arrays.asList(667, 386, 5),
					Arrays.asList(902, 386, 3),
					Arrays.asList(902, 110, 1)),
			Arrays.asList(Arrays.asList(0, 274, 0),//経路: 1
					Arrays.asList(667, 274, 3),
					Arrays.asList(667, 386, 5),
					Arrays.asList(902, 386, 3),
					Arrays.asList(902, 110, 1)));
	List<List<Integer>> enemyList = Arrays.asList(
			Arrays.asList(0, 0, 100),
			Arrays.asList(3, 1, 100),
			Arrays.asList(1, 0, 200),
			Arrays.asList(2, 0, 300));
	
	@Override
	protected List<BufferedImage> fieldImageList() {
		return fieldImageList;
	}
	@Override
	protected List<List<Boolean>> existsfieldConditionsList() {
		return existsfieldConditionsList;
	}
	@Override
	protected List<List<Integer>> facilityPlacementList() {
		return facilityPlacementList;
	}
	@Override
	protected List<List<Integer>> nearUnitPlacementList() {
		return nearUnitPlacementList;
	}
	@Override
	protected List<List<Integer>> farUnitPlacementList() {
		return farUnitPlacementList;
	}
	@Override
	protected List<List<Integer>> allUnitPlacementList() {
		return allUnitPlacementList;
	}
	@Override
	protected List<List<List<Integer>>> moveList() {
		return moveList;
	}
	@Override
	protected List<List<Integer>> enemyList() {
		return enemyList;
	}
}

/*
ステージはパワポの1画面の大きさ
キャラはパワポの点線1マスの大きさで作ること
画像の背景はColor(254, 254 ,254)にする(この色が透明色に置き換わる)
facilityPlacementListは 1項目に城位置 2項目以降に城門
existsfieldConditionsListは①フィード画像番号, ②各城門の生存状況の順にリスト化
キャラのステータスは 最大HP, 残存HP, 攻撃, 防御, 射程, 攻撃速度, 味方: 足止め数 敵: 移動速度 の順でリスト化
回復役は攻撃力がマイナス表記
unitNumber, enemyNumberは各statusListの配置順
moveListは①List:経路Number, ②List:経路での移動変化順, ③List:x座標, y座標, までの移動方向コード(初期配置:0, ↑:1, →:3, ↓:5, ←:7)
enemyListは enemyNumber, 経路Number, 出撃タイミング (100 = 1 s) の順でリスト化
*/