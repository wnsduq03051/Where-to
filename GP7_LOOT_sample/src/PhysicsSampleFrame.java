import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;

import loot.*;
import loot.graphics.DrawableObject;

/**
 * LOOT를 간단한 물리 환경과 함께 사용한 예제입니다.
 * 
 * @author Racin
 *
 */
@SuppressWarnings("serial")
public class PhysicsSampleFrame extends GameFrame
{
	/*
	 * 조작:
	 * 
	 * 스페이스 바:
	 * 
	 * 		누른 순간 모든 공의 속도를 0으로 만듦
	 * 
	 * 컨트롤 키:
	 * 
	 * 		누르는 동안 모든 공의 속도 / 가속도를 일시적으로 0으로 만듦
	 * 
	 * 마우스 왼쪽 버튼:
	 * 
	 * 		누르고 있는 동안 마우스 포인터 위치로 향하는 인력 적용
	 * 		떼는 순간, 그동안 누르고 있던 시간에 비례하여 척력 적용
	 * 
	 */
	
	/* -------------------------------------------
	 * 
	 * 상수 및 내부 클래스 선언 부분
	 * 
	 */
	
	static final int numberOfBalls = 10000;				//화면 내에 있는 공의 수(너무 많으면 FPS 저하의 원인이 됨)
	static final int ball_width = 5;					//공 하나의 가로 길이(단위는 픽셀)
	static final int ball_height = 5;					//공 하나의 세로 길이(단위는 픽셀)
	
	static final double max_gravitation = 0.1;			//인력의 최대값(단위는 픽셀/ms^2)
	static final double max_repulsion = 10.0;			//척력의 최대값(단위는 픽셀/ms^2)
	static final double max_velocity_x = 160;			//X방향 속력의 최대값(단위는 픽셀/ms)
	static final double max_velocity_y = 120;			//X방향 속력의 최대값(단위는 픽셀/ms)
	
	static final double coef_gravitation = 300.0;		//인력을 계산할 때 사용하는 계수
	static final double coef_repulsion = 0.1;			//척력을 계산할 때 사용하는 계수
	static final double coef_friction = -0.001;			//마찰력을 적용하기 위한 계수(속도에 이 값을 곱한 만큼이 마찰력이 됨. 따라서 이 값은 음수여야 함. 마찰력의 단위는 픽셀/ms^2)

	/**
	 * '공' 하나를 표현하는 클래스입니다.<br>
	 * LOOT 라이브러리에 있는 DrawableObject class를 상속받아<br>
	 * 위치, 속도, 가속도 값을 추가로 선언하여 사용합니다.<br>
	 * <br>
	 * 기존의 x, y는 int 형식이라 소숫점 이하 정보는 담을 수 없기에<br>
	 * 물리 환경에서 사용하기에는 적절하지 않습니다.<br>
	 * 그러므로 p_x, p_y를 별도로 선언하여 위치 정보를 다루고<br>
	 * 매 프레임마다 p_x, p_y의 값을 버림하여 x, y에 담아 각 공의 '화면상의 위치'를 지정합니다.
	 * 
	 * @author Racin
	 *
	 */
	public class Ball extends DrawableObject
	{
		public double p_x;
		public double p_y;
		public double v_x;
		public double v_y;
		public double a_x;
		public double a_y;
		
		public Ball(int x, int y)
		{
			super(x, y,	ball_width, ball_height, images.GetImage("ball"));
			
			p_x = x;
			p_y = y;
		}
	}
	
	/* -------------------------------------------
	 * 
	 * 필드 선언 부분
	 * 
	 */
	
	Ball[] balls = new Ball[numberOfBalls];					//화면 내에 있는 공 목록
	long startTime_pressing;								//마우스 왼쪽 버튼을 누르기 시작한 시각
	
	long timeStamp_firstFrame = 0;							//첫 프레임의 timeStamp -> 실행 이후로 경과된 시간 계산에 사용
	long timeStamp_lastFrame = 0;							//직전 프레임의 timeStamp -> 물리량 계산에 사용
	
	
	/* -------------------------------------------
	 * 
	 * 메서드 정의 부분
	 * 
	 */
	
