import java.awt.Color;
import java.awt.event.MouseEvent;

import loot.GameFrame;
import loot.GameFrameSettings;

/**
 * LOOT의 음원 재생 기능을 중점적으로 활용한 예제입니다.
 * 
 * @author Racin
 *
 */
@SuppressWarnings("serial")
public class AudioSampleFrame extends GameFrame
{
	/*
	 * 조작:
	 * 
	 * 마우스 왼쪽 버튼:
	 * 
	 * 		누르고 있는 동안 음원 재생 시작
	 * 		- 최소 간격에 맞게 띄엄띄엄 재생을 시도
	 * 		- 계속 누르고 있으면 채널 수 설정에 맞게 여러 번 중첩되어 들리게 됨
	 * 
	 */
	
	/* -------------------------------------------
	 * 
	 * 상수 선언 부분
	 * 
	 */
	
	//각 Play() 호출 사이의 최소 간격을 지정합니다. (실제 간격은 프레임 실행에 따라 오차가 발생할 수 있습니다)
	static final long interval_play_ms = 600;
	
	//재생할 채널 수('동시에' 얼마나 많이 재생할 수 있는지)를 지정합니다. 
	static final int number_of_channels = 5;
	
	
	/* -------------------------------------------
	 * 
	 * 필드 선언 부분
	 * 
	 */
	
	//호출 간격을 재기 위해 마지막으로 재생을 시작했던 프레임의 실행 시각을 담아 두는 필드
	long timeStamp_lastPlayed = 0;

	
	/* -------------------------------------------
	 * 
	 * 메서드 정의 부분
	 * 
	 */
	
	public AudioSampleFrame(GameFrameSettings settings)
	{
		super(settings);
		
		inputs.BindMouseButton(MouseEvent.BUTTON1, 0);

		audios.LoadAudio("Audios/sample.wav", "sample", number_of_channels);
	}

	@Override
	public boolean Initialize()
	{
		//이 예제의 경우 초기화할 내용이 없음
		return true;
	}

	@Override
	public boolean Update(long timeStamp)
	{
		//입력을 버튼에 반영. 이 메서드는 항상 Update()의 시작 부분에서 호출해 주어야 함
		inputs.AcceptInputs();
		
		//버튼을 누르고 있으며 마지막으로 재생을 시작한지 일정 시간 이상 경과한 경우
		if ( inputs.buttons[0].isPressed == true && timeStamp - timeStamp_lastPlayed > interval_play_ms )
		{
			//현재 시각을 기록해 두고 재생 시작
			timeStamp_lastPlayed = timeStamp;
			audios.Play("sample");
		}
		
		return true;
	}

	@Override
	public void Draw(long timeStamp)
	{
		//그리기 작업 시작 - 이 메서드는 Draw()의 가장 위에서 항상 호출해 주어야 함
		BeginDraw();

		//버튼을 누르고 있다면 배경을 청산가리색으로 칠함
		if ( inputs.buttons[0].isPressed == true )
		{
			SetColor(Color.cyan);
			g.fillRect(0, 0, settings.canvas_width, settings.canvas_height);
			SetColor(Color.black);
			DrawString(30, 34, "여기를 눌러보세요");
		}
		//그렇지 않은 경우 배경을 기본 배경색으로 칠함
		else
		{
			ClearScreen();
			DrawString(30, 34, "여기를 눌러보세요");
		}
		
		//그리기 작업 끝 - 이 메서드는 Draw()의 가장 아래에서 항상 호출해 주어야 함
		EndDraw();
	}

}
