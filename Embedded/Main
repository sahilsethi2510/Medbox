// MEDBOX
/*

#CopyRighrt: Team AUTOMATRONIZ
Authors: Sahil Sethi
	 	 Divya Gupta
	     Preetika Maini
		 Ankit Saini
*/

// Inclusion of the header files udes
#include <LPC21xx.H>
#include "lcd.h"
#include "rtc.h"
#include "uart0.h"		   
#include "uart1.h"


// Declaration of the variables
char count=0;
char check=0,checkflag=0; 
int r[5],d[18],i=0,j=0;
int h=0,m=0,s,m0,m1,h0,h1;
int ha1=0,ma1=0,m0a1,m1a1,h0a1=0,h1a1=0;
int ha2=0,ma2=0,m0a2,m1a2,h0a2,h1a2;
int ha3=0,ma3=0,m0a3,m1a3,h0a3,h1a3;
int ha4=0,ma4=0,m0a4,m1a4,h0a4,h1a4;


// Function to receive the real time clock from the mobile application used
void clock_receive()
	{
	 while(i<4)
	 {
		// Reception Of Data serially
		// The data received is subracted by 48 to convert the data from ASCII value to the Interger values
	 	r[i++]=uart0_rec()-48;
		if(i==4)
			{
			m0=	r[3];
			m1=r[2];
			h0 =r[1];
			h1=r[0];
			m=(m1*10)+m0;
			h=((h1*10)+h0);
			// Setting of LCD position 
			lcd_cmd(0XC0);
			lcd_str("                ");
			// Setting of the RTC(Real Time Clock)	
			RTC_ISR_init(h,m,0);
 	IOCLR0|=(1<<6)|(1<<7)|(1<<14);

			}
		}
	}

// Function to Set the Alarm
void set_alarm(char count)
	{
	// Using the alarm registers of LPC2148
	ALHOUR=d[count]*10+d[count+1];
	ALMIN=d[count+2]*10+d[count+3];
	ALSEC=0;
	}

// Function to receive the time of the dosage at which the alarm is to be set
void no_of_dosage()
	{
	lcd_cmd(0X80);
  	lcd_str("  ENTER DOSAGE  ");
  	while(j<=15)
		{
		// Reception Of Data serially
		// The data received is subracted by 48 to convert the data from ASCII value to the Interger values
	 	d[j++]=uart0_rec()-48;
		// Settinf the alarm
		set_alarm(0);
		if(j==16)
			{
			// Displaying the values received of the alarm values
			// Alarm 1
			lcd_cmd(0X80);
			lcd_str("                ");
			m0a1=d[3];
			m1a1=d[2];
			h0a1=d[1];
			h1a1=d[0];
			lcd_cmd(0X80);
			lcd_data(h1a1+48);
			lcd_data(h0a1+48);
			lcd_data(m1a1+48);
			lcd_data(m0a1+48);
			// Alarm 2
			m0a2=d[7];
			m1a2=d[6];
			h0a2=d[5];
			h1a2=d[4]; 
			lcd_cmd(0X85);
			lcd_data(h1a2+48);
			lcd_data(h0a2+48);
			lcd_data(m1a2+48);
			lcd_data(m0a2+48);
			// Alarm 3
			m0a3=d[11];
			m1a3=d[10];
			h0a3=d[9];
			h1a3=d[8];	 
			lcd_cmd(0X8A);
			lcd_data(h1a3+48);
			lcd_data(h0a3+48);
			lcd_data(m1a3+48);
			lcd_data(m0a3+48);
			// Alarm 4
			m0a4=d[15];
			m1a4=d[14];
			h0a4=d[13];
			h1a4=d[12];	 
			lcd_cmd(0XCA);
			lcd_data(h1a4+48);
			lcd_data(h0a4+48);
			lcd_data(m1a4+48);
			lcd_data(m0a4+48);
 	IOCLR0|=(1<<6)|(1<<7)|(1<<14);

			}
		}
	}




