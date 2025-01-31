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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
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
	JFrame frame = new JFrame();
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
    		new Stage1InitialData();
    	});
    	stageSelectButton2.addActionListener(e->{
    		showMessageDialog(frame,"現在調整中");
    	});
    	stageSelectButton3.addActionListener(e->{
    		showMessageDialog(frame,"現在調整中");
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
    	
    	stageSelectButton2.setText("Stage2");
    	stageSelectButton2.setFont(font);
    	stageSelectButton2.setBounds(140,50,100,100);
    	
    	stageSelectButton3.setText("Stage3");
    	stageSelectButton3.setFont(font);
    	stageSelectButton3.setBounds(260,50,100,100);
    }
}



//♦ゲーム処理♦

//ゲーム画面表示
class StageFrame {
	protected StageFrame(List<BufferedImage> fieldImageList, List<List<Integer>> nearUnitPlacementList, List<List<Integer>> farUnitPlacementList, List<List<Integer>> allUnitPlacementList, List<List<List<Integer>>> moveList,	List<List<Integer>> enemyList) {
		JFrame gameFrame = new JFrame("Stage1");
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setSize(1235, 600);
		gameFrame.setResizable(false);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.add(new StagePanel(fieldImageList, nearUnitPlacementList, farUnitPlacementList, allUnitPlacementList, moveList, enemyList));
		gameFrame.setVisible(true);
	}
}

class StagePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener{
	Timer timer;
	long gameTime;
	JLabel actionCommentLabel = new JLabel();
	JButton rangeONButton = new JButton();
	boolean existsRangeDisplay = true;
	List<BufferedImage> fieldImageList;
	List<List<Integer>> nearUnitPlacementList;
	List<List<Integer>> farUnitPlacementList;
	List<List<Integer>> allUnitPlacementList;
	List<List<List<Integer>>> moveList;
	List<List<Integer>> enemyList;
	SoldierInitialData SoldierInitialData = new SoldierInitialData();
	PlacementInitialData PlacementInitialData = new PlacementInitialData();
	EnemyInitialData EnemyInitialData = new EnemyInitialData();
	List<BufferedImage> defaultSodierImageList = new ArrayList<>();
	List<List<Integer>> defaultSodierStatusList = new ArrayList<>();
	List<List<ValueRange>> defualtUnitPlacementList = new ArrayList<>();
	List<BufferedImage> defaultPlacementImageList = new ArrayList<>();
	List<BufferedImage> defaultEnemyImageList = new ArrayList<>();
	List<List<Integer>> defaultEnemyStatusList = new ArrayList<>();
	List<BufferedImage> enemyImageList = new ArrayList<>();
	List<List<Integer>> enemyStatusList = new ArrayList<>();
	List<List<Integer>> enemyPlacementList = new ArrayList<>();
	List<EnemyMotion> EnemyMotionList = new ArrayList<>();
	List<EnemyMove> EnemyMoveList = new ArrayList<>();
	final static int UNIT_SIZE = 60;
	final static int CORRECTION_POSITION = 15;
	int mouseX;
	int mouseY;
	int unitNumber;
	boolean canSelect;
	List<BufferedImage> soldierImageList = new ArrayList<>();
	List<List<Integer>> soldierStatusList = new ArrayList<>();
	List<List<Integer>> soldierPlacementList = new ArrayList<>();
	List<List<Integer>> residueNearUnitPlacementList = new ArrayList<>();
	List<List<Integer>> residueFarUnitPlacementList = new ArrayList<>();
	List<List<Integer>> residueAllUnitPlacementList = new ArrayList<>();
	List<SoldierMotion> SoldierMotionList = new ArrayList<>();
	AttackJudgment AttackJudgment = new AttackJudgment();
	boolean existsGameOver;
	Font font = new Font("Aria", Font.BOLD|Font.ITALIC, 150);
	
