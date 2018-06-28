
/*
 Defining pins of LCD setting
 RS : pin which enables a user to select the instruction mode or the character mode of a LCD. 
 RW : pin which enable user to set read / write mode of LCD
 EN : when this pin is set to logical high the LCD stsrts processing the incoming data
*/
#define RS (1<<16)
#define RW (1<<17)
#define EN (1<<18)
// DATA PINS P0.19-P0.22
 
// Function to provide the Delay
void delay(unsigned int ms)
	{
	T0MCR=0x04;//set MR0 for counting
	T0MR0=(3000*5*ms);//1 msec delay counting value foe 3MHz
	T0TC=0x00;//make sure  TC is 0
	T0TCR=0x01;// start timer 0
	while(T0TC<T0MR0);// wait till T0TC matches MR0
	}
 
// Function to give commands to the LCD 
void lcd_cmd(char c)
 	{
 	IOCLR0|=RS;// CLEAR RS
 	IOCLR0|=RW;// CLEAR RW
 	IOCLR0|=(0X0F<<19);
 	IOSET0|=(((c&0xF0)>>4)<<19); 
 	IOSET0|=EN;
 	delay(3);
 	IOCLR0|=EN; 
 	IOCLR0|=(0X0F<<19);
 	IOSET0|=((c&0x0F)<<19);
 	IOSET0|=EN;
 	delay(3);
 	IOCLR0|=EN;  
 	}

// Function to send  data to the LCD
void lcd_data(char c)
	{
	 IOSET0|=RS;//  RS
	 IOCLR0|=RW;// CLEAR RW
	 IOCLR0|=(0X0F<<19);
	 IOSET0|=(((c&0xF0)>>4)<<19);
	 IOSET0|=EN;
	 delay(3);
	 IOCLR0|=EN; 
	 IOCLR0|=(0X0F<<19);
	 IOSET0|=((c&0x0F)<<19);
	 IOSET0|=EN;
	 delay(3);
	 IOCLR0|=EN; 
	 }				 
 
// Functon to initialise the LCD 
void lcd_init()
 	{
    lcd_cmd(0x33);
	lcd_cmd(0x32);
	lcd_cmd(0x28);
	lcd_cmd(0x01);// CLEARING THE LCD
	lcd_cmd(0x06);// ENTRY MODE
	lcd_cmd(0x0E);// DISPLAY ON CURSOR ON
	lcd_cmd(0x80);//FIRST LINE FIRST ROW
	}

// Function to send the a string on to the LCD 
void lcd_str(char *str)
	{
	while(*str!='\0')
		{
		lcd_data(*str++);	
		}
	}