int main()
	{
	// Setting the port pins to behave as output pins
 	IODIR0|=RS|RW|EN;
 	IODIR0|=0x0F<<19;
	IODIR0|=(1<<6)|(1<<7)|(1<<14)|(1<<11);
  	// Setting the port pins to LOW
 	IOCLR0|=(1<<6)|(1<<7)|(1<<14);
	IOSET0|=(1<<11);
	// Initialising the UART to the baudrate to 9600 bits per second
	uart0_init(9600);
  	uart1_init(9600);
	// Initialising the LCD to be int the write mode
  	lcd_init();
	// Initial values of the LCD when powered  on
  	lcd_cmd(0X80);
  	lcd_str("     MEDBOX      ");
  	lcd_cmd(0XC0);
  	lcd_str("    SET TIME     ");
	// Calling of the function defined above 
  	clock_receive();
	no_of_dosage();
	// This is the inifite loop working till the system is powered on
	while(1)
  		{
		// IOPIN0 is the register accessed to check whether the swith attached to the pin number 15 of the PORT0
		if((IOPIN0 & (1<<15))==0)
			{
			// Waiting for the switch pressed to be released 
			while((IOPIN0 & (1<<15))==0);
			// Setting the port pins to LOW
 			IOSET0=(1<<11);
			IOSET0=(1<<14);
			check=0;
			}
		// IOPIN0 is the register accessed to check whether the swith attached to the pin number 12 of the PORT0
		if((IOPIN0 & (1<<12))==0)
			{					 
			// Waiting for the switch pressed to be released 
			while((IOPIN0 & (1<<12))==0);
			// Setting the motor to rotate anti-clockwise for half a second
			IOCLR0=(1<<6);
			IOSET0=(1<<7);
			delay(530);
			IOCLR0=(1<<7);
			}
		// IOPIN0 is the register accessed to check whether the swith attached to the pin number 13 of the PORT0
		if((IOPIN0 & (1<<13))==0)
			{
			// Waiting for the switch pressed to be released 
			while((IOPIN0 & (1<<13))==0);
			// Setting the motor to rotate clockwise for half a second
			IOSET0=(1<<6);
			IOCLR0=(1<<7);
			delay(530);
			IOCLR0=(1<<6);
			}
		// Checking for the update to RTC
		if(flag==1)
			{	
			//Setting the Value to the RTC
			s=SEC;
			m=MIN;
			h=HOUR;
			// Displaying Clock on the LCD
			lcd_cmd(0XC0);
			lcd_data((h/10)+'0');
			lcd_data((h%10)+'0') ;	
			lcd_data(':');
			lcd_data((m/10)+'0');
			lcd_data((m%10)+'0') ;
			lcd_data(':');
			lcd_data((s/10)+'0');
			lcd_data((s%10)+'0') ;
			flag=0;
			check++;
 			// Checking whether the buzzer is turned off if nor the sending the messege using the GSM module(SIM900A) 
			if((check==60) && (checkflag==1))
				{
				// Initialising the GSM module
				uart1_str("AT\n\r");
				delay(2000);
				// Enters the GSM module to messege mode
				uart1_str("AT+CMGF=1\n\r");
				delay(2000);
				// Setting of number to whom the messege has to bes send
				uart1_str("AT+CMGS=\"8800900350\"\n\r");
				delay(3000);
	  			// Messege that has to be send
				uart1_str("THE PERSON HAS NOT TAKEN THE MEDICINE\n\r");
	  			uart1_trans(26);// cntl + z
				delay(8000);
				check=0;
				checkflag=0;
				}	 
			}
		// Checking whether the alarm is set or not
		if(alarm==1)
			{	  
			alarm=0;
			IOCLR0=(1<<11);
			IOSET0=(1<<14);
			count+=4;
			// setting of the next alarm
			set_alarm(count);
			check=0;
			checkflag=1;
			//rotating th motor clockwise
			IOSET0=(1<<6);
			IOCLR0=(1<<7);
			delay(530);
			IOCLR0=(1<<6);
			}
		}
	}			   
