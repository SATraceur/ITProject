package BCILED;

import com.pi4j.io.gpio.*;
import java.awt.Color;

/**
 * APA102 specifications can be found here: https://cdn-shop.adafruit.com/datasheets/APA102.pdf
 * @author satraceur
 */
public class LEDdriver {

    /**
     * LEDdriver constructor gets a handle to the GPIO controller before defining
     * the data and clock pins to be used. 
     */
    public LEDdriver() {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput data = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22);
        final GpioPinDigitalOutput clk = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21);
    }
    
    /**
     * This method allows the user to write an array of colors to the LED strip.
     * @param colors - An array or colors to write to the LED strip.
     * @param count - 16-bit int representing the number of pixels in the LED strip.
     */
    public void write(Color colors[], short count) {
        this.startFrame();
        for(short i = 0; i < count; i++) {
            sendColorFrame(colors[i]);
        }
        this.endFrame(count);
    }

    /**
     * Sends a 32-bit start frame of 0's as per the APA102 datasheet.
     * ---------------------------------------------
     * | 00000000 | 00000000 | 00000000 | 00000000 |
     * ---------------------------------------------
     */
    private void startFrame() {
        data.low();
        clk.low();
        // 32-bit start frame of 0's
        this.transfer(0);
        this.transfer(0);
        this.transfer(0);
        this.transfer(0);
    }

    /**
     * Sends the end frame to the LED strip.
     * Fancy stuff happening here, refer to arduino header APA102.h for more details.
     * @param count - 16-bit int representing the number of pixels in the LED strip.
     */
    private void endFrame(short count) {
        transfer(0xFF);
        for (short i = 0; i < 5 + count / 16; i++) {
            transfer(0);
        }
    }

    /**
     * Sends the color frame of the following format.
     *  Start bits| Brightness |   BLUE   |   GREEN  |   RED   |
     * ---------------------------------------------------------
     * |   111    |   11111    | xxxxxxxx | xxxxxxxx |xxxxxxxx |
     * ---------------------------------------------------------
     * @param color - User specified color of the LED
     */
    private void sendColorFrame(Color color) {
      // Default to max brightness
      transfer((byte)0b11111111);
      transfer((byte)color.getBlue());
      transfer((byte)color.getGreen());
      transfer((byte)color.getRed());
    }

    /**
     * Outputs a byte on the data pin by isolating each bit and pulsing the clk. 
     * Data is latched via the APA102 on the rising edge of the clk signal.
     * @param b - Byte to be transmitted over the data pin
     */
    public void transfer(byte b) {
        // Isolate each bit and set data pin accordingly before pulsing clk to latch data
        data.setState((b >> 7 & 1) > 0 ? true : false);
        clk.high();
        clk.low();
        data.setState((b >> 6 & 1) > 0 ? true : false);
        clk.high();
        clk.low();
        data.setState((b >> 5 & 1) > 0 ? true : false);
        clk.high();
        clk.low();
        data.setState((b >> 4 & 1) > 0 ? true : false);
        clk.high();
        clk.low();
        data.setState((b >> 3 & 1) > 0 ? true : false);
        clk.high();
        clk.low();
        data.setState((b >> 2 & 1) > 0 ? true : false);
        clk.high();
        clk.low();
        data.setState((b >> 1 & 1) > 0 ? true : false);
        clk.high();
        clk.low();
        data.setState((b >> 0 & 1) > 0 ? true : false);
        clk.high();
        clk.low();

    }

}
