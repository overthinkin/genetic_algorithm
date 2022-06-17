import java.util.Arrays;

public class GA {
	private int[][] dataMatrix;
	private int ch_len, FG_Num, limitSolution, thresholds_fit, eliteNum, newGenerNum;
	private chromosome[] generationPool;
	private chromosome[] elite_Chs;
	private chromosome bestSolution = null;	
	
	//데이터 + 염색체 길이 + 염색체 갯수 + 목적치 + th_fit + 엘리트수 + 교배횟수
	GA(int[][] m_dataMatrix, int m_ch_len, int m_FG_Num, int m_limitSolution, 
			int m_thresholds_fit, int m_eliteNum, int m_newGenerNum){
		this.dataMatrix = m_dataMatrix;
		this.ch_len = m_ch_len;
		this.FG_Num = m_FG_Num;
		this.limitSolution = m_limitSolution;
		this.thresholds_fit = m_thresholds_fit;
		this.eliteNum = m_eliteNum;
		this.newGenerNum = m_newGenerNum;
		
		generationPool = new chromosome[FG_Num];
		elite_Chs = new chromosome[eliteNum];
		for(int i=0; i<FG_Num;i++){
			generationPool[i] = new chromosome(ch_len);
			setFirst_G(generationPool[i]);
			calFit(generationPool[i]);
		}
		elite_Chs = setElite(generationPool);
		G_printf(generationPool,elite_Chs);//1세대 출력
		for(int i=0; i<newGenerNum;i++){
			generationPool = nextG(generationPool, elite_Chs);
			elite_Chs = setElite(generationPool);
			G_printf(generationPool,elite_Chs); //각 세대 확인
			if(elite_Chs[0].fit < limitSolution){//작은게 좋은거
				System.out.println("����� ���Ͽ����ϴ�.");
				bestSolution = elite_Chs[0];
				break;
			}
//          if(elite_Chs[0].useFit > limitSolution)//큰게 좋은거
//              System.out.println("설정한 최소값에 대한 결과값을 구하지 못하였습니다.");
//              bestSolution = elite_Chs[0];
//              break;
		}
		bestSolution = elite_Chs[0];
		System.out.println("������ �ּҰ��� ���� ������� ������ ���Ͽ����ϴ�.");
	}
	
	//외부로부터 최고값 출력
	public int getBestCost(){
		return bestSolution.fit;
	}
	
	//외부로부터 최고값의 경로 출력 
	public String getBestPath(){
		String path=null;
		for(int i=0; i<ch_len;i++){
			path+=bestSolution.order[i]+" ";
		}
		return path;
	}
	
	
	//첫번째 세대를 만들어 주는 함수 - 상황에 따라 변경
	private void setFirst_G(chromosome ch){
		int[] tempArr = new int[ch_len];
		for(int i=0;i<ch_len;i++)
			tempArr[i] = i;
		//셔플부분
		int seed;
		int temp;
		for(int i=1;i<ch_len;i++){
			seed=(int)(Math.random()*(ch_len-1))+1;
			temp = tempArr[i];
			tempArr[i]=tempArr[seed];
			tempArr[seed]=temp;
		}
		ch.order = tempArr;
	}
	
	//염색체에 대한 적합도를 측정한다.
	//크기가 작은게 좋은 경우 useFit으로 해서 계산한다. Why 그래야 나중에 계산하기 편함
	private void calFit(chromosome ch){
		int sumOfcost = 0;
		
		for(int i=0;i<ch_len-1;i++){
			sumOfcost += dataMatrix[ch.order[i]][ch.order[i+1]];
		}
		
		ch.fit = sumOfcost;
		if(thresholds_fit==0)//thresholds_fit이 0이면 Fit값이 큰게 좋은거
			ch.useFit = sumOfcost;
		else	//반대로 thresholds_fit값이 있으면 작은게 좋은거
			ch.useFit = thresholds_fit-sumOfcost;
	}
	
	 //엘리트 염색체를 구한다.
	private chromosome[] setElite(chromosome[] generationPool){
		chromosome temp[] =  new chromosome[eliteNum+1];
		chromosome result[] =  new chromosome[eliteNum];
		for(int i=0;i<eliteNum;i++)
			temp[i] = generationPool[i];
		for(int i=eliteNum;i<FG_Num;i++){
			temp[eliteNum] = generationPool[i];
			Arrays.sort(temp);
		}
		for(int i=0;i<eliteNum;i++){
			result[i] = temp[i];
		}
		return result;
	}
	
