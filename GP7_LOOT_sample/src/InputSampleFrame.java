import java.awt.Color;
import java.awt.event.KeyEvent;

import loot.*;
import loot.graphics.DrawableObject;

/**
 * LOOT의 입력 기능을 중점적으로 활용한 예제입니다.
 * 
 * @author Racin
 *
 */
@SuppressWarnings("serial")
public class InputSampleFrame extends GameFrame
{
	/*
	 * 조작:
	 * 
	 * Q:
	 * 		1P 주먹 내지르기
	 * 
	 * A:
	 * 		1P 방어
	 * 
	 * P:
	 * 		2P 주먹 내지르기
	 * 
	 * L:
	 * 		2P 방어
	 * 
	 * 스페이스 바:
	 * 
	 * 		다른 키를 누르지 않은 상태에서 눌렀다 떼면 게임 시작 / 재시작
	 */
	
	/* -------------------------------------------
	 * 
	 * 상수 및 내부 클래스 선언 부분
	 * 
	 */
	
	static final int lifebar_width = 160;	//HP 게이지의 가로 길이
	static final int player_maxHP = 100;	//플레이어의 최대 HP

	enum GameState
	{
		Started,	//게임이 아직 시작되지 않은 상태
		Ready,		//스페이스 바를 누른 상태, 이제 떼면 게임이 시작됨
		Running,	//게임이 시작된 상태
		Finished,	//승자가 결정된 상태
		BahnChick	//반칙을 범해 게임이 끝난 상태
	}
	
	enum PlayerState
	{
		Normal,		//아무 버튼도 안 누름
		Guarding,	//방어중
		Punching	//주먹을 내지름
	}

	class Player extends DrawableObject
	{
		public PlayerState state;
		public int HP;
		public LifeBar lifeBar;
		
		public Player(boolean isPlayer1)
		{
			state = PlayerState.Normal;
			HP = player_maxHP;
			lifeBar = new LifeBar(isPlayer1);
			
			if ( isPlayer1 == true )
			{
				x = 0;
				y = 0;
				width = 300;
				height = 600;
			}
			else
			{
				x = 400;
				y = 0;
				width = -300;	//너비 또는 높이값이 음수면 그림이 좌우 또는 상하로 반전됨
				height = 600;
			}
			
			image = images.GetImage("player_normal");
		}
	}

	class LifeBar extends DrawableObject
	{
		public LifeBar(boolean isForPlayer1)
		{
			if ( isForPlayer1 == true )
			{
				x = 10;
				y = 10;
				width = lifebar_width;
				height = 20;
			}
			else
			{
				x = 390;
				y = 10;
				width = -lifebar_width;	//너비 또는 높이값이 음수면 그림이 좌우 또는 상하로 반전됨
				height = 20;
			}
			
			image = images.GetImage("lifebar_green");
		}
	}
	
	
	/* -------------------------------------------
	 * 
	 * 필드 선언 부분
	 * 
	 */

	Player p1;
	Player p2;
	GameState state = GameState.Started;	//정상적인 경우 Started -> Ready -> Running -> Finished -> Ready -> ...의 순서로 전환됨
	int numberOfWinner;						//1: 1P 승, 2: 2P 승, 3: 모두 패

	
	/* -------------------------------------------
	 * 
	 * 메서드 정의 부분
	 * 
	 */

	public InputSampleFrame(GameFrameSettings settings)
	{
		super(settings);
		System.out.println("InputSampleFrame 시작됨");
		
		images.LoadImage("Images/player_normal.png", "player_normal");
		images.LoadImage("Images/player_guarding.png", "player_guarding");
		images.LoadImage("Images/player_punching.png", "player_punching");
		images.LoadImage("Images/lifebar_green.png", "lifebar_green");
		images.LoadImage("Images/lifebar_red.png", "lifebar_red");
		
		inputs.BindKey(KeyEvent.VK_Q, 0);
		inputs.BindKey(KeyEvent.VK_A, 1);
		inputs.BindKey(KeyEvent.VK_P, 2);
		inputs.BindKey(KeyEvent.VK_L, 3);
		inputs.BindKey(KeyEvent.VK_SPACE, 4);
		
	}

