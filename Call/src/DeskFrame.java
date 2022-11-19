import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.DrawableObject;

@SuppressWarnings("serial")
public class DeskFrame extends GameFrame
{
	static long interval_play_ms = 0;

	class Desk extends DrawableObject
	{
		Button btn_PortGuide ;
		Button portGuide ;
		Button port[] = new Button[12];
		Button btn_Quit ;
		
		boolean isInPlay = false;
		int callerPort;		// Caller Port
		int receiverPort;	// Receiver Port
		int selectedCallerPort;		// Caller Port
		int selectedReceiverPort;	// Receiver Port
		int k1 = 0;			// 랜덤하게 caller의 포트번호를 감춤
		int k2 = 0;			// 랜덤하게 receiver의 포트번호를 감춤
		boolean isAllConnected = false;

		public Desk()
		{
			btn_PortGuide  = new Button("btn_PortGuide",  200, 700,  132,  51, "image", "PortGuide");
			portGuide      = new Button("PortGuide",      110, 100,   0,    0,  "image", "PortGuide");
			btn_Quit       = new Button("btn_Quit",  settings.canvas_width/2 - 60, 700, 132, 51, "image", "btn_Quit");
			
			setPorts();
			
			settings.canvas_width = 1280;
			settings.canvas_height = 900;

			x = 0;
			y = 0;
			width  = settings.canvas_width;
			height = settings.canvas_height;
			
			image = images.GetImage("Desk");
		}
		public void setPorts()
		{
			port[0]  = new Button("Port[0]-100",  210, 230,   75,    75,  "image", "Port_Off", "Wangsim");
			port[1]  = new Button("Port[1]-150",  210, 370,   75,    75,  "image", "Port_Off", "Yusung");
			port[2]  = new Button("Port[2]-200",  210, 500,   75,    75,  "image", "Port_Off", "Dajon");
			port[3]  = new Button("Port[3]-250",  400, 230,   75,    75,  "image", "Port_Off", "Jeanjan");
			port[4]  = new Button("Port[4]-300",  400, 370,   75,    75,  "image", "Port_Off", "Dagu");
			port[5]  = new Button("Port[5]-350",  400, 500,   75,    75,  "image", "Port_Off", "Noeun");
			port[6]  = new Button("Port[6]-400",  590, 230,   75,    75,  "image", "Port_Off", "Hongda");
			port[7]  = new Button("Port[7]-450",  590, 370,   75,    75,  "image", "Port_Off", "Fran");
			port[8]  = new Button("Port[8]-500",  590, 500,   75,    75,  "image", "Port_Off", "Sincho");
			port[9]  = new Button("Port[9]-550",  790, 230,   75,    75,  "image", "Port_Off", "Esdur");
			port[10] = new Button("Port[10]-600", 790, 370,   75,    75,  "image", "Port_Off", "Hospital");
			port[11] = new Button("Port[11]-650", 790, 500,   75,    75,  "image", "Port_Off", "Police");
			
		}
		public void callGenerator(long timeStamp)
		{
			int connetedPorts = 0; 
			if (isInPlay == false && timeStamp - timeStamp_lastPlayed > interval_play_ms)
			{
				for (int i=0; i<12; i++)
				{
					if (port[i].getConnetedPort() >= 0) connetedPorts++;
					if (connetedPorts == 6)
					{
						isAllConnected = true;
					}
				}

				if (isAllConnected == false)
				{
					callerPort = (int) ((Math.random() * 12) + 0);		// Caller Port
					receiverPort = (int) ((Math.random() * 12) + 0);	// Receiver Port
					k1 = (int) ((Math.random() * 2) + 0);				// Caller Port 숨기기 여부를 위한 임의의 수
					k2 = (int) ((Math.random() * 2) + 0);				// Receiver Port 숨기기 여부를 위한 임의의 수
						
					if ("Port_Off".equals(port[callerPort].getImageName()) 
							&& "Port_Off".equals(port[receiverPort].getImageName())
							&& callerPort != receiverPort
							)
					{
						System.out.println("callGenerator(): "+ callerPort +" ==> "+ receiverPort);
						
						port[callerPort].setImageName("Port_On");
						isInPlay = true;
					}						
				}

			}
		}
	}
	
