// OPEN AND CLOSE AARDUINO SERVO

#include <Servo.h>
#include <SoftwareSerial.h>


char blueToothVal;           //value sent over via bluetooth
char lastValue;              //stores last state of device (on/off)

int LED = 13;     // Most Arduino boards have an onboard LED on pin 13
Servo myservo;    // Create servo object to control the servo
SoftwareSerial BLE_Shield(4,5);
bool makeSound;
int soundTimes;


//Specify digital pin on the Arduino that the positive lead of piezo buzzer is attached.
int piezoPin = 8;
 
void setup()
{
 Serial.begin(9600); 
 pinMode(13,OUTPUT);
 
  myservo.attach(9);        // Attach the servo object to pin 9
  myservo.write(90);         // Initialize servo position to 0

  BLE_Shield.begin(9600);   // Setup the serial port at 9600 bps. This is the BLE Shield default baud rate. 
//sound code
  makeSound =false;
  soundTimes = 3;
  
}
 
 
void loop()
{
  if(Serial.available())
  {//if there is data being recieved
    blueToothVal=Serial.read(); //read it
  }
  if (blueToothVal=='c')
  {//if value from bluetooth serial is n
    if (lastValue!='o')
      myservo.write(180);
  }
  else if (blueToothVal=='o')
  {//if value from bluetooth serial is f
    myservo.write(90);   
    tone( 8, 2000, 500);
    
  }else if (blueToothVal=='s')
  {//make a sound
    makeSound = true;
  }

  if(makeSound && soundTimes > 0){
     tone( 8, 2000, 500);
     delay(1000);
     soundTimes -=1;
  }else{
    soundTimes = 3;
    makeSound = false;
    blueToothVal =' ';
  }
 }

 