	@Override
	public boolean Initialize()
	{
		System.out.println("Initialize 시작됨");
		p1 = new Player(true);
		p2 = new Player(false);
		
		LoadColor(Color.black);
		LoadFont("궁서체 18");		//대결에 진지하게 임할 수 있도록 궁서체 사용
		
		return true;
	}

	@Override
	public boolean Update(long timeStamp)
	{
		System.out.println("Update 시작됨");
		//입력을 버튼에 반영. 이 메서드는 항상 Update()의 시작 부분에서 호출해 주어야 함
		inputs.AcceptInputs();
		
		switch ( state )
		{
		case Ready:
			//준비 상태에서 플레이어가 버튼을 누르면 반칙
			if ( inputs.buttons[0].isPressed == true ||
				 inputs.buttons[1].isPressed == true ||
				 inputs.buttons[2].isPressed == true ||
				 inputs.buttons[3].isPressed == true )
			{
				//누가 버튼을 눌렀는지 확인 - 이 때는 공격이 방어보다 우선 적용됨
				p1.state = PlayerState.Normal;
				p2.state = PlayerState.Normal;
				
				if ( inputs.buttons[0].isPressed == true )
				{
					p1.state = PlayerState.Punching;
					numberOfWinner += 2;
				}
				
				else if ( inputs.buttons[1].isPressed == true )
				{
					p1.state = PlayerState.Guarding;
					numberOfWinner += 2;
				}

				if ( inputs.buttons[2].isPressed == true )
				{
					p2.state = PlayerState.Punching;
					numberOfWinner += 1;
				}
				
				else if ( inputs.buttons[3].isPressed == true )
				{
					p2.state = PlayerState.Guarding;
					numberOfWinner += 1;
				}
				
				state = GameState.BahnChick;
			}
			//두 플레이어가 버튼을 누르지 않은 상태에서 스페이스 바를 떼면 게임 시작
			else if ( inputs.buttons[4].IsReleasedNow() == true )
				state = GameState.Running;
			break;
		case Running:
			//각 플레이어의 버튼 입력 적용 - 여기서는 둘 다 누르고 있는 경우 아무 것도 안 누르고 있는 것으로 간주
			p1.state = PlayerState.Normal;
			p2.state = PlayerState.Normal;
			
			if ( inputs.buttons[0].isPressed == true && inputs.buttons[1].isPressed == false )
				p1.state = PlayerState.Punching;
			
			if ( inputs.buttons[0].isPressed == false && inputs.buttons[1].isPressed == true )
				p1.state = PlayerState.Guarding;

			if ( inputs.buttons[2].isPressed == true && inputs.buttons[3].isPressed == false )
				p2.state = PlayerState.Punching;
			
			if ( inputs.buttons[2].isPressed == false && inputs.buttons[3].isPressed == true )
				p2.state = PlayerState.Guarding;
			
			/*
			 * 공격 판정
			 * 
			 * 내가 주먹을 내질렀을 때
			 * 		상대가 방어중이었다면 내 체력을 0으로 만듦
			 * 		그렇지 않다면 상대의 체력을 0으로 만듦
			 */
			
			if ( p1.state == PlayerState.Punching )
			{
				if ( p2.state == PlayerState.Guarding )
					p1.HP = 0;
				
				else
					p2.HP = 0;
			}
			
			if ( p2.state == PlayerState.Punching )
			{
				if ( p1.state == PlayerState.Guarding )
					p2.HP = 0;
				
				else
					p1.HP = 0;
			}
			

			/*
			 * 방어 패널티 적용
			 * 
			 * 방어중이었다면 체력을 1만큼 감소시킴 - 최소값은 0
			 */
			
			if ( p1.state == PlayerState.Guarding && p1.HP != 0 )
				--p1.HP;
			
			if ( p2.state == PlayerState.Guarding && p2.HP != 0 )
				--p2.HP;
			
			
			/*
			 * 체력이 0이 된 플레이어가 있는 경우 게임 끝
			 * 
			 * 둘 다 동시에 0이 된 경우 둘 다 진 것으로 간주
			 */
			if ( p1.HP == 0 )
			{
				numberOfWinner += 2;
				state = GameState.Finished;
			}

			if ( p2.HP == 0 )
			{
				numberOfWinner += 1;
				state = GameState.Finished;
			}
			break;
		case Started:
		case Finished:
		case BahnChick:
			//다른 버튼을 누르지 않은 상태에서 스페이스 바를 누르면 게임 준비 
			if ( inputs.buttons[0].isPressed == false &&
				 inputs.buttons[1].isPressed == false &&
				 inputs.buttons[2].isPressed == false &&
				 inputs.buttons[3].isPressed == false &&
				 inputs.buttons[4].IsPressedNow() == true )
			{
				numberOfWinner = 0;
				p1.state = PlayerState.Normal;
				p2.state = PlayerState.Normal;
				p1.HP = player_maxHP;
				p2.HP = player_maxHP;
				state = GameState.Ready;
			}
			break;
		}

		/*
		 * 그리기에 사용할 필드 재설정
		 */
		
		switch ( p1.state )
		{
		case Normal:
			p1.image = images.GetImage("player_normal");
			break;
		case Guarding:
			p1.image = images.GetImage("player_guarding");
			break;
		case Punching:
			p1.image = images.GetImage("player_punching");
			break;
		}
		
		switch ( p2.state )
		{
		case Normal:
			p2.image = images.GetImage("player_normal");
			break;
		case Guarding:
			p2.image = images.GetImage("player_guarding");
			break;
		case Punching:
			p2.image = images.GetImage("player_punching");
			break;
		}
		
		p1.lifeBar.width = p1.HP * lifebar_width / player_maxHP;
		p2.lifeBar.width = -1 * p2.HP * lifebar_width / player_maxHP;
		
		if ( p1.HP < player_maxHP / 2 )
			p1.lifeBar.image = images.GetImage("lifebar_red");
		else
			p1.lifeBar.image = images.GetImage("lifebar_green");
		
		if ( p2.HP < player_maxHP / 2 )
			p2.lifeBar.image = images.GetImage("lifebar_red");
		else
			p2.lifeBar.image = images.GetImage("lifebar_green");
			
		
		return true;
	}

