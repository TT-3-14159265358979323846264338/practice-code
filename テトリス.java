package テトリス;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class テトリス{
	public static void main(String[] args){
		frame game=new frame();
		game.add(new panel());
		game.setVisible(true);
	}
}



//画面の表示
class frame extends JFrame{
	public frame(){
		setTitle("テトリス");
		setSize(350,500);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}



//テトリス本体表示
class panel extends JPanel implements KeyListener, ActionListener{
	//初期設定	
	field field=new field();
	mino mino=new mino();
	Timer timer;
	JFrame flame=new JFrame();
	int end;
	
	//ゲーム操作
	public panel() {
		setFocusable(true);
		addKeyListener(this);
		timer = new Timer(200, this);
		timer.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//画面の描写
		field.draw(g,timer);
		mino.minodraw(g,timer);
		mino.actdraw(g,timer);
		
		//ボタンの表示
		JButton button1=new JButton("一時停止");
		button1.setBounds(20,422,90,30);
		add(button1);
		button1.addActionListener(e->{
			timer.stop();
			showMessageDialog(flame,"ゲームを一時停止しています");
			timer.restart();
		});
		
		JButton button2=new JButton("終了");
		button2.setBounds(120,422,80,30);
		add(button2);
		button2.addActionListener(e->{
			timer.stop();
			end=showConfirmDialog(flame,"ゲームを終了しますか","終了確認",YES_NO_OPTION , QUESTION_MESSAGE);
			if(end==0) {
				System.exit(0);
			}else {
				timer.restart();
			}
		});
		
		requestFocus();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		mino.keyact(e.getKeyCode(),timer);
		repaint();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		mino.timer();
		repaint();
	}
}



//初期画面
class field {
	//初期設定
	Color black=new Color(0,0,0);
	Font font1=new Font("Arial",Font.BOLD,25);
	Font font2=new Font("Arial",Font.PLAIN,20);
	Font font3=new Font("Arial",Font.BOLD|Font.ITALIC,10);
	
	int[][] cell=new int[27][20];
	
	//画面描写
	public void draw(Graphics g,Timer timer) {
		g.setColor(black);
		
		for(int j=1;j<13;j++) {
		    for(int i=0;i<25;i++) {
		        g.fillRect(5+j*15,40+i*15,14,14);
		    }
		}
		for(int j=15;j<20;j++) {
		    for(int i=0;i<4;i++) {
		        g.fillRect(5+j*15,40+i*15,14,14);
		    }
		}
		for(int j=15;j<20;j++) {
		    for(int i=6;i<10;i++) {
		        g.fillRect(5+j*15,40+i*15,14,14);
		    }
		}
		
	    g.fillPolygon(new int[] {247,287,267},new int[] {107,107,123},3);
	    g.fillPolygon(new int[] {247,287,267},new int[] {197,197,213},3);
	    
	    g.fillRect(265,300,5,20);
	    g.fillPolygon(new int[] {267,259,275},new int[] {286,300,300},3);
	    g.fillRect(265,340,5,20);
	    g.fillPolygon(new int[] {267,259,275},new int[] {374,360,360},3);
	    g.fillRect(235,327,20,5);
	    g.fillPolygon(new int[] {221,235,235},new int[] {329,321,337},3);
	    g.fillRect(280,327,20,5);
	    g.fillPolygon(new int[] {314,300,300},new int[] {329,321,337},3);
	    
		g.setFont(font1);
		g.drawString("next",240,235);
	    g.drawString("Score:",30,30);
	    g.drawString("z",235,410);
	    g.drawString("c",285,410);
	    
	    g.setFont(font2);
	    g.drawString("Action Key",220,280);
	    
	    g.setFont(font3);
	    g.drawString("Turn Left",215,425);
	    g.drawString("Turn Right",270,425);
	    g.drawString("Left",210,320);
	    g.drawString("Right",305,320);
	    g.drawString("Down (MAX)",274,295);
	    g.drawString("Down (one)",205,375);
	}
}



//ミノの表示
class mino{
	//初期設定
	defmino defmino=new defmino();
	