	//初期設定取り込み
    protected StagePanel(List<BufferedImage> fieldImageList, List<List<Integer>> nearUnitPlacementList, List<List<Integer>> farUnitPlacementList, List<List<Integer>> allUnitPlacementList, List<List<List<Integer>>> moveList,	List<List<Integer>> enemyList) {
    	timer = new Timer(10, this);
		timer.start();
    	addMouseListener(this);
    	addMouseMotionListener(this);
    	setBackground(new Color(240, 170, 80));
    	add(actionCommentLabel);
    	add(rangeONButton);
    	rangeONButton.addActionListener(e->{
    		existsRangeDisplay = (existsRangeDisplay)? false: true;
    	});
		this.fieldImageList = new ArrayList<>(fieldImageList);
		this.nearUnitPlacementList = new ArrayList<>(nearUnitPlacementList);
		this.farUnitPlacementList = new ArrayList<>(farUnitPlacementList);
		this.allUnitPlacementList = new ArrayList<>(allUnitPlacementList);
		this.moveList = new ArrayList<>(moveList);
		this.enemyList = new ArrayList<>(enemyList);
		residueNearUnitPlacementList = Stream.concat(nearUnitPlacementList.stream(), allUnitPlacementList.stream())
				.collect(Collectors.toList());
		residueFarUnitPlacementList = Stream.concat(farUnitPlacementList.stream(), allUnitPlacementList.stream())
				.collect(Collectors.toList());
		residueAllUnitPlacementList = Stream.concat(residueNearUnitPlacementList.stream(), farUnitPlacementList.stream())
				.collect(Collectors.toList());
		defaultSodierImageList = SoldierInitialData.soldierImage();
		defaultSodierStatusList = SoldierInitialData.soldierStatus();
		defualtUnitPlacementList = SoldierInitialData.unitPlacement();
		defaultPlacementImageList = PlacementInitialData.placementImage();
		defaultEnemyImageList = EnemyInitialData.enemyImage();
		defaultEnemyStatusList = EnemyInitialData.enemyStatus();
		for(int i = 0; i < this.enemyList.size(); i ++) {
			enemyImageList.add(defaultEnemyImageList.get(this.enemyList.get(i).get(0) * 2));
			enemyStatusList.add(new ArrayList<>(defaultEnemyStatusList.get(this.enemyList.get(i).get(0))));
			enemyPlacementList.add(new ArrayList<>(this.moveList.get(this.enemyList.get(i).get(1)).get(0)));
			EnemyMoveList.add(new EnemyMove(this.moveList.get(this.enemyList.get(i).get(1)), this.enemyList.get(i), enemyStatusList.get(i), enemyPlacementList.get(i)));
			EnemyMotionList.add(new EnemyMotion(enemyStatusList.size() - 1, enemyImageList, defaultEnemyImageList.get(this.enemyList.get(i).get(0) * 2), defaultEnemyImageList.get(this.enemyList.get(i).get(0) * 2 + 1), enemyStatusList, EnemyMoveList.get(i)));
		}
    }
    