	//다음 세대를 구하는 함수이다.
	private chromosome[] nextG(chromosome[] generationPool, chromosome[] elite_Chs){
		chromosome[] newGeneration = new chromosome[FG_Num];
		int s_ch1=-1, s_ch2=-1;
		int allArea = 0;
		int area[] = new int [FG_Num];
		
		for(int i=0;i<FG_Num;i++)
			allArea+=generationPool[i].useFit;
		for(int i=1;i<FG_Num;i++){
			area[i]=(int)((float)generationPool[i].useFit/allArea*1000);
			area[i]+=area[i-1];
		}
		for(int i=0;i<eliteNum;i++)
			newGeneration[i] = elite_Chs[i];
		for(int i=eliteNum;i<FG_Num;i++){
			s_ch1=-1;
			s_ch2=-1;
			while(s_ch1 == s_ch2){
				s_ch1 = ch_Select(area);
				s_ch2 = ch_Select(area);
			}
			//System.out.println("ch1 : "+s_ch1+" ch2 : "+s_ch2);
			newGeneration[i] = crossover(generationPool[s_ch1],generationPool[s_ch2]);
			mutation(newGeneration[i]);
			calFit(newGeneration[i]);
		}
		return newGeneration;
	}
	
	//염색체를 교배하는 함수이다. 이 함수가 알고리즘의 성능을 좌우한다.
	private chromosome crossover(chromosome ch1, chromosome ch2) {
		int cutPoint = FG_Num/2;
		chromosome child_Ch = new chromosome(ch_len);
		for(int i=0; i<ch_len;i++){
			if(i<cutPoint)
				child_Ch.order[i] = ch1.order[i];
			else
				child_Ch.order[i] = ch2.order[i];
		}
		
		for(int i=cutPoint; i<ch_len;i++){
			if(child_Ch.isExist(ch2.order[i], i)){
				for(int j=0; j<cutPoint;j++){
					if(!child_Ch.isExist(ch2.order[j], i)){
						child_Ch.order[i] = ch2.order[j];
						break;
					}
				}	
			}
		}
		return child_Ch;
	}

	//크기에 따라 확률이 다른 선택방법이다
	private int ch_Select(int[] area){
		int seed;
		seed=(int)(Math.random()*1000)+1;
		for(int i=1;i<FG_Num;i++){
			if(area[i]>seed){
				return i;
				}
		}
		return 0;
	}
	
	//돌연변이 생성 함수이다.
	private void mutation(chromosome ch){
		int seed, changeAt, temp;
		for(int i=1;i<ch_len;i++){
			seed = (int)(Math.random()*1000)+1;
			if(seed<11){
				changeAt = (int)(Math.random()*ch_len-1)+1;
				temp = ch.order[i];
				ch.order[i] = ch.order[changeAt];
				ch.order[changeAt] = temp;
			}
		}
	}
	
//한 세대의 염색체를 모두 출력하는 함수이다.
	private void G_printf(chromosome[] generationPool, chromosome[] elite_Chs){
		for(int i=0;i<FG_Num;i++){
			System.out.print("[");
			for(int j=0;j<ch_len;j++){
				System.out.print(generationPool[i].order[j]+" ");
			}
			System.out.print("] : ");
			System.out.println(generationPool[i].useFit+" "+generationPool[i].fit);
		}
		System.out.println("========elite========");
		for(int i=0;i<eliteNum;i++){
			System.out.print("[");
			for(int j=0;j<ch_len;j++){
				System.out.print(elite_Chs[i].order[j]+" ");
			}
			System.out.print("] : ");
			System.out.println(elite_Chs[i].useFit+" "+elite_Chs[i].fit);
		}
		System.out.println("=========================");
	}
	
	//염색체 클레스이다.
	private class chromosome implements Comparable<chromosome>{
		int order[] = null;
		int fit=0;
		int useFit=0;
		chromosome(int ch_len){
			order = new int[ch_len];
		}
		public int compareTo(chromosome ch) {
            return ch.useFit-this.useFit;  // 자기 자신이 기준이 되면 오름차순 
		}								//상대가 기준이면 내림차순 feat.sort
		public boolean isExist(int data, int currentAt){
			for(int i=0;i<ch_len;i++){
				if(i!=currentAt){
					if (order[i]==data)
						return true;
				}
			}
			return false;
		}
	}
}