	class Button extends DrawableObject
	{
		private String imageName;
		private String city;
		
		private int connetedPort = -1;

		public String getImageName() {
			return imageName;
		}

		public void setImageName(String imageName) {
			this.imageName = imageName;
			this.image = images.GetImage(imageName);

		}
		public int getConnetedPort() {
			return connetedPort;
		}
		
		public void setConnetedPort(int connetedPort) {
			this.connetedPort = connetedPort;
		}
		

		public Button(String id, int x, int y, int width, int height, String type, String text) 
		{
			super.x = x;
			super.y = y;
			super.width = width;
			super.height = height;
			imageName = text;
			if ("image".equals(type))
			{
				image = images.GetImage(text);
			}
			
		}
		public Button(String id, int x, int y, int width, int height, String type, String text, String cityName)
		{
			super.x = x;
			super.y = y;
			super.width = width;
			super.height = height;
			imageName = text;
			if ("image".equals(type))
			{
				image = images.GetImage(text);
			}
			city = cityName;
		}
		
	}
	
	/* -------------------------------------------
	 * 
	 * 필드 선언 부분
	 * 
	 */

	Desk desk;
	
	//호출 간격을 재기 위해 마지막으로 생성했던 프레임의 실행 시각을 담아 두는 필드
	long timeStamp_lastPlayed = 0;

	
	/* -------------------------------------------
	 * 
	 * 메서드 정의 부분
	 * 
	 */

	public DeskFrame(GameFrameSettings settings)
	{
		super(settings);
		System.out.println("DeskFrame()");
		
		images.LoadImage("Images/desk.png",      "Desk");
		images.LoadImage("Images/PortGuide.png", "PortGuide");
		images.LoadImage("Images/Port_Off.png",  "Port_Off");
		images.LoadImage("Images/Port_OffPlugged.png",  "Port_OffPlugged");
		images.LoadImage("Images/Port_On.png",  "Port_On");
		images.LoadImage("Images/Port_OnPlugged.png",  "Port_OnPlugged");
		images.LoadImage("Images/btn_Quit.jpg",  "btn_Quit");
	}

	@Override
	public boolean Initialize()
	{
		desk = new Desk();
		
		LoadColor(Color.red);
		LoadFont("궁서체 23");		
		
		inputs.BindKey(KeyEvent.VK_CONTROL, 0);
		inputs.BindKey(KeyEvent.VK_SPACE, 1);
		
		inputs.BindMouseButton(MouseEvent.BUTTON1, 2);
		
		return true;
	}