	//画面の描写
    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		initialDraw(g);
		atackRange(g);
		enemyDraw(g);
		soldierDraw(g);
		placementDraw(g);
		gameOverDraw(g);
    }
    
    //ユニット操作
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		for(int i = 0; i < defualtUnitPlacementList.size(); i++) {
			if(defualtUnitPlacementList.get(i).get(0).isValidIntValue(mouseX)
					&& defualtUnitPlacementList.get(i).get(1).isValidIntValue(mouseY)){
				unitNumber = i;
				canSelect = true;
	    		break;
	    	}
		}
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		if(unitNumber <= 2) {
			for(List<Integer> i : residueNearUnitPlacementList) {
				if(placementDetermination(i)) {
					break;
				}
			}
		}else if(3 <= unitNumber && unitNumber <= 5){
			for(List<Integer> i : residueFarUnitPlacementList) {
				if(placementDetermination(i)) {
					break;
				}
			}
		}else if(6 <= unitNumber) {
			for(List<Integer> i : residueAllUnitPlacementList) {
				if(placementDetermination(i)) {
					break;
				}
			}
		}
		canSelect = false;
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		repaint();
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
		actionCommentLabel.setText("↓ユニットを配置してください↓");
		actionCommentLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 13));
		actionCommentLabel.setBounds(1010,15,300,20);
		rangeONButton.setText("射程表示");
		rangeONButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
		rangeONButton.setBounds(1060,475,100,50);
		g.drawImage(fieldImageList.get(0), 0, 0, this);
		for(int i = 0; i < defaultSodierImageList.size(); i += 2) {
			g.drawImage(defaultSodierImageList.get(i), 1015 + i % 4 * 50, 55 + i / 4 * 100, this);
		}
		for(List<Integer> i : nearUnitPlacementList) {
			g.drawImage(defaultPlacementImageList.get(0), i.get(0), i.get(1), this);
		}
		for(List<Integer> i : farUnitPlacementList) {
			g.drawImage(defaultPlacementImageList.get(1), i.get(0), i.get(1), this);
		}
		for(List<Integer> i : allUnitPlacementList) {
			g.drawImage(defaultPlacementImageList.get(2), i.get(0), i.get(1), this);
		}
	}
	
	//射程
	private void atackRange(Graphics g) {
		if(existsRangeDisplay) {
			if(!(soldierStatusList.size() == 0)) {
				for(int i = 0; i < soldierStatusList.size(); i++) {
					g.setColor(new Color(255, 0, 0, 30));
					g.fillOval(soldierPlacementList.get(i).get(0) + CORRECTION_POSITION - soldierStatusList.get(i).get(4),
							soldierPlacementList.get(i).get(1) + CORRECTION_POSITION - soldierStatusList.get(i).get(4),
							soldierStatusList.get(i).get(4) * 2 + UNIT_SIZE,
							soldierStatusList.get(i).get(4) * 2 + UNIT_SIZE);
				}
			}
			for(int i = 0; i < enemyStatusList.size(); i++) {
				if(enemyList.get(i).get(2) <= gameTime) {
					g.setColor(new Color(255, 0, 0, 30));
					g.fillOval(enemyPlacementList.get(i).get(0) + CORRECTION_POSITION - enemyStatusList.get(i).get(4),
							enemyPlacementList.get(i).get(1) + CORRECTION_POSITION - enemyStatusList.get(i).get(4),
							enemyStatusList.get(i).get(4) * 2 + UNIT_SIZE,
							enemyStatusList.get(i).get(4) * 2 + UNIT_SIZE);
				}
			}
		}
	}
	
	//敵移動
	private void enemyDraw(Graphics g) {
		for(int i = 0; i < enemyList.size(); i++) {
			if(enemyList.get(i).get(2) == gameTime) {
				EnemyMoveList.get(i).enemyMoveStart();
			}else if(enemyList.get(i).get(2) < gameTime && 0 < enemyStatusList.get(i).get(1)) {
				if(AttackJudgment.judgment(enemyStatusList.get(i).get(4), enemyPlacementList.get(i), soldierPlacementList, soldierStatusList)) {
					EnemyMotionList.get(i).enemyMotionStart();
				}else {
					EnemyMotionList.get(i).enemyMotionStop();
				}
				HPDraw(g, enemyStatusList.get(i), enemyPlacementList.get(i));
				g.drawImage(enemyImageList.get(i), enemyPlacementList.get(i).get(0), enemyPlacementList.get(i).get(1), this);
			}
			if(EnemyMoveList.get(i).gameOver()) {
				existsGameOver = true;
			}
		}
	}
	
	//配置ユニット
	private void soldierDraw (Graphics g) {
		if(!(soldierImageList.size() == 0)) {
			for(int i = 0; i < soldierImageList.size(); i++) {
				if(0 < soldierStatusList.get(i).get(1)) {
					if(AttackJudgment.judgment(soldierStatusList.get(i).get(4), soldierPlacementList.get(i), enemyPlacementList, enemyStatusList)) {
						SoldierMotionList.get(i).soldierMotionStart();
					}else {
						SoldierMotionList.get(i).soldierMotionStop();
					}
					HPDraw(g, soldierStatusList.get(i), soldierPlacementList.get(i));
					g.drawImage(soldierImageList.get(i), soldierPlacementList.get(i).get(0), soldierPlacementList.get(i).get(1), this);
				}
			}
		}
	}
	
	//ユニット移動
	private void placementDraw(Graphics g) {
		if(canSelect) {
			g.drawImage(defaultSodierImageList.get(unitNumber * 2), mouseX - 50, mouseY - 50, this);
		}
	}
	
	//ゲームオーバー
	private void gameOverDraw(Graphics g) {
		if(existsGameOver) {
	    	Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(10));
			g2d.draw(font.createGlyphVector(g2d.getFontRenderContext(),"GAME").getOutline(400, 250));
			g2d.draw(font.createGlyphVector(g2d.getFontRenderContext(),"OVER").getOutline(400, 400));
			g2d.setFont(font);
			g2d.setColor(Color.RED);
			g2d.drawString("GAME",400,250);
			g2d.drawString("OVER",400,400);
			for(int i = 0; i < SoldierMotionList.size(); i ++) {
				SoldierMotionList.get(i).soldierMotionStop();
			}
			for(int i = 0; i < EnemyMotionList.size(); i ++) {
				EnemyMotionList.get(i).enemyMotionStop();
			}
			for(int i = 0; i < EnemyMoveList.size(); i ++) {
				EnemyMoveList.get(i).enemyMoveStop();
			}
			timer.stop();
			removeMouseListener(this);
	    	removeMouseMotionListener(this);
		}
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
			soldierImageList.add(defaultSodierImageList.get(unitNumber * 2));
			soldierStatusList.add(new ArrayList<>(defaultSodierStatusList.get(unitNumber)));
			soldierPlacementList.add(Arrays.asList(placementList.get(0) - CORRECTION_POSITION, placementList.get(1) - CORRECTION_POSITION));
			SoldierMotionList.add(new SoldierMotion(soldierStatusList.size() - 1, soldierImageList, defaultSodierImageList.get(unitNumber * 2), defaultSodierImageList.get(unitNumber * 2 + 1), soldierStatusList));
			if(residueNearUnitPlacementList.contains(placementList)) {
				residueNearUnitPlacementList.remove(residueNearUnitPlacementList.indexOf(placementList));
			}
			if(residueFarUnitPlacementList.contains(placementList)) {
				residueFarUnitPlacementList.remove(residueFarUnitPlacementList.indexOf(placementList));
			}
			if(residueAllUnitPlacementList.contains(placementList)) {
				residueAllUnitPlacementList.remove(residueAllUnitPlacementList.indexOf(placementList));
			}
			return true;
		}
		return false;
	}
}

