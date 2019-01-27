#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include <string.h>
#include <fcntl.h>
#include <termios.h>
#include <sys/ioctl.h>

int main_menu(void);

//------------------------------------------------------------------------
// 설 명 : 어플리케이션 처리 
// 매 계 : 명령행 매계변수
// 반 환 : 없음
// 주 의 : 
//------------------------------------------------------------------------
int main( int argc, char **argv )
{

    int     handle;
    struct  termios  oldtio,newtio;
    
    char    *TitleMessage = "Welcome Serial Port\r\n";
    char    Buff[256];
    int     RxCount;
    int     loop;
    int     ending;
    
    int key;
            
    // 화일을 연다.
    handle = open( "/dev/ttyTHS2", O_RDWR | O_NOCTTY );
    if( handle < 0 ) 
    {
        //화일 열기 실패
        printf( "Serial Open Fail [/dev/ttyTHS2]\r\n "  );
        exit(0);
    }
    
    tcgetattr( handle, &oldtio );  // 현재 설정을 oldtio에 저장

    memset( &newtio, 0, sizeof(newtio) );
    newtio.c_cflag = B115200 | CS8 | CLOCAL | CREAD ; 
    newtio.c_iflag = IGNPAR;
    newtio.c_oflag = 0;

    //set input mode (non-canonical, no echo,.....)
    newtio.c_lflag = 0;
    
    newtio.c_cc[VTIME] = 128;    // time-out 값으로 사용된다. time-out 값은 TIME*0.1초 이다.
    newtio.c_cc[VMIN]  = 0;     // MIN은 read가 리턴되기 위한 최소한의 문자 개수
    
    tcflush( handle, TCIFLUSH );
    tcsetattr( handle, TCSANOW, &newtio );
    
    // 타이틀 메세지를 표출한다. 
    write( handle, TitleMessage, strlen( TitleMessage ) );
    
    // 1 문자씩 받아서 되돌린다. 
    ending = 0;

    while((key=main_menu()) != 0)
    {
        switch(key)
        {

           case 'a':
               printf("a \n");               
               Buff[0] = 'a';
               write( handle, Buff, 1 );
               break;

           case 'b':
               printf("b \n");               
               Buff[0] = 'b';
               write( handle, Buff, 1 );
               break;

           case 'c':
               printf("c\n");               
               Buff[0] = 'c';
               write( handle, Buff, 1 );
               break;

           case 'd':
               printf("d\n");               
               Buff[0] = 'd';
               write( handle, Buff, 1 );
               break;  

           case 'i':
               printf("i\n");               
               Buff[0] = 'i';
               write( handle, Buff, 1 );
               break;  

           case 'I':
               printf("No.6\n");               
               Buff[0] = 'I';
               write( handle, Buff, 1 );
               break;  

           case 'D':
               printf("No.7\n");
               Buff[0] = 'D';
               write( handle, Buff, 1 );
               break;  

           case '8':
               printf("No.8\n");
               Buff[0] = '8';
               write( handle, Buff, 1 );
               break;

           case '9':
               printf("No.9\n");
               break;

           case '0':
               printf("No.0\n");
               break;

           case 'q':
               printf("exit\n");
               tcsetattr( handle, TCSANOW, &oldtio ); // 이전 상태로 되돌린다. 
               close( handle );   // 화일을 닫는다.               
               exit(0);
               break;

           default :
               printf("Wrong key ..... try again\n");
               break;
        }
    }
    
    tcsetattr( handle, TCSANOW, &oldtio ); // 이전 상태로 되돌린다. 
    
    close( handle );   // 화일을 닫는다.
    
    return 0;    
}

int getch(void)
{
    struct termios oldattr, newattr;
    int ch;
    tcgetattr( STDIN_FILENO, &oldattr );
    newattr = oldattr;
    newattr.c_lflag &= ~( ICANON | ECHO );
    tcsetattr( STDIN_FILENO, TCSANOW, &newattr );
    ch = getchar();
    tcsetattr( STDIN_FILENO, TCSANOW, &oldattr );
    return ch;
}
 
int main_menu(void)
{
    int key;

    printf("\n\n");
    printf("-------------------------------------------------\n");
    printf("                    MAIN MENU\n");
    printf("-------------------------------------------------\n");
    printf(" a. Turn Left                       \n");
    printf(" b. Turn Right                       \n");
    printf(" c. Forward                       \n");
    printf(" d. backward                       \n");
    printf(" i. stop                       \n");

    printf("-------------------------------------------------\n");
    printf(" q. Motor Control application QUIT                 \n");
    printf("-------------------------------------------------\n");
    printf("\n\n");
 
    printf("SELECT THE COMMAND NUMBER : ");

    key=getch();
 
    return key;
}

