void uart0_init(int baud)
{
unsigned int x;
	   
PINSEL0|=0X05;//PO.0 AS TX AND P0.1 AS RX

U0LCR=0X83;//enable baud access
x=(unsigned int)(3000000*5/(16*baud));
U0DLL=x%256;
U0DLM=x/256;
U0LCR=0X03;// disable baud access
U0FCR=0X01;// enable fifo
}

void uart0_trans(char c)
{
U0THR=c;
while((U0LSR&(1<<5))==0);
}

char uart0_rec()
{
char a;
while((U0LSR&(1<<0))==0);
a=U0RBR;
return(a);
}
void uart0_str(char *str)
{
while(*str!='\0')
	{
	uart0_trans(*str++);	
	}
}