//extends用基本行動
class Motion{
	Timer timer;
	int number;
	List<BufferedImage> imageList;
	BufferedImage defaultNormalImage;
	BufferedImage defaultActonImage;
	List<Integer> statusList;
	
	protected void motion(int number, List<BufferedImage> imageList, BufferedImage defaultNormalImage, BufferedImage defaultActonImage, List<List<Integer>> statusList) {
		this.number = number;
		this.imageList = imageList;
		this.defaultNormalImage = defaultNormalImage;
		this.defaultActonImage = defaultActonImage;
		this.statusList = statusList.get(this.number);
	}
}

//自軍行動
class SoldierMotion extends Motion implements ActionListener{
	protected SoldierMotion(int soldierNumber, List<BufferedImage> soldierImageList, BufferedImage defaultNormalSoldierImage, BufferedImage defaultActonSoldierImage, List<List<Integer>> soldierStatusList) {
		super.motion(soldierNumber, soldierImageList, defaultNormalSoldierImage, defaultActonSoldierImage, soldierStatusList);
		timer = new Timer(statusList.get(5),this);
		timer.setInitialDelay(100);
	}
	
	protected void soldierMotionStart() {
		if(!timer.isRunning()) {
			timer.restart();
		}
	}
	
	protected void soldierMotionStop() {
		imageList.set(number, defaultNormalImage);
		timer.stop();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		imageList.set(number, 
				(imageList.get(number) == defaultActonImage)
				? defaultNormalImage: defaultActonImage);
	}
}

//敵軍行動
class EnemyMotion extends Motion implements ActionListener{
	EnemyMove EnemyMove;
	
	protected EnemyMotion(int enemyNumber, List<BufferedImage> enemyImageList, BufferedImage defaultNormalEnemyImage, BufferedImage defaultActonEnemyImage, List<List<Integer>> enemyStatusList, EnemyMove EnemyMove) {
		super.motion(enemyNumber, enemyImageList, defaultNormalEnemyImage, defaultActonEnemyImage, enemyStatusList);
		this.EnemyMove = EnemyMove;
		timer = new Timer(statusList.get(5),this);
		timer.setInitialDelay(100);
	}
	