	@Override
	public void Draw(long timeStamp)
	{
		System.out.println("Draw 시작됨");
		//그리기 작업 시작 - 이 메서드는 Draw()의 가장 위에서 항상 호출해 주어야 함
		BeginDraw();
		
		//화면을 다시 배경색으로 채움
		ClearScreen();
		
		p1.lifeBar.Draw(g);
		p2.lifeBar.Draw(g);
		
		if ( p1.state == PlayerState.Punching )
		{
			p2.Draw(g);
			p1.Draw(g);
		}
		else
		{
			p1.Draw(g);
			p2.Draw(g);
		}
		
		switch ( state )
		{
		case Started:
			DrawString(10, 50, "    1P는 Q랑 A, 2P는 P랑 L로 조작한다.    ");
			DrawString(10, 72, "다른 키 말고 스페이스 바만 누르면 시작한다.");
			break;
		case Ready:
			DrawString(10, 50, "       스페이스 바를 떼면 시작한다.       ");
			DrawString(10, 72, "      지금 다른 키를 누르면 반칙이다.      ");
			break;
		case Running:
			DrawString(10, 72, "                 시작했다.                 ");
			break;
		case Finished:
			if ( numberOfWinner == 3 )
				DrawString(10, 50, "                둘 다 졌다.               ");
			else
				DrawString(10, 50, "               %dP가 이겼다.               ", numberOfWinner);
			
			DrawString(10, 72, "     스페이스 바를 누르면 다시 시작한다.   ");
			break;
		case BahnChick:
			if ( numberOfWinner == 3 )
				DrawString(10, 50, "            둘 다 반칙을 범했다.          ");
			else
				DrawString(10, 50, "            %dP가 반칙을 범했다.           ", 3 - numberOfWinner);
			
			DrawString(10, 72, "     스페이스 바를 누르면 다시 시작한다.   ");
			break;
		}
		
		//그리기 작업 끝 - 이 메서드는 Draw()의 가장 아래에서 항상 호출해 주어야 함
		EndDraw();
	}

}
