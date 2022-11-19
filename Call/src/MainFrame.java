import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.DrawableObject;

/**
 * LOOT의 입력 기능을 중점적으로 활용한 예제입니다.
 * 
 * @author racin
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends GameFrame
{

	//각 Play() 호출 사이의 최소 간격을 지정합니다. (실제 간격은 프레임 실행에 따라 오차가 발생할 수 있습니다)
	static long interval_play_ms = 0;
	// 배경음악 일시정지
	boolean isPaused = false;
	//재생할 채널 수('동시에' 얼마나 많이 재생할 수 있는지)를 지정합니다. 
	static final int number_of_channels = 5;


	class MainTitle extends DrawableObject
	{
		Button btn_Start;
		Button btn_Quit ;
		
		public MainTitle()
		{
			btn_Start = new Button("btn_Start", settings.canvas_width/2 - 60, 500, 132, 51, "image", "btn_Start");
			btn_Quit  = new Button("btn_Quit",  settings.canvas_width/2 - 60, 600, 132, 51, "image", "btn_Quit");

			x = 0;
			y = 0;
			width  = 1280;
			height = 900;
			
			image = images.GetImage("mainTitle");
		}		
	}	
	
	class Button extends DrawableObject
	{
		public Button(String id, int x, int y, int width, int height, String type, String text) {
			super.x = x;
			super.y = y;
			super.width = width;
			super.height = height;
			if ("image".equals(type))
			{
				image = images.GetImage(text);
			}
			
		}
		
	}
	
	/* -------------------------------------------
	 * 
	 * 필드 선언 부분
	 * 
	 */

	MainTitle mainTitle;
	//호출 간격을 재기 위해 마지막으로 재생을 시작했던 프레임의 실행 시각을 담아 두는 필드
	long timeStamp_lastPlayed = 0;

	
	/* -------------------------------------------
	 * 
	 * 메서드 정의 부분
	 * 
	 */

	public MainFrame(GameFrameSettings settings)
	{
		super(settings);
		System.out.println("MainFrame()");
		
		images.LoadImage("Images/title.jpg",     "mainTitle");
		images.LoadImage("Images/btn_Start.jpg", "btn_Start");
		images.LoadImage("Images/btn_Quit.jpg",  "btn_Quit");

		audios.LoadAudio("Audios/MainTheme.wav", "MainTheme", number_of_channels);

	}

	@Override
	public boolean Initialize()
	{
		mainTitle = new MainTitle();
		
		LoadColor(Color.MAGENTA);
		LoadFont("궁서체 18");		
		
		interval_play_ms = audios.GetLength("MainTheme");

		inputs.BindKey(KeyEvent.VK_Q,     0);
		inputs.BindKey(KeyEvent.VK_SPACE, 1);
		
		inputs.BindMouseButton(MouseEvent.BUTTON1, 2);

		return true;
	}

	@Override
	public boolean Update(long timeStamp)
	{
		//입력을 버튼에 반영. 이 메서드는 항상 Update()의 시작 부분에서 호출해 주어야 함
		inputs.AcceptInputs();
		
		// 배경음악 플레이
		// 처음실행 / 마지막으로 재생을 시작한지 일정 시간 이상 경과한 경우
		if (isPaused == false
				&& (timeStamp_lastPlayed == 0 
					|| timeStamp - timeStamp_lastPlayed > interval_play_ms
				)
			)
		{
			//현재 시각을 기록해 두고 재생 시작
			timeStamp_lastPlayed = timeStamp;
			audios.Play("MainTheme");
		}
		
		if (inputs.buttons[0].isPressed == true)		// Q KEY
		{
			System.out.println("Q KEY pressed");
			audios.Stop("MainTheme");			
		}		
		if (inputs.buttons[1].isPressed == true)		// SPACE KEY
		{
			System.out.println("SPACE KEY pressed");
			timeStamp_lastPlayed = 0;
			audios.Play("MainTheme");
		}
		if (inputs.buttons[2].IsReleasedNow() == true)		// 마우스 clicked
		{
			// Start 버튼이 클릭됨
			if (inputs.pos_mouseCursor.x >= mainTitle.btn_Start.x 
					&& inputs.pos_mouseCursor.x <= mainTitle.btn_Start.x + mainTitle.btn_Start.width
					&& inputs.pos_mouseCursor.y >= mainTitle.btn_Start.y
					&& inputs.pos_mouseCursor.y <= mainTitle.btn_Start.y + mainTitle.btn_Start.height
				)
			{
				System.out.println("Start Clicked");
				audios.Stop("MainTheme");
				isPaused = true;
				(new DeskFrame(settings)).setVisible(true);
				this.setVisible(false);
			}
			// Quit 버튼이 클릭됨
			if (inputs.pos_mouseCursor.x >= mainTitle.btn_Quit.x 
					&& inputs.pos_mouseCursor.x <= mainTitle.btn_Quit.x + mainTitle.btn_Quit.width
					&& inputs.pos_mouseCursor.y >= mainTitle.btn_Quit.y
					&& inputs.pos_mouseCursor.y <= mainTitle.btn_Quit.y + mainTitle.btn_Quit.height
				)
			{
				System.out.println("Quit Clicked");
				audios.Stop("MainTheme");
				System.exit(0); // 윈도우 종료
			}
		}
		
		return true;
	}

	@Override
	public void Draw(long timeStamp)
	{
		//그리기 작업 시작 - 이 메서드는 Draw()의 가장 위에서 항상 호출해 주어야 함
		BeginDraw();
		
		//화면을 다시 배경색으로 채움
		ClearScreen();
		
		mainTitle.Draw(g);
		mainTitle.btn_Start.Draw(g);
		mainTitle.btn_Quit.Draw(g);

		//그리기 작업 끝 - 이 메서드는 Draw()의 가장 아래에서 항상 호출해 주어야 함
		EndDraw();
	}

}