	protected void enemyMotionStart() {
		if(!timer.isRunning()) {
			timer.restart();
		}
	}
	
	protected void enemyMotionStop() {
		imageList.set(number, defaultNormalImage);
		timer.stop();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(imageList.get(number) == defaultActonImage) {
			imageList.set(number, defaultNormalImage);
			EnemyMove.enemyMoveStart();
		}else {
			imageList.set(number, defaultActonImage);
			EnemyMove.enemyMoveStop();
		}
	}
}

class EnemyMove implements ActionListener{
	Timer timer;
	int x;
	int y;
	List<List<Integer>> moveList;
	List<Integer> enemyList;
	List<Integer> enemyStatusList;
	List<Integer> enemyPlacementList;
	int route = 1;
	boolean existsGameOver;
	
	protected EnemyMove(List<List<Integer>> moveList, List<Integer> enemyList, List<Integer> enemyStatusList, List<Integer> enemyPlacementList) {
		this.moveList = moveList;
		this.enemyList = enemyList;
		this.enemyStatusList = enemyStatusList;
		this.enemyPlacementList = enemyPlacementList;
		timer = new Timer(enemyStatusList.get(6), this);
		timer.setInitialDelay(0);
	}
	
	protected void enemyMoveStart() {
		if(!timer.isRunning()) {
			timer.start();
		}
	}
	
	protected void enemyMoveStop() {
		timer.stop();
	}
	
	protected boolean gameOver() {
		return existsGameOver;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
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
				break;
			}
			enemyPlacementList.set(0, enemyPlacementList.get(0) + x);
			enemyPlacementList.set(1, enemyPlacementList.get(1) + y);
			if(Math.abs(moveList.get(route).get(0)	- enemyPlacementList.get(0) + x) <= 5
					&& Math.abs(moveList.get(route).get(1)	- enemyPlacementList.get(1) + y) <= 5) {
				route++;
			}
		}catch(Exception gameOver) {
			existsGameOver = true;
		}
	}
}

//攻撃可否
class AttackJudgment{
	double distance;
	double minDistance;
	
	protected boolean judgment(int atackRange, List<Integer> placement, List<List<Integer>> anotherPlacementList, List<List<Integer>> anotherStatusList) {
		minDistance = 0;
		for(int i = 0; i < anotherPlacementList.size(); i++) {
			if(!(anotherStatusList.get(i).get(1) == 0)) {
				distance = (double) Math.sqrt(Math.pow(placement.get(0) - anotherPlacementList.get(i).get(0), 2) + Math.pow(placement.get(1) - anotherPlacementList.get(i).get(1), 2));
				if(minDistance == 0) {
					minDistance = distance;
				}else {
					minDistance = (minDistance <= distance)? minDistance: distance;
				}
			}
		}
		if(minDistance == 0) {
			return false;
		}else {
			return (minDistance <= atackRange + StagePanel.UNIT_SIZE)? true: false;
		}
	}
}



//♦基本データ♦

//画像取り込み
class InputImage{
	BufferedImage image;
	List<BufferedImage> imageList = new ArrayList<>();
	int resizeWidth;
	int resizeHeight;
	BufferedImage resizeImage;
	BufferedImage finalImage;
	
