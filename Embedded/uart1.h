 void uart1_init(int baud)
{
unsigned int x;
	   
PINSEL0|=(1<<16);//PO.8 AS TX 
PINSEL0|=(1<<18);//P0.9 AS RX
U1LCR=0X83;//enable baud access
x=(unsigned int)(5*3000000/(16*baud));
U1DLL=x%256;
U1DLM=x/256;
U1LCR=0X03;// disable baud access
U1FCR=0X01;// enable fifo
}

void uart1_trans(char c)
{
U1THR=c;
while((U1LSR&(1<<5))==0);
}

char uart1_rec()
{
char a;
while((U1LSR&(1<<0))==0);
a=U1RBR;
return(a);
}


void uart1_str(char *str)
{
while(*str!='\0')
	{
	uart1_trans(*str++);	
	}
}
