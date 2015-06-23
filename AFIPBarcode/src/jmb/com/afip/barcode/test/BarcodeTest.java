package jmb.com.afip.barcode.test;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

import com.jmb.afip.barcode.AFIPBarcodeFactory;

public class BarcodeTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AFIPBarcodeFactory barcode = new AFIPBarcodeFactory("3367657801901000162545730015425201507102");
			barcode.setBarHeight(80);
			barcode.setDrawingText(true);
			barcode.setFont(new Font("Arial",Font.PLAIN, 16));
			
			BufferedImage image = barcode.createInt2of5(600, 80);
			javax.imageio.ImageIO.write(image, "png", new File("C:\\Users\\jmbalbas\\Desktop\\barcodeTest.png"));
    			
    	} catch (Exception e) {
    		System.out.println(e.toString());
    		e.printStackTrace();
    	}
	}
}