	Color black=new Color(0,0,0);//ミノ配色コード0
	Color yellow=new Color(255,255,0);//ミノ配色コード1
	Color red=new Color(255,0,0);//ミノ配色コード2
	Color blue=new Color(0,0,255);//ミノ配色コード3
	Color green=new Color(0,128,0);//ミノ配色コード4
	Color purple=new Color(128,0,128);//ミノ配色コード5
	Color cyan=new Color(0,255,255);//ミノ配色コード6
	Color pink=new Color(255,192,203);//ミノ配色コード7
	
	Font font1=new Font("Arial",Font.BOLD,25);
	Font font4=new Font("Aria",Font.BOLD|Font.ITALIC,80);
		
	int[][] cell=new int[27][20];//テトリスのフィールド、移動時の処理のため実際のフィールドより少し大きめ
	int[][][] mino=new int[3][3][4];//デフォルトで発生させるミノの定義
	int[][][] actmino=new int[3][3][4];//操作しているアクティブミノ
	int[] bottom=new int[3];//アクティブミノの一番下のマスの確認
	int[] right=new int[3];//アクティブミノ一番右のマスの確認
	int[] left=new int[3];//アクティブミノ一番左のマスの確認
	int cnt;//ミノ落下時に何マス落下したか
	boolean over;//ゲームオーバー判定
	int score;//合計スコア
	int chain;//同時消し時のスコアボーナス
	int time;//ゲーム開始からの時間
	int x;//水平方向の変化
	int turn;//回転方向の変化
	int turnp;//回転keyが入力された時に、回転後のミノ形状を指定する
	boolean act;//keyが入力された時に、そのkeyが実行可能であるか判定
	int MAXdown;//アクティブミノから移動可能な最下セルまでの距離
	int[] partdown=new int[3];//MAXdownを求めるための変数