	public PhysicsSampleFrame(GameFrameSettings settings)
	{
		super(settings);
		
		inputs.BindKey(KeyEvent.VK_SPACE, 0);				//스페이스 바를 누른 순간 모든 공의 속도가 0이 됨
		inputs.BindKey(KeyEvent.VK_CONTROL, 1);				//컨트롤 키를 누르는 동안 모든 공의 속도 / 가속도가 일시적으로 0이 됨
		inputs.BindMouseButton(MouseEvent.BUTTON1, 2);		//마우스 왼쪽 버튼을 누르는 동안 인력 작용, 떼는 순간 척력 작용
		
		images.LoadImage("Images/ball.png", "ball");
	}
	
	@Override
	public boolean Initialize()
	{
		Random rand = new Random();
		
		//각 공을 랜덤 위치에 배치
		for ( int iBall = 0; iBall < balls.length; ++iBall )
			balls[iBall] = new Ball(rand.nextInt(settings.canvas_width - ball_width - 2) + 1, rand.nextInt(settings.canvas_height - ball_height - 2) + 1);
		
		//FPS 출력에 사용할 색 및 글자체 가져오기
		LoadColor(Color.black);
		LoadFont("Consolas BOLD 24");
		
		return true;
	}

	@Override
	public boolean Update(long timeStamp)
	{
		/*
		 * 입력 처리
		 */
		
		//입력을 버튼에 반영. 이 메서드는 항상 Update()의 시작 부분에서 호출해 주어야 함
		inputs.AcceptInputs();
		
		//각 버튼의 상태를 검사하여 각 공에 어떤 작업을 수행해야 하는지 체크
		boolean isStopRequested;
		boolean isPauseRequested;
		boolean isGravitationRequested;
		boolean isRepulsionRequested;
		
		//이번 프레임에 스페이스 바를 눌렀다면 모든 공의 속도를 0으로 만듦
		isStopRequested = inputs.buttons[0].IsPressedNow();

		//컨트롤 키를 누르고 있다면 모든 공의 속도 및 가속도를 일시적으로 0으로 만듦
		isPauseRequested = inputs.buttons[1].isPressed;

		//이번 프레임에 마우스 버튼을 눌렀다면 현재 시각 기록 -> 척력 계산에 사용됨
		if ( inputs.buttons[2].IsPressedNow() == true )
			startTime_pressing = timeStamp;
		
		//마우스 버튼을 누르고 있다면 인력 적용
		isGravitationRequested = inputs.buttons[2].isPressed;
		
		//이번 프레임에 마우스 버튼을 뗐다면 척력 적용
		isRepulsionRequested = inputs.buttons[2].IsReleasedNow();

		
		/*
		 * 입력 검증
		 * 
		 * -	입력 검증 작업은 코드 구성에 따라 생략할 수 있지만
		 * 		일반적인 경우 이렇게 미리 검사 및 보정 작업을 수행하는게 오류 절감에 도움이 됨
		 */
		
		//컨트롤 키를 누르고 있다면 굳이 인력 / 척력을 계산할 필요가 없으므로 해당 변수 재설정
		if ( isPauseRequested == true )
		{
			isGravitationRequested = false;
			isRepulsionRequested = false;
		}
		
		//(물리 법칙과는 맞지 않지만) 좀 더 예쁜 폭발을 연출하기 위해 척력이 작용하기 직전에 모든 공의 현재 속도를 0으로 만듦
		if ( isRepulsionRequested == true )
			isStopRequested = true;
		
		/*
		 * 입력 적용
		 */

		//지난 프레임 이후로 경과된 시간 측정
		double interval = timeStamp - timeStamp_lastFrame;
		
		//모든 공에 대해
		for ( Ball ball : balls )
		{
			ball.a_x = 0;
			ball.a_y = 0;
			
			//이번 프레임에 스페이스 바를 눌렀다면 속도를 0으로 만듦
			if ( isStopRequested == true )
			{
				ball.v_x = 0;
				ball.v_y = 0;
			}
			
			//마우스 버튼을 누르고 있다면 인력 적용
			if ( isGravitationRequested == true )
			{
				double displacement_x = inputs.pos_mouseCursor.x - ball.p_x - ball_width / 2;
				double displacement_y = inputs.pos_mouseCursor.y - ball.p_y - ball_height / 2;
				double squaredDistance = displacement_x * displacement_x + displacement_y * displacement_y;
				double gravitation = coef_gravitation * interval / squaredDistance;
				
				if ( gravitation > max_gravitation )
					gravitation = max_gravitation;
				
				ball.a_x = gravitation * displacement_x / Math.sqrt(squaredDistance);
				ball.a_y = gravitation * displacement_y / Math.sqrt(squaredDistance);
			}
			
			//이번 프레임에 마우스 버튼을 뗐다면 척력 적용
			if ( isRepulsionRequested == true )
			{
				double displacement_x = inputs.pos_mouseCursor.x - ball.p_x - ball_width / 2;
				double displacement_y = inputs.pos_mouseCursor.y - ball.p_y - ball_height / 2;
				double squaredDistance = displacement_x * displacement_x + displacement_y * displacement_y;
				double repulsion = coef_repulsion * ( timeStamp - startTime_pressing ) / squaredDistance;

				if ( repulsion > max_repulsion )
					repulsion = max_repulsion;
				
				ball.a_x = -1.0 * repulsion * displacement_x / Math.sqrt(squaredDistance);
				ball.a_y = -1.0 * repulsion * displacement_y / Math.sqrt(squaredDistance);
			}
			
			//컨트롤 키가 눌려 있지 않다면 속도 / 가속도 반영
			if ( isPauseRequested == false )
			{
				//마찰력 계산
				ball.a_x += coef_friction * interval * ball.v_x;
				ball.a_y += coef_friction * interval * ball.v_y;
				
				//가속도를 속도에 적용 - 가속도의 경우 미리 시간을 곱했으므로 여기서 더 곱하지는 않음
				ball.v_x += ball.a_x;
				ball.v_y += ball.a_y;
				
				//이 예제에서는 창 가장자리에 부딪히면 반사하므로 속도의 절대값이 특정 값보다 작아지도록 보정
				ball.v_x %= max_velocity_x;
				ball.v_y %= max_velocity_y;
				
				//속도를 위치에 적용 - 이 때는 시간을 곱하여 적용
				ball.p_x += ball.v_x * interval;
				ball.p_y += ball.v_y * interval;
				
				/*
				 * 반사 체크
				 * 
				 * -	원래는 충돌이 일어난 시각을 먼저 파악하여 가속도를 반사 전/후로 나누어 적용해야 하지만
				 * 		간단한 환경(특히, 마찰력과 같은 항력이 존재하는 환경)에서는 그렇게 하지 않아도 큰 지장은 없음
				 */
				boolean isWithinCanvas = true;
				
				do
				{
					//반사가 발생할 때마다 속도 반전, 위치 수정
					isWithinCanvas = true;
					
					if ( ball.p_x < 0 )
					{
						ball.v_x = -ball.v_x;
						ball.p_x = -ball.p_x;
						isWithinCanvas = false;
					}
					
					if ( ball.p_x >= settings.canvas_width - ball_width )
					{
						ball.v_x = -ball.v_x;
						ball.p_x = 2 * ( settings.canvas_width - ball_width ) - ball.p_x;
						isWithinCanvas = false;
					}
					
					if ( ball.p_y < 0 )
					{
						ball.v_y = -ball.v_y;
						ball.p_y = -ball.p_y;
						isWithinCanvas = false;
					}
					
					if ( ball.p_y >= settings.canvas_height - ball_height )
					{
						ball.v_y = -ball.v_y;
						ball.p_y = 2 * ( settings.canvas_height - ball_height ) - ball.p_y;
						isWithinCanvas = false;
					}
				}
				while ( isWithinCanvas == false );
				
				//마지막으로, 공의 위치를 기반으로 해당 공을 그릴 픽셀값 설정
				ball.x = (int)ball.p_x;
				ball.y = (int)ball.p_y;
			}
		}

		//이번이 첫 프레임이었다면 시작 시각 기록
		if ( timeStamp_firstFrame == 0 )
			timeStamp_firstFrame = timeStamp;
		
		//이제 '직전 프레임'이 될 이번 프레임의 시작 시각 기록
		timeStamp_lastFrame = timeStamp;
		
		return true;
	}

	@Override
	public void Draw(long timeStamp)
	{
		//그리기 작업 시작 - 이 메서드는 Draw()의 가장 위에서 항상 호출해 주어야 함
		BeginDraw();
		
		//화면을 다시 배경색으로 채움
		ClearScreen();

		for ( Ball ball : balls )
			ball.Draw(g);
		
		DrawString(24, 48, "FPS:  %.2f", loop.GetFPS());
		DrawString(24, 78, "Time: %dms", (int)(timeStamp_lastFrame - timeStamp_firstFrame));
		
		//그리기 작업 끝 - 이 메서드는 Draw()의 가장 아래에서 항상 호출해 주어야 함
		EndDraw();
	}
}
