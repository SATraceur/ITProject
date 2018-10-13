package BCILED;

import com.pi4j.io.gpio.*;
import java.awt.Color;
import java.util.ArrayList;

/**
 * APA102 specifications can be found here: https://cdn-shop.adafruit.com/datasheets/APA102.pdf
 * @author SATraceur
 */
public class LEDdriver {

    private GpioController gpio;
    private GpioPinDigitalOutput data;
    private GpioPinDigitalOutput clk;   
    
    /**
    * LEDdriver constructor gets a handle to the GPIO controller before defining
    * the data and clock pins to be used. 
    */
    public LEDdriver() {
        gpio = GpioFactory.getInstance();
        data = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22);
        clk = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21);
        data.low();
        clk.low();
    }
    
    /**
     * This method allows the user to write an array of colors to the LED strip.
     * @param colors - An array or colors to write to the LED strip.
     * @param count - 16-bit int representing the number of pixels in the LED strip.
     */
    public void write(ArrayList<Color> colors, int pixels) {
        this.startFrame();
        for(int i = 0; i < pixels; i++) {
            sendColorFrame(colors.get(i));
        }
        this.endFrame(pixels);
    }

    /**
     * Sends a 32-bit start frame of 0's as per the APA102 datasheet.
     * ---------------------------------------------
     * | 00000000 | 00000000 | 00000000 | 00000000 |
     * ---------------------------------------------
     */
    private void startFrame() {
        for(int i = 0; i < 4; i++ ) {
            this.transfer(0b00000000);
        }   
    }

    /**
     * Sends the end frame to the LED strip.
     * Fancy stuff happening here, refer to Arduino header APA102.h for more details.
     * @param count - 16-bit int representing the number of pixels in the LED strip.
     */
    private void endFrame(int pixels) {
        for (short i = 0; i <  (pixels + 15)/16; i++) {
            transfer(0b11111111);
        }
        data.low();
        clk.low();     
    }

    /**
     * Sends the color frame of the following format.
     * Start bits | Brightness |   BLUE   |   GREEN  |   RED   |
     * ---------------------------------------------------------
     * |   111    |   11111    | xxxxxxxx | xxxxxxxx |xxxxxxxx |
     * ---------------------------------------------------------
     * @param color - User specified color of the LED
     */
    private void sendColorFrame(Color color) {
        transfer(0b11100111); // Default to 1/4 brightness
        transfer(color.getBlue());
        transfer(color.getGreen());
        transfer(color.getRed());
    }

    /**
     * Outputs a byte on the data pin by isolating each bit, setting the data line and pulsing the clk. 
     * Data is latched via the APA102 on the rising edge of the clk signal.
     * @param b - Byte to be transmitted over the data pin
     */
    private void transfer(int b) {         
        for(int i = 7; i >= 0; i--) {
            data.setState((b >> i & 1) > 0 ? true : false);
            clk.high();
            clk.low();
        }                  
    }
}