	//フィールドミノの描写
	public void minodraw(Graphics g,Timer timer) {
		
		//ミノの移動可否判定
		if(time<3) {//ゲーム開始時にはNextにミノがないため、最初3回は単独でループさせる
			cnt=0;
		}else {
			//アクティブミノの下が、フィード外であるか、他ミノがあるか確認
			//開始時にはbottomがなく、cell[-1]でエラーを起こさないようにif(time<3)の中に移動
			for(int j=0;j<3;j++) {
				if(!(bottom[j]==0)) {
					if(cnt+bottom[j]==26||!(cell[cnt+bottom[j]-1][5+j+x]==0)) {
						for(int j2=0;j2<3;j2++) {
							for(int i2=0;i2<3;i2++) {
								if(cell[i2+cnt-1][5+j2+x]==0) {
									cell[i2+cnt-1][5+j2+x]=actmino[i2][j2][turn];
								}
							}
						}
						
						cnt=0;
						x=0;
						turn=0;
						break;
					}
				}
			}
		}
		
		//ミノの移動
		if(cnt==0) {
			//一度cnt++しておかないと、cnt=0でkeyが操作された時に、再度ミノが投入されてしまう
			cnt++;
			
			//ゲームオーバー確認
			for(int j=5;j<8;j++) {
			    for(int i=0;i<2;i++) {
			    	if(!(cell[i][j]==0)) {
			    		over=true;
			    		timer.stop();
			    	}
			    }
			}			
			
			//次のミノの呼び出し
			List<Integer> list=Arrays.asList(1,2,3,4,5,6,7);
			int randomcode=new Random().nextInt(list.size());
			Integer code=list.get(randomcode);
			defmino.def(code,mino);
			
			//次のミノを投入
			code=cell[8][17];
			
			for(int j=0;j<3;j++) {
			    for(int i=0;i<2;i++) {
			    	cell[7+i][16+j]=cell[1+i][16+j];
			    }
			}
			
			for(int j=0;j<3;j++) {
			    for(int i=0;i<2;i++) {
			    	cell[1+i][16+j]=mino[i][j][0];
			    }
			}
			
			defmino.def(code,mino);
			actdraw(g,timer);
			
			//消去処理
			chain=0;
			for(int i=2;i<25;i++) {
				for(int j=1;j<13;j++) {
					if(cell[i][j]==0){
						break;
					}
					if(j==12) {
						for(int i2=i;i2>=2;i2--) {
							for(int j2=1;j2<13;j2++) {
								cell[i2][j2]=cell[i2-1][j2];
							}
						}
						
						chain++;
					}
				}
			}
			
			if(!(chain==0)) {
				score+=120*(Math.pow(chain,3));
			}
			
		}
		
		//画面の描写
		for(int j=1;j<13;j++) {
		    for(int i=0;i<25;i++) {
		    	if(!((cell[i][j])==0)) {
		    		switch(cell[i][j]) {
		        	case 1:
		        		g.setColor(yellow);
		        		break;
		        	case 2:
		        		g.setColor(red);
		        		break;
		        	case 3:
		        		g.setColor(blue);
		        		break;
		        	case 4:
		        		g.setColor(green);
		        		break;
		        	case 5:
		        		g.setColor(purple);
		        		break;
		        	case 6:
		        		g.setColor(cyan);
		        		break;
		        	case 7:
		        		g.setColor(pink);
			        }
		        
			        g.fillRect(5+j*15,40+i*15,14,14);
		    	}
		        
		    }
		}
		for(int j=15;j<20;j++) {
		    for(int i=0;i<4;i++) {
		    	if(!((cell[i][j])==0)) {
		    		switch(cell[i][j]) {
		        	case 1:
		        		g.setColor(yellow);
		        		break;
		        	case 2:
		        		g.setColor(red);
		        		break;
		        	case 3:
		        		g.setColor(blue);
		        		break;
		        	case 4:
		        		g.setColor(green);
		        		break;
		        	case 5:
		        		g.setColor(purple);
		        		break;
		        	case 6:
		        		g.setColor(cyan);
		        		break;
		        	case 7:
		        		g.setColor(pink);
			        }
		        
			        g.fillRect(5+j*15,40+i*15,14,14);
		    	}
		        
		    }
		}
		for(int j=15;j<20;j++) {
		    for(int i=6;i<10;i++) {
		    	if(!((cell[i][j])==0)) {
		    		switch(cell[i][j]) {
		        	case 1:
		        		g.setColor(yellow);
		        		break;
		        	case 2:
		        		g.setColor(red);
		        		break;
		        	case 3:
		        		g.setColor(blue);
		        		break;
		        	case 4:
		        		g.setColor(green);
		        		break;
		        	case 5:
		        		g.setColor(purple);
		        		break;
		        	case 6:
		        		g.setColor(cyan);
		        		break;
		        	case 7:
		        		g.setColor(pink);
			        }
		        
			        g.fillRect(5+j*15,40+i*15,14,14);
		    	}
		        
		    }
		}
		
		g.setColor(black);
		g.setFont(font1);
		String finalscore=String.valueOf(score);
		g.drawString(finalscore,120,30);	    
	    
		if(over) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.setStroke(new BasicStroke(10));
			g2d.draw(font4.createGlyphVector(g2d.getFontRenderContext(),"GAME").getOutline(40, 150));
			g2d.draw(font4.createGlyphVector(g2d.getFontRenderContext(),"OVER").getOutline(40, 220));
			
			g2d.setFont(font4);
			g2d.setColor(red);
			g2d.drawString("GAME",40,150);
			g2d.drawString("OVER",40,220);
		}
	}
	