	@Override
	public boolean Update(long timeStamp)
	{
		//입력을 버튼에 반영. 이 메서드는 항상 Update()의 시작 부분에서 호출해 주어야 함
		inputs.AcceptInputs();

		desk.callGenerator(timeStamp);
		
		if (inputs.buttons[2].IsReleasedNow() == true)		// 마우스 clicked
		{
//			System.out.println(inputs.pos_mouseCursor);
			
			// btn_PortGuide 버튼이 클릭됨
			if (inputs.pos_mouseCursor.x >= desk.btn_PortGuide.x 
					&& inputs.pos_mouseCursor.x <= desk.btn_PortGuide.x + desk.btn_PortGuide.width
					&& inputs.pos_mouseCursor.y >= desk.btn_PortGuide.y
					&& inputs.pos_mouseCursor.y <= desk.btn_PortGuide.y + desk.btn_PortGuide.height
				)
			{
				System.out.println("btn_PortGuide Clicked");
				if (desk.portGuide.width > 0)
				{
					desk.portGuide.width  = 0;
					desk.portGuide.height = 0;
				}
				else
				{
					desk.portGuide.width  = 820;
					desk.portGuide.height = 600;
				}
			}
			// PortGuide 가 클릭됨
			if (inputs.pos_mouseCursor.x >= desk.portGuide.x 
					&& inputs.pos_mouseCursor.x <= desk.portGuide.x + desk.portGuide.width
					&& inputs.pos_mouseCursor.y >= desk.portGuide.y
					&& inputs.pos_mouseCursor.y <= desk.portGuide.y + desk.portGuide.height
				)
			{
				System.out.println("PortGuide Clicked");
				desk.portGuide.width  = 0;
				desk.portGuide.height = 0;
			}
			// Port[] 가 클릭됨
			for(int i=0; i<12; i++)
			{
				if (inputs.pos_mouseCursor.x >= desk.port[i].x 
						&& inputs.pos_mouseCursor.x <= desk.port[i].x + desk.port[i].width
						&& inputs.pos_mouseCursor.y >= desk.port[i].y
						&& inputs.pos_mouseCursor.y <= desk.port[i].y + desk.port[i].height
						)
				{
					System.out.println("Port["+ i +"] Clicked : "+ desk.port[i].imageName);
					desk.selectedReceiverPort = i;
					desk.port[desk.callerPort].setConnetedPort(i);
					
					if (desk.receiverPort == i)
					{
						System.out.println("Update()::Matched : "+ desk.callerPort +", "+ i);
						desk.isInPlay = false;
						desk.port[desk.callerPort].setImageName("Port_OnPlugged");
						desk.port[i].setImageName("Port_OnPlugged");
						desk.callGenerator(timeStamp);
					}
					else if ("Port_Off".equals(desk.port[i].getImageName()))
					{
						desk.port[i].setImageName("Port_On");
					}
					else if ("Port_On".equals(desk.port[i].getImageName()))
					{
						desk.port[i].setImageName("Port_Off");
						
					}
				}				
			}
			// Quit 버튼이 클릭됨
			if (inputs.pos_mouseCursor.x >= desk.btn_Quit.x 
					&& inputs.pos_mouseCursor.x <= desk.btn_Quit.x + desk.btn_Quit.width
					&& inputs.pos_mouseCursor.y >= desk.btn_Quit.y
					&& inputs.pos_mouseCursor.y <= desk.btn_Quit.y + desk.btn_Quit.height
				)
			{
				System.out.println("Quit Clicked");
				(new MainFrame(settings)).setVisible(true);
				this.setVisible(false);
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

		desk.Draw(g);					// Desk 화면
		desk.btn_PortGuide.Draw(g);		// PortGuide 버튼
		for(int i=0; i<12; i++)
		{
			desk.port[i].Draw(g);		// Port[0]~Port[11]
		}
		desk.portGuide.Draw(g);			// PortGuide
		desk.btn_Quit.Draw(g);			// 종료 버튼

		DrawLine((Graphics2D) g);
		
		String callerPortNo = "" + (100 + desk.callerPort * 50) ;
		if (desk.k1 == 0) callerPortNo = "***" ;
		
		String receiverPortNo = "" + (100 + desk.receiverPort * 50) ;
		if (desk.k2 == 0) receiverPortNo = "***" ;
		
		DrawString(950, 250, "Incoming call");
		DrawString(960, 290, "from ["+ callerPortNo + "] "+ desk.port[desk.callerPort].city);
		DrawString(960, 310, "to ["+ receiverPortNo +"] "+ desk.port[desk.receiverPort].city);
		if (desk.isAllConnected == true)
		{
			DrawString(950, 400, "모든 연결에");
			DrawString(950, 440, "성공하였습니다.");
		}
		else
		{
			DrawString(950, 400, "연결중...");
		}

		//그리기 작업 끝 - 이 메서드는 Draw()의 가장 아래에서 항상 호출해 주어야 함
		EndDraw();
	}

	public void DrawLine(Graphics2D g)
	{
		Graphics2D g2d = g;
		for (int i=0; i<12; i++)
		{
//			System.out.println("desk.port["+ i +"].getConnetedPort(): "+ desk.port[i].getConnetedPort());
			if (desk.port[i].getConnetedPort() >= 0)
			{
				g2d.drawLine(desk.port[i].x, desk.port[i].y, desk.port[desk.port[i].getConnetedPort()].x, desk.port[desk.port[i].getConnetedPort()].y);
			}
		}
		g2d.drawLine(desk.port[desk.callerPort].x, desk.port[desk.callerPort].y, inputs.pos_mouseCursor.x, inputs.pos_mouseCursor.y);
	}

}
