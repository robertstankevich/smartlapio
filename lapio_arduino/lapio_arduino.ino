#include <math.h>
#include <Wire.h>
#include "I2Cdev.h"
#include "MPU6050.h"

#include "HX711.h"  //You must have this library in your arduino library folder

#define DOUT  4
#define CLK  3

HX711 scale(DOUT, CLK);

//Change this calibration factor as per your load cell once it is found you many need to vary it in thousands
float calibration_factor = -146650; //-106600 worked for my 40Kg max scale setup 
float weight = 0;
float weight_array[100];


const int MPU_addr=0x68; int16_t AcX,AcY,AcZ,Tmp,GyX,GyY,GyZ;
int minVal=265; int maxVal=402;

int state = 0;
int old_state = 0;
float old_weight = 2;
int a = 0;
int weight_count = 0;
float weight_amount = 0;
double x; double y; double z;








double ThermistorC(int RawADC) {
 double Temp;
 Temp = log(10000.0*((1024.0/RawADC-1))); 
 Temp = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * Temp * Temp ))* Temp );
 Temp = Temp - 273.15;          
 return Temp;
}

void setup() {
Wire.begin();
Wire.beginTransmission(MPU_addr);
Wire.write(0x6B); 
Wire.write(0); 
Wire.endTransmission(true);
 Serial.begin(9600);
 
  scale.set_scale(-96650);  //Calibration Factor obtained from first sketch
  scale.tare();             //Reset the scale to 0  

 
}

void loop() {    
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr,14,true);
  AcX=Wire.read()<<8|Wire.read();
  AcY=Wire.read()<<8|Wire.read();
  AcZ=Wire.read()<<8|Wire.read(); 
  int xAng = map(AcX,minVal,maxVal,-90,90); 
  int yAng = map(AcY,minVal,maxVal,-90,90); 
  int zAng = map(AcZ,minVal,maxVal,-90,90);
  x= (RAD_TO_DEG * (atan2(-yAng, -zAng)+PI)) - 179;
  y= (RAD_TO_DEG * (atan2(-xAng, -zAng)+PI)) - 193;
  z= (RAD_TO_DEG * (atan2(-yAng, -xAng)+PI)) - 250;         
  double valC;                
  int tempC;              
  valC=analogRead(0);      
  tempC=floor(ThermistorC(valC));
  weight=scale.get_units();



if(((y<-155 && y>-160) || (y>155 && y<160)) || (z>110 || z<-175)){
  state = 1;
  
 
  }
  else {
    state = 0;
  }


  if(state != old_state && state==1){
    Serial.print("S1#");
    Serial.print("T");
    Serial.print(tempC);  
    Serial.print("#");
    
    delay(10);
    
    weight = weight_amount / weight_count;
    memset(weight_array, 0, sizeof(weight_array));
    if(weight > 0.1){
    Serial.print("W");
    Serial.print(weight, 3);
    Serial.print("#");  
    weight = 0;
    weight_amount = 0;
    weight_count = 0;
    a=0;
    }
    
    old_state = state;
      
    }
    else if(state != old_state && state==0){
    Serial.print("S0#");
    old_state = state;
    }

  if(weight>old_weight){
    if(a!=100){
      weight_array[a] = (weight - 1.8) / 1.41 ;
      a++;
    for (int i = 0; i < 100; i++){
      if(weight_array[i]>0){
        weight_count++;
        weight_amount += weight_array[i];
      }
   
  }
    }
  }
  if(Serial.available())
  {
    char temp = Serial.read();
    if(temp == 't' || temp == 'T')
      scale.tare();  //Reset the scale to zero      
  }
}