	//アクティブミノの描写
	public void actdraw(Graphics g,Timer timer) {
		//アクティブミノの登録
		for(int j=0;j<3;j++) {
		    for(int i=2;i>=0;i--) {	
		    	actmino[i][j][turn]=mino[i][j][turn];
		    }
		}
		
		//アクティブミノの形状確認
		for(int j=0;j<3;j++) {
		    for(int i=2;i>=0;i--) {	
		    	if(!(actmino[i][j][turn]==0)) {
		    		bottom[j]=i+1;
		    		break;
		    	}
		    	bottom[j]=0;
		    }
		}
		
		for(int i=0;i<3;i++) {
		    for(int j=2;j>=0;j--) {	
		    	if(!(actmino[i][j][turn]==0)) {
		    		right[i]=j+1;
		    		break;
		    	}
		    	right[i]=0;
		    }
		}
		
		for(int i=0;i<3;i++) {
		    for(int j=0;j<3;j++) {	
		    	if(!(actmino[i][j][turn]==0)) {
		    		left[i]=j+1;
		    		break;
		    	}
		    	left[i]=0;
		    }
		}
		
		//画面描写
		for(int j=0;j<3;j++) {
		    for(int i=0;i<3;i++) {
		    	if(!((actmino[i][j][turn])==0)) {
		    		switch(actmino[i][j][turn]) {
		        	case 1:
		        		g.setColor(yellow);
		        		break;
		        	case 2:
		        		g.setColor(red);
		        		break;
		        	case 3:
		        		g.setColor(blue);
		        		break;
		        	case 4:
		        		g.setColor(green);
		        		break;
		        	case 5:
		        		g.setColor(purple);
		        		break;
		        	case 6:
		        		g.setColor(cyan);
		        		break;
		        	case 7:
		        		g.setColor(pink);
			        }
		        
			        g.fillRect(20+(4+j+x)*15,40+(i+cnt-1)*15,14,14);
		    	}
		    }
		}
	}
	
	//key入力処理
	public void keyact(int key,Timer timer) {
		//回転key入力と同時に落下処理が発生するときに、他のミノに食い込むことがある
		//そのため、key入力時に落下処理を一時停止する
		//その結果、keyを連打するとミノが落下しない裏技ができる
		switch(key) {
		case KeyEvent.VK_RIGHT:
			//アクティブミノの右が、フィード外であるか、他ミノがあるか確認
			act=true;
			
			for(int i=0;i<3;i++) {
				if(!(right[i]==0)) {
					if(x+right[i]==8||!(cell[cnt+i-1][5+x+right[i]]==0)) {
						act=false;
						break;
					}
				}
			}
			
			//可能であれば移動させる
			if(act) {
				timer.stop();
				x++;
				timer.restart();
			}
			
			break;
			
		case KeyEvent.VK_LEFT:
			//アクティブミノの左が、フィード外であるか、他ミノがあるか確認
			act=true;
			
			for(int i=0;i<3;i++) {
				if(!(left[i]==0)) {
					if(x+left[i]==-3||!(cell[cnt+i-1][3+x+left[i]]==0)) {
						act=false;
						break;
					}
				}
			}
			
			//可能であれば移動させる
			if(act) {
				timer.stop();
				x--;
				timer.restart();
			}
			
			break;
			
		case KeyEvent.VK_C:
			//アクティブミノを回転した位置が、フィード外であるか、他ミノがあるか確認
			act=true;
			if(turn==3) {
				turnp=0;
			}else {
				turnp=turn+1;
			}
			
			if((bottom[2]==0&&x==6)||(bottom[0]==0&&x==-5)) {
				act=false;
			}else {
				for(int j=0;j<3;j++) {
					for(int i=0;i<3;i++) {
						if(!(cell[i+cnt-1][5+j+x]==0)&&!(actmino[i][j][turnp]==0)) {
							act=false;
							break;
						}
					}
				}
			}
			
			//可能であれば移動させる
			if(act) {
				timer.stop();
				turn=turnp;
				timer.restart();
			}
			
			break;
			
		case KeyEvent.VK_Z:
			//アクティブミノを回転した位置が、フィード外であるか、他ミノがあるか確認
			act=true;
			if(turn==0) {
				turnp=3;
			}else {
				turnp=turn-1;
			}
			
			if((bottom[2]==0&&x==6)||(bottom[0]==0&&x==-5)) {
				act=false;
			}else {
				for(int j=0;j<3;j++) {
					for(int i=0;i<3;i++) {
						if(!(cell[i+cnt-1][5+j+x]==0)&&!(actmino[i][j][turnp]==0)) {
							act=false;
							break;
						}
					}
				}
			}
			
			//可能であれば移動させる
			if(act) {
				timer.stop();
				turn=turnp;
				timer.restart();
			}
			
			break;
			
		case KeyEvent.VK_DOWN:
			//アクティブミノの下が、フィード外であるか、他ミノがあるか確認
			act=true;
			for(int j=0;j<3;j++) {
				if(!(bottom[j]==0)) {
					if(cnt+bottom[j]==26||!(cell[cnt+bottom[j]-1][5+j+x]==0)) {
						act=false;
					}
				}
			}
			
			//可能であれば移動させる
			if(act) {
				cnt++;
			}
			
			break;
			
		case KeyEvent.VK_UP:
			//アクティブミノが移動できる最下セルを探す
			MAXdown=0;
			partdown[0]=0;
			partdown[1]=0;
			partdown[2]=0;
			
			for(int j=0;j<3;j++) {
				if(!(bottom[j]==0)) {
					for(int i=0;i<25;i++) {
						if(cnt+bottom[j]+i==26||!(cell[i+cnt-1+bottom[j]][5+j+x]==0)){
							partdown[j]=i;
							break;
						}
					}
				}else {
					partdown[j]=30;
				}
			}
			
			MAXdown=partdown[0];
			for(int i=1;i<3;i++) {
				MAXdown=Math.min(MAXdown,partdown[i]);
			}
			
			//移動
			if(MAXdown>0) {
				timer.stop();
				for(int j=0;j<3;j++) {
					for(int i=0;i<3;i++) {
						if(cell[i+cnt-1+MAXdown][5+j+x]==0) {
							cell[i+cnt-1+MAXdown][5+j+x]=actmino[i][j][turn];
						}
					}
				}
				cnt=0;
				x=0;
				turn=0;
				timer.restart();
			}
		}
	}
	
