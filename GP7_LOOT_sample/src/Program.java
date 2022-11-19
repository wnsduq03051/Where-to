import loot.GameFrame;
import loot.GameFrameSettings;


public class Program
{

	public static void main(String[] args)
	{
		int mode = 2;
		GameFrameSettings settings = new GameFrameSettings();
		
		if ( mode == 0 )
		{
			settings.window_title = "LOOT를 사용한 물리 예제";
			settings.canvas_width = 1024;
			settings.canvas_height = 768;
			
//			settings.gameLoop_interval_ns = 10000000;		//100FPS에 해당 - 이를 버틸 수 있는 컴퓨터는 그리 많지 않을테니 보통 실제 FPS는 이보다 떨어지게 됨
			settings.gameLoop_interval_ns = 16666666;		//약 60FPS에 해당
			//settings.gameLoop_interval_ns = 100000000;	//10FPS에 해당 - 화면이 초당 10번밖에 갱신되지 않으면 버벅거리는게 눈에 보임
			
			settings.gameLoop_use_virtualTimingMode = false;
			settings.numberOfButtons = 3;
			
			GameFrame window = new PhysicsSampleFrame(settings);
			window.setVisible(true);
		}
		
		if ( mode == 1 )
		{
//			GameFrameSettings settings = new GameFrameSettings();
			settings.window_title = "LOOT를 사용한 입력 예제";
			settings.canvas_width = 400;
			settings.canvas_height = 600;
			
			//settings.gameLoop_interval_ns = 10000000;		//100FPS에 해당 - '동시에 키를 입력'하는 상황이 상대적으로 적게 연출됨
			settings.gameLoop_interval_ns = 16666666;		//약 60FPS에 해당
			//settings.gameLoop_interval_ns = 100000000;	//10FPS에 해당 - '동시에 키를 입력'하는 상황이 꽤 자주 연출됨
			
			settings.gameLoop_use_virtualTimingMode = false;
			settings.numberOfButtons = 5;
			
			GameFrame window = new InputSampleFrame(settings);
			window.setVisible(true);
		}
		
		if ( mode == 2 )
		{
//			GameFrameSettings settings = new GameFrameSettings();
			settings.window_title = "LOOT를 사용한 음원 재생 예제";
			settings.canvas_width = 160;
			settings.canvas_height = 60;
			
			settings.gameLoop_interval_ns = 10000000;		//100FPS에 해당 - 이 예제는 그리기 작업이 거의 없으므로 프레임을 빠르게 실행시키기 위해 이렇게 설정
			
			settings.gameLoop_use_virtualTimingMode = false;
			settings.numberOfButtons = 1;
			
			GameFrame window = new AudioSampleFrame(settings);
			window.setVisible(true);
		}
	}
}
