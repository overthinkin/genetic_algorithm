import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MST {
	public static void main(String[] argv){
		String fileName = "input.dat";
		int[][] dataMatrix = null;
		int nodeNum = 0;
		String temp;
	
		try {
			FileReader Fr = new FileReader(fileName); 
			BufferedReader Br = new BufferedReader(Fr); 
			try {
				if(Br.ready()){ 
					System.out.println("파일이 로드 되었습니다.");
					nodeNum = Integer.valueOf(Br.readLine());
					dataMatrix = new int [nodeNum][nodeNum];
					while((temp=Br.readLine())!=null){
						dataMatrix[Integer.valueOf(temp.split(" ")[0])]
								[Integer.valueOf(temp.split(" ")[1])] = Integer.valueOf(temp.split(" ")[2]);
						dataMatrix[Integer.valueOf(temp.split(" ")[1])]
								[Integer.valueOf(temp.split(" ")[0])]= Integer.valueOf(temp.split(" ")[2]);
					}
				}
			} catch (IOException e) {
				System.err.println("파일 로드를 실패했습니다..");
			} 
		} catch (FileNotFoundException e) { 
				System.err.println("다음 파일이 없습니다. : " + fileName); 
		}
		//입력확인
//		for(int i=0; i<nodeNum;i++){
//			for(int j=0; j<nodeNum;j++){
//				System.out.print(dataMatrix[i][j]+" "); 
//			}
//			System.out.println();
//		}
		
		//데이터 + 염색체 길이 + 염색체 갯수 + 목적치 + th_fit + 엘리트수 + 교배횟수
		GA mGA = new GA(dataMatrix, nodeNum,10,1000,80000,2,10000);

	}

}