	//timer処理
	public void timer() {
		cnt++;
		time++;
	}	
}



//デフォルトで発生するミノ設定
class defmino{
	public void def(int code,int[][][] mino) {
		switch(code) {
		case 0:
			for(int k=0;k<4;k++) {
				for(int j=0;j<3;j++) {
					for(int i=0;i<3;i++) {
						mino[i][j][k]=0;//初期化
					}
				}
			}
			break;
	    case 1:
	    	mino[0][0][0]=0   ;mino[0][1][0]=0   ;mino[0][2][0]=0   ;//Imino
	    	mino[1][0][0]=code;mino[1][1][0]=code;mino[1][2][0]=code;
	    	mino[2][0][0]=0   ;mino[2][1][0]=0   ;mino[2][2][0]=0   ;
	    	
	    	mino[0][0][1]=0   ;mino[0][1][1]=code;mino[0][2][1]=0   ;
	    	mino[1][0][1]=0   ;mino[1][1][1]=code;mino[1][2][1]=0   ;
	    	mino[2][0][1]=0   ;mino[2][1][1]=code;mino[2][2][1]=0   ;
	    	
	    	mino[0][0][2]=0   ;mino[0][1][2]=0   ;mino[0][2][2]=0   ;
	    	mino[1][0][2]=code;mino[1][1][2]=code;mino[1][2][2]=code;
	    	mino[2][0][2]=0   ;mino[2][1][2]=0   ;mino[2][2][2]=0   ;
	    	
	    	mino[0][0][3]=0   ;mino[0][1][3]=code;mino[0][2][3]=0   ;
	    	mino[1][0][3]=0   ;mino[1][1][3]=code;mino[1][2][3]=0   ;
	    	mino[2][0][3]=0   ;mino[2][1][3]=code;mino[2][2][3]=0   ;
	    	break;
	    case 2:	
	    	mino[0][0][0]=code;mino[0][1][0]=code;mino[0][2][0]=0   ;//Omino
	    	mino[1][0][0]=code;mino[1][1][0]=code;mino[1][2][0]=0   ;
	    	mino[2][0][0]=0   ;mino[2][1][0]=0   ;mino[2][2][0]=0   ;
	    	
	    	mino[0][0][1]=code;mino[0][1][1]=code;mino[0][2][1]=0   ;
	    	mino[1][0][1]=code;mino[1][1][1]=code;mino[1][2][1]=0   ;
	    	mino[2][0][1]=0   ;mino[2][1][1]=0   ;mino[2][2][1]=0   ;
	    	
	    	mino[0][0][2]=code;mino[0][1][2]=code;mino[0][2][2]=0   ;
	    	mino[1][0][2]=code;mino[1][1][2]=code;mino[1][2][2]=0   ;
	    	mino[2][0][2]=0   ;mino[2][1][2]=0   ;mino[2][2][2]=0   ;
	    	
	    	mino[0][0][3]=code;mino[0][1][3]=code;mino[0][2][3]=0   ;
	    	mino[1][0][3]=code;mino[1][1][3]=code;mino[1][2][3]=0   ;
	    	mino[2][0][3]=0   ;mino[2][1][3]=0   ;mino[2][2][3]=0   ;
	    	break;
	    case 3:
	    	mino[0][0][0]=code;mino[0][1][0]=code;mino[0][2][0]=code;//Tmino
	    	mino[1][0][0]=0   ;mino[1][1][0]=code;mino[1][2][0]=0   ;
	    	mino[2][0][0]=0   ;mino[2][1][0]=0   ;mino[2][2][0]=0   ;
	    	
	    	mino[0][0][1]=0   ;mino[0][1][1]=0   ;mino[0][2][1]=code;
	    	mino[1][0][1]=0   ;mino[1][1][1]=code;mino[1][2][1]=code;
	    	mino[2][0][1]=0   ;mino[2][1][1]=0   ;mino[2][2][1]=code;
	    	
	    	mino[0][0][2]=0   ;mino[0][1][2]=0   ;mino[0][2][2]=0   ;
	    	mino[1][0][2]=0   ;mino[1][1][2]=code;mino[1][2][2]=0   ;
	    	mino[2][0][2]=code;mino[2][1][2]=code;mino[2][2][2]=code;
	    	
	    	mino[0][0][3]=code;mino[0][1][3]=0   ;mino[0][2][3]=0   ;
	    	mino[1][0][3]=code;mino[1][1][3]=code;mino[1][2][3]=0   ;
	    	mino[2][0][3]=code;mino[2][1][3]=0   ;mino[2][2][3]=0   ;
	    	break;
	    case 4:
	    	mino[0][0][0]=0   ;mino[0][1][0]=code;mino[0][2][0]=code;//Smino
	    	mino[1][0][0]=code;mino[1][1][0]=code;mino[1][2][0]=0   ;
	    	mino[2][0][0]=0   ;mino[2][1][0]=0   ;mino[2][2][0]=0   ;
	    	
	    	mino[0][0][1]=0   ;mino[0][1][1]=code;mino[0][2][1]=0   ;
	    	mino[1][0][1]=0   ;mino[1][1][1]=code;mino[1][2][1]=code;
	    	mino[2][0][1]=0   ;mino[2][1][1]=0   ;mino[2][2][1]=code;
	    	
	    	mino[0][0][2]=0   ;mino[0][1][2]=0   ;mino[0][2][2]=0   ;
	    	mino[1][0][2]=0   ;mino[1][1][2]=code;mino[1][2][2]=code;
	    	mino[2][0][2]=code;mino[2][1][2]=code;mino[2][2][2]=0   ;
	    	
	    	mino[0][0][3]=code;mino[0][1][3]=0   ;mino[0][2][3]=0   ;
	    	mino[1][0][3]=code;mino[1][1][3]=code;mino[1][2][3]=0   ;
	    	mino[2][0][3]=0   ;mino[2][1][3]=code;mino[2][2][3]=0   ;
	    	break;
	    case 5:
	    	mino[0][0][0]=code;mino[0][1][0]=code;mino[0][2][0]=0   ;//Zmino
	    	mino[1][0][0]=0   ;mino[1][1][0]=code;mino[1][2][0]=code;
	    	mino[2][0][0]=0   ;mino[2][1][0]=0   ;mino[2][2][0]=0   ;
	    	
	    	mino[0][0][1]=0   ;mino[0][1][1]=0   ;mino[0][2][1]=code;
	    	mino[1][0][1]=0   ;mino[1][1][1]=code;mino[1][2][1]=code;
	    	mino[2][0][1]=0   ;mino[2][1][1]=code;mino[2][2][1]=0   ;
	    	
	    	mino[0][0][2]=0   ;mino[0][1][2]=0   ;mino[0][2][2]=0   ;
	    	mino[1][0][2]=code;mino[1][1][2]=code;mino[1][2][2]=0   ;
	    	mino[2][0][2]=0   ;mino[2][1][2]=code;mino[2][2][2]=code;
	    	
	    	mino[0][0][3]=0   ;mino[0][1][3]=code;mino[0][2][3]=0   ;
	    	mino[1][0][3]=code;mino[1][1][3]=code;mino[1][2][3]=0   ;
	    	mino[2][0][3]=code;mino[2][1][3]=0   ;mino[2][2][3]=0   ;
	    	break;
	    case 6:
	    	mino[0][0][0]=code;mino[0][1][0]=0   ;mino[0][2][0]=0   ;//Jmino
	    	mino[1][0][0]=code;mino[1][1][0]=code;mino[1][2][0]=code;
	    	mino[2][0][0]=0   ;mino[2][1][0]=0   ;mino[2][2][0]=0   ;
	    	
	    	mino[0][0][1]=0   ;mino[0][1][1]=code;mino[0][2][1]=code;
	    	mino[1][0][1]=0   ;mino[1][1][1]=code;mino[1][2][1]=0   ;
	    	mino[2][0][1]=0   ;mino[2][1][1]=code;mino[2][2][1]=0   ;
	    	
	    	mino[0][0][2]=0   ;mino[0][1][2]=0   ;mino[0][2][2]=0   ;
	    	mino[1][0][2]=code;mino[1][1][2]=code;mino[1][2][2]=code;
	    	mino[2][0][2]=0   ;mino[2][1][2]=0   ;mino[2][2][2]=code;
	    	
	    	mino[0][0][3]=0   ;mino[0][1][3]=code;mino[0][2][3]=0   ;
	    	mino[1][0][3]=0   ;mino[1][1][3]=code;mino[1][2][3]=0   ;
	    	mino[2][0][3]=code;mino[2][1][3]=code;mino[2][2][3]=0   ;
	    	break;
	    case 7:
	    	mino[0][0][0]=0   ;mino[0][1][0]=0   ;mino[0][2][0]=code;//Lmino
	    	mino[1][0][0]=code;mino[1][1][0]=code;mino[1][2][0]=code;
	    	mino[2][0][0]=0   ;mino[2][1][0]=0   ;mino[2][2][0]=0   ;
	    	
	    	mino[0][0][1]=0   ;mino[0][1][1]=code;mino[0][2][1]=0   ;
	    	mino[1][0][1]=0   ;mino[1][1][1]=code;mino[1][2][1]=0   ;
	    	mino[2][0][1]=0   ;mino[2][1][1]=code;mino[2][2][1]=code;
	    	
	    	mino[0][0][2]=0   ;mino[0][1][2]=0   ;mino[0][2][2]=0   ;
	    	mino[1][0][2]=code;mino[1][1][2]=code;mino[1][2][2]=code;
	    	mino[2][0][2]=code;mino[2][1][2]=0   ;mino[2][2][2]=0   ;
	    	
	    	mino[0][0][3]=code;mino[0][1][3]=code;mino[0][2][3]=0   ;
	    	mino[1][0][3]=0   ;mino[1][1][3]=code;mino[1][2][3]=0   ;
	    	mino[2][0][3]=0   ;mino[2][1][3]=code;mino[2][2][3]=0   ;
		}
	}
}


