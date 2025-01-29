package オセロ;
 import static javax.swing.JOptionPane.*;

import javax.swing.JFrame;
 
public class オセロ {
	public static void main(String[] args) {
		 
		  //マス、石、手番などの定義
		  int[][] cell=new int[8][8]; 
				  
		  for(int i=0; i<8; i++) {
		   for(int j=0; j<8; j++) {
		    cell[i][j]=0;
		   }
		  }
				  
		  cell[3][3]=1;
		  cell[3][4]=2;
		  cell[4][3]=2;
		  cell[4][4]=1;
		  
		  String[][] stone=new String[8][8];
				  
		  for(int i=0; i<8; i++) {
		   for(int j=0; j<8; j++) {
		    stone[i][j]="　";
		   }
		  }
		  
		  int turn=2;
		  String turncomment="　";
		  
		  int count0=0;
		  int count1=0;
		  int count2=0;
		  int finish=0;  
		  
		  JFrame flame=new JFrame();
		  
		  int resulti=0;
		  int resultj=0;
		  
		  int[] judge=new int[8];
		 
		  //ゲーム開始
		  while(true) {
		 
		   //盤面の表示
		   count0=0;
		   count1=0;
		   count2=0;
		   
		   for(int i=0; i<8; i++) {
		    for(int j=0; j<8; j++) {
		     if(cell[i][j]==2) {
		      stone[i][j]="○";
		      count2=count2+1;
		     }else if(cell[i][j]==1) {
		      stone[i][j]="●";
		      count1=count1+1;
		     }else {
		      stone[i][j]="　";	
		      count0=count0+1;
		     }
		    }
		   }
			
		   System.out.println("");
		   System.out.println("　 ８ ７ ６ ５ ４ ３ ２ １");
		   System.out.println("　────────────");
		   for(int i=0; i<=7; i++) {  
			System.out.print("　");   
		    for(int j=7; j>=0; j--) {	  
		     System.out.print(("|")+stone[j][i]);
		    }
		    System.out.print("| ");
		    System.out.println(i+1);
		    System.out.println("　────────────");
		   }  
		   
		   //手番変更
		   if(turn==2) {
			turn=1;
		    turncomment="●先手の番です";
		   }else {
			turn=2;   
			turncomment="○後手の番です";
		   }

		   //石を置けるかの判定
		   if(finish==2||count0==0) {
			break;
		   }  
		   
		   for(int i=0;i<8;i++) {
			resulti=i;
			for(int j=0;j<8;j++) {
			 resultj=j;
			 
			 if(cell[resultj][resulti]==0) {
			  sub sub= new sub();
			  sub.Method(cell,turn,resulti,resultj,judge);     

			  if(!(judge[0]==1)||!(judge[1]==1)||!(judge[2]==1)||!(judge[3]==1)||!(judge[4]==1)||!(judge[5]==1)||!(judge[6]==1)||!(judge[7]==1)) {
			   i=10;
			   break;
			  }
			 }
			}
		   }
		    
		   if(judge[0]==1&&judge[1]==1&&judge[2]==1&&judge[3]==1&&judge[4]==1&&judge[5]==1&&judge[6]==1&&judge[7]==1) {
		    showMessageDialog(flame,"石を置ける場所がありません"+"\n"+"次の手番に移ります"+"\n"+"2回連続で置けない場合はゲームを終了します");
		    finish=finish+1;
			continue;
		   }
		   
		   finish=0;
		   
		   //石の配置
		   while(true) {
			try {
				
			 //配置位置の入力
		     String resultstr=showInputDialog(flame,"石を置く列行の順で入力してください",turncomment, INFORMATION_MESSAGE);
		    
		     //ゲームの終了確認
		     if(resultstr==null) {
		      int end=showConfirmDialog(flame,"ゲームを終了しますか","終了確認",YES_NO_OPTION , QUESTION_MESSAGE);
		      if(end==0) {
		       System.exit(0);  
		      }else {
		       continue;
		      }
		     }
		     
		     //入力されたマスの読み込み
		     int resultint=Integer.parseInt(resultstr);
		     resulti=(resultint/1)%10-1;	
		     resultj=(resultint/10)%10-1;
		     
		     //周囲の石の確認
		     sub sub= new sub();
		     sub.Method(cell,turn,resulti,resultj,judge);
		     
		     //入力された石の判定
		     if(resulti<0||resulti>7||resultj<0||resultj>7||!(cell[resultj][resulti]==0)) {
		      showMessageDialog(flame, "入力されたマスには、既に石があるか、盤外です");    	 
		     }else if(judge[0]==1&&judge[1]==1&&judge[2]==1&&judge[3]==1&&judge[4]==1&&judge[5]==1&&judge[6]==1&&judge[7]==1) {
		      showMessageDialog(flame,"入力されたマスでは、相手の石を裏返しにできません");  
		     }else {
		       cell[resultj][resulti]=turn;
		       break;
		     }
		     
			}catch(Exception e) {
			 showMessageDialog(flame,"数字のみ入力してください");
			}
		   }
		    
		   //裏返し
		   if(!(judge[0]==1)){
		    for(int k=1;k<judge[0];k++) {
		     cell[resultj][resulti-k]=turn;
		    }
		   }
		   
		   if(!(judge[1]==1)){
			for(int k=1;k<judge[1];k++) {
			 cell[resultj-k][resulti-k]=turn;
			}
		   }    
		   
		   if(!(judge[2]==1)){
			for(int k=1;k<judge[2];k++) {
			 cell[resultj-k][resulti]=turn;
			}
		   }    
		  
		   if(!(judge[3]==1)){
			for(int k=1;k<judge[3];k++) {
			 cell[resultj-k][resulti+k]=turn;
			}
		   }    
		   
		   if(!(judge[4]==1)){
			for(int k=1;k<judge[4];k++) {
			 cell[resultj][resulti+k]=turn;
			}
		   }    
		   
		   if(!(judge[5]==1)){
			for(int k=1;k<judge[5];k++) {
			 cell[resultj+k][resulti+k]=turn;
			}
		   }    
		   
		   if(!(judge[6]==1)){
			for(int k=1;k<judge[6];k++) {
			 cell[resultj+k][resulti]=turn;
			}
		   }    
		   
		   if(!(judge[7]==1)){
			for(int k=1;k<judge[7];k++) {
			 cell[resultj+k][resulti-k]=turn;
			}
		   }    
		   
		  }	
		  
		  //ゲーム終了処理
		   if(count1>count2) {
		    showMessageDialog(flame,count1+"vs"+count2+"\n"+"で先手番の勝利です");
		   }else if(count1<count2) {
		    showMessageDialog(flame,count2+"vs"+count1+"\n"+"で後手番の勝利です");
		   }else {
			showMessageDialog(flame,count1+"vs"+count2+"\n"+"で引き分けです");  
		   }
		   
	}
}






		//周囲の石の確認(judge[0]を12時方向への判定用とし、judge[1]以降は時計回りへの判定に利用)

		class sub{
		 public void Method(int[][] cell,int turn,int resulti,int resultj,int[] judge){
			 
			 for(int k=0;k<8;k++) {
			  judge[k]=1;
		     }
			 
		     while(true) {
		      try {
		       if(cell[resultj][resulti-judge[0]]==0) {
		      	judge[0]=1;
		        break;
		       }else if(cell[resultj][resulti-judge[0]]==turn) {
		       	break;
		       }
		      }catch(Exception e){
		       judge[0]=1;  
		       break;
		      }
		     judge[0]=judge[0]+1; 
		     }  
		     
		     while(true) {
		      try {
		       if(cell[resultj-judge[1]][resulti-judge[1]]==0) {
		    	judge[1]=1;
		        break;
		       }else if(cell[resultj-judge[1]][resulti-judge[1]]==turn) {
		    	break;
		       }
		      }catch(Exception e){
		       judge[1]=1;  
		       break;
		      }
		      judge[1]=judge[1]+1; 
		     }  
		     
		     while(true) {
		      try {
		       if(cell[resultj-judge[2]][resulti]==0) {
		    	judge[2]=1;
		        break;
		       }else if(cell[resultj-judge[2]][resulti]==turn) {
		    	break;
		       }
		      }catch(Exception e){
		       judge[2]=1;  
		       break;
		      }
		      judge[2]=judge[2]+1; 
		     }   
		     
		     while(true) {
		      try {
		       if(cell[resultj-judge[3]][resulti+judge[3]]==0) {
		    	judge[3]=1;
		        break;
		       }else if(cell[resultj-judge[3]][resulti+judge[3]]==turn) {
		    	break;
		       }
		      }catch(Exception e){
		       judge[3]=1;  
		       break;
		      }
		      judge[3]=judge[3]+1; 
		     }    
		     
		     while(true) {
		      try {
		       if(cell[resultj][resulti+judge[4]]==0) {
		    	judge[4]=1;
		        break;
		       }else if(cell[resultj][resulti+judge[4]]==turn) {
		    	break;
		       }
		      }catch(Exception e){
		       judge[4]=1;  
		       break;
		      }
		      judge[4]=judge[4]+1; 
		     }

		     while(true) {
		      try {
		       if(cell[resultj+judge[5]][resulti+judge[5]]==0) {
		    	judge[5]=1;
		        break;
		       }else if(cell[resultj+judge[5]][resulti+judge[5]]==turn) {
		    	break;
		       }
		      }catch(Exception e){
		       judge[5]=1;  
		       break;
		      }
		      judge[5]=judge[5]+1; 
		     }      

		     while(true) {
		      try {
		       if(cell[resultj+judge[6]][resulti]==0) {
		    	judge[6]=1;
		        break;
		       }else if(cell[resultj+judge[6]][resulti]==turn) {
		    	break;
		       }
		      }catch(Exception e){
		       judge[6]=1;  
		       break;
		      }
		      judge[6]=judge[6]+1; 
		     }      
		     
		      while(true) {
		      try {
		       if(cell[resultj+judge[7]][resulti-judge[7]]==0) {
		    	judge[7]=1;
		        break;
		       }else if(cell[resultj+judge[7]][resulti-judge[7]]==turn) {
		    	break;
		       }
		      }catch(Exception e){
		       judge[7]=1;  
		       break;
		      }
		      judge[7]=judge[7]+1; 
		     } 	 
		 }
		}