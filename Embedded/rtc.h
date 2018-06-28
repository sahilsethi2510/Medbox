char flag=0;
char alarm=0;
void RTC_ISR() __irq
	{
  	if(ILR==0X01)
	{
	flag=1;
	ILR=0X01;
	}
	else if(ILR==0X03)
	{
	alarm=1;
	ILR=0X03;
	}
	
	VICVectAddr=0x00;//clear the inerrupt address
	}


void RTC_ISR_init(int h,int m,int s)
	{
	SEC=s;
	MIN=m;
	HOUR=h;
	AMR=0XF8;
	PREINT=(unsigned int)((3000000*5)/32768)-1;
	PREFRAC=((3000000*5)-((PREINT+1)*32768));
	VICVectAddr3=(unsigned int)RTC_ISR;//RTC ISR ADDRESS
	VICVectCntl3=0X2D;// ENABLE SLOT FOR RTC
	VICIntEnable |=(1<<13);//enable RTC
	CCR=0x01;
	CIIR=0X01;
	}