	protected List<BufferedImage> Input(List<String> imageNameList){
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
class SoldierInitialData{
	List<String> sodierNameList = Arrays.asList(
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
	List<List<Integer>> soldierStatusList = Arrays.asList(
			Arrays.asList(1000, 1000, 100, 100, 20, 1000),//sord
			Arrays.asList(700, 700, 70, 70, 50, 700),//spear
			Arrays.asList(3000, 3000, 30, 300, 20, 1500),//shield
			Arrays.asList(700, 700, 70, 70, 100, 1000),//dart
			Arrays.asList(600, 600, 50, 50, 150, 500),//bow
			Arrays.asList(800, 800, 150, 70, 200, 2000),//gun
			Arrays.asList(1000, 1000, 50, 100, 100, 1000),//hammer
			Arrays.asList(1000, 1000, 0, 100, 100, 1000));//fan
	List<List<ValueRange>> unitPlacementList = Arrays.asList(
			Arrays.asList(ValueRange.of(1040, 1085), ValueRange.of(80, 125)),//sord
			Arrays.asList(ValueRange.of(1140, 1185), ValueRange.of(80, 125)),//spear
			Arrays.asList(ValueRange.of(1040, 1080), ValueRange.of(175, 225)),//shield
			Arrays.asList(ValueRange.of(1135, 1185), ValueRange.of(175, 225)),//dart
			Arrays.asList(ValueRange.of(1045, 1085), ValueRange.of(280, 320)),//bow
			Arrays.asList(ValueRange.of(1140, 1180), ValueRange.of(280, 320)),//gun
			Arrays.asList(ValueRange.of(1035, 1085), ValueRange.of(375, 425)),//hammer
			Arrays.asList(ValueRange.of(1135, 1190), ValueRange.of(380, 420)));//fan
	InputImage InputImage = new InputImage();
	List<BufferedImage> sodierImageList = new ArrayList<>();
	
	protected List<BufferedImage> soldierImage() {
		sodierImageList = InputImage.Input(sodierNameList);
		return sodierImageList;
	}
	
	protected List<List<Integer>> soldierStatus() {
		return soldierStatusList;
	}
	
	protected List<List<ValueRange>> unitPlacement(){
		return unitPlacementList;
	}
}

//配置位置データ
class PlacementInitialData{
	List<String> placementNameList = Arrays.asList(
			"image/soldier/near placement.png",
			"image/soldier/far placement.png",
			"image/soldier/all placement.png");
	InputImage InputImage = new InputImage();
	List<BufferedImage> placementImageList = new ArrayList<>();
	
	protected List<BufferedImage> placementImage(){
		placementImageList = InputImage.Input(placementNameList);
		return placementImageList;
	}
}

//敵軍データ
class EnemyInitialData{
	List<String> enemyNameList = Arrays.asList(
			"image/enemy/blue slime normal.png",
			"image/enemy/blue slime action.png",
			"image/enemy/green slime normal.png",
			"image/enemy/green slime action.png",
			"image/enemy/red slime normal.png",
			"image/enemy/red slime action.png",
			"image/enemy/yellow slime normal.png",
			"image/enemy/yellow slime action.png");
	List<List<Integer>> enemyStatusList = Arrays.asList(
			Arrays.asList(1000, 1000, 20, 20, 20, 1000, 100),//1: blue slime
			Arrays.asList(2000, 2000, 20, 20, 20, 1000, 100),//2: green slime
			Arrays.asList(1000, 1000, 40, 20, 20, 1000, 100),//3: red slime
			Arrays.asList(1000, 1000, 20, 20, 20, 1000, 50));//4: yellow slime
	InputImage InputImage = new InputImage();
	List<BufferedImage> enemyImageList = new ArrayList<>();
	
	protected List<BufferedImage> enemyImage(){
		enemyImageList = InputImage.Input(enemyNameList);
		return enemyImageList;
	}
	
	protected List<List<Integer>> enemyStatus(){
		return enemyStatusList;
	}
}



//Stage1データ
class Stage1InitialData {
	List<String> fieldNameList = Arrays.asList(
			"image/field/stage1-1.png",
			"image/field/stage1-2.png",
			"image/field/stage1-3.png");
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
	InputImage InputImage = new InputImage();
	List<BufferedImage> fieldImageList = new ArrayList<>();
	
	protected Stage1InitialData() {
		fieldImageList = InputImage.Input(fieldNameList);
		new StageFrame(fieldImageList, nearUnitPlacementList, farUnitPlacementList, allUnitPlacementList, moveList, enemyList);
	}
}

/*
ステージはパワポの1画面の大きさ
キャラはパワポの点線1マスの大きさで作ること
キャラのステータスは 最大HP, 残存HP, 攻撃, 防御, 射程, 攻撃速度, (敵のみ 移動速度) の順でリスト化
画像の背景はColor(254, 254 ,254)にする(この色が透明色に置き換わる)
unitNumber, enemyNumberは各statusListの配置順
moveListは①List:経路Number, ②List:経路での移動変化順, ③List:x座標, y座標, までの移動方向コード(初期配置:0, ↑:1, →:3, ↓:5, ←:7)
enemyListは enemyNumber, 経路Number, 出撃タイミング (100 = 1 s) の順でリスト化
*/